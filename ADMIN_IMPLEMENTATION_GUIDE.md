# Admin Panel Implementation Guide

## Quick Reference

### Using Admin Helper Functions

All admin functions are accessible via `window.admin.*` namespace after `admin.js` loads.

#### Form Operations
```javascript
// Populate a select dropdown with API data
await window.admin.populateSelect(selectId, endpoint, labelField, valueField);

// Fill form fields from object
window.admin.fillForm(formId, data);

// Get form data as object
const formData = window.admin.getFormData(formId);

// Validate required fields
if (!window.admin.validateRequired(formId, requiredFields)) return;

// Disable/enable form while submitting
window.admin.setFormSubmitting(formId, true);
```

#### Data Display
```javascript
// Format currency to IDR
window.admin.formatCurrency(1000000); // "Rp 1.000.000"

// Format date
window.admin.formatDate("2024-11-30"); // "30 Nov 2024 14:30"
window.admin.formatDateOnly("2024-11-30"); // "30 Nov 2024"

// Get status badge classes
window.admin.getStatusBadge("COMPLETED"); // "bg-gray-100 text-gray-800"
window.admin.getPaymentStatusBadge("SUCCESS"); // "bg-green-100 text-green-800"

// Create table row HTML
const row = window.admin.createTableRow(data, columns);
```

#### CRUD Operations
```javascript
// Delete item with confirmation
await window.admin.deleteItem(endpoint, itemId, itemName);

// Generic API call with error handling
const result = await window.admin.apiCall(endpoint, {
    method: 'POST',
    body: JSON.stringify(data)
});
```

#### Utilities
```javascript
// Show notification
window.admin.showToast("Item deleted!", "success");
window.admin.showToast("Error occurred", "error");

// Confirmation dialog
if (window.admin.showConfirm("Delete this item?")) {
    // User clicked OK
}

// Paginate data
const paginated = window.admin.paginate(array, pageNumber, pageSize);

// Search items
const results = window.admin.searchItems(items, searchTerm, searchFields);

// Debounce function
const debouncedSearch = window.admin.debounce(searchFunction, 300);
```

#### Authentication
```javascript
// Check if user is admin
if (window.admin.isAdmin()) {
    // Show admin features
}

// Protect admin page (redirects if not admin)
document.addEventListener('DOMContentLoaded', () => {
    window.admin.protectAdminPage();
});
```

---

## Adding New Management Page

### Step 1: Create HTML File
```html
<!DOCTYPE html>
<html>
<head>
    <title>New Item Management - Event Konser</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link rel="stylesheet" href="/css/main.css">
</head>
<body class="bg-gray-50" data-page="newitem-management">
    <div class="flex">
        <div id="admin-sidebar-placeholder"></div>
        <div class="flex-1 flex flex-col">
            <header id="navbar-placeholder"></header>
            <main class="flex-1 min-h-screen pb-16">
                <!-- Your content here -->
            </main>
            <footer id="footer-placeholder"></footer>
        </div>
    </div>

    <script src="/js/api.js"></script>
    <script src="/js/auth.js"></script>
    <script src="/js/admin.js"></script>
    <script src="/js/app.js"></script>
    <script>
        document.addEventListener('DOMContentLoaded', async () => {
            window.admin.protectAdminPage();
            // Your init code here
        });
    </script>
</body>
</html>
```

### Step 2: Add Link to Sidebar
Edit `src/main/resources/static/components/admin-sidebar.html`:
```html
<a href="/newitem-management.html" class="admin-sidebar-link">
    📦 New Items
</a>
```

### Step 3: Add to Navbar Dropdown
Edit `src/main/resources/static/components/navbar.html`:
```html
<a href="/newitem-management.html" class="admin-dropdown-link block px-4 py-2 text-sm text-gray-700 hover:bg-primary-50 hover:text-primary-600">📦 New Items</a>
```

### Step 4: Create Backend Endpoints
```java
@RestController
@RequestMapping("/api/newitem")
@PreAuthorize("hasRole('ADMIN')")
public class NewItemController {
    
    @GetMapping
    public ApiResponse<?> getAll() { }
    
    @GetMapping("/{id}")
    public ApiResponse<?> getById(@PathVariable Long id) { }
    
    @PostMapping
    public ApiResponse<?> create(@RequestBody NewItemDTO dto) { }
    
    @PutMapping("/{id}")
    public ApiResponse<?> update(@PathVariable Long id, @RequestBody NewItemDTO dto) { }
    
    @DeleteMapping("/{id}")
    public ApiResponse<?> delete(@PathVariable Long id) { }
}
```

