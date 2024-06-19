package portfolio.LibraryLoan.repository;

import portfolio.LibraryLoan.dto.response.ReservationDto;
import portfolio.LibraryLoan.entity.Reservation;

import java.util.List;
import java.util.Optional;

public interface ReservationRepositoryCustom {

    List<ReservationDto> findReservationByIdAndStatus(Long memberId, List<Integer> status);

    Reservation findReservationsByIdAndBookIdsAndStatus(Long memberId, Long bookId, int status);

    Optional<Reservation> findFirstActiveReservation(Long bookId, int status);

    int findReservationCountByMemberIdAndBookId(Long memberId, Long bookId, List<Integer> statuses);

    int findReservationCountByMemberId(Long memberId, List<Integer> statuses);

    void updateOverdueReservation(int batchSize, int expiredCode, int availableCode);

}
