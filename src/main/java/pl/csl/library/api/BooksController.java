package pl.csl.library.api;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.server.ResponseStatusException;
import pl.csl.library.dto.BookSearchResponse;
import pl.csl.library.service.GoogleBooksService;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5174")
@RequestMapping("/api/books")
public class BooksController {

    private final GoogleBooksService googleBooksService;

    @GetMapping("/search")
    public BookSearchResponse search(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        if (q == null || q.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "q is required");
        }
        if (size < 1) size = 1;
        if (size > 40) size = 40;
        if (page < 0) page = 0;

        return googleBooksService.search(q, page, size);
    }
}