---

## Common Patterns

### Modal CRUD Form
```javascript
// Edit item
function editItem(itemId) {
    const item = itemsData.find(i => i.id === itemId);
    window.admin.fillForm('item-form', item);
    document.getElementById('item-modal').classList.remove('hidden');
}

// Save item
document.getElementById('item-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const formData = window.admin.getFormData('item-form');
    
    try {
        const response = await window.admin.apiCall('/api/newitem', {
            method: 'POST',
            body: JSON.stringify(formData)
        });
        
        if (response.success) {
            window.admin.showToast('Saved successfully!', 'success');
            await loadItems();
        }
    } catch (error) {
        window.admin.showToast('Error saving item', 'error');
    }
});
```

### Table with Search
```javascript
function renderTable(items) {
    const tbody = document.getElementById('items-table-body');
    tbody.innerHTML = items.map(item => `
        <tr class="hover:bg-gray-50">
            <td>${item.id}</td>
            <td>${item.name}</td>
            <td>
                <button onclick="editItem(${item.id})">Edit</button>
                <button onclick="deleteItem(${item.id})">Delete</button>
            </td>
        </tr>
    `).join('');
}

function handleSearch() {
    const query = document.getElementById('search-box').value;
    const filtered = window.admin.searchItems(itemsData, query, ['name', 'description']);
    renderTable(filtered);
}
```

### Populate Dropdown
```javascript
async function loadCategories() {
    await window.admin.populateSelect(
        'category-select',
        '/api/category',
        'namaKategori',
        'idKategori'
    );
}
```

---

## API Response Format

All backend endpoints should return consistent format:

```json
{
    "success": true,
    "message": "Operation successful",
    "data": {
        "id": 1,
        "name": "Item Name",
        ...
    },
    "timestamp": "2024-11-30T14:30:00"
}
```

---

## CSS Utilities (Tailwind)

### Status Badges
```html
<span class="px-3 py-1 rounded-full text-xs font-medium bg-green-100 text-green-800">Active</span>
<span class="px-3 py-1 rounded-full text-xs font-medium bg-red-100 text-red-800">Inactive</span>
```

### Buttons
```html
<!-- Primary -->
<button class="px-4 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700">Action</button>

<!-- Secondary -->
<button class="px-4 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50">Cancel</button>

<!-- Danger -->
<button class="px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700">Delete</button>
```

### Forms
```html
<div>
    <label class="block text-sm font-medium text-gray-700 mb-1">Field Name *</label>
    <input type="text" class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500" required>
</div>
```

---

## Debugging

### Check Auth Status
```javascript
console.log('User:', window.auth.getUser());
console.log('Is Admin:', window.auth.isAdmin());
console.log('Token:', window.auth.getToken());
```

### Check API Response
```javascript
const response = await fetch('/api/endpoint');
const data = await response.json();
console.log('API Response:', data);
```

### Monitor Network
- Open DevTools (F12)
- Go to Network tab
- Perform action
- Check request headers include `Authorization: Bearer <token>`
- Check response is 200 (not 403 Forbidden)

---

## Troubleshooting

| Issue | Solution |
|-------|----------|
| Sidebar not loading | Check admin-sidebar-placeholder exists in HTML |
| Admin menu not showing | Verify `window.auth.isAdmin()` returns true |
| API calls failing | Check token in localStorage, verify @PreAuthorize on controller |
| Form not populating | Ensure API returns correct field names matching form input names |
| Dropdown not opening | Check admin-menu-trigger and setupAdminMenuDropdown() runs |
| Redirect to login on admin page | Verify `protectAdminPage()` called after all scripts loaded |

---

## Performance Tips

1. **Debounce search**: Use `window.admin.debounce()` for search inputs
2. **Paginate results**: Limit table rows to 50 per page
3. **Lazy load images**: Use loading="lazy" on img tags
4. **Cache API responses**: Store in memory if data doesn't change frequently
5. **Minify JS**: Use webpack/gulp in production

---

**Last Updated:** November 30, 2025
