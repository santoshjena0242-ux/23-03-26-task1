package com.library.controller;

import com.library.entity.Book;
import com.library.entity.IssueRecord;
import com.library.entity.User;
import com.library.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/librarian")
@PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
@RequiredArgsConstructor
public class LibrarianController {

    private final BookService bookService;
    private final UserService userService;
    private final IssueRecordService issueRecordService;
    private final AuthorService authorService;

    // ── Dashboard ──────────────────────────────────────────────
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalBooks",    bookService.countAll());
        model.addAttribute("availableBooks", bookService.countAvailable());
        model.addAttribute("activeIssues",  issueRecordService.countActive());
        model.addAttribute("overdueList",   issueRecordService.findOverdue());
        model.addAttribute("recentIssues",  issueRecordService.findAllActive());
        return "librarian/dashboard";
    }

    // ── Book Catalog ───────────────────────────────────────────
    @GetMapping("/books")
    public String listBooks(@RequestParam(required = false) String search, Model model) {
        if (search != null && !search.isBlank()) {
            model.addAttribute("books", bookService.searchByTitle(search));
            model.addAttribute("search", search);
        } else {
            model.addAttribute("books", bookService.findAll());
        }
        return "librarian/books";
    }

    // ── Issue a Book ───────────────────────────────────────────
    @GetMapping("/issue")
    public String issueBookForm(Model model) {
        model.addAttribute("books",   bookService.findAvailable());
        model.addAttribute("members", userService.findMembers());
        return "librarian/issue-form";
    }

    @PostMapping("/issue")
    public String issueBook(@RequestParam Long bookId,
                            @RequestParam Long userId,
                            RedirectAttributes ra) {
        try {
            Book book = bookService.findById(bookId)
                    .orElseThrow(() -> new RuntimeException("Book not found"));
            User user = userService.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            IssueRecord record = issueRecordService.issueBook(user, book);
            ra.addFlashAttribute("success",
                    "Book \"" + book.getTitle() + "\" issued to " + user.getFullName()
                    + ". Due: " + record.getDueDate());
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/librarian/issue";
    }

    // ── Return a Book ──────────────────────────────────────────
    @GetMapping("/return")
    public String returnBookForm(Model model) {
        model.addAttribute("activeRecords", issueRecordService.findAllActive());
        return "librarian/return-form";
    }

    @PostMapping("/return/{recordId}")
    public String returnBook(@PathVariable Long recordId, RedirectAttributes ra) {
        try {
            IssueRecord record = issueRecordService.returnBook(recordId);
            ra.addFlashAttribute("success",
                    "Book \"" + record.getBook().getTitle() + "\" returned successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/librarian/return";
    }

    // ── All Issue Records ──────────────────────────────────────
    @GetMapping("/issues")
    public String listIssues(Model model) {
        model.addAttribute("records", issueRecordService.findAll());
        return "librarian/issues";
    }

    // ── Overdue Books ──────────────────────────────────────────
    @GetMapping("/overdue")
    public String overdueBooks(Model model) {
        model.addAttribute("overdueList", issueRecordService.findOverdue());
        return "librarian/overdue";
    }

    // ── Member List ────────────────────────────────────────────
    @GetMapping("/members")
    public String listMembers(Model model) {
        model.addAttribute("members", userService.findMembers());
        return "librarian/members";
    }

    @GetMapping("/members/{id}/history")
    public String memberHistory(@PathVariable Long id, Model model) {
        User user = userService.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("user", user);
        model.addAttribute("records", issueRecordService.findByUser(id));
        return "librarian/member-history";
    }
}
