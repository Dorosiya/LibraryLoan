package portfolio.LibraryLoan.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import portfolio.LibraryLoan.dto.response.ReservationDto;
import portfolio.LibraryLoan.entity.*;
import portfolio.LibraryLoan.enums.BookStatusCode;
import portfolio.LibraryLoan.enums.RoleType;
import portfolio.LibraryLoan.exception.BookUnavailableException;
import portfolio.LibraryLoan.repository.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class ReservationServiceTest {

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
    private ReservationRepository reservationRepository;

    @Autowired
    private ReservationService reservationService;

    private Member testMember;
    private Member anotherMember;
    private Book testBook;
    private Book loanedBook;

    @BeforeEach
    void setup() {
        Role userRole = Role.builder()
                .roleName(RoleType.ROLE_USER)
                .build();
        roleRepository.save(userRole);

        testMember = Member.builder()
                .role(userRole)
                .username("TestUser")
                .password("password")
                .age(25)
                .email("testuser@example.com")
                .build();
        memberRepository.save(testMember);

        anotherMember = Member.builder()
                .role(userRole)
                .username("AnotherUser")
                .password("password")
                .age(30)
                .email("anotheruser@example.com")
                .build();
        memberRepository.save(anotherMember);

        testBook = Book.builder()
                .title("Test Book")
                .author("Test Author")
                .publisher("Test Publisher")
                .yearOfPublication("2023")
                .stock(1)
                .unitPrice(100)
                .price(100)
                .build();

        BookStatus testBookStatus = BookStatus.builder()
                .book(testBook)
                .status(BookStatusCode.IN_LOAN.getValue())
                .build();

        loanedBook = Book.builder()
                .title("Loaned Book")
                .author("Test Author")
                .publisher("Test Publisher")
                .yearOfPublication("2023")
                .stock(1)
                .unitPrice(100)
                .price(100)
                .build();

        BookStatus loanedBookStatus = BookStatus.builder()
                .book(loanedBook)
                .status(BookStatusCode.IN_LOAN.getValue())
                .build();

        loanedBook.linkBookStatus(loanedBookStatus); // link the status to the book

        bookRepository.save(testBook);
        bookRepository.save(loanedBook);
        bookStatusRepository.save(testBookStatus);
        bookStatusRepository.save(loanedBookStatus);
    }

    @AfterEach
    void cleanUp() {
        reservationRepository.deleteAll();
        loanRepository.deleteAll();
        bookStatusRepository.deleteAll();
        bookRepository.deleteAll();
        memberRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @DisplayName("예약 생성 테스트")
    @Test
    void reservation() {
        // when
        reservationService.reservation(testMember.getUsername(), loanedBook.getBookId());

        // then
        List<Reservation> reservations = reservationRepository.findAll();
        assertThat(reservations).hasSize(1);
        assertThat(reservations.get(0).getMember().getUsername()).isEqualTo(testMember.getUsername());
        assertThat(reservations.get(0).getBook().getTitle()).isEqualTo(loanedBook.getTitle());
    }

    @DisplayName("중복 예약 테스트")
    @Test
    void reservation_DuplicateReservation() {
        // given
        reservationService.reservation(testMember.getUsername(), loanedBook.getBookId());

        // when & then
        assertThrows(BookUnavailableException.class, () -> {
            reservationService.reservation(testMember.getUsername(), loanedBook.getBookId());
        });
    }

    @DisplayName("예약 도서 한도 초과 테스트")
    @Test
    void reservation_ExceedLimit() {
        // given
        Book anotherBook1 = Book.builder()
                .title("Another Book 1")
                .author("Test Author")
                .publisher("Test Publisher")
                .yearOfPublication("2023")
                .stock(1)
                .unitPrice(100)
                .price(100)
                .build();

        BookStatus bookStatus1 = BookStatus.builder()
                .book(anotherBook1)
                .status(BookStatusCode.IN_LOAN.getValue())
                .build();
        anotherBook1.linkBookStatus(bookStatus1);

        bookRepository.save(anotherBook1);
        bookStatusRepository.save(bookStatus1);

        Book anotherBook2 = Book.builder()
                .title("Another Book 2")
                .author("Test Author")
                .publisher("Test Publisher")
                .yearOfPublication("2023")
                .stock(1)
                .unitPrice(100)
                .price(100)
                .build();

        BookStatus bookStatus2 = BookStatus.builder()
                .book(anotherBook2)
                .status(BookStatusCode.IN_LOAN.getValue())
                .build();
        anotherBook2.linkBookStatus(bookStatus2);

        bookRepository.save(anotherBook2);
        bookStatusRepository.save(bookStatus2);

        Book anotherBook3 = Book.builder()
                .title("Another Book 3")
                .author("Test Author")
                .publisher("Test Publisher")
                .yearOfPublication("2023")
                .stock(1)
                .unitPrice(100)
                .price(100)
                .build();

        BookStatus bookStatus3 = BookStatus.builder()
                .book(anotherBook3)
                .status(BookStatusCode.IN_LOAN.getValue())
                .build();
        anotherBook3.linkBookStatus(bookStatus3);

        bookRepository.save(anotherBook3);
        bookStatusRepository.save(bookStatus3);

        reservationService.reservation(testMember.getUsername(), anotherBook1.getBookId());
        reservationService.reservation(testMember.getUsername(), anotherBook2.getBookId());
        reservationService.reservation(testMember.getUsername(), anotherBook3.getBookId());

        // when & then
        assertThrows(BookUnavailableException.class, () -> {
            reservationService.reservation(testMember.getUsername(), loanedBook.getBookId());
        });
    }

    @DisplayName("예약 조회 테스트")
    @Test
    void getReservations() {
        // given
        reservationService.reservation(testMember.getUsername(), loanedBook.getBookId());

        // when
        List<ReservationDto> reservations = reservationService.getReservations(testMember.getUsername());

        // then
        assertThat(reservations).hasSize(1);
        assertThat(reservations.get(0).getTitle()).isEqualTo(loanedBook.getTitle());
    }
}