package portfolio.LibraryLoan.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import portfolio.LibraryLoan.dto.response.ComplexCountDto;
import portfolio.LibraryLoan.entity.*;
import portfolio.LibraryLoan.enums.BookStatusCode;
import portfolio.LibraryLoan.enums.LoanStatusCode;
import portfolio.LibraryLoan.enums.ReservationStatusCode;
import portfolio.LibraryLoan.enums.RoleType;
import portfolio.LibraryLoan.repository.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ComplexDataServiceTest {

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
    private MemberService memberService;

    @Autowired
    private ComplexDataService complexDataService;

    private Member testMember;
    private Book testBook;
    private BookStatus testBookStatus;

    @BeforeEach
    void setup() {
        Role userRole = Role.builder()
                .roleName(RoleType.ROLE_USER)
                .build();
        roleRepository.save(userRole);

        testMember = Member.builder()
                .role(userRole)
                .username("TestUser" + System.currentTimeMillis())
                .password("password")
                .age(25)
                .email("testuser" + System.currentTimeMillis() + "@example.com")
                .build();
        memberRepository.save(testMember);

        testBookStatus = BookStatus.builder()
                .status(BookStatusCode.AVAILABLE.getValue())
                .build();

        testBook = Book.builder()
                .title("Test Book")
                .author("Test Author")
                .publisher("Test Publisher")
                .yearOfPublication("2023")
                .stock(1)
                .unitPrice(100)
                .price(100)
                .bookStatus(testBookStatus)
                .build();

        testBookStatus.setBook(testBook);
        bookRepository.save(testBook);
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

    @DisplayName("대출 및 예약된 도서의 개수를 조회")
    @Test
    void getLoanAndReservationCount() {
        // given
        Loan loan = Loan.builder()
                .book(testBook)
                .member(testMember)
                .status(LoanStatusCode.IN_LOAN.getValue())
                .build();
        loanRepository.save(loan);

        Reservation reservation = Reservation.builder()
                .book(testBook)
                .member(testMember)
                .status(ReservationStatusCode.RESERVED.getValue())
                .build();
        reservationRepository.save(reservation);

        // when
        ComplexCountDto result = complexDataService.getLoanAndReservationCount(testMember.getUsername());

        // then
        assertThat(result.getLoanCount()).isEqualTo(1);
        assertThat(result.getReservationCount()).isEqualTo(1);
    }

}