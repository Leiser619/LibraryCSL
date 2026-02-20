package pl.csl.library.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.csl.library.dto.ReadBookCreateRequest;
import pl.csl.library.dto.ReadBookResponse;
import pl.csl.library.service.ReadBookService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/me/books")
public class MyReadBooksController {

    private final ReadBookService readBookService;

    // TODO: JWT brać userid z tokena
    private Long currentUserId() {
        return 1L;
    }

    @GetMapping
    public List<ReadBookResponse> getMine() {
        return readBookService.getMyReadBooks(currentUserId());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReadBookResponse add(@Valid @RequestBody ReadBookCreateRequest req) {
        return readBookService.addToMyReadBooks(currentUserId(), req);
    }

    @DeleteMapping("/{googleVolumeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@PathVariable String googleVolumeId) {
        readBookService.removeFromMyReadBooks(currentUserId(), googleVolumeId);
    }
}