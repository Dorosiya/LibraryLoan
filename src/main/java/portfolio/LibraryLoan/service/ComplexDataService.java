package portfolio.LibraryLoan.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import portfolio.LibraryLoan.dto.response.ComplexCountDto;
import portfolio.LibraryLoan.entity.Member;
import portfolio.LibraryLoan.enums.LoanStatusCode;
import portfolio.LibraryLoan.enums.ReservationStatusCode;
import portfolio.LibraryLoan.repository.LoanRepository;
import portfolio.LibraryLoan.repository.ReservationRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ComplexDataService {

    private final MemberService memberService;
    private final LoanRepository loanRepository;
    private final ReservationRepository reservationRepository;

    private static final int LOAN_STATUS_IN_LOAN = LoanStatusCode.IN_LOAN.getValue();
    private static final int LOAN_STATUS_OVERDUE = LoanStatusCode.OVERDUE.getValue();
    private static final int RESERVATION_STATUS_AVAILABLE = ReservationStatusCode.AVAILABLE.getValue();
    private static final int RESERVATION_STATUS_RESERVED = ReservationStatusCode.RESERVED.getValue();

    // 대출 도서, 예약 도서 조회
    @Transactional
    public ComplexCountDto getLoanAndReservationCount(String username) {
        Member findMember = memberService.getMember(username);
        List<Integer> loanStatusCodes = List.of(LOAN_STATUS_IN_LOAN, LOAN_STATUS_OVERDUE);
        int loanItemCount = loanRepository.getLoanCountByIdAndStatus(findMember.getMemberId(), loanStatusCodes);

        List<Integer> statusCode = List.of(RESERVATION_STATUS_RESERVED
                , RESERVATION_STATUS_AVAILABLE);

        int reservationCount = reservationRepository.findReservationCountByMemberId(findMember.getMemberId()
                , statusCode);

        return new ComplexCountDto(loanItemCount, reservationCount);
    }

}
