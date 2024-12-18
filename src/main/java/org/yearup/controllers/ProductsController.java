package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.models.Product;
import org.yearup.data.ProductDao;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("products")  // Maps this controller to the "/products" URL
@CrossOrigin  // Enables cross-origin requests (useful for API consumption from different domains)
public class ProductsController {

    private final ProductDao productDao;  // Data access object for interacting with products

    @Autowired
    public ProductsController(ProductDao productDao) {
        this.productDao = productDao;  // Injecting the ProductDao dependency
    }

    // Method to search products based on various filters (category, price, color)
    @GetMapping("")
    @PreAuthorize("permitAll()")  // Anyone can access this endpoint
    public List<Product> search(@RequestParam(name = "cat", required = false) Integer categoryId,
                                @RequestParam(name = "minPrice", required = false) BigDecimal minPrice,
                                @RequestParam(name = "maxPrice", required = false) BigDecimal maxPrice,
                                @RequestParam(name = "color", required = false) String color) {
        try {
            return productDao.search(categoryId, minPrice, maxPrice, color);  // Fetch filtered products
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving products.");  // Handle errors
        }
    }

    // Method to get a product by its ID
    @GetMapping("{id}")
    @PreAuthorize("permitAll()")  // Anyone can access this endpoint
    public Product getById(@PathVariable int id) {
        try {
            var product = productDao.getById(id);  // Fetch product by ID
            if (product == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found.");  // Handle not found
            }
            return product;
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving product.");
        }
    }

    // Method to add a new product (only accessible by ADMIN users)
    @PostMapping()
    @PreAuthorize("hasRole('ROLE_ADMIN')")  // Only admins can create a product
    public Product addProduct(@RequestBody Product product) {
        try {
            return productDao.create(product);  // Insert new product into the database
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error creating product.");
        }
    }

    // Method to update an existing product (only accessible by ADMIN users)
    @PutMapping("{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")  // Only admins can update a product
    public void updateProduct(@PathVariable int id, @RequestBody Product product) {
        try {
            productDao.update(id, product);  // Update product by ID
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error updating product.");
        }
    }

    // Method to delete a product (only accessible by ADMIN users)
    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")  // Only admins can delete a product
    public void deleteProduct(@PathVariable int id) {
        try {
            var product = productDao.getById(id);  // Check if the product exists
            if (product == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found.");  // Handle not found
            }
            productDao.delete(id);  // Delete the product
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error deleting product.");
        }
    }
}
