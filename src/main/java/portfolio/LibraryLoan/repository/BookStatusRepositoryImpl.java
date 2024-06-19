package portfolio.LibraryLoan.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import portfolio.LibraryLoan.entity.QBookStatus;

import java.util.List;

import static portfolio.LibraryLoan.entity.QBookStatus.bookStatus;

public class BookStatusRepositoryImpl implements BookStatusRepositoryCustom {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public BookStatusRepositoryImpl(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public void updateBookStatusesInBatches(List<Long> loanItemBookIds, int batchSize, int overdueCode) {
        for (int i = 0; i < loanItemBookIds.size(); i += batchSize) {
            List<Long> batch = loanItemBookIds.subList(i, Math.min(i + batchSize, loanItemBookIds.size()));
            queryFactory
                    .update(bookStatus)
                    .set(bookStatus.status, overdueCode)
                    .where(bookStatus.book.bookId.in(batch))
                    .execute();

            em.flush();
            em.clear();
        }
    }

}
