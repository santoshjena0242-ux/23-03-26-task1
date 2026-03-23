package com.library.controller;

import com.library.entity.User;
import com.library.service.BookService;
import com.library.service.IssueRecordService;
import com.library.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/member")
@PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN','MEMBER')")
@RequiredArgsConstructor
public class MemberController {

    private final BookService bookService;
    private final UserService userService;
    private final IssueRecordService issueRecordService;

    private User getCurrentUser(Authentication auth) {
        return userService.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        User user = getCurrentUser(auth);
        model.addAttribute("user", user);
        model.addAttribute("activeIssues", issueRecordService.findActiveByUser(user.getId()));
        model.addAttribute("totalBooks",   bookService.countAll());
        model.addAttribute("availableBooks", bookService.countAvailable());
        return "member/dashboard";
    }

    @GetMapping("/books")
    public String browseBooks(@RequestParam(required = false) String search,
                              @RequestParam(required = false) String genre,
                              Model model) {
        if (search != null && !search.isBlank()) {
            model.addAttribute("books", bookService.searchByTitle(search));
            model.addAttribute("search", search);
        } else {
            model.addAttribute("books", bookService.findAll());
        }
        return "member/books";
    }

    @GetMapping("/my-books")
    public String myBooks(Authentication auth, Model model) {
        User user = getCurrentUser(auth);
        model.addAttribute("records", issueRecordService.findByUser(user.getId()));
        return "member/my-books";
    }
}
