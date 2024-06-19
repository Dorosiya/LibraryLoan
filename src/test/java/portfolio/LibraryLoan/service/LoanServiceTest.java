package portfolio.LibraryLoan.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import portfolio.LibraryLoan.dto.response.LoanBookDto;
import portfolio.LibraryLoan.entity.*;
import portfolio.LibraryLoan.enums.BookStatusCode;
import portfolio.LibraryLoan.enums.LoanStatusCode;
import portfolio.LibraryLoan.enums.RoleType;
import portfolio.LibraryLoan.exception.BookNotFoundException;
import portfolio.LibraryLoan.exception.BookUnavailableException;
import portfolio.LibraryLoan.exception.UserNotFoundException;
import portfolio.LibraryLoan.repository.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@SpringBootTest
@Transactional
public class LoanServiceTest {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookStatusRepository bookStatusRepository;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private LoanService loanService;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReservationService reservationService;

    private Member testMember;
    private Member anotherMember;
    private Member anotherMember2;

    @BeforeEach
    void setup() {
        Role userRole = Role.builder()
                .roleName(RoleType.ROLE_USER)
                .build();
        roleRepository.save(userRole);

        testMember = Member.builder()
                .role(userRole)
                .username("NewUser")
                .password("2345")
                .age(31)
                .email("NewUser@newUser.com")
                .build();
        memberRepository.save(testMember);

        anotherMember = Member.builder()
                .role(userRole)
                .username("AnotherUser")
                .password("6789")
                .age(29)
                .email("AnotherUser@anotherUser.com")
                .build();
        memberRepository.save(anotherMember);

        anotherMember2 = Member.builder()
                .role(userRole)
                .username("AnotherUser2")
                .password("67892")
                .age(22)
                .email("AnotherUser2@anotherUser.com")
                .build();
        memberRepository.save(anotherMember2);
    }

