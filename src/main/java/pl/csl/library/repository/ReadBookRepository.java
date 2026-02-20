package pl.csl.library.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.csl.library.model.ReadBook;

import java.util.List;
import java.util.Optional;

public interface ReadBookRepository extends JpaRepository<ReadBook,Long> {
    List<ReadBook> findAllByUserIdOrderByAddedAtDesc(Long userId);

    boolean existsByUserIdAndGoogleVolumeId(Long userId, String googleVolumeId);

    Optional<ReadBook> findByUserIdAndGoogleVolumeId(Long userId, String googleVolumeId);

    void deleteByUserIdAndGoogleVolumeId(Long userId, String googleVolumeId);
}
