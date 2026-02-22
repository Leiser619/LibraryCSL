package pl.csl.library.dto;
public record BookSearchItem(
        String googleVolumeId,
        String title,
        String authors,
        String thumbnailUrl,
        String publishedDate
) {}