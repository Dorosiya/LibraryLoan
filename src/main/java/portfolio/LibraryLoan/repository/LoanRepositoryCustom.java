package portfolio.LibraryLoan.repository;

import portfolio.LibraryLoan.dto.response.LoanBookDto;
import portfolio.LibraryLoan.entity.Loan;

import java.util.List;
import java.util.Optional;

public interface LoanRepositoryCustom {

    List<LoanBookDto> findLoanedBooksByMemberId(Long memberId, List<Integer> loanStatusValue);

    int findLoanByMemberIdAndStatus(Long memberId, List<Integer> loanStatusValue);

    Optional<Loan> findLoanByBookIdAndMemberIdAndStatus(Long memberId, Long bookId, List<Integer> loanStatusValue);

    boolean existsLoanItemByMemberAndBookAndStatus(Long memberId, Long bookId, int statusValue);

    int getLoanCountByIdAndStatus(Long memberId, List<Integer> loanStatusValue);

    void updateOverdueLoans(int overdueCode, int availableCode, int batchSize);

    List<Long> findLoanByOverdueCode(int overdueCode);
}
