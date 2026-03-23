package com.library.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "books")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 200)
    private String title;

    @Column(unique = true, length = 20)
    private String isbn;

    @Column(length = 100)
    private String genre;

    @Column(length = 100)
    private String publisher;

    private Integer publishedYear;

    @Min(0)
    @NotNull
    @Column(nullable = false)
    @Builder.Default
    private Integer totalCopies = 1;

    @Min(0)
    @Column(nullable = false)
    @Builder.Default
    private Integer availableCopies = 1;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "author_id")
    private Author author;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<IssueRecord> issueRecords;

    @Override
    public String toString() {
        return "Book{id=" + id + ", title='" + title + "', isbn='" + isbn + "'}";
    }
}
