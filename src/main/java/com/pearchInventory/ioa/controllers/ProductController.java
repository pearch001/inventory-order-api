package com.pearchInventory.ioa.controllers;

import com.pearchInventory.ioa.dtos.ProductDTO;
import com.pearchInventory.ioa.enums.ResponseCode;
import com.pearchInventory.ioa.model.Product;
import com.pearchInventory.ioa.services.ProductService;
import com.pearchInventory.ioa.utils.GenericData;
import com.pearchInventory.ioa.utils.Response;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<Response> createProduct(@Valid @RequestBody ProductDTO productDTO) {
        Product product = productService.createProduct(productDTO);
        return ResponseEntity.status(201).body(new Response(ResponseCode.SUCCESS,new GenericData<>(product)) );
    }

    @GetMapping
    public ResponseEntity<Response> getAllProducts() {
        return ResponseEntity.ok(new Response(ResponseCode.SUCCESS,new GenericData<>(productService.getAllProducts()) ));

    }

    @GetMapping("/{sku}")
    public ResponseEntity<Response> getProductBySku(@PathVariable String sku) {
        return ResponseEntity.ok(new Response(ResponseCode.SUCCESS,new GenericData<>(productService.getProductBySku(sku))) );
    }

    @PutMapping("/{sku}/stock")
    public ResponseEntity<Response> updateStock(@PathVariable String sku, @RequestParam Integer stock) {
        return ResponseEntity.ok(new Response(ResponseCode.SUCCESS,new GenericData<>(productService.updateStock(sku, stock))) );
    }

    @DeleteMapping("/{sku}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String sku) {
        productService.deleteProduct(sku);
        return ResponseEntity.noContent().build();
    }
}