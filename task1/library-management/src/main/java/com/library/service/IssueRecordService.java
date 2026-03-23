package com.library.service;

import com.library.entity.Book;
import com.library.entity.IssueRecord;
import com.library.entity.User;
import com.library.repository.IssueRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IssueRecordService {

    private static final int DEFAULT_LOAN_DAYS = 14;
    private static final int MAX_BOOKS_PER_USER = 3;

    private final IssueRecordRepository issueRecordRepository;
    private final BookService bookService;

    public List<IssueRecord> findAll() {
        return issueRecordRepository.findAll();
    }

    public Optional<IssueRecord> findById(Long id) {
        return issueRecordRepository.findById(id);
    }

    public List<IssueRecord> findByUser(Long userId) {
        return issueRecordRepository.findByUserId(userId);
    }

    public List<IssueRecord> findActiveByUser(Long userId) {
        return issueRecordRepository.findActiveIssuesByUser(userId);
    }

    public List<IssueRecord> findOverdue() {
        // Mark overdue records first
        markOverdueRecords();
        return issueRecordRepository.findByStatus(IssueRecord.Status.OVERDUE);
    }

    public List<IssueRecord> findAllActive() {
        return issueRecordRepository.findByStatus(IssueRecord.Status.ISSUED);
    }

    @Transactional
    public IssueRecord issueBook(User user, Book book) {
        long activeCount = issueRecordRepository.countActiveIssuesByUser(user.getId());
        if (activeCount >= MAX_BOOKS_PER_USER) {
            throw new IllegalStateException("User has reached the maximum limit of " + MAX_BOOKS_PER_USER + " issued books.");
        }

        bookService.decrementAvailable(book.getId());

        IssueRecord record = IssueRecord.builder()
                .book(book)
                .user(user)
                .issueDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(DEFAULT_LOAN_DAYS))
                .status(IssueRecord.Status.ISSUED)
                .build();
        return issueRecordRepository.save(record);
    }

    @Transactional
    public IssueRecord returnBook(Long issueRecordId) {
        IssueRecord record = issueRecordRepository.findById(issueRecordId)
                .orElseThrow(() -> new RuntimeException("Issue record not found: " + issueRecordId));

        if (record.getStatus() == IssueRecord.Status.RETURNED) {
            throw new IllegalStateException("Book already returned.");
        }

        record.setReturnDate(LocalDate.now());
        record.setStatus(IssueRecord.Status.RETURNED);
        bookService.incrementAvailable(record.getBook().getId());
        return issueRecordRepository.save(record);
    }

    @Transactional
    public void markOverdueRecords() {
        List<IssueRecord> overdue = issueRecordRepository.findOverdueRecords(LocalDate.now());
        overdue.forEach(r -> r.setStatus(IssueRecord.Status.OVERDUE));
        issueRecordRepository.saveAll(overdue);
    }

    public long countAll() {
        return issueRecordRepository.count();
    }

    public long countActive() {
        return issueRecordRepository.findByStatus(IssueRecord.Status.ISSUED).size()
             + issueRecordRepository.findByStatus(IssueRecord.Status.OVERDUE).size();
    }
}
