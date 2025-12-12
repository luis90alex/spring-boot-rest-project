package com.restlearningjourney.store.products;

import com.restlearningjourney.store.common.ErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping()
    public List<ProductDto> getAllProducts
            (@RequestParam(required = false, name ="categoryId") Byte categoryId) {
        System.out.println("getAllProducts - categoryId = " + categoryId);
        return productService.getAllProducts(categoryId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) {
        ProductDto productDto = productService.getProductById(id);
        return ResponseEntity.ok(productDto);
    }

    @PostMapping
    public ResponseEntity<ProductDto> createProduct(
            @RequestBody ProductDto productDtoRequest,
            UriComponentsBuilder uriBuilder) {

        ProductDto productDto = productService.createProduct(productDtoRequest);
        var uri = uriBuilder.path("/products/{id}").buildAndExpand(productDto.getId()).toUri();
        System.out.println("createProduct - uri = " + uri);
        return ResponseEntity.created(uri).body(productDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(
            @PathVariable Long id,
            @RequestBody ProductDto productDto) {
        ProductDto productDtoUpdated = productService.updateProduct(id, productDto);
        return ResponseEntity.ok(productDtoUpdated);
    }

    @DeleteMapping ("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorDto> handleProductNotFound(){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ErrorDto("Product not found")
        );
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<ErrorDto> handleCategoryNotFound(){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ErrorDto("Product not found")
        );
    }
}
