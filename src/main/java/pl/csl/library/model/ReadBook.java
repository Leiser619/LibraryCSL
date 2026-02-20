package pl.csl.library.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(name="read_books",
        uniqueConstraints = {
        @UniqueConstraint(
                name="uq_read_books_user_volume",
                columnNames = {"user_id","google_volume_id"}
        )})
@Getter
@Setter
public class ReadBook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "google_volume_id",nullable = false)
    private String googleVolumeId;

    @Column(nullable = false)
    private String title;
    @Column
    private String authors;
    @Column(name="thumbnail_url")
    private String thumbnailUrl;

    @Column(name = "published_date")
    private String publishedDate;
    @Column(name = "added_at", insertable = false,updatable = false)
    private OffsetDateTime addedAt;



    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name="user_id",nullable = false)
    private User user;

}
