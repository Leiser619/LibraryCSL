package pl.csl.library.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ReadBookCreateRequest(
        @NotBlank
        String googleVolumeId,
        @NotBlank @Size(max = 512)
        String title,
        String authors,
        String thumbnailUrl,
        String publishedDate
) {}