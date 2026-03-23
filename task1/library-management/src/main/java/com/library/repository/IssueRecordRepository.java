package com.library.repository;

import com.library.entity.IssueRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface IssueRecordRepository extends JpaRepository<IssueRecord, Long> {
    List<IssueRecord> findByUserId(Long userId);
    List<IssueRecord> findByBookId(Long bookId);
    List<IssueRecord> findByStatus(IssueRecord.Status status);

    @Query("SELECT ir FROM IssueRecord ir WHERE ir.user.id = :userId AND ir.status = 'ISSUED'")
    List<IssueRecord> findActiveIssuesByUser(Long userId);

    @Query("SELECT ir FROM IssueRecord ir WHERE ir.dueDate < :today AND ir.status = 'ISSUED'")
    List<IssueRecord> findOverdueRecords(LocalDate today);

    @Query("SELECT COUNT(ir) FROM IssueRecord ir WHERE ir.user.id = :userId AND ir.status = 'ISSUED'")
    long countActiveIssuesByUser(Long userId);
}
