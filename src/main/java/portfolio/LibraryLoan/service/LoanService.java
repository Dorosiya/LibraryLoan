package portfolio.LibraryLoan.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import portfolio.LibraryLoan.dto.response.LoanBookDto;
import portfolio.LibraryLoan.entity.*;
import portfolio.LibraryLoan.enums.BookStatusCode;
import portfolio.LibraryLoan.enums.LoanStatusCode;
import portfolio.LibraryLoan.enums.ReservationStatusCode;
import portfolio.LibraryLoan.exception.BookNotFoundException;
import portfolio.LibraryLoan.exception.BookUnavailableException;
import portfolio.LibraryLoan.exception.LoanNotFoundException;
import portfolio.LibraryLoan.repository.BookRepository;
import portfolio.LibraryLoan.repository.BookStatusRepository;
import portfolio.LibraryLoan.repository.LoanRepository;
import portfolio.LibraryLoan.repository.ReservationRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class LoanService {

    private final MemberService memberService;
    private final BookStatusService bookStatusService;
    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;
    private final BookStatusRepository bookStatusRepository;
    private final ReservationRepository reservationRepository;

    private static final int LOAN_STATUS_IN_LOAN = LoanStatusCode.IN_LOAN.getValue();
    private static final int LOAN_STATUS_OVERDUE = LoanStatusCode.OVERDUE.getValue();
    private static final int LOAN_STATUS_RETURNED = LoanStatusCode.RETURNED.getValue();
    private static final int BOOK_STATUS_AVAILABLE = BookStatusCode.AVAILABLE.getValue();
    private static final int BOOK_STATUS_RESERVED = BookStatusCode.RESERVED.getValue();
    private static final int RESERVATION_STATUS_AVAILABLE = ReservationStatusCode.AVAILABLE.getValue();
    private static final int RESERVATION_STATUS_COMPLETED = ReservationStatusCode.COMPLETED.getValue();
    private static final int LOAN_RETURN_DAYS = 7;
    private static final int MAX_LOAN_LIMIT = 3;

    @Transactional
    public void createLoan(String username, Long bookId) {
        Member findMember = memberService.getMember(username);

        // 도서 검증(도서가 존재 하는지 확인)
        Book findBook = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("도서를 찾을 수 없습니다. 도서 ID : " + bookId));

        // 도서 상태 조회
        BookStatus findBookStatus = bookStatusRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("도서의 상태를 찾을 수 없습니다. 도서 ID : " + bookId));

        // 대출 불가 도서 조회
        bookStatusService.validNonLoanableBook(findBookStatus.getBook().getBookId());

        // 도서 최대 대출 권수 검증
        List<Integer> loanStatusCodes = List.of(LOAN_STATUS_IN_LOAN, LOAN_STATUS_OVERDUE);
        validationLoanCount(findMember, loanStatusCodes, MAX_LOAN_LIMIT);

        reservationRepository.findFirstActiveReservation(bookId, RESERVATION_STATUS_AVAILABLE)
                .ifPresent(reservation -> {
                    if (!reservation.getMember().getMemberId().equals(findMember.getMemberId())) {
                        throw new BookUnavailableException("다른 사람이 예약 중 입니다.");
                    }
                    reservation.changeStatus(RESERVATION_STATUS_COMPLETED);
                    reservationRepository.save(reservation);
                });

        // 도서 대여 로직
        findBookStatus.changeStatus(BookStatusCode.IN_LOAN.getValue());
        Loan createLoan = Loan.builder()
                .member(findMember)
                .book(findBook)
                .loanDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(14))
                .returnDate(null)
                .status(LOAN_STATUS_IN_LOAN)
                .build();


        // DB 저장
        bookStatusRepository.save(findBookStatus);
        loanRepository.save(createLoan);
    }

    private void validationLoanCount(Member findMember, List<Integer> loanStatusCodes, int limitCount) {
        int loanCount = loanRepository.findLoanByMemberIdAndStatus(findMember.getMemberId(), loanStatusCodes);
        if (loanCount >= limitCount) {
            throw new BookUnavailableException("최대 대출 권수를 초과하여 대출이 불가능합니다. 최대 대출 권수 : " + limitCount + "권");
        }
    }

    @Transactional(readOnly = true)
    public List<LoanBookDto> getLoanBooks(String username) {
        Member findMember = memberService.getMember(username);

        List<Integer> loanStatusCodes = List.of(LOAN_STATUS_IN_LOAN, LOAN_STATUS_OVERDUE);

        List<LoanBookDto> loanedBooks = loanRepository.findLoanedBooksByMemberId(findMember.getMemberId(), loanStatusCodes);

        return loanedBooks;
    }

    // 반납
    @Transactional
    public void returnBook(String username, Long bookId) {
        Member findMember = memberService.getMember(username);

        List<Integer> loanStatusCodes = List.of(LOAN_STATUS_IN_LOAN, LOAN_STATUS_OVERDUE);

        Loan findLoan = loanRepository.findLoanByBookIdAndMemberIdAndStatus(
                        findMember.getMemberId(), bookId, loanStatusCodes)
                .orElseThrow(() -> new LoanNotFoundException("사용자의 대여 내역을 찾을 수 없습니다. 사용자 ID : " + findMember.getMemberId()));

        BookStatus findBookStatus = bookStatusRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("도서의 상태를 찾을 수 없습니다. 도서 ID : " + bookId));

        // 날짜 설정
        LocalDate now = LocalDate.now();

        // 대출 아이템 상태 업데이트
        findLoan.changeStatusAndReturnDate(LOAN_STATUS_RETURNED, now);

        // 도서 상태, 도서 예약 정보 업데이트
        LocalDate expireDate = now.plusDays(LOAN_RETURN_DAYS);
        Optional<Reservation> firstReservation = reservationRepository.findFirstActiveReservation(
                bookId, ReservationStatusCode.RESERVED.getValue());

        firstReservation.ifPresentOrElse(reservation -> {
                    reservation.changeStatus(RESERVATION_STATUS_AVAILABLE);
                    reservation.changeExpireDate(expireDate);
                    findBookStatus.changeStatus(BOOK_STATUS_RESERVED);
                    reservationRepository.save(reservation);
                    /*sendEmailService.sendEmail(findMember.getEmail(),
                            "예약도서 대출가능 알림",
                            "예약된 도서가 현재 대출이 가능합니다. 대출 가능 일자: " + expireDate);*/
                },
                () -> {
                    findBookStatus.changeStatus(BOOK_STATUS_AVAILABLE);
                });

        // DB 저장
        loanRepository.save(findLoan);
        bookStatusRepository.save(findBookStatus);
    }

    @Transactional
    public void updateOverdueLoan() {

        // 연체된 대출 정보를 업데이트합니다.
        loanRepository.updateOverdueLoans(
                LoanStatusCode.OVERDUE.getValue(),
                LoanStatusCode.IN_LOAN.getValue(),
                500
        );

    }
}
