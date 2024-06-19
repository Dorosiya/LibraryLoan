package portfolio.LibraryLoan.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import portfolio.LibraryLoan.dto.response.LoanBookDto;
import portfolio.LibraryLoan.entity.Loan;
import portfolio.LibraryLoan.entity.QBook;
import portfolio.LibraryLoan.entity.QLoan;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static portfolio.LibraryLoan.entity.QBook.book;
import static portfolio.LibraryLoan.entity.QLoan.loan;

public class LoanRepositoryImpl implements LoanRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    public LoanRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
        this.em = em;
    }

    @Override
    public List<LoanBookDto> findLoanedBooksByMemberId(Long memberId, List<Integer> loanStatusValue) {
        return queryFactory
                .select(Projections.constructor(LoanBookDto.class,
                        loan.book.bookId,
                        loan.book.title,
                        loan.book.author,
                        loan.book.publisher,
                        loan.book.yearOfPublication,
                        loan.loanDate,
                        loan.dueDate))
                .from(loan)
                .where(
                        loan.member.memberId.eq(memberId),
                        loan.status.in(loanStatusValue)
                )
                .fetch();
    }

    @Override
    public int findLoanByMemberIdAndStatus(Long memberId, List<Integer> loanStatusValue) {
        Long count = queryFactory
                .select(loan.count())
                .from(loan)
                .where(
                        loan.member.memberId.eq(memberId)
                        , loan.status.in(loanStatusValue))
                .fetchOne();
        return count != null ? count.intValue() : 0;
    }

    @Override
    public Optional<Loan> findLoanByBookIdAndMemberIdAndStatus(Long memberId, Long bookId, List<Integer> loanStatusValue) {
        Loan loan = queryFactory
                .select(QLoan.loan)
                .from(QLoan.loan)
                .where(
                        QLoan.loan.member.memberId.eq(memberId),
                        QLoan.loan.book.bookId.eq(bookId),
                        QLoan.loan.status.in(loanStatusValue))
                .fetchOne();
        return Optional.ofNullable(loan);
    }

    @Override
    public boolean existsLoanItemByMemberAndBookAndStatus(Long memberId, Long bookId, int statusValue) {
        Integer fetchOne = queryFactory
                .selectOne()
                .from(loan)
                .where(
                        loan.member.memberId.eq(memberId),
                        loan.book.bookId.eq(bookId),
                        loan.status.eq(statusValue)
                )
                .fetchFirst();

        return fetchOne != null;
    }

    @Override
    public int getLoanCountByIdAndStatus(Long memberId, List<Integer> loanStatusValue) {
        Long count = queryFactory
                .select(loan.count())
                .from(loan)
                .where(
                        loan.member.memberId.eq(memberId)
                        , loan.status.in(loanStatusValue))
                .fetchOne();
        return count != null ? count.intValue() : 0;
    }

    @Override
    public void updateOverdueLoans(int overdueCode, int availableCode, int batchSize) {
        List<Long> loanIds = queryFactory
                .select(loan.loanId)
                .from(loan)
                .where(
                        loan.status.eq(availableCode),
                        loan.dueDate.lt(LocalDate.now()))
                .fetch();

        for (int i = 0; i < loanIds.size(); i += batchSize) {
            List<Long> batchIds = loanIds.subList(i, Math.min(i + batchSize, loanIds.size()));
            queryFactory
                    .update(loan)
                    .set(loan.status, overdueCode)
                    .where(loan.loanId.in(batchIds))
                    .execute();

            em.flush();
            em.clear();
        }
    }

    @Override
    public List<Long> findLoanByOverdueCode(int overdueCode) {
        return queryFactory
                .select(loan.book.bookId)
                .from(loan)
                .where(loan.status.eq(overdueCode))
                .fetch();
    }
}
