package portfolio.LibraryLoan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import portfolio.LibraryLoan.entity.BookStatus;

public interface BookStatusRepository extends JpaRepository<BookStatus, Long>, BookStatusRepositoryCustom {
}
