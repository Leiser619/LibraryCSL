package pl.csl.library.dto;


import java.util.List;

public record BookSearchResponse(
        List<BookSearchItem> items,
        int page,
        int size,
        int totalItems
) {}