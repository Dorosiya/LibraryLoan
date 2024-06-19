package portfolio.LibraryLoan.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import portfolio.LibraryLoan.dto.response.ReservationDto;
import portfolio.LibraryLoan.entity.Book;
import portfolio.LibraryLoan.entity.BookStatus;
import portfolio.LibraryLoan.entity.Member;
import portfolio.LibraryLoan.entity.Reservation;
import portfolio.LibraryLoan.enums.BookStatusCode;
import portfolio.LibraryLoan.enums.LoanStatusCode;
import portfolio.LibraryLoan.enums.ReservationStatusCode;
import portfolio.LibraryLoan.exception.BookNotFoundException;
import portfolio.LibraryLoan.exception.BookUnavailableException;
import portfolio.LibraryLoan.repository.BookRepository;
import portfolio.LibraryLoan.repository.BookStatusRepository;
import portfolio.LibraryLoan.repository.LoanRepository;
import portfolio.LibraryLoan.repository.ReservationRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ReservationService {

    private final MemberService memberService;
    private final BookRepository bookRepository;
    private final BookStatusRepository bookStatusRepository;
    private final LoanRepository loanRepository;
    private final ReservationRepository reservationRepository;

    private static final int LOAN_STATUS_IN_LOAN = LoanStatusCode.IN_LOAN.getValue();
    private static final int LOAN_STATUS_OVERDUE = LoanStatusCode.OVERDUE.getValue();
    private static final int BOOK_STATUS_AVAILABLE = BookStatusCode.AVAILABLE.getValue();
    private static final int BOOK_STATUS_UNAVAILABLE = BookStatusCode.UNAVAILABLE.getValue();
    private static final int RESERVATION_STATUS_RESERVED = ReservationStatusCode.RESERVED.getValue();
    private static final int RESERVATION_STATUS_AVAILABLE = ReservationStatusCode.AVAILABLE.getValue();
    private static final int RESERVATION_LIMIT = 3;

    @Transactional(readOnly = true)
    public List<ReservationDto> getReservations(String username) {

        Member findMember = memberService.getMember(username);

        List<Integer> reserveCode = List.of(RESERVATION_STATUS_RESERVED, RESERVATION_STATUS_AVAILABLE);

        return reservationRepository.findReservationByIdAndStatus(findMember.getMemberId(), reserveCode);
    }

    // 단건 도서 예약 기능
    @Transactional
    public void reservation(String username, Long bookId) {
        // 사용자 조회 및 검증
        Member findMember = memberService.getMember(username);

        // 도서 조회 및 검증
        Book findBook = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("도서를 찾을 수 없습니다. 도서 ID : " + bookId));

        // 도서 상태 조회 및 검증
        BookStatus findBookStatus = bookStatusRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("도서의 상태를 찾을 수 없습니다. 도서 ID : " + bookId));

        List<Integer> loanStatusCodes = List.of(LOAN_STATUS_IN_LOAN, LOAN_STATUS_OVERDUE);
        // 본인이 이미 대출 했는 지 체크
        if (loanRepository.existsLoanItemByMemberAndBookAndStatus(findMember.getMemberId(), bookId, LoanStatusCode.IN_LOAN.getValue())) {
            throw new BookUnavailableException("이미 대출 중 입니다.");
        }

        // 중복 예약 체크
        validateDuplicateReservation(bookId, findMember);

        // 유저 예약도서 권수 체크
        validateReservationLimit(findMember);

        // BookStatus의 status 상태 코드
        // 1.대출가능, 2.대출중, 3.연체중, 4.예약중, 5.대출 불가
        checkBookAvailability(findBookStatus);

        LocalDate reservationDate = LocalDate.now();

        // BookReservation의 reservation_status 상태 코드
        // 1.예약 중, 2.대여 가능, 3.예약 만료, 4.예약 취소 5.예약 완료
        // 예약 정보 생성
        Reservation reservationBook = createBookReservation(findMember, findBook, reservationDate);

        // 데이터베이스 저장
        reservationRepository.save(reservationBook);
    }

    // 도서의 상태가 대출 가능, 사용 불가 상태 일 때 예약 불가능
    private static void checkBookAvailability(BookStatus findBookStatus) {
        if (findBookStatus.getStatus() == BOOK_STATUS_AVAILABLE ||
                findBookStatus.getStatus() == BOOK_STATUS_UNAVAILABLE) {
            throw new BookUnavailableException("해당 도서는 예약이 불가능한 상태입니다. 도서 ID : " + findBookStatus.getBook().getBookId());
        }
    }

    private void validateReservationLimit(Member findMember) {
        int currentReservationCount  = reservationRepository.findReservationCountByMemberId(findMember.getMemberId(), List.of(1, 2));
        if (currentReservationCount >= RESERVATION_LIMIT) {
            throw new BookUnavailableException("최대 예약 가능 권수를 초과했습니다. 현재 예약 권수 : " + currentReservationCount);
        }
    }

    private void validateDuplicateReservation(Long bookId, Member findMember) {
        int reservationCount = reservationRepository.findReservationCountByMemberIdAndBookId(findMember.getMemberId(), bookId, List.of(1, 2));
        if (reservationCount > 0) { // 예약 중인 도서가 하나라도 있으면 예외 발생
            throw new BookUnavailableException("이미 예약중인 도서입니다. 도서 ID : " + bookId);
        }
    }

    private static Reservation createBookReservation(Member findMember, Book findBook, LocalDate reservationDate) {
        Reservation reservationBook = Reservation.builder()
                .member(findMember)
                .book(findBook)
                .reservationDate(reservationDate)
                .expireDate(null)
                .status(ReservationStatusCode.RESERVED.getValue()) // 예약 중 상태 코드(1) 부여
                .build();
        return reservationBook;
    }

    @Transactional
    public void updateOverdueReservation() {
        reservationRepository.updateOverdueReservation(
                500,
                ReservationStatusCode.EXPIRED.getValue(),
                ReservationStatusCode.AVAILABLE.getValue()
        );
    }

}
