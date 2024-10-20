package io.hhplus.ecommerce.application.facade;

import io.hhplus.ecommerce.domain.service.product.FindProductService;
import io.hhplus.ecommerce.application.dto.product.ProductDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class QueryFacade {

    private final FindProductService findProductService;

    /**
     * 상품 조회 기능
     */
    public ProductDto getProduct(Long productId) {
        return findProductService.getProduct(productId);
    }


    /**
     * 가장 많이 팔린 상품 5개 조회
     */
    public List<ProductDto> getTopFiveProducts() {
        return findProductService.getTopFiveProducts();
    }
}
