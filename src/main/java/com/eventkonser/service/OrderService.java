package com.eventkonser.service;

import com.eventkonser.model.*;
import com.eventkonser.repository.*;
import com.eventkonser.exception.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final NotificationService notificationService;
    
    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public long countAllOrders() {
        try {
            // Gunakan count query yang lebih efficient
            long count = orderRepository.count();
            log.info("Total orders count: {}", count);
            return count;
        } catch (Exception e) {
            log.error("Error counting all orders: ", e);
            return 0L;
        }
    }
    
    @Transactional(readOnly = true)
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Order tidak ditemukan dengan ID: " + id));
    }
    
    @Transactional(readOnly = true)
    public List<Order> getOrdersByUser(Long userId) {
        return orderRepository.findByUser_IdPenggunaOrderByTanggalPembelianDesc(userId);
    }
    
    @Transactional(readOnly = true)
    public List<Order> getOrdersByUserWithDetails(Long userId) {
        return orderRepository.findByUserWithDetails(userId);
    }
    
    @Transactional(readOnly = true)
    public Order getOrderByQRCode(String qrCode) {
        return orderRepository.findByQrCode(qrCode)
            .orElseThrow(() -> new ResourceNotFoundException("Order tidak ditemukan dengan QR Code: " + qrCode));
    }
    
    /**
     * BOOKING TICKET - CRITICAL TRANSACTION
     * Menggunakan Pessimistic Lock untuk prevent race condition
     */
    @Transactional
    public Order bookTicket(Long userId, Long ticketId, Integer quantity, 
                           BigDecimal discountAmount, String promoCode, BigDecimal subtotal) {
        // 1. Validasi user
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User tidak ditemukan"));
        
        // 2. Lock ticket row dan validasi stok (PESSIMISTIC LOCK)
        Ticket ticket = ticketRepository.findByIdWithLock(ticketId)
            .orElseThrow(() -> new ResourceNotFoundException("Tiket tidak ditemukan"));
        
        // 3. Validasi status tiket
        if (ticket.getStatus() != TicketStatus.AVAILABLE) {
            throw new InvalidRequestException("Tiket tidak tersedia");
        }
        
        // 4. Validasi stok
        if (ticket.getStok() < quantity) {
            throw new InsufficientStockException(
                "Stok tiket tidak mencukupi. Tersisa: " + ticket.getStok()
            );
        }
        
        // 5. Validasi max pembelian
        Integer maxPembelian = ticket.getMaxPembelian();
        if (maxPembelian != null && quantity > maxPembelian) {
            throw new InvalidRequestException(
                "Maksimal pembelian " + maxPembelian + " tiket per transaksi"
            );
        }
        
        // 6. Hitung total harga
        BigDecimal totalHarga = ticket.getHarga().multiply(BigDecimal.valueOf(quantity));
        
        // 7. Create order
        Order order = new Order();
        order.setUser(user);
        order.setTicket(ticket);
        order.setJumlah(quantity);
        order.setTotalHarga(totalHarga);
        order.setTanggalPembelian(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        
        // Set discount dan promo info
        order.setSubtotal(subtotal != null ? subtotal : totalHarga);
        order.setDiscountAmount(discountAmount != null ? discountAmount : BigDecimal.ZERO);
        order.setPromoCode(promoCode);
        
        // Snapshot info untuk history
        try {
            Event event = ticket.getEvent();
            if (event != null) {
                order.setEventName(event.getNamaEvent());
                order.setEventDate(event.getTanggalMulai() != null ? event.getTanggalMulai().toString() : "");
                order.setEventImageUrl(event.getPosterUrl()); // Capture poster URL
                order.setIdEvent(event.getIdEvent()); // Capture event ID
                if (event.getVenue() != null) {
                    order.setVenueName(event.getVenue().getNamaVenue());
                }
            }
        } catch (Exception e) {
            // If event/venue data is corrupted, just use ticket info
            order.setEventName("Event Information Unavailable");
            order.setEventDate("");
            order.setVenueName("Venue Information Unavailable");
        }
        order.setTicketType(ticket.getJenisTiket());
        
        // 8. Kurangi stok
        ticket.setStok(ticket.getStok() - quantity);
        ticketRepository.save(ticket);
        
        // Update status jika sold out
        if (ticket.getStok() == 0) {
            ticket.setStatus(TicketStatus.SOLD_OUT);
        }
        
        // 9. Save order
        Order savedOrder = orderRepository.save(order);
        
        // 10. Create payment record
        Payment payment = new Payment();
        payment.setOrder(savedOrder);
        payment.setJumlahBayar(totalHarga);
        payment.setStatusPembayaran(PaymentStatus.PENDING);
        payment.setMetodePembayaran("MOCK"); // Default MOCK payment untuk development
        payment.setExpiredAt(LocalDateTime.now().plusMinutes(15));
        paymentRepository.save(payment);
        
        // 11. Send notification ke user
        try {
            String eventName = savedOrder.getEventName() != null ? savedOrder.getEventName() : "Event";
            notificationService.sendNotification(
                userId,
                "Pemesanan Tiket Berhasil",
                "Pesanan Anda untuk event " + eventName + " sebanyak " + quantity + " tiket telah dibuat. Silakan selesaikan pembayaran dalam 15 menit.",
                "order",
                "/orders/" + savedOrder.getIdPembelian()
            );
        } catch (Exception e) {
            // Log error tapi jangan sampai bikin order failed
            System.err.println("⚠️ Notification failed: " + e.getMessage());
        }
        
        return savedOrder;
        // Jika ada error, semua rollback otomatis karena @Transactional
    }
    
    @Transactional
    public Order cancelOrder(Long orderId) {
        Order order = getOrderById(orderId);
        
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new InvalidRequestException("Order tidak dapat dibatalkan");
        }
        
        // Kembalikan stok
        Ticket ticket = order.getTicket();
        ticket.setStok(ticket.getStok() + order.getJumlah());
        ticket.setStatus(TicketStatus.AVAILABLE);
        ticketRepository.save(ticket);
        
        // Update order status
        order.setStatus(OrderStatus.CANCELLED);
        Order cancelledOrder = orderRepository.save(order);
        
        // Send notification
        try {
            notificationService.sendNotification(
                order.getUser().getIdPengguna(),
                "Pemesanan Dibatalkan",
                "Pesanan Anda untuk event " + (order.getEventName() != null ? order.getEventName() : "Event") + " telah dibatalkan. Stok tiket telah dikembalikan.",
                "order",
                "/orders/" + orderId
            );
        } catch (Exception e) {
            System.err.println("⚠️ Notification failed: " + e.getMessage());
        }
        
        return cancelledOrder;
    }
    
    @Transactional
    public Order markAsUsed(String qrCode) {
        Order order = getOrderByQRCode(qrCode);
        
        if (order.getStatus() != OrderStatus.PAID) {
            throw new InvalidRequestException("Tiket tidak valid untuk check-in");
        }
        
        if (order.getUsedAt() != null) {
            throw new InvalidRequestException("Tiket sudah digunakan pada: " + order.getUsedAt());
        }
        
        order.setStatus(OrderStatus.USED);
        order.setUsedAt(LocalDateTime.now());
        
        return orderRepository.save(order);
    }
    
    @Transactional
    public void processExpiredOrders() {
        List<Order> expiredOrders = orderRepository.findExpiredOrders(LocalDateTime.now());
        
        for (Order order : expiredOrders) {
            // Kembalikan stok
            Ticket ticket = order.getTicket();
            ticket.setStok(ticket.getStok() + order.getJumlah());
            ticket.setStatus(TicketStatus.AVAILABLE);
            ticketRepository.save(ticket);
            
            // Update status
            order.setStatus(OrderStatus.EXPIRED);
            orderRepository.save(order);
        }
    }
    
    @Transactional(readOnly = true)
    public Double getTotalRevenue() {
        try {
            Double revenue = orderRepository.getTotalRevenue();
            
            // Jika query mengembalikan null, hitung manual dari semua order PAID
            if (revenue == null) {
                List<Order> paidOrders = orderRepository.findByStatus(OrderStatus.PAID);
                revenue = paidOrders.stream()
                    .map(Order::getTotalHarga)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .doubleValue();
                System.out.println("getTotalRevenue (manual): " + revenue + " from " + paidOrders.size() + " PAID orders");
            } else {
                System.out.println("getTotalRevenue (query): " + revenue);
            }
            
            return revenue != null ? revenue : 0.0;
        } catch (Exception e) {
            System.err.println("Error calculating getTotalRevenue: " + e.getMessage());
            e.printStackTrace();
            return 0.0;
        }
    }
    
    @Transactional(readOnly = true)
    public long countOrdersByStatus(OrderStatus status) {
        return orderRepository.countByStatus(status);
    }
    
    @Transactional(readOnly = true)
    public List<Order> getOrdersByUserAndStatus(Long userId, OrderStatus status) {
        return orderRepository.findByUser_IdPenggunaAndStatusOrderByTanggalPembelianDesc(userId, status);
    }
    
    @Transactional
    public Order updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = getOrderById(orderId);
        order.setStatus(newStatus);
        return orderRepository.save(order);
    }
}