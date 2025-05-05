package com.pearchInventory.ioa.services;

import com.pearchInventory.ioa.dtos.ProductDTO;
import com.pearchInventory.ioa.exceptions.BadCredentialsException;
import com.pearchInventory.ioa.exceptions.EmailExistsException;
import com.pearchInventory.ioa.exceptions.ProductExistsException;
import com.pearchInventory.ioa.exceptions.ProductNotFoundException;
import com.pearchInventory.ioa.model.Product;
import com.pearchInventory.ioa.repositories.ProductRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;


    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Creates a new product based on the provided ProductDTO.
     * The method is transactional to ensure atomicity in case of database operations.
     *
     * @param productDTO Data Transfer Object containing product details
     * @return The saved Product entity
     */
    @Transactional
    public Product createProduct(ProductDTO productDTO) {
        Product product = new Product();
        product.setName(productDTO.name()); // Set the product's name from the DTO
        product.setSku(productDTO.sku()); // Set the product's SKU from the DTO
        product.setPrice(productDTO.price()); // Set the product's price from the DTO
        product.setStock(productDTO.stock()); // Set the product's stock from the DTO

        try {
            return productRepository.save(product); // Save the product to the database
        } catch (DataIntegrityViolationException e) {
            // Handle database constraint violations (e.g., unique constraint)
            throw new ProductExistsException("A product with this sku already exists");
        } catch (Exception e) {
            // Handle other unexpected exceptions
            throw new BadCredentialsException("An error occurred while saving the user");
        }


    }

    /**
     * Retrieves all products from the database.
     *
     * @return A list of all Product entities
     */
    public List<Product> getAllProducts() {
        return productRepository.findAll(); // Fetch all products from the repository
    }

    /**
     * Retrieves a product by its SKU.
     * Throws a RuntimeException if the product is not found.
     *
     * @param sku The SKU of the product to retrieve
     * @return The Product entity with the specified SKU
     */
    public Product getProductBySku(String sku) {
        return productRepository.findBySku(sku)
                .orElseThrow(() -> new ProductNotFoundException("Product not found")); // Handle missing product
    }

    /**
     * Updates the stock of a product identified by its SKU.
     * The method is transactional to ensure consistency during the update.
     *
     * @param sku   The SKU of the product to update
     * @param stock The new stock value
     * @return The updated Product entity
     */
    @Transactional
    public Product updateStock(String sku, Integer stock) {
        Product product = productRepository.findBySku(sku)
                .orElseThrow(() -> new ProductNotFoundException("Product not found")); // Handle missing product
        product.setStock(stock); // Update the stock value
        return productRepository.save(product); // Save the updated product
    }

    /**
     * Deletes a product identified by its SKU.
     * The method is transactional to ensure consistency during the deletion.
     *
     * @param sku The SKU of the product to delete
     */
    @Transactional
    public void deleteProduct(String sku) {
        Product product = productRepository.findBySku(sku)
                .orElseThrow(() -> new ProductNotFoundException("Product not found")); // Ensure the product exists
        product.setDeleted(true); // Mark the product as deleted
        productRepository.save(product); // Save the updated product
    }
}