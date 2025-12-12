package com.restlearningjourney.store.products;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void givenCategoryId_whenGetAllProducts_thenReturnProductsOfThatCategory() {
        // given: categoryId provided and repository returns filtered products
        byte categoryId = 3;
        Product p1 = new Product();
        Product p2 = new Product();
        when(productRepository.findProductsByCategoryId(categoryId)).thenReturn(List.of(p1, p2));

        ProductDto dto1 = mock(ProductDto.class);
        ProductDto dto2 = mock(ProductDto.class);
        when(productMapper.toDto(p1)).thenReturn(dto1);
        when(productMapper.toDto(p2)).thenReturn(dto2);

        // when: calling service
        List<ProductDto> result = productService.getAllProducts(categoryId);

        // then: returns mapped DTOs for that category
        assertNotNull(result);
        assertEquals(2, result.size());
        assertSame(dto1, result.get(0));
        assertSame(dto2, result.get(1));

        verify(productRepository).findProductsByCategoryId(categoryId);
        verify(productMapper).toDto(p1);
        verify(productMapper).toDto(p2);
    }

    @Test
    void givenNoCategoryId_whenGetAllProducts_thenReturnAllProducts() {
        // given: no categoryId, repository returns all products including categories
        Product p = new Product();
        when(productRepository.findAllIncludingCategories()).thenReturn(List.of(p));

        ProductDto dto = mock(ProductDto.class);
        when(productMapper.toDto(p)).thenReturn(dto);

        // when
        List<ProductDto> result = productService.getAllProducts(null);

        // then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertSame(dto, result.get(0));

        verify(productRepository).findAllIncludingCategories();
        verify(productMapper).toDto(p);
    }

    @Test
    void givenExistingProductId_whenGetProductById_thenReturnProductDto() {
        // given: product exists in repository
        Long id = 5L;
        Product product = new Product();
        when(productRepository.findById(id)).thenReturn(Optional.of(product));

        ProductDto dto = mock(ProductDto.class);
        when(productMapper.toDto(product)).thenReturn(dto);

        // when
        ProductDto result = productService.getProductById(id);

        // then
        assertNotNull(result);
        assertSame(dto, result);

        verify(productRepository).findById(id);
        verify(productMapper).toDto(product);
    }

    @Test
    void givenNonExistingProductId_whenGetProductById_thenThrowProductNotFoundException() {
        // given: repository returns empty
        Long id = 100L;
        when(productRepository.findById(id)).thenReturn(Optional.empty());

        // when & then: expect exception
        assertThrows(ProductNotFoundException.class, () -> productService.getProductById(id));

        verify(productRepository).findById(id);
        verifyNoInteractions(productMapper);
    }

    @Test
    void givenValidProductDto_whenCreateProduct_thenSaveAndReturnDtoWithId() {
        // given: productDto and mapping to entity, category exists
        ProductDto dto = new ProductDto(1L,"name", "description",
                BigDecimal.ONE , (byte) 2);
        Product productEntity = new Product();
        productEntity.setCategory(new Category(dto.getCategoryId()));
        // productEntity initially has no id
        when(productMapper.toEntity(dto)).thenReturn(productEntity);
        Category category = new Category();
        when(categoryRepository.findById(dto.getCategoryId())).thenReturn(Optional.of(category));

        // simulate save assigns id
        doAnswer(invocation -> {
            Product p = invocation.getArgument(0);
            p.setId(10L);
            return p;
        }).when(productRepository).save(productEntity);

        // when
        ProductDto result = productService.createProduct(dto);

        // then: dto should be updated with generated id
        assertNotNull(result);
        assertEquals(10L, result.getId());

        verify(productMapper).toEntity(dto);
        verify(categoryRepository).findById(dto.getCategoryId());
        verify(productRepository).save(productEntity);
    }

    @Test
    void givenProductDtoWithMissingCategory_whenCreateProduct_thenThrowCategoryNotFoundException() {
        // given: mapper -> entity, but category not found
        ProductDto dto = mock(ProductDto.class);
        Product productEntity = new Product();
        when(productMapper.toEntity(dto)).thenReturn(productEntity);
        when(categoryRepository.findById(dto.getCategoryId())).thenReturn(Optional.empty());

        // when & then
        assertThrows(CategoryNotFoundException.class, () -> productService.createProduct(dto));

        verify(productMapper).toEntity(dto);
        verify(categoryRepository).findById(dto.getCategoryId());
        verifyNoInteractions(productRepository);
    }

    @Test
    void givenExistingProductAndValidCategory_whenUpdateProduct_thenUpdateAndReturnDto() {
        // given: category exists and product exists in DB
        Long id = 7L;
        ProductDto dto = new ProductDto(1L,"name", "description",
                BigDecimal.ONE , (byte) 2);
        Category category = new Category(dto.getCategoryId());
        when(categoryRepository.findById(dto.getCategoryId())).thenReturn(Optional.of(category));

        Product productDb = new Product();
        productDb.setId(id);
        when(productRepository.findById(id)).thenReturn(Optional.of(productDb));

        // productMapper.updateProduct mutates productDb; we don't need to implement behavior, just verify it is called
        doNothing().when(productMapper).updateProduct(dto, productDb);

        // when
        ProductDto result = productService.updateProduct(id, dto);

        // then
        assertNotNull(result);
        assertEquals(id, result.getId());

        verify(categoryRepository).findById(dto.getCategoryId());
        verify(productRepository).findById(id);
        verify(productMapper).updateProduct(dto, productDb);
        verify(productRepository).save(productDb);
    }

    @Test
    void givenUpdateProductWithMissingCategory_whenUpdateProduct_thenThrowCategoryNotFoundException() {
        // given: category lookup fails
        Long id = 7L;
        ProductDto dto = mock(ProductDto.class);
        dto.setCategoryId((byte) 99);

        when(categoryRepository.findById(dto.getCategoryId())).thenReturn(Optional.empty());

        // when & then
        assertThrows(CategoryNotFoundException.class, () -> productService.updateProduct(id, dto));

        verify(categoryRepository).findById(dto.getCategoryId());
        verifyNoInteractions(productRepository, productMapper);
    }

    @Test
    void givenUpdateProductWithMissingProductInDb_whenUpdateProduct_thenThrowProductNotFoundException() {
        // given: category ok but product not found
        Long id = 8L;
        ProductDto dto = mock(ProductDto.class);
        dto.setCategoryId((byte) 1);

        when(categoryRepository.findById(dto.getCategoryId())).thenReturn(Optional.of(new Category()));
        when(productRepository.findById(id)).thenReturn(Optional.empty());

        // when & then
        assertThrows(ProductNotFoundException.class, () -> productService.updateProduct(id, dto));

        verify(categoryRepository).findById(dto.getCategoryId());
        verify(productRepository).findById(id);
        verifyNoInteractions(productMapper);
    }

    @Test
    void givenExistingProductId_whenDeleteProduct_thenDeleteCalled() {
        // given: product exists
        Long id = 11L;
        Product product = new Product();
        when(productRepository.findById(id)).thenReturn(Optional.of(product));

        // when
        productService.deleteProduct(id);

        // then
        verify(productRepository).findById(id);
        verify(productRepository).delete(product);
    }

    @Test
    void givenNonExistingProductId_whenDeleteProduct_thenThrowProductNotFoundException() {
        // given: product not found
        Long id = 12L;
        when(productRepository.findById(id)).thenReturn(Optional.empty());

        // when & then
        assertThrows(ProductNotFoundException.class, () -> productService.deleteProduct(id));

        verify(productRepository).findById(id);
        verify(productRepository, never()).delete(any());
    }
}