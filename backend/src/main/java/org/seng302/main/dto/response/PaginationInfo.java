package org.seng302.main.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Data transfer object response for pagination info
 */
@Getter
@Setter
@NoArgsConstructor
public class PaginationInfo<T> {

    private List<T> paginationElements;
    private int totalPages;
    private long totalElements;

    /**
     * Constructor for pagination info response object
     *
     * @param elements      List of elements
     * @param totalPages    Total pages
     * @param totalElements Total elements in page
     */
    public PaginationInfo(List<T> elements, int totalPages, long totalElements) {
        this.paginationElements = elements;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
    }
}
