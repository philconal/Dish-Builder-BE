package com.conal.dishbuilder.dto.request.filter;

import com.conal.dishbuilder.dto.request.FilterBaseRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public class DishFilterRequest extends FilterBaseRequest {
    private String name;
    private String description;
    private BigDecimal minTotalPrice;
    private BigDecimal maxTotalPrice;
    private BigDecimal minDiscount;
    private BigDecimal maxDiscount;
    private UUID userId;
    private String userName;
    private UUID ingredientId;
    private String ingredientName;
    private String sortBy = "name";
    private String sortDirection = "ASC";
    private int page = 0;
    private int size = 10;
}