    @AfterEach
    void cleanUp() {
        reservationRepository.deleteAll();
        loanRepository.deleteAll();
        bookRepository.deleteAll();
        memberRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @DisplayName("대출 생성 테스트")
    @Test
    void createLoan() {
        // given
        Book testBook = Book.builder()
                .title("testTitle")
                .author("TestAuthor")
                .publisher("TestPublisher")
                .yearOfPublication("2024")
                .stock(11)
                .unitPrice(100)
                .price(100)
                .build();

        BookStatus testBookStatus = BookStatus.builder()
                .book(testBook)
                .status(BookStatusCode.AVAILABLE.getValue())
                .build();

        testBook.linkBookStatus(testBookStatus);

        Book savedBook = bookRepository.save(testBook);
        bookStatusRepository.save(testBookStatus);

        // when
        loanService.createLoan(testMember.getUsername(), savedBook.getBookId());

        // then
        Loan findLoan = loanRepository.findLoanByBookIdAndMemberIdAndStatus(
                        testMember.getMemberId(), savedBook.getBookId(), List.of(LoanStatusCode.IN_LOAN.getValue()))
                .orElseThrow(() -> new BookNotFoundException("대출 내역을 찾을 수 없습니다."));

        BookStatus findBookStatus = bookStatusRepository.findById(savedBook.getBookId())
                .orElseThrow(() -> new BookNotFoundException("책의 상태를 찾을 수 없습니다."));

        assertThat(findLoan.getBook().getBookId()).isEqualTo(savedBook.getBookId());
        assertThat(findLoan.getMember().getMemberId()).isEqualTo(testMember.getMemberId());
        assertThat(findLoan.getStatus()).isEqualTo(LoanStatusCode.IN_LOAN.getValue());
        assertThat(findBookStatus.getStatus()).isEqualTo(BookStatusCode.IN_LOAN.getValue());
    }

    @DisplayName("최대 대출 한도 초과 테스트")
    @Test
    void createLoan_ExceedingMaxLoanLimit() {
        // given
        for (int i = 1; i <= 3; i++) {
            Book book = Book.builder()
                    .title("testTitle" + i)
                    .author("TestAuthor")
                    .publisher("TestPublisher")
                    .yearOfPublication("2024")
                    .stock(1)
                    .unitPrice(100)
                    .price(100)
                    .build();

            BookStatus bookStatus = BookStatus.builder()
                    .book(book)
                    .status(BookStatusCode.AVAILABLE.getValue())
                    .build();

            book.linkBookStatus(bookStatus);

            Book savedBook = bookRepository.save(book);
            bookStatusRepository.save(bookStatus);

            loanService.createLoan(testMember.getUsername(), savedBook.getBookId());
        }

        // given - additional book to exceed limit
        Book extraBook = Book.builder()
                .title("extraBook")
                .author("TestAuthor")
                .publisher("TestPublisher")
                .yearOfPublication("2024")
                .stock(1)
                .unitPrice(100)
                .price(100)
                .build();

        BookStatus extraBookStatus = BookStatus.builder()
                .book(extraBook)
                .status(BookStatusCode.AVAILABLE.getValue())
                .build();

        extraBook.linkBookStatus(extraBookStatus);

        Book savedExtraBook = bookRepository.save(extraBook);
        bookStatusRepository.save(extraBookStatus);

        // when & then
        assertThrows(BookUnavailableException.class, () -> {
            loanService.createLoan(testMember.getUsername(), savedExtraBook.getBookId());
        });
    }

    @DisplayName("대출할 수 없는 상태의 도서 대출 시도 테스트")
    @Test
    void createLoan_BookNotAvailable() {
        // given
        Book testBook = Book.builder()
                .title("testTitle")
                .author("TestAuthor")
                .publisher("TestPublisher")
                .yearOfPublication("2024")
                .stock(1)
                .unitPrice(100)
                .price(100)
                .build();

        BookStatus testBookStatus = BookStatus.builder()
                .book(testBook)
                .status(BookStatusCode.IN_LOAN.getValue())  // 책 상태를 대출 중으로 설정
                .build();

        testBook.linkBookStatus(testBookStatus);

        Book savedBook = bookRepository.save(testBook);
        bookStatusRepository.save(testBookStatus);

        // when & then
        assertThrows(BookUnavailableException.class, () -> {
            loanService.createLoan(testMember.getUsername(), savedBook.getBookId());
        });
    }

    @DisplayName("대출된 도서 반납 테스트")
    @Test
    void returnLoan() {
        // given
        Book testBook = Book.builder()
                .title("testTitle")
                .author("TestAuthor")
                .publisher("TestPublisher")
                .yearOfPublication("2024")
                .stock(1)
                .unitPrice(100)
                .price(100)
                .build();

        BookStatus testBookStatus = BookStatus.builder()
                .book(testBook)
                .status(BookStatusCode.AVAILABLE.getValue())
                .build();

        testBook.linkBookStatus(testBookStatus);

        Book savedBook = bookRepository.save(testBook);
        bookStatusRepository.save(testBookStatus);

        loanService.createLoan(testMember.getUsername(), savedBook.getBookId());

        // when
        loanService.returnBook(testMember.getUsername(), savedBook.getBookId());

        // then
        Loan findLoan = loanRepository.findLoanByBookIdAndMemberIdAndStatus(
                        testMember.getMemberId(), savedBook.getBookId(), List.of(LoanStatusCode.RETURNED.getValue()))
                .orElseThrow(() -> new BookNotFoundException("대출 내역을 찾을 수 없습니다."));

        BookStatus findBookStatus = bookStatusRepository.findById(savedBook.getBookId())
                .orElseThrow(() -> new BookNotFoundException("책의 상태를 찾을 수 없습니다."));

        assertThat(findLoan.getStatus()).isEqualTo(LoanStatusCode.RETURNED.getValue());
        assertThat(findBookStatus.getStatus()).isEqualTo(BookStatusCode.AVAILABLE.getValue());
    }

    @DisplayName("연체된 대출 상태 업데이트 테스트")
    @Test
    void updateOverdueLoan() {
        // given
        Book testBook = Book.builder()
                .title("testTitle")
                .author("TestAuthor")
                .publisher("TestPublisher")
                .yearOfPublication("2024")
                .stock(1)
                .unitPrice(100)
                .price(100)
                .build();

        BookStatus testBookStatus = BookStatus.builder()
                .book(testBook)
                .status(BookStatusCode.AVAILABLE.getValue())
                .build();

        testBook.linkBookStatus(testBookStatus);

        Book savedBook = bookRepository.save(testBook);
        bookStatusRepository.save(testBookStatus);

        loanService.createLoan(testMember.getUsername(), savedBook.getBookId());

        // 대출 상태를 IN_LOAN에서 연체 상태로 변경
        Loan findLoan = loanRepository.findLoanByBookIdAndMemberIdAndStatus(
                        testMember.getMemberId(), savedBook.getBookId(), List.of(LoanStatusCode.IN_LOAN.getValue()))
                .orElseThrow(() -> new BookNotFoundException("대출 내역을 찾을 수 없습니다."));

        // 리플렉션을 사용하여 dueDate를 어제로 설정
        setField(findLoan, "dueDate", findLoan.getLoanDate().minusDays(1));
        loanRepository.save(findLoan);

        // when
        loanService.updateOverdueLoan();

        // then
        Loan overdueLoan = loanRepository.findLoanByBookIdAndMemberIdAndStatus(
                        testMember.getMemberId(), savedBook.getBookId(), List.of(LoanStatusCode.OVERDUE.getValue()))
                .orElseThrow(() -> new BookNotFoundException("연체 대출 내역을 찾을 수 없습니다."));

        assertThat(overdueLoan.getStatus()).isEqualTo(LoanStatusCode.OVERDUE.getValue());
    }

    @DisplayName("대출 내역 조회 테스트")
    @Test
    void getLoanBooks() {
        // given
        Book testBook1 = Book.builder()
                .title("testTitle1")
                .author("TestAuthor")
                .publisher("TestPublisher")
                .yearOfPublication("2024")
                .stock(1)
                .unitPrice(100)
                .price(100)
                .build();

        BookStatus testBookStatus1 = BookStatus.builder()
                .book(testBook1)
                .status(BookStatusCode.AVAILABLE.getValue())
                .build();

        testBook1.linkBookStatus(testBookStatus1);

        Book testBook2 = Book.builder()
                .title("testTitle2")
                .author("TestAuthor")
                .publisher("TestPublisher")
                .yearOfPublication("2024")
                .stock(1)
                .unitPrice(100)
                .price(100)
                .build();

        BookStatus testBookStatus2 = BookStatus.builder()
                .book(testBook2)
                .status(BookStatusCode.AVAILABLE.getValue())
                .build();

        testBook2.linkBookStatus(testBookStatus2);

        bookRepository.save(testBook1);
        bookRepository.save(testBook2);
        bookStatusRepository.save(testBookStatus1);
        bookStatusRepository.save(testBookStatus2);

        loanService.createLoan(testMember.getUsername(), testBook1.getBookId());
        loanService.createLoan(testMember.getUsername(), testBook2.getBookId());

        // when
        List<LoanBookDto> loanedBooks = loanService.getLoanBooks(testMember.getUsername());

        // then
        assertThat(loanedBooks.size()).isEqualTo(2);
        assertThat(loanedBooks).extracting("title")
                .containsExactlyInAnyOrder("testTitle1", "testTitle2");
    }

    @DisplayName("예약된 도서를 대출 시도 테스트")
    @Test
    void createLoan_ReservedBook() {
        // given
        Book testBook = Book.builder()
                .title("testTitle")
                .author("TestAuthor")
                .publisher("TestPublisher")
                .yearOfPublication("2024")
                .stock(1)
                .unitPrice(100)
                .price(100)
                .build();

        BookStatus testBookStatus = BookStatus.builder()
                .book(testBook)
                .status(BookStatusCode.AVAILABLE.getValue())
                .build();

        testBook.linkBookStatus(testBookStatus);

        Book savedBook = bookRepository.save(testBook);
        bookStatusRepository.save(testBookStatus);

        // 먼저 도서를 대출함
        loanService.createLoan(testMember.getUsername(), savedBook.getBookId());

        // 예약 생성
        reservationService.reservation(anotherMember.getUsername(), savedBook.getBookId());

        // when & then
        assertThrows(BookUnavailableException.class, () -> {
            loanService.createLoan(anotherMember2.getUsername(), savedBook.getBookId());
        });
    }

    @DisplayName("대출 상태 업데이트 후 다시 대출 시도 테스트")
    @Test
    void createLoan_AfterReturn() {
        // given
        Book testBook = Book.builder()
                .title("testTitle")
                .author("TestAuthor")
                .publisher("TestPublisher")
                .yearOfPublication("2024")
                .stock(1)
                .unitPrice(100)
                .price(100)
                .build();

        BookStatus testBookStatus = BookStatus.builder()
                .book(testBook)
                .status(BookStatusCode.AVAILABLE.getValue())
                .build();

        testBook.linkBookStatus(testBookStatus);

        Book savedBook = bookRepository.save(testBook);
        bookStatusRepository.save(testBookStatus);

        loanService.createLoan(testMember.getUsername(), savedBook.getBookId());
        loanService.returnBook(testMember.getUsername(), savedBook.getBookId());

        // when
        loanService.createLoan(testMember.getUsername(), savedBook.getBookId());

        // then
        Loan findLoan = loanRepository.findLoanByBookIdAndMemberIdAndStatus(
                        testMember.getMemberId(), savedBook.getBookId(), List.of(LoanStatusCode.IN_LOAN.getValue()))
                .orElseThrow(() -> new BookNotFoundException("대출 내역을 찾을 수 없습니다."));

        BookStatus findBookStatus = bookStatusRepository.findById(savedBook.getBookId())
                .orElseThrow(() -> new BookNotFoundException("책의 상태를 찾을 수 없습니다."));

        assertThat(findLoan.getBook().getBookId()).isEqualTo(savedBook.getBookId());
        assertThat(findLoan.getMember().getMemberId()).isEqualTo(testMember.getMemberId());
        assertThat(findLoan.getStatus()).isEqualTo(LoanStatusCode.IN_LOAN.getValue());
        assertThat(findBookStatus.getStatus()).isEqualTo(BookStatusCode.IN_LOAN.getValue());
    }

    @DisplayName("잘못된 사용자로 대출 시도 테스트")
    @Test
    void createLoan_InvalidUser() {
        // given
        Book testBook = Book.builder()
                .title("testTitle")
                .author("TestAuthor")
                .publisher("TestPublisher")
                .yearOfPublication("2024")
                .stock(1)
                .unitPrice(100)
                .price(100)
                .build();

        BookStatus testBookStatus = BookStatus.builder()
                .book(testBook)
                .status(BookStatusCode.AVAILABLE.getValue())
                .build();

        testBook.linkBookStatus(testBookStatus);

        Book savedBook = bookRepository.save(testBook);
        bookStatusRepository.save(testBookStatus);

        // when & then
        assertThrows(UserNotFoundException.class, () -> {
            loanService.createLoan("invalidUser", savedBook.getBookId());
        });
    }
}
