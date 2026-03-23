package com.library.controller;

import com.library.entity.Author;
import com.library.entity.Book;
import com.library.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final BookService bookService;
    private final AuthorService authorService;
    private final IssueRecordService issueRecordService;

    // ── Dashboard ──────────────────────────────────────────────
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalUsers",   userService.countAll());
        model.addAttribute("totalBooks",   bookService.countAll());
        model.addAttribute("totalAuthors", authorService.countAll());
        model.addAttribute("activeIssues", issueRecordService.countActive());
        model.addAttribute("overdueList",  issueRecordService.findOverdue());
        return "admin/dashboard";
    }

    // ── User Management ────────────────────────────────────────
    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("users", userService.findAll());
        return "admin/users";
    }

    @GetMapping("/users/new")
    public String newUserForm(Model model) {
        model.addAttribute("roles", new String[]{"ROLE_ADMIN", "ROLE_LIBRARIAN", "ROLE_MEMBER"});
        return "admin/user-form";
    }

    @PostMapping("/users/new")
    public String createUser(@RequestParam String fullName,
                             @RequestParam String email,
                             @RequestParam String password,
                             @RequestParam String roleName,
                             RedirectAttributes ra) {
        try {
            userService.createUser(fullName, email, password, roleName);
            ra.addFlashAttribute("success", "User created successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/toggle")
    public String toggleUser(@PathVariable Long id, RedirectAttributes ra) {
        userService.toggleUserStatus(id);
        ra.addFlashAttribute("success", "User status updated.");
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes ra) {
        userService.deleteUser(id);
        ra.addFlashAttribute("success", "User deleted.");
        return "redirect:/admin/users";
    }

    // ── Author Management ──────────────────────────────────────
    @GetMapping("/authors")
    public String listAuthors(Model model) {
        model.addAttribute("authors", authorService.findAll());
        return "admin/authors";
    }

    @GetMapping("/authors/new")
    public String newAuthorForm(Model model) {
        model.addAttribute("author", new Author());
        return "admin/author-form";
    }

    @PostMapping("/authors/new")
    public String createAuthor(@RequestParam String name,
                               @RequestParam(required = false) String bio,
                               @RequestParam(required = false) String nationality,
                               RedirectAttributes ra) {
        Author author = Author.builder().name(name).bio(bio).nationality(nationality).build();
        authorService.save(author);
        ra.addFlashAttribute("success", "Author added successfully.");
        return "redirect:/admin/authors";
    }

    @GetMapping("/authors/{id}/edit")
    public String editAuthorForm(@PathVariable Long id, Model model) {
        Author author = authorService.findById(id)
                .orElseThrow(() -> new RuntimeException("Author not found"));
        model.addAttribute("author", author);
        return "admin/author-form";
    }

    @PostMapping("/authors/{id}/edit")
    public String updateAuthor(@PathVariable Long id,
                               @RequestParam String name,
                               @RequestParam(required = false) String bio,
                               @RequestParam(required = false) String nationality,
                               RedirectAttributes ra) {
        Author author = authorService.findById(id)
                .orElseThrow(() -> new RuntimeException("Author not found"));
        author.setName(name);
        author.setBio(bio);
        author.setNationality(nationality);
        authorService.save(author);
        ra.addFlashAttribute("success", "Author updated.");
        return "redirect:/admin/authors";
    }

    @PostMapping("/authors/{id}/delete")
    public String deleteAuthor(@PathVariable Long id, RedirectAttributes ra) {
        authorService.delete(id);
        ra.addFlashAttribute("success", "Author deleted.");
        return "redirect:/admin/authors";
    }

    // ── Book Management ────────────────────────────────────────
    @GetMapping("/books")
    public String listBooks(Model model) {
        model.addAttribute("books", bookService.findAll());
        return "admin/books";
    }

    @GetMapping("/books/new")
    public String newBookForm(Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("authors", authorService.findAll());
        return "admin/book-form";
    }

    @PostMapping("/books/new")
    public String createBook(@RequestParam String title,
                             @RequestParam(required = false) String isbn,
                             @RequestParam(required = false) String genre,
                             @RequestParam(required = false) String publisher,
                             @RequestParam(required = false) Integer publishedYear,
                             @RequestParam Integer totalCopies,
                             @RequestParam Long authorId,
                             RedirectAttributes ra) {
        var author = authorService.findById(authorId).orElse(null);
        Book book = Book.builder()
                .title(title).isbn(isbn).genre(genre)
                .publisher(publisher).publishedYear(publishedYear)
                .totalCopies(totalCopies)
                .author(author)
                .build();
        bookService.save(book);
        ra.addFlashAttribute("success", "Book added successfully.");
        return "redirect:/admin/books";
    }

    @GetMapping("/books/{id}/edit")
    public String editBookForm(@PathVariable Long id, Model model) {
        Book book = bookService.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        model.addAttribute("book", book);
        model.addAttribute("authors", authorService.findAll());
        return "admin/book-form";
    }

    @PostMapping("/books/{id}/edit")
    public String updateBook(@PathVariable Long id,
                             @RequestParam String title,
                             @RequestParam(required = false) String isbn,
                             @RequestParam(required = false) String genre,
                             @RequestParam(required = false) String publisher,
                             @RequestParam(required = false) Integer publishedYear,
                             @RequestParam Integer totalCopies,
                             @RequestParam Long authorId,
                             RedirectAttributes ra) {
        Book book = bookService.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        var author = authorService.findById(authorId).orElse(null);
        int diff = totalCopies - book.getTotalCopies();
        book.setTitle(title);
        book.setIsbn(isbn);
        book.setGenre(genre);
        book.setPublisher(publisher);
        book.setPublishedYear(publishedYear);
        book.setTotalCopies(totalCopies);
        book.setAvailableCopies(Math.max(0, book.getAvailableCopies() + diff));
        book.setAuthor(author);
        bookService.update(book);
        ra.addFlashAttribute("success", "Book updated.");
        return "redirect:/admin/books";
    }

    @PostMapping("/books/{id}/delete")
    public String deleteBook(@PathVariable Long id, RedirectAttributes ra) {
        bookService.delete(id);
        ra.addFlashAttribute("success", "Book deleted.");
        return "redirect:/admin/books";
    }

    // ── Issue Records (read-only view for admin) ───────────────
    @GetMapping("/issues")
    public String listIssues(Model model) {
        model.addAttribute("records", issueRecordService.findAll());
        return "admin/issues";
    }
}
