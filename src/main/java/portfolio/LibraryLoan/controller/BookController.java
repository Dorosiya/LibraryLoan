package portfolio.LibraryLoan.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import portfolio.LibraryLoan.dto.request.BookSearchCondDto;
import portfolio.LibraryLoan.dto.response.BookDto;
import portfolio.LibraryLoan.service.BookService;
import portfolio.LibraryLoan.service.MemberService;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class BookController {

    private final BookService bookService;

    // 도서 조회
    @GetMapping("/book")
    public ResponseEntity<Page<BookDto>> findBook(BookSearchCondDto cond, Pageable pageable) {
        Page<BookDto> bookDto = bookService.findBook(cond, pageable);

        return new ResponseEntity<>(bookDto, HttpStatus.OK);
    }

}
