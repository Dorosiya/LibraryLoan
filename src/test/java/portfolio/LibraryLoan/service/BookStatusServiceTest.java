package portfolio.LibraryLoan.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import portfolio.LibraryLoan.entity.Book;
import portfolio.LibraryLoan.entity.BookStatus;
import portfolio.LibraryLoan.entity.Loan;
import portfolio.LibraryLoan.enums.BookStatusCode;
import portfolio.LibraryLoan.enums.LoanStatusCode;
import portfolio.LibraryLoan.exception.BookNotFoundException;
import portfolio.LibraryLoan.exception.BookUnavailableException;
import portfolio.LibraryLoan.repository.BookRepository;
import portfolio.LibraryLoan.repository.BookStatusRepository;
import portfolio.LibraryLoan.repository.LoanRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class BookStatusServiceTest {

    @Autowired
    private BookStatusRepository bookStatusRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private BookStatusService bookStatusService;

    @AfterEach
    void cleanUp() {
        bookStatusRepository.deleteAll();
        bookRepository.deleteAll();
    }

    @DisplayName("overdue 밸리데이션 테스트")
    @Test
    void validNonLoanableBook_Overdue() {
        // given
        Book testBook = Book.builder()
                .title("Test Book")
                .author("Test Author")
                .publisher("Test Publisher")
                .yearOfPublication("2023")
                .stock(1)
                .unitPrice(100)
                .price(100)
                .build();

        BookStatus overdueBookStatus = BookStatus.builder()
                .status(BookStatusCode.OVERDUE.getValue())
                .book(testBook)
                .build();

        testBook.linkBookStatus(overdueBookStatus);
        bookRepository.save(testBook);

        // when & then
        assertThrows(BookUnavailableException.class, () -> {
            bookStatusService.validNonLoanableBook(testBook.getBookId());
        });
    }

    @DisplayName("unavailable 밸리데이션 테스트")
    @Test
    void validNonLoanableBook_Unavailable() {
        // given
        Book testBook = Book.builder()
                .title("Test Book")
                .author("Test Author")
                .publisher("Test Publisher")
                .yearOfPublication("2023")
                .stock(1)
                .unitPrice(100)
                .price(100)
                .build();

        BookStatus unavailableBookStatus = BookStatus.builder()
                .status(BookStatusCode.UNAVAILABLE.getValue())
                .book(testBook)
                .build();

        testBook.linkBookStatus(unavailableBookStatus);
        bookRepository.save(testBook);

        // when & then
        assertThrows(BookUnavailableException.class, () -> {
            bookStatusService.validNonLoanableBook(testBook.getBookId());
        });
    }

    @DisplayName("연체상태 업데이트 테스트")
    @Test
    void updateOverdueBookStatus() {
        // given
        Book testBook = Book.builder()
                .title("Test Book")
                .author("Test Author")
                .publisher("Test Publisher")
                .yearOfPublication("2023")
                .stock(1)
                .unitPrice(100)
                .price(100)
                .build();

        BookStatus bookStatus = BookStatus.builder()
                .status(BookStatusCode.AVAILABLE.getValue())
                .book(testBook)
                .build();

        testBook.linkBookStatus(bookStatus);
        bookRepository.save(testBook);

        loanRepository.save(Loan.builder()
                .book(testBook)
                .status(LoanStatusCode.OVERDUE.getValue())
                .build());

        // when
        bookStatusService.updateOverdueBookStatus();

        // then
        BookStatus updatedBookStatus = bookStatusRepository.findById(testBook.getBookStatus().getBookStatusId())
                .orElseThrow(() -> new BookNotFoundException("책의 상태를 찾을 수 없습니다."));
        assertThat(updatedBookStatus.getStatus()).isEqualTo(BookStatusCode.OVERDUE.getValue());
    }
}