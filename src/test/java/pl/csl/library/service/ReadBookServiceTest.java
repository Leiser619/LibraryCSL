package pl.csl.library.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.csl.library.dto.ReadBookCreateRequest;
import pl.csl.library.dto.ReadBookResponse;
import pl.csl.library.error.DuplicateResourceException;
import pl.csl.library.model.ReadBook;
import pl.csl.library.model.User;
import pl.csl.library.repository.ReadBookRepository;

import jakarta.persistence.EntityNotFoundException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReadBookServiceTest {

    @Mock
    private ReadBookRepository readBookRepository;

    @InjectMocks
    private ReadBookService readBookService;

    private Long userId;

    @BeforeEach
    void setUp() {
        userId = 123L;
    }

    @Test
    void getMyReadBooks_shouldReturnMappedResponsesInOrder() {
        ReadBook b1 = new ReadBook();
        b1.setId(1L);
        b1.setGoogleVolumeId("gid-1");
        b1.setTitle("T1");
        b1.setAuthors("A1");
        b1.setThumbnailUrl("http://t1");
        b1.setPublishedDate("2020");
        b1.setAddedAt(OffsetDateTime.parse("2026-01-02T10:00:00+00:00"));

        ReadBook b2 = new ReadBook();
        b2.setId(2L);
        b2.setGoogleVolumeId("gid-2");
        b2.setTitle("T2");
        b2.setAuthors("A2, A3");
        b2.setThumbnailUrl("http://t2");
        b2.setPublishedDate("2019");
        b2.setAddedAt(OffsetDateTime.parse("2026-01-01T10:00:00+00:00"));

        when(readBookRepository.findAllByUserIdOrderByAddedAtDesc(userId))
                .thenReturn(List.of(b1, b2));

        List<ReadBookResponse> out = readBookService.getMyReadBooks(userId);

        assertThat(out).hasSize(2);

        assertThat(out.get(0).googleVolumeId()).isEqualTo("gid-1");
        assertThat(out.get(0).title()).isEqualTo("T1");
        assertThat(out.get(0).authors()).isEqualTo("A1");
        assertThat(out.get(0).addedAt()).isEqualTo(OffsetDateTime.parse("2026-01-02T10:00:00+00:00"));

        assertThat(out.get(1).googleVolumeId()).isEqualTo("gid-2");

        verify(readBookRepository).findAllByUserIdOrderByAddedAtDesc(userId);
        verifyNoMoreInteractions(readBookRepository);
    }

    @Test
    void addToMyReadBooks_shouldThrowDuplicateResourceException_whenAlreadyExists() {
        ReadBookCreateRequest req = new ReadBookCreateRequest(
                "gid-1", "Title", "Auth", "http://img", "2020"
        );

        when(readBookRepository.existsByUserIdAndGoogleVolumeId(userId, "gid-1"))
                .thenReturn(true);

        assertThatThrownBy(() -> readBookService.addToMyReadBooks(userId, req))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Książka na liście przeczytanych");

        verify(readBookRepository).existsByUserIdAndGoogleVolumeId(userId, "gid-1");
        verifyNoMoreInteractions(readBookRepository);
    }

    @Test
    void addToMyReadBooks_shouldSaveBookWithUserRef_andReturnResponse() {
        ReadBookCreateRequest req = new ReadBookCreateRequest(
                "gid-99", "Clean Code", "Robert C. Martin", "http://img", "2008"
        );

        when(readBookRepository.existsByUserIdAndGoogleVolumeId(userId, "gid-99"))
                .thenReturn(false);

        ArgumentCaptor<ReadBook> captor = ArgumentCaptor.forClass(ReadBook.class);

        ReadBook saved = new ReadBook();
        saved.setId(55L);
        User u = new User();
        u.setId(userId);
        saved.setUser(u);

        saved.setGoogleVolumeId("gid-99");
        saved.setTitle("Clean Code");
        saved.setAuthors("Robert C. Martin");
        saved.setThumbnailUrl("http://img");
        saved.setPublishedDate("2008");
        saved.setAddedAt(OffsetDateTime.parse("2026-01-01T00:00:00+00:00"));

        when(readBookRepository.save(any(ReadBook.class))).thenReturn(saved);

        ReadBookResponse out = readBookService.addToMyReadBooks(userId, req);
        verify(readBookRepository).existsByUserIdAndGoogleVolumeId(userId, "gid-99");
        verify(readBookRepository).save(captor.capture());

        ReadBook toSave = captor.getValue();
        assertThat(toSave.getUser()).isNotNull();
        assertThat(toSave.getUser().getId()).isEqualTo(userId); // referencja usera
        assertThat(toSave.getGoogleVolumeId()).isEqualTo("gid-99");
        assertThat(toSave.getTitle()).isEqualTo("Clean Code");
        assertThat(toSave.getAuthors()).isEqualTo("Robert C. Martin");
        assertThat(toSave.getThumbnailUrl()).isEqualTo("http://img");
        assertThat(toSave.getPublishedDate()).isEqualTo("2008");

        assertThat(out.id()).isEqualTo(55L);
        assertThat(out.googleVolumeId()).isEqualTo("gid-99");
        assertThat(out.authors()).isEqualTo("Robert C. Martin");
        assertThat(out.addedAt()).isEqualTo(OffsetDateTime.parse("2026-01-01T00:00:00+00:00"));

        verifyNoMoreInteractions(readBookRepository);
    }

    @Test
    void removeFromMyReadBooks_shouldDelete_whenFound() {
        String gid = "gid-1";
        ReadBook existing = new ReadBook();
        existing.setId(1L);
        existing.setGoogleVolumeId(gid);

        when(readBookRepository.findByUserIdAndGoogleVolumeId(userId, gid))
                .thenReturn(Optional.of(existing));

        readBookService.removeFromMyReadBooks(userId, gid);

        verify(readBookRepository).findByUserIdAndGoogleVolumeId(userId, gid);
        verify(readBookRepository).delete(existing);
        verifyNoMoreInteractions(readBookRepository);
    }

    @Test
    void removeFromMyReadBooks_shouldThrowEntityNotFoundException_whenNotFound() {
        String gid = "missing";
        when(readBookRepository.findByUserIdAndGoogleVolumeId(userId, gid))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> readBookService.removeFromMyReadBooks(userId, gid))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Nie ma książki na liście");

        verify(readBookRepository).findByUserIdAndGoogleVolumeId(userId, gid);
        verifyNoMoreInteractions(readBookRepository);
    }
}