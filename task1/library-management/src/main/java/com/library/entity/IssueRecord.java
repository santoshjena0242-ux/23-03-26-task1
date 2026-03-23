package com.library.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "issue_records")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IssueRecord {

    public enum Status {
        ISSUED, RETURNED, OVERDUE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDate issueDate;

    @Column(nullable = false)
    private LocalDate dueDate;

    private LocalDate returnDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private Status status = Status.ISSUED;

    @Column(length = 500)
    private String remarks;

    @Override
    public String toString() {
        return "IssueRecord{id=" + id + ", book=" + (book != null ? book.getTitle() : null)
                + ", user=" + (user != null ? user.getEmail() : null)
                + ", status=" + status + "}";
    }
}
