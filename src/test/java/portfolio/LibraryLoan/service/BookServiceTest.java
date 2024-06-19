package portfolio.LibraryLoan.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import portfolio.LibraryLoan.dto.request.BookSearchCondDto;
import portfolio.LibraryLoan.dto.response.BookDto;
import portfolio.LibraryLoan.entity.Book;
import portfolio.LibraryLoan.entity.BookStatus;
import portfolio.LibraryLoan.entity.Member;
import portfolio.LibraryLoan.entity.Role;
import portfolio.LibraryLoan.enums.BookStatusCode;
import portfolio.LibraryLoan.enums.RoleType;
import portfolio.LibraryLoan.repository.BookRepository;
import portfolio.LibraryLoan.repository.BookStatusRepository;
import portfolio.LibraryLoan.repository.MemberRepository;
import portfolio.LibraryLoan.repository.RoleRepository;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class BookServiceTest {

    @Autowired
    BookRepository bookRepository;

    @Autowired
    BookStatusRepository bookStatusRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    RoleRepository roleRepository;

    @BeforeEach
    void setup() {
        Role userRole = Role.builder()
                .roleName(RoleType.ROLE_USER)
                .build();
        roleRepository.save(userRole);

        Member testMember = Member.builder()
                .role(userRole)
                .username("NewUser")
                .password("2345")
                .age(31)
                .email("NewUser@newUSer.com")
                .build();
        memberRepository.save(testMember);
    }

    @DisplayName("복합조건 도서 조회 테스트")
    @Test
    void findBooksComplex() {
        // given
        Book testBook = Book.builder()
                .title("testTitle")
                .author("TestAuthor")
                .publisher("TestPublisher")
                .yearOfPublication("2024")
                .stock(11)
                .unitPrice(100)
                .price(100)
                .build();

        BookStatus testBookStatus = BookStatus.builder()
                .book(testBook)
                .status(BookStatusCode.IN_LOAN.getValue())
                .build();

        testBook.linkBookStatus(testBookStatus);

        Book saveBook = bookRepository.save(testBook);
        bookStatusRepository.save(testBookStatus);

        BookSearchCondDto bookSearchCondDto = new BookSearchCondDto();
        bookSearchCondDto.setTitle("testTitle");
        // when
        Page<BookDto> findBook = bookRepository.findBooksComplex(bookSearchCondDto, Pageable.ofSize(10));

        // then
        assertThat(findBook.getContent().get(0).getBookId()).isEqualTo(1);

    }
}