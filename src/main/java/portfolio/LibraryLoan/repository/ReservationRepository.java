package portfolio.LibraryLoan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import portfolio.LibraryLoan.entity.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long>, ReservationRepositoryCustom {
}
