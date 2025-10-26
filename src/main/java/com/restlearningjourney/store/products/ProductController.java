package com.restlearningjourney.store.products;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;

    @GetMapping()
    public List<ProductDto> getAllProducts
            (@RequestParam(required = false, name ="categoryId")
             Byte categoryId)
    {
        System.out.println("getAllProducts - categoryId = " + categoryId);
        List<Product> products;
        if (categoryId != null){
            products = productRepository.findProductsByCategoryId(categoryId);
        }else {
            products = productRepository.findAllIncludingCategories();
        }
        return products
                .stream()
                .map(product -> productMapper.toDto(product))
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) {
        var product = productRepository.findById(id).orElse(null);
        if (product == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(productMapper.toDto(product));
    }

    @PostMapping
    public ResponseEntity<ProductDto> createProduct(
            @RequestBody ProductDto productDto,
            UriComponentsBuilder uriBuilder) {
        System.out.println("createProduct - productDto = " + productDto);

        var product =  productMapper.toEntity(productDto);
        System.out.println("createProduct - product = " + product);
        Category category = categoryRepository.findById(productDto.getCategoryId())
                .orElse(null);
        if (category == null){
            return ResponseEntity.badRequest().build();
        }
        product.setCategory(category);
        productRepository.save(product);
        productDto.setId(product.getId());
        System.out.println("createProduct - productDto Including id = " + productDto);
        var uri = uriBuilder.path("/products/{id}").buildAndExpand(productDto.getId()).toUri();
        System.out.println("createProduct - uri = " + uri);
        return ResponseEntity.created(uri).body(productDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(
            @PathVariable Long id,
            @RequestBody ProductDto productDto) {

        System.out.println("updateProduct - id = " + id);
        System.out.println("updateProduct - product = " + productDto);
        var category = categoryRepository.findById(productDto.getCategoryId()).orElse(null);
        if (category == null){
            return ResponseEntity.badRequest().build();
        }
        var productDb = productRepository.findById(id).orElse(null);
        if (productDb == null){
            return ResponseEntity.notFound().build();
        }
        System.out.println(productDb);
        productMapper.updateProduct(productDto, productDb);
        productDb.setCategory(category);
        productRepository.save(productDb);
        productDto.setId(productDb.getId());
        return ResponseEntity.ok(productMapper.toDto(productDb));
    }

    @DeleteMapping ("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        System.out.println("deleteProduct - id = " + id);
        var product = productRepository.findById(id).orElse(null);
        if (product == null){
            return ResponseEntity.notFound().build();
        }
        productRepository.delete(product);
        return ResponseEntity.ok().build();
    }
}
