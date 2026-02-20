package pl.csl.library.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.csl.library.dto.ReadBookCreateRequest;
import pl.csl.library.dto.ReadBookResponse;
import pl.csl.library.error.DuplicateResourceException;
import pl.csl.library.model.ReadBook;
import pl.csl.library.model.User;
import pl.csl.library.repository.ReadBookRepository;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReadBookService {

    private final ReadBookRepository readBookRepository;

    @Transactional(readOnly = true)
    public List<ReadBookResponse> getMyReadBooks(Long userId) {
        return readBookRepository.findAllByUserIdOrderByAddedAtDesc(userId)
                .stream()
                .map(ReadBookService::toResponse)
                .toList();
    }

    @Transactional
    public ReadBookResponse addToMyReadBooks(Long userId, ReadBookCreateRequest req) {
        if (readBookRepository.existsByUserIdAndGoogleVolumeId(userId, req.googleVolumeId())) {
            throw new DuplicateResourceException("Książka na liście przeczytanych.");
        }
        //nie laduje calego usera zeby szybciej bylo
        User userRef = new User();
        userRef.setId(userId);

        ReadBook book = new ReadBook();
        book.setUser(userRef);
        book.setGoogleVolumeId(req.googleVolumeId());
        book.setTitle(req.title());
        book.setAuthors(req.authors());
        book.setThumbnailUrl(req.thumbnailUrl());
        book.setPublishedDate(req.publishedDate());

        ReadBook saved = readBookRepository.save(book);
        return toResponse(saved);
    }

    @Transactional
    public void removeFromMyReadBooks(Long userId, String googleVolumeId) {
        ReadBook book = readBookRepository.findByUserIdAndGoogleVolumeId(userId, googleVolumeId)
                .orElseThrow(() -> new EntityNotFoundException("Nie ma książki na liście."));
        readBookRepository.delete(book);
    }

    private static ReadBookResponse toResponse(ReadBook b) {
        return new ReadBookResponse(
                b.getId(),
                b.getGoogleVolumeId(),
                b.getTitle(),
                b.getAuthors(),
                b.getThumbnailUrl(),
                b.getPublishedDate(),
                b.getAddedAt()
        );
    }
}