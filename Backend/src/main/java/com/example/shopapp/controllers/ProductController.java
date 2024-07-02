package com.example.shopapp.controllers;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import jakarta.validation.Valid;

import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.shopapp.components.LocalizationUtils;
import com.example.shopapp.dtos.requests.product.ProductDTORequest;
import com.example.shopapp.dtos.requests.product.ProductImageDTORequest;
import com.example.shopapp.dtos.responses.ResponseObject;
import com.example.shopapp.dtos.responses.product.ProductDTOResponse;
import com.example.shopapp.dtos.responses.product.ProductListDTOResponse;
import com.example.shopapp.models.Product;
import com.example.shopapp.models.ProductImage;
import com.example.shopapp.services.product.IProductService;
import com.example.shopapp.services.product.redis.IProductRedisService;
import com.example.shopapp.utils.MessageKeys;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.javafaker.Faker;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("${api.prefix}/products")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ProductController {
    IProductService productService;
    IProductRedisService productRedisService;
    Logger logger = Logger.getLogger(ProductController.class.getName());
    private final LocalizationUtils localizationUtils;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> createProduct(@Valid @RequestBody ProductDTORequest request)
            throws Exception {
        return ResponseEntity.ok(ResponseObject.builder()
                .message("Create product successfully")
                .code(HttpStatus.CREATED.value())
                .data(productService.createProduct(request))
                .build());
    }

    @GetMapping("")
    public ResponseEntity<ProductListDTOResponse> getAllProducts(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0", name = "category_id") Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit)
            throws JsonProcessingException {

        int totalPages = 0;
        if (page > 0) {
            page -= 1;
        }
        /**
         * tạo pageable từ thông tin trang và giới hạn
         */
        PageRequest pageRequest = PageRequest.of(page, limit, Sort.by("id").ascending());

        List<ProductDTOResponse> productDTOResponses =
                productRedisService.getAllProducts(keyword, categoryId, pageRequest);
        if (productDTOResponses == null) {
            Page<ProductDTOResponse> productPages = productService.getAllProducts(keyword, categoryId, pageRequest);
            // get total page
            totalPages = productPages.getTotalPages();
            productDTOResponses = productPages.getContent();
            // Bổ sung totalPages vào các đối tượng ProductResponse
            for (ProductDTOResponse product : productDTOResponses) {
                product.setTotalPages(totalPages);
            }
            productRedisService.saveAllProducts(productDTOResponses, keyword, categoryId, pageRequest);
        }
        return ResponseEntity.ok(ProductListDTOResponse.builder()
                .products(productDTOResponses)
                .totalPages(totalPages)
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject> getProductById(@PathVariable("id") Long productId) throws Exception {
        Product existingProduct = productService.getProductById(productId);
        return ResponseEntity.ok(ResponseObject.builder()
                .data(existingProduct)
                .message("Get detail product successfully")
                .code(HttpStatus.BAD_REQUEST.value())
                .build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> deleteProduct(@PathVariable long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ResponseObject.builder()
                .message(String.format("Product with id = %d deleted successfully", id))
                .code(HttpStatus.NO_CONTENT.value())
                .build());
    }

    // update a product
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> updateProduct(@PathVariable long id, @RequestBody ProductDTORequest request)
            throws Exception {
        Product updatedProduct = productService.updateProduct(id, request);
        return ResponseEntity.ok(ResponseObject.builder()
                .data(updatedProduct)
                .message("Update product successfully")
                .code(HttpStatus.NO_CONTENT.value())
                .build());
    }

    @PostMapping(value = "uploads/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> uploadImages(
            @PathVariable("id") Long id, @ModelAttribute("files") List<MultipartFile> files) throws Exception {
        Product existingProduct = productService.getProductById(id);
        files = files == null ? new ArrayList<MultipartFile>() : files;
        // kiem tra co bao nhiu file upload len
        if (files.size() > ProductImage.MAXIMUM_IMAGES_PER_PRODUCT) {
            return ResponseEntity.badRequest()
                    .body(ResponseObject.builder()
                            .message("Cannot upload more than 5 images")
                            .code(HttpStatus.BAD_REQUEST.value())
                            .build());
        }

        List<ProductImage> productImages = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file.getSize() == 0) {
                continue;
            }
            // kiem tra kich thuoc file upload len
            if (file.getSize() > 10 * 1024 * 1024) {
                return ResponseEntity.badRequest()
                        .body(ResponseObject.builder()
                                .message(localizationUtils.getLocalizationMessage(MessageKeys.UPLOAD_IMAGES_FILE_LARGE))
                                .code(HttpStatus.BAD_REQUEST.value())
                                .build());
            }

            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest()
                        .body(ResponseObject.builder()
                                .message(localizationUtils.getLocalizationMessage(
                                        MessageKeys.UPLOAD_IMAGES_FILE_MUST_BE_IMAGE))
                                .code(HttpStatus.BAD_REQUEST.value())
                                .build());
            }

            // lưu file va cap nhat thumbnail dto
            String filename = productService.storeFile(file);

            // luu vào db
            ProductImage productImage = productService.createProductImage(
                    id, ProductImageDTORequest.builder().imageUrl(filename).build());
            productImages.add(productImage);
        }
        return ResponseEntity.ok()
                .body(ResponseObject.builder()
                        .message("Upload image successfully")
                        .code(HttpStatus.CREATED.value())
                        .data(productImages)
                        .build());
    }

    @PostMapping("fakes")
    public ResponseEntity<ResponseObject> createFakeProducts() throws Exception {
        Faker faker = new Faker();
        for (int i = 0; i < 1_000_000; i++) {
            String productName = faker.commerce().productName();
            if (productService.existsByName(productName)) {
                continue;
            }
            ProductDTORequest request = ProductDTORequest.builder()
                    .name(productName)
                    .price((double) faker.number().numberBetween(10, 90_000_000))
                    .description(faker.lorem().sentence())
                    .thumbnail("")
                    .categoryId((long) faker.number().numberBetween(2, 5))
                    .build();
            productService.createProduct(request);
        }
        return ResponseEntity.ok(ResponseObject.builder()
                .message("Insert fake products succcessfully")
                .data(null)
                .code(HttpStatus.CREATED.value())
                .build());
    }

    @GetMapping("images/{filename}")
    public ResponseEntity<?> viewImage(@PathVariable String filename) throws Exception {
        Path imagePath = Paths.get("uploads/" + filename);
        UrlResource resource = new UrlResource(imagePath.toUri());
        if (resource.exists()) {
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(resource);
        } else {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(new UrlResource(Paths.get("uploads/notfound.jpeg").toUri()));
        }
    }

    @GetMapping("by-ids")
    public ResponseEntity<ResponseObject> getProductsByIds(@RequestParam("ids") String ids) {
        // chuyen chuoi thanh mang cac so nguyen
        List<Long> productIds =
                Arrays.stream(ids.split(",")).map(Long::parseLong).toList();
        List<Product> products = productService.findProductsByIds(productIds);
        return ResponseEntity.ok(ResponseObject.builder()
                .message("Get products by ids successfully")
                .code(HttpStatus.OK.value())
                .data(products)
                .build());
    }
}
