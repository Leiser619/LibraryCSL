package pl.csl.library.dto;

import java.time.OffsetDateTime;

public record ReadBookResponse(
        Long id,
        String googleVolumeId,
        String title,
        String authors,
        String thumbnailUrl,
        String publishedDate,
        OffsetDateTime addedAt
) {}