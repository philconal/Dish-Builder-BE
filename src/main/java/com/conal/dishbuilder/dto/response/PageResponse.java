package com.conal.dishbuilder.dto.response;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Builder
@Data
public class PageResponse<T> {
    private List<T> data;
    private int size;
    private int page;
    private long totalElements;
    private int totalPages;
    private boolean isIgnorePaging;

    public static <T> PageResponse<T> fromPage(Page<T> pageable) {
        return PageResponse.<T>builder()
                .size(pageable.getSize())
                .page(pageable.getPageable().isPaged() ? pageable.getPageable().getPageNumber() : 0)
                .totalElements(pageable.getTotalElements())
                .totalPages(pageable.getPageable().isPaged() ? pageable.getTotalPages() : 1)
                .data(pageable.getContent())
                .isIgnorePaging(pageable.getPageable().isUnpaged())
                .build();
    }
}
