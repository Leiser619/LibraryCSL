package pl.csl.library.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;

    @Column(nullable = false,unique = true)
    private String email;

    @Column(name="password_hash" , nullable = false)
    private String passwordHash;

    @Column(name="created_at",insertable = false,updatable = false)
    private OffsetDateTime createdAt;
    @OneToMany(mappedBy = "user" ,cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReadBook> readBooks=new ArrayList<>();

}
