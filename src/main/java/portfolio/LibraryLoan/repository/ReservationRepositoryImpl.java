package portfolio.LibraryLoan.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import portfolio.LibraryLoan.dto.response.ReservationDto;
import portfolio.LibraryLoan.entity.QReservation;
import portfolio.LibraryLoan.entity.Reservation;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static portfolio.LibraryLoan.entity.QBook.book;
import static portfolio.LibraryLoan.entity.QReservation.reservation;

public class ReservationRepositoryImpl implements ReservationRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    public ReservationRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
        this.em = em;
    }

    @Override
    public List<ReservationDto> findReservationByIdAndStatus(Long memberId, List<Integer> status) {
        return queryFactory
                .select(Projections.constructor(ReservationDto.class,
                        reservation.reservationId,
                        reservation.book.bookId,
                        reservation.book.title,
                        reservation.book.author,
                        reservation.book.publisher,
                        reservation.book.yearOfPublication,
                        reservation.reservationDate,
                        reservation.expireDate,
                        reservation.status))
                .from(reservation)
                .join(reservation.book, book)
                .where(
                        reservation.member.memberId.eq(memberId)
                        , reservation.status.in(status)
                )
                .fetch();
    }

    @Override
    public Reservation findReservationsByIdAndBookIdsAndStatus(Long memberId, Long bookId, int status) {
        return queryFactory
                .select(reservation)
                .from(reservation)
                .where(
                        reservation.member.memberId.eq(memberId),
                        reservation.book.bookId.eq(bookId),
                        reservation.status.eq(status))
                .fetchOne();
    }

    @Override
    public Optional<Reservation> findFirstActiveReservation(Long bookId, int status) {
        Reservation reservation = queryFactory
                .select(QReservation.reservation)
                .from(QReservation.reservation)
                .where(
                        QReservation.reservation.book.bookId.eq(bookId),
                        QReservation.reservation.status.eq(status))
                .orderBy(QReservation.reservation.reservationId.asc())
                .limit(1)
                .fetchOne();

        return Optional.ofNullable(reservation);
    }

    @Override
    public int findReservationCountByMemberIdAndBookId(Long memberId, Long bookId, List<Integer> statuses) {
        Long count = queryFactory
                .select(reservation.count())
                .from(reservation)
                .where(
                        reservation.member.memberId.eq(memberId),
                        reservation.book.bookId.eq(bookId),
                        reservation.status.in(statuses))
                .fetchOne();

        return count != null ? count.intValue() : 0;
    }

    @Override
    public int findReservationCountByMemberId(Long memberId, List<Integer> statuses) {
        Long count = queryFactory
                .select(reservation.count())
                .from(reservation)
                .where(
                        reservation.member.memberId.eq(memberId),
                        reservation.status.in(statuses))
                .fetchOne();

        return count != null ? count.intValue() : 0;
    }

    @Override
    public void updateOverdueReservation(int batchSize, int expiredCode, int availableCode) {
        List<Long> reservationIds = queryFactory
                .select(reservation.reservationId)
                .from(reservation)
                .where(
                        reservation.status.eq(availableCode),
                        reservation.expireDate.lt(LocalDate.now()))
                .fetch();

        for (int i = 0; i < reservationIds.size(); i += batchSize) {
            List<Long> batchIds = reservationIds.subList(i, Math.min(i + batchSize, reservationIds.size()));
            queryFactory
                    .update(reservation)
                    .set(reservation.status, expiredCode)
                    .where(reservation.reservationId.in(batchIds))
                    .execute();

            em.flush();
            em.clear();
        }
    }
}
