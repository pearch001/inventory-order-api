package com.pearchInventory.ioa.services;

import com.pearchInventory.ioa.dtos.ProductDTO;
import com.pearchInventory.ioa.exceptions.ProductNotFoundException;
import com.pearchInventory.ioa.model.Product;
import com.pearchInventory.ioa.repositories.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void createProduct_Success() {
        ProductDTO productDTO = new ProductDTO("Product1", "SKU123", BigDecimal.valueOf(100), 10);
        Product product = new Product();
        product.setName(productDTO.name());
        product.setSku(productDTO.sku());
        product.setPrice(productDTO.price());
        product.setStock(productDTO.stock());

        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product savedProduct = productService.createProduct(productDTO);

        assertNotNull(savedProduct);
        assertEquals("Product1", savedProduct.getName());
        assertEquals("SKU123", savedProduct.getSku());
        assertEquals(BigDecimal.valueOf(100), savedProduct.getPrice());
        assertEquals(10, savedProduct.getStock());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void getAllProducts_Success() {
        Product product1 = new Product();
        product1.setName("Product1");
        product1.setSku("SKU123");

        Product product2 = new Product();
        product2.setName("Product2");
        product2.setSku("SKU124");

        when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2));

        List<Product> products = productService.getAllProducts();

        assertNotNull(products);
        assertEquals(2, products.size());
        verify(productRepository).findAll();
    }

    @Test
    void getProductBySku_Success() {
        Product product = new Product();
        product.setName("Product1");
        product.setSku("SKU123");

        when(productRepository.findBySku(anyString())).thenReturn(Optional.of(product));

        Product foundProduct = productService.getProductBySku("SKU123");

        assertNotNull(foundProduct);
        assertEquals("Product1", foundProduct.getName());
        assertEquals("SKU123", foundProduct.getSku());
        verify(productRepository).findBySku("SKU123");
    }

    @Test
    void getProductBySku_ProductNotFound() {
        when(productRepository.findBySku(anyString())).thenReturn(Optional.empty());

        ProductNotFoundException exception = assertThrows(ProductNotFoundException.class, () -> productService.getProductBySku("SKU123"));
        assertEquals("Product not found", exception.getMessage());
        verify(productRepository).findBySku("SKU123");
    }

    @Test
    void updateStock_Success() {
        Product product = new Product();
        product.setName("Product1");
        product.setSku("SKU123");
        product.setStock(10);

        when(productRepository.findBySku(anyString())).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product updatedProduct = productService.updateStock("SKU123", 20);

        assertNotNull(updatedProduct);
        assertEquals(20, updatedProduct.getStock());
        verify(productRepository).findBySku("SKU123");
        verify(productRepository).save(product);
    }

    @Test
    void updateStock_ProductNotFound() {
        when(productRepository.findBySku(anyString())).thenReturn(Optional.empty());

        ProductNotFoundException exception = assertThrows(ProductNotFoundException.class, () -> productService.updateStock("SKU123", 20));
        assertEquals("Product not found", exception.getMessage());
        verify(productRepository).findBySku("SKU123");
    }

    @Test
    void deleteProduct_SoftDelete_Success() {
        // Arrange
        Product product = new Product();
        product.setName("Product1");
        product.setSku("SKU123");
        product.setDeleted(false); // Initially not deleted

        when(productRepository.findBySku(anyString())).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // Act
        productService.deleteProduct("SKU123");

        // Assert
        assertTrue(product.isDeleted()); // Verify the product is marked as deleted
        verify(productRepository).findBySku("SKU123");
        verify(productRepository).save(product);
    }

    @Test
    void deleteProduct_SoftDelete_ProductNotFound() {
        // Arrange
        when(productRepository.findBySku(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        ProductNotFoundException exception = assertThrows(ProductNotFoundException.class,
                () -> productService.deleteProduct("SKU123"));
        assertEquals("Product not found", exception.getMessage());
        verify(productRepository).findBySku("SKU123");
        verify(productRepository, never()).save(any(Product.class));
    }
}