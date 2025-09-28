package com.conal.dishbuilder.dto.request.filter;

import com.conal.dishbuilder.dto.request.FilterBaseRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CategoryFilterRequest extends FilterBaseRequest {
    private String name;
    private String description;
    private String sortBy = "name";
    private String sortDirection = "ASC";
    private int page = 0;
    private int size = 10;
}
