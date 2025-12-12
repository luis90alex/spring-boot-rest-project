package com.restlearningjourney.store.products;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, ProductMapper productMapper, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.categoryRepository = categoryRepository;
    }

    public List<ProductDto> getAllProducts(Byte categoryId) {

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

    public ProductDto getProductById(Long id) {
        var product = productRepository.findById(id).orElse(null);
        if (product == null){
            throw new ProductNotFoundException();
        }
        return productMapper.toDto(product);
    }

    public ProductDto createProduct(ProductDto productDto) {
        System.out.println("createProduct - productDto = " + productDto);

        var product =  productMapper.toEntity(productDto);
        Category category = categoryRepository.findById(productDto.getCategoryId())
                .orElse(null);
        if (category == null){
            throw new CategoryNotFoundException();
        }
        product.setCategory(category);
        productRepository.save(product);
        productDto.setId(product.getId());
        System.out.println("createProduct - productDto Including id = " + productDto);
        return productDto;
    }

    public ProductDto updateProduct(Long id, ProductDto productDto) {

        System.out.println("updateProduct - id = " + id);
        System.out.println("updateProduct - product = " + productDto);
        var category = categoryRepository.findById(productDto.getCategoryId()).orElse(null);
        if (category == null){
            throw new CategoryNotFoundException();
        }
        var productDb = productRepository.findById(id).orElse(null);
        if (productDb == null){
            throw new ProductNotFoundException();
        }
        System.out.println(productDb);
        productMapper.updateProduct(productDto, productDb);
        productDb.setCategory(category);
        productRepository.save(productDb);
        productDto.setId(productDb.getId());
        return productDto;
    }

    public void deleteProduct(Long id) {
        System.out.println("deleteProduct - id = " + id);
        var product = productRepository.findById(id).orElse(null);
        if (product == null){
            throw new ProductNotFoundException();
        }
        productRepository.delete(product);
    }
}
