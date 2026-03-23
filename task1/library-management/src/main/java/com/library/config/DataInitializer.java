package com.library.config;

import com.library.entity.Author;
import com.library.entity.Book;
import com.library.entity.Role;
import com.library.entity.User;
import com.library.repository.AuthorRepository;
import com.library.repository.BookRepository;
import com.library.repository.RoleRepository;
import com.library.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Seeding initial data...");

        // --- Roles ---
        Role adminRole    = getOrCreateRole("ROLE_ADMIN");
        Role librarianRole = getOrCreateRole("ROLE_LIBRARIAN");
        Role memberRole   = getOrCreateRole("ROLE_MEMBER");

        // --- Users ---
        if (!userRepository.existsByEmail("admin@library.com")) {
            User admin = User.builder()
                    .fullName("System Administrator")
                    .email("admin@library.com")
                    .password(passwordEncoder.encode("admin123"))
                    .enabled(true)
                    .roles(Set.of(adminRole))
                    .build();
            userRepository.save(admin);
            log.info("Created admin user: admin@library.com / admin123");
        }

        if (!userRepository.existsByEmail("librarian@library.com")) {
            User librarian = User.builder()
                    .fullName("Jane Librarian")
                    .email("librarian@library.com")
                    .password(passwordEncoder.encode("lib123"))
                    .enabled(true)
                    .roles(Set.of(librarianRole))
                    .build();
            userRepository.save(librarian);
            log.info("Created librarian user: librarian@library.com / lib123");
        }

        if (!userRepository.existsByEmail("member@library.com")) {
            User member = User.builder()
                    .fullName("John Member")
                    .email("member@library.com")
                    .password(passwordEncoder.encode("member123"))
                    .enabled(true)
                    .roles(Set.of(memberRole))
                    .build();
            userRepository.save(member);
            log.info("Created member user: member@library.com / member123");
        }

        // --- Authors ---
        if (authorRepository.count() == 0) {
            Author author1 = authorRepository.save(Author.builder()
                    .name("George Orwell")
                    .bio("English novelist and essayist, journalist and critic.")
                    .nationality("British")
                    .build());

            Author author2 = authorRepository.save(Author.builder()
                    .name("J.K. Rowling")
                    .bio("British author best known for the Harry Potter series.")
                    .nationality("British")
                    .build());

            Author author3 = authorRepository.save(Author.builder()
                    .name("Yuval Noah Harari")
                    .bio("Israeli public intellectual, historian and author.")
                    .nationality("Israeli")
                    .build());

            Author author4 = authorRepository.save(Author.builder()
                    .name("F. Scott Fitzgerald")
                    .bio("American novelist and short story writer.")
                    .nationality("American")
                    .build());

            // --- Books ---
            bookRepository.save(Book.builder()
                    .title("1984")
                    .isbn("978-0451524935")
                    .genre("Dystopian Fiction")
                    .publisher("Secker & Warburg")
                    .publishedYear(1949)
                    .totalCopies(5)
                    .availableCopies(5)
                    .author(author1)
                    .build());

            bookRepository.save(Book.builder()
                    .title("Animal Farm")
                    .isbn("978-0451526342")
                    .genre("Political Satire")
                    .publisher("Secker & Warburg")
                    .publishedYear(1945)
                    .totalCopies(4)
                    .availableCopies(4)
                    .author(author1)
                    .build());

            bookRepository.save(Book.builder()
                    .title("Harry Potter and the Philosopher's Stone")
                    .isbn("978-0439708180")
                    .genre("Fantasy")
                    .publisher("Bloomsbury")
                    .publishedYear(1997)
                    .totalCopies(6)
                    .availableCopies(6)
                    .author(author2)
                    .build());

            bookRepository.save(Book.builder()
                    .title("Harry Potter and the Chamber of Secrets")
                    .isbn("978-0439064873")
                    .genre("Fantasy")
                    .publisher("Bloomsbury")
                    .publishedYear(1998)
                    .totalCopies(5)
                    .availableCopies(5)
                    .author(author2)
                    .build());

            bookRepository.save(Book.builder()
                    .title("Sapiens: A Brief History of Humankind")
                    .isbn("978-0062316097")
                    .genre("History")
                    .publisher("Harper")
                    .publishedYear(2011)
                    .totalCopies(3)
                    .availableCopies(3)
                    .author(author3)
                    .build());

            bookRepository.save(Book.builder()
                    .title("Homo Deus: A Brief History of Tomorrow")
                    .isbn("978-0062464316")
                    .genre("History")
                    .publisher("Harper")
                    .publishedYear(2015)
                    .totalCopies(3)
                    .availableCopies(3)
                    .author(author3)
                    .build());

            bookRepository.save(Book.builder()
                    .title("The Great Gatsby")
                    .isbn("978-0743273565")
                    .genre("Classic Fiction")
                    .publisher("Scribner")
                    .publishedYear(1925)
                    .totalCopies(4)
                    .availableCopies(4)
                    .author(author4)
                    .build());

            log.info("Seeded {} books and {} authors", bookRepository.count(), authorRepository.count());
        }

        log.info("Data seeding complete.");
    }

    private Role getOrCreateRole(String name) {
        return roleRepository.findByName(name).orElseGet(() -> {
            Role role = Role.builder().name(name).build();
            return roleRepository.save(role);
        });
    }
}
