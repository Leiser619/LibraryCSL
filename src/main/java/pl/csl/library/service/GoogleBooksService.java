package pl.csl.library.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import pl.csl.library.dto.BookSearchItem;
import pl.csl.library.dto.BookSearchResponse;


import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GoogleBooksService {

    private final WebClient googleBooksWebClient;

    public BookSearchResponse search(String q, int page, int size) {
        int startIndex = page * size;

        Map<String, Object> raw = googleBooksWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/volumes")
                        .queryParam("q", q)
                        .queryParam("startIndex", startIndex)
                        .queryParam("maxResults", size)
//                        .queryParam("key", googleBooksApiKey)
                        .build()
                )
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (raw == null) {
            return new BookSearchResponse(List.of(), page, size, 0);
        }

        int totalItems = (int) raw.getOrDefault("totalItems", 0);
        Object itemsObj = raw.get("items");

        if (!(itemsObj instanceof List<?> itemsList)) {
            return new BookSearchResponse(List.of(), page, size, totalItems);
        }

        List<BookSearchItem> items = itemsList.stream()
                .filter(m -> m instanceof Map)
                .map(m -> (Map<String, Object>) m)
                .map(this::mapItem)
                .toList();

        return new BookSearchResponse(items, page, size, totalItems);
    }

    private BookSearchItem mapItem(Map<String, Object> item) {
        String id = (String) item.get("id");

        Map<String, Object> volumeInfo = (Map<String, Object>) item.getOrDefault("volumeInfo", Map.of());
        String title = (String) volumeInfo.getOrDefault("title", "");
        String publishedDate = (String) volumeInfo.getOrDefault("publishedDate", null);
        Object authorsObj = volumeInfo.get("authors");
        String authors = null;
        if (authorsObj instanceof List<?> a) {
            authors = String.join(", ", a.stream().map(String::valueOf).toList());
        }

        Map<String, Object> imageLinks = (Map<String, Object>) volumeInfo.getOrDefault("imageLinks", Map.of());
        String thumbnail = (String) imageLinks.getOrDefault("thumbnail", null);

        return new BookSearchItem(id, title, authors, thumbnail, publishedDate);
    }
}