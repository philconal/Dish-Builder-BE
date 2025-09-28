package com.conal.dishbuilder.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DishResponse {
    private UUID id;
    private String name;
    private String description;
    private UUID tenantId;
    private BigDecimal totalPrice;
    private BigDecimal discount;
    private BigDecimal vat;
    private UUID userId;
    private String userName;
    private List<IngredientInfo> ingredients;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IngredientInfo {
        private UUID id;
        private String name;
        private String description;
        private BigDecimal price;
        private UUID categoryId;
        private String categoryName;
    }
}
