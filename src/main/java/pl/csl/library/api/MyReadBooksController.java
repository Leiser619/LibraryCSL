package pl.csl.library.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.csl.library.dto.ReadBookCreateRequest;
import pl.csl.library.dto.ReadBookResponse;
import pl.csl.library.security.UserPrincipal;
import pl.csl.library.service.ReadBookService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5174")
@RequestMapping("/api/me/books")
public class MyReadBooksController {

    private final ReadBookService readBookService;

    @GetMapping
    public List<ReadBookResponse> getMine(@AuthenticationPrincipal UserPrincipal principal) {
        return readBookService.getMyReadBooks(principal.getId());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReadBookResponse add(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody ReadBookCreateRequest req
    ) {
        return readBookService.addToMyReadBooks(principal.getId(), req);
    }

    @DeleteMapping("/{googleVolumeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String googleVolumeId
    ) {
        readBookService.removeFromMyReadBooks(principal.getId(), googleVolumeId);
    }
}