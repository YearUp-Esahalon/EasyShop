package org.yearup.data.mysql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yearup.models.Product;
import org.yearup.data.ProductDao;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class MySqlProductDao extends MySqlDaoBase implements ProductDao {

    @Autowired
    public MySqlProductDao(DataSource dataSource) {
        super(dataSource);
    }

    // Simplified search method with dynamic query construction based on parameters
    @Override
    public List<Product> search(Integer categoryId, BigDecimal minPrice, BigDecimal maxPrice, String color) {
        List<Product> products = new ArrayList<>();

        // Start with a base query
        StringBuilder sql = new StringBuilder("SELECT * FROM products WHERE 1=1");

        // Dynamically append conditions based on the provided filters
        if (categoryId != null) {
            sql.append(" AND category_id = ?");
        }
        if (minPrice != null) {
            sql.append(" AND price >= ?");
        }
        if (maxPrice != null) {
            sql.append(" AND price <= ?");
        }
        if (color != null && !color.isEmpty()) {
            sql.append(" AND color = ?");
        }

        // Execute the query and map the results to product objects
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql.toString());

            // Set the values in the prepared statement based on the parameters
            int index = 1;
            if (categoryId != null) {
                statement.setInt(index++, categoryId);
            }
            if (minPrice != null) {
                statement.setBigDecimal(index++, minPrice);
            }
            if (maxPrice != null) {
                statement.setBigDecimal(index++, maxPrice);
            }
            if (color != null && !color.isEmpty()) {
                statement.setString(index++, color);
            }

            // Execute query and populate the product list
            ResultSet row = statement.executeQuery();
            while (row.next()) {
                Product product = mapRow(row);
                products.add(product);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return products;
    }

    // Retrieve a list of products by categoryId
    @Override
    public List<Product> listByCategoryId(int categoryId) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE category_id = ?";

        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, categoryId);

            ResultSet row = statement.executeQuery();
            while (row.next()) {
                Product product = mapRow(row);
                products.add(product);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return products;
    }

    // Retrieve a product by its ID
    @Override
    public Product getById(int productId) {
        String sql = "SELECT * FROM products WHERE product_id = ?";
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, productId);

            ResultSet row = statement.executeQuery();
            if (row.next()) {
                return mapRow(row);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    // Create a new product and return it
    @Override
    public Product create(Product product) {
        String sql = "INSERT INTO products(name, price, category_id, description, color, image_url, stock, featured) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            statement.setString(1, product.getName());
            statement.setBigDecimal(2, product.getPrice());
            statement.setInt(3, product.getCategoryId());
            statement.setString(4, product.getDescription());
            statement.setString(5, product.getColor());
            statement.setString(6, product.getImageUrl());
            statement.setInt(7, product.getStock());
            statement.setBoolean(8, product.isFeatured());

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                // Retrieve the generated keys
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    // Get the generated product ID and fetch the product
                    int productId = generatedKeys.getInt(1);
                    return getById(productId);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    // Update an existing product
    @Override
    public void update(int productId, Product product) {
        // Check if the product exists
        Product existingProduct = getById(productId);
        if (existingProduct == null) {
            throw new RuntimeException("Product not found for update");
        }

        // SQL query to update the product fields
        String sql = "UPDATE products SET name = ?, price = ?, category_id = ?, description = ?, color = ?, " +
                "image_url = ?, stock = ?, featured = ? WHERE product_id = ?";

        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, product.getName());
            statement.setBigDecimal(2, product.getPrice());
            statement.setInt(3, product.getCategoryId());
            statement.setString(4, product.getDescription());
            statement.setString(5, product.getColor());
            statement.setString(6, product.getImageUrl());
            statement.setInt(7, product.getStock());
            statement.setBoolean(8, product.isFeatured());
            statement.setInt(9, productId);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("Failed to update product");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error updating product: " + e.getMessage(), e);
        }
    }

    // Delete a product by its ID
    @Override
    public void delete(int productId) {
        String sql = "DELETE FROM products WHERE product_id = ?";

        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, productId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Map a ResultSet row to a Product object
    protected static Product mapRow(ResultSet row) throws SQLException {
        int productId = row.getInt("product_id");
        String name = row.getString("name");
        BigDecimal price = row.getBigDecimal("price");
        int categoryId = row.getInt("category_id");
        String description = row.getString("description");
        String color = row.getString("color");
        int stock = row.getInt("stock");
        boolean isFeatured = row.getBoolean("featured");
        String imageUrl = row.getString("image_url");

        return new Product(productId, name, price, categoryId, description, color, stock, isFeatured, imageUrl);
    }
}
