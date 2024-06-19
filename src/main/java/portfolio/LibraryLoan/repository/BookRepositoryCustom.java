package portfolio.LibraryLoan.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import portfolio.LibraryLoan.dto.request.BookSearchCondDto;
import portfolio.LibraryLoan.dto.response.BookDto;

public interface BookRepositoryCustom {

    Page<BookDto> findBooksComplex(BookSearchCondDto cond, Pageable pageable);

}
