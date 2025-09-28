package com.conal.dishbuilder.dto.request.filter;

import com.conal.dishbuilder.dto.request.FilterBaseRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public class IngredientsFilterRequest extends FilterBaseRequest {
    private String name;
    private String description;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private UUID categoryId;
    private String categoryName;
    private String sortBy = "name";
    private String sortDirection = "ASC";
    private int page = 0;
    private int size = 10;
}
