package com.restlearningjourney.store.products;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);


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
        logger.info("createProduct - productDto = {}" , productDto);

        var product =  productMapper.toEntity(productDto);
        Category category = categoryRepository.findById(productDto.getCategoryId())
                .orElse(null);
        if (category == null){
            throw new CategoryNotFoundException();
        }
        product.setCategory(category);
        productRepository.save(product);
        productDto.setId(product.getId());
        logger.info("createProduct - productDto Including id = {}" , productDto);
        return productDto;
    }

    public ProductDto updateProduct(Long id, ProductDto productDto) {

        logger.info("updateProduct - id = {}" , id);
        logger.info("updateProduct - product = {}" , productDto);
        var category = categoryRepository.findById(productDto.getCategoryId()).orElse(null);
        if (category == null){
            throw new CategoryNotFoundException();
        }
        var productDb = productRepository.findById(id).orElse(null);
        if (productDb == null){
            throw new ProductNotFoundException();
        }
        logger.info("productDb = {}",productDb);
        productMapper.updateProduct(productDto, productDb);
        productDb.setCategory(category);
        productRepository.save(productDb);
        productDto.setId(productDb.getId());
        return productDto;
    }

    public void deleteProduct(Long id) {
        logger.info("deleteProduct - id = {}" , id);
        var product = productRepository.findById(id).orElse(null);
        if (product == null){
            throw new ProductNotFoundException();
        }
        productRepository.delete(product);
    }
}
