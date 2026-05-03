package com.cosmos.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * JD 1.6 — Wrapper for a page of users: stable JSON for list endpoints instead of a bare array.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagedUsersResponse {

    private List<UserResponse> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;

    public static PagedUsersResponse from(Page<UserResponse> page) {
        return PagedUsersResponse.builder()
                .content(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }
}
