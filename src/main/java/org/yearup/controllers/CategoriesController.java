package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.yearup.data.CategoryDao;
import org.yearup.data.ProductDao;
import org.yearup.models.Category;
import org.yearup.models.Product;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController  // Marks this class as a REST controller (to handle API requests)
@RequestMapping("/categories")  // Base URL for category operations, e.g., /categories
@CrossOrigin  // Allows cross-origin requests, so the API can be accessed from different domains
public class CategoriesController {

    // Injecting the CategoryDao and ProductDao to interact with the database
    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private ProductDao productDao;

    // GET /categories - Fetch all categories
    @GetMapping("")  // Handles GET requests to /categories
    public List<Category> getAll() {
        // Fetch all categories using the categoryDao
        return categoryDao.getAllCategories();
    }

    // GET /categories/{id} - Fetch a category by ID
    @GetMapping("/{id}")  // Handles GET requests to /categories/{id}
    public Category getById(@PathVariable int id) {
        // Fetch the category by its ID using categoryDao
        return categoryDao.getById(id);
    }

    // GET /categories/{categoryId}/products - Fetch all products for a specific category
    @GetMapping("/{categoryId}/products")  // Handles GET requests to /categories/{categoryId}/products
    public List<Product> getProductsById(@PathVariable int categoryId) {
        // Fetch all products in the category using productDao
        return productDao.listByCategoryId(categoryId);
    }

    // POST /categories - Add a new category (only accessible by ADMIN users)
    @PreAuthorize("hasRole('ADMIN')")  // Ensures that only users with the 'ADMIN' role can call this
    @PostMapping("")  // Handles POST requests to /categories
    public Category addCategory(@RequestBody Category category) {
        // Add a new category to the database using categoryDao
        return categoryDao.create(category);
    }

    // PUT /categories/{id} - Update an existing category by ID (only accessible by ADMIN users)
    @PreAuthorize("hasRole('ADMIN')")  // Ensures that only users with the 'ADMIN' role can call this
    @PutMapping("/{id}")  // Handles PUT requests to /categories/{id}
    public Category updateCategory(@PathVariable int id, @RequestBody Category category) {
        // Update the category using categoryDao, and return the updated category
        categoryDao.update(id, category);
        return categoryDao.getById(id);  // Return the updated category from the database
    }

    // DELETE /categories/{id} - Delete a category by ID (only accessible by ADMIN users)
    @PreAuthorize("hasRole('ADMIN')")  // Ensures that only users with the 'ADMIN' role can call this
    @DeleteMapping("/{id}")  // Handles DELETE requests to /categories/{id}
    public void deleteCategory(@PathVariable int id) {
        // Delete the category from the database using categoryDao
        categoryDao.delete(id);
    }
}
