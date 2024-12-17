package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.CategoryDao;
import org.yearup.data.ProductDao;
import org.yearup.models.Category;
import org.yearup.models.Product;

import java.util.List;

@RestController  // Marks this class as a REST controller
@RequestMapping("/categories")  // URL path for category-related endpoints
@CrossOrigin  // Allows cross-origin requests (useful for frontend-backend communication)
public class CategoriesController {

    private final CategoryDao categoryDao;  // Interface for Category DAO (Database access)
    private final ProductDao productDao;  // Interface for Product DAO (Database access)

    // Constructor-based dependency injection for CategoryDao and ProductDao
    @Autowired
    public CategoriesController(CategoryDao categoryDao, ProductDao productDao) {
        this.categoryDao = categoryDao;
        this.productDao = productDao;
    }

    // GET /categories - Get all categories
    @GetMapping("")  // Handles GET requests to /categories
    public ResponseEntity<List<Category>> getAll() {
        try {
            List<Category> categories = categoryDao.getAllCategories();  // Fetch all categories
            return ResponseEntity.ok(categories);  // Return 200 OK with categories
        } catch (Exception ex) {
            // If any exception occurs, return a 500 INTERNAL SERVER ERROR
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving categories", ex);
        }
    }

    // GET /categories/{id} - Get category by ID
    @GetMapping("/{id}")  // Handles GET requests to /categories/{id}
    public ResponseEntity<Category> getById(@PathVariable int id) {
        Category category = categoryDao.getById(id);  // Fetch category by ID
        if (category == null) {
            // If category not found, return 404 NOT FOUND
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found");
        }
        return ResponseEntity.ok(category);  // Return 200 OK with the category data
    }

    // GET /categories/{categoryId}/products - Get all products for a specific category
    @GetMapping("/{categoryId}/products")  // Handles GET requests to /categories/{categoryId}/products
    public ResponseEntity<List<Product>> getProductsByCategoryId(@PathVariable int categoryId) {
        try {
            List<Product> products = productDao.listByCategoryId(categoryId);  // Fetch products by category ID
            return ResponseEntity.ok(products);  // Return 200 OK with the products list
        } catch (Exception ex) {
            // Return 500 INTERNAL SERVER ERROR in case of exception
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving products for category", ex);
        }
    }

    // POST /categories - Add a new category (only accessible by ADMIN)
    @PreAuthorize("hasRole('ROLE_ADMIN')")  // Only users with ADMIN role can add a category
    @PostMapping("")  // Handles POST requests to /categories
    public ResponseEntity<Category> addCategory(@RequestBody Category category) {
        try {
            Category createdCategory = categoryDao.create(category);  // Create new category in the database
            return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);  // Return 201 Created with the new category data
        } catch (Exception ex) {
            // Return 500 INTERNAL SERVER ERROR if an error occurs
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error creating category", ex);
        }
    }

    // PUT /categories/{id} - Update an existing category by ID (only accessible by ADMIN)
    @PreAuthorize("hasRole('ROLE_ADMIN')")  // Only users with ADMIN role can update a category
    @PutMapping("/{id}")  // Handles PUT requests to /categories/{id}
    public ResponseEntity<Category> updateCategory(@PathVariable int id, @RequestBody Category category) {
        try {
            categoryDao.update(id, category);  // Update the category in the database
            Category updatedCategory = categoryDao.getById(id);  // Retrieve the updated category
            return ResponseEntity.ok(updatedCategory);  // Return 200 OK with the updated category data
        } catch (Exception ex) {
            // Return 500 INTERNAL SERVER ERROR if an error occurs
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error updating category", ex);
        }
    }

    // DELETE /categories/{id} - Delete a category by ID (only accessible by ADMIN)
    @PreAuthorize("hasRole('ROLE_ADMIN')")  // Only users with ADMIN role can delete a category
    @DeleteMapping("/{id}")  // Handles DELETE requests to /categories/{id}
    @ResponseStatus(HttpStatus.NO_CONTENT)  // Return 204 No Content status when deletion is successful
    public void deleteCategory(@PathVariable int id) {
        try {
            categoryDao.delete(id);  // Delete the category by ID from the database
        } catch (Exception ex) {
            // Return 500 INTERNAL SERVER ERROR if an error occurs
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error deleting category", ex);
        }
    }
}
