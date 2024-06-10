package com.example.shopapp.services.product;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.example.shopapp.dtos.requests.ProductDtoRequest;
import com.example.shopapp.dtos.requests.ProductImageDtoRequest;
import com.example.shopapp.dtos.responses.product.ProductDtoResponse;
import com.example.shopapp.exceptions.DataNotFoundException;
import com.example.shopapp.exceptions.InvalidParamException;
import com.example.shopapp.mappers.ProductMapper;
import com.example.shopapp.models.Category;
import com.example.shopapp.models.Product;
import com.example.shopapp.models.ProductImage;
import com.example.shopapp.repositories.CategoryRepository;
import com.example.shopapp.repositories.ProductImageRepository;
import com.example.shopapp.repositories.ProductRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductService implements IProductService {
    ProductRepository productRepository;
    CategoryRepository categoryRepository;
    ProductImageRepository productImageRepository;
    ProductMapper productMapper;

    @Override
    @Transactional
    public Product createProduct(ProductDtoRequest request) throws Exception {
        Category existingCategory = categoryRepository
                .findById(request.getCategoryId())
                .orElseThrow(
                        () -> new DataNotFoundException("Cannot find category with id: " + request.getCategoryId()));
        Product newProduct = productMapper.toProduct(request);
        newProduct.setCategory(existingCategory);
        return productRepository.save(newProduct);
    }

    @Override
    public Product getProductById(long productId) throws Exception {
        Optional<Product> optionalProduct = productRepository.getDetailProduct(productId);
        if (optionalProduct.isPresent()) {
            return optionalProduct.get();
        }
        throw new DataNotFoundException("Cannot find product with id =" + productId);
    }

    @Override
    public Page<ProductDtoResponse> getAllProducts(String keyword, Long categoryId, PageRequest pageRequest) {
        // Lấy danh sách sản phẩm theo trang (page), giới hạn (limit), và categoryId (nếu có)
        Page<Product> productsPage;
        productsPage = productRepository.searchProducts(categoryId, keyword, pageRequest);
        return productsPage.map(ProductDtoResponse::fromProduct);
    }

    @Override
    @Transactional
    public Product updateProduct(long id, ProductDtoRequest request) throws Exception {
        Product existingProduct = productRepository
                .findById(id)
                .orElseThrow(() -> new DataNotFoundException("Cannot find product with id = " + id));
        if (existingProduct != null) {
            Category existingCategory = categoryRepository
                    .findById(request.getCategoryId())
                    .orElseThrow(() ->
                            new DataNotFoundException("Cannot find category with id = " + request.getCategoryId()));
            //            productMapper.updateUser(existProduct, request);
            if (request.getName() != null && !request.getName().isEmpty()) {
                existingProduct.setName(request.getName());
            }

            existingProduct.setCategory(existingCategory);
            if (request.getPrice() >= 0) {
                existingProduct.setPrice(request.getPrice());
            }
            if (request.getDescription() != null && !request.getDescription().isEmpty()) {
                existingProduct.setDescription(request.getDescription());
            }
            if (request.getThumbnail() != null && !request.getThumbnail().isEmpty()) {
                existingProduct.setDescription(request.getThumbnail());
            }
            return productRepository.save(existingProduct);
        }
        return null;
    }

    @Override
    public void deleteProduct(long id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        optionalProduct.ifPresent(productRepository::delete);
    }

    @Override
    public boolean existsByName(String name) {
        return productRepository.existsByName(name);
    }

    @Override
    public ProductImage createProductImage(Long productId, ProductImageDtoRequest productImageDtoRequest)
            throws Exception {
        Product existingProduct = productRepository
                .findById(productId)
                .orElseThrow(() -> new DataNotFoundException(
                        "Cannot find product with id: " + productImageDtoRequest.getProductId()));
        ProductImage newProductImage = ProductImage.builder()
                .product(existingProduct)
                .imageUrl(productImageDtoRequest.getImageUrl())
                .build();

        // Ko cho insert quá 5 ảnh cho 1 sản phẩm
        int size = productImageRepository.findByProductId(productId).size();
        if (size >= ProductImage.MAXIMUM_IMAGES_PER_PRODUCT) {
            throw new InvalidParamException("Number of images must be <= " + ProductImage.MAXIMUM_IMAGES_PER_PRODUCT);
        }
        if (existingProduct.getThumbnail() == null) {
            existingProduct.setThumbnail(newProductImage.getImageUrl());
        }
        productRepository.save(existingProduct);
        return productImageRepository.save(newProductImage);
    }

    @Override
    public List<Product> findProductsByIds(List<Long> productIds) {
        return productRepository.findProductsByIds(productIds);
    }

    /**
     * @Destination: Lưu file ảnh, trả về tên file
     * @param file
     * @return
     * @throws IOException
     */
    @Override
    public String storeFile(MultipartFile file) throws IOException {
        if (!isImageFile(file) || file.getOriginalFilename() == null) {
            throw new IOException("Only image files are allowed");
        }

        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        String uniqueFilename = System.currentTimeMillis() + "_" + fileName;

        Path uploadPath = Paths.get("uploads");
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path destination = Paths.get(uploadPath.toString(), uniqueFilename);
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        return uniqueFilename;
    }

    /**
     * @Destination: Xóa file ảnh
     * @param filename
     * @throws IOException
     */
    @Override
    public void deleteFile(String filename) throws IOException {
        Path path = Paths.get("uploads", filename);
        if (Files.exists(path)) {
            Files.delete(path);
        } else {
            throw new IOException("File not found");
        }
    }

    /**
     * @Destination: kiểm tra file có phải là file ảnh không
     * @param file
     * @return
     */
    private boolean isImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }
}
