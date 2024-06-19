package portfolio.LibraryLoan.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import portfolio.LibraryLoan.dto.request.BookSearchCondDto;
import portfolio.LibraryLoan.dto.response.BookDto;
import portfolio.LibraryLoan.repository.BookRepository;

@RequiredArgsConstructor
@Service
public class BookService {

    private final BookRepository bookRepository;

    // 도서 조회
    @Transactional(readOnly = true)
    public Page<BookDto> findBook(BookSearchCondDto cond, Pageable pageable) {
        Page<BookDto> findBooks = bookRepository.findBooksComplex(cond, pageable);

        return findBooks;
    }

}
