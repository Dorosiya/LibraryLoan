package portfolio.LibraryLoan.repository;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import portfolio.LibraryLoan.dto.request.BookSearchCondDto;
import portfolio.LibraryLoan.dto.response.BookDto;
import portfolio.LibraryLoan.entity.QBookStatus;

import java.util.List;

import static portfolio.LibraryLoan.entity.QBook.book;
import static portfolio.LibraryLoan.entity.QBookStatus.bookStatus;

public class BookRepositoryImpl implements BookRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public BookRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<BookDto> findBooksComplex(BookSearchCondDto cond, Pageable pageable) {
        List<BookDto> content = queryFactory
                .select(Projections.constructor(BookDto.class,
                        book.bookId,
                        book.title,
                        book.author,
                        book.publisher,
                        book.yearOfPublication,
                        getStatusToString(bookStatus)))
                .from(book)
                .join(book.bookStatus, bookStatus)
                .where(
                        titleContains(cond.getTitle()),
                        authorContains(cond.getAuthor()),
                        publisherContains(cond.getPublisher()),
                        yearOfPublicationContains(cond.getYearOfPublication()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(book.count())
                .from(book)
                .where(
                        titleContains(cond.getTitle()),
                        authorContains(cond.getAuthor()),
                        publisherContains(cond.getPublisher()),
                        yearOfPublicationContains(cond.getYearOfPublication())
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private BooleanExpression titleContains(String titleOfBook) {
        return titleOfBook == null ? null : book.title.contains(titleOfBook);
    }

    private BooleanExpression authorContains(String author) {
        return author == null ? null : book.author.contains(author);
    }

    private BooleanExpression publisherContains(String publisher) {
        return publisher == null ? null : book.publisher.contains(publisher);
    }

    private BooleanExpression yearOfPublicationContains(String yearOfPublication) {
        return yearOfPublication == null ? null : book.yearOfPublication.contains(yearOfPublication);
    }

    private Expression<String> getStatusToString(QBookStatus bookStatusCode) {
        return new CaseBuilder()
                .when(bookStatus.status.eq(1)).then("대출가능")
                .when(bookStatus.status.eq(2)).then("대출 중")
                .when(bookStatus.status.eq(3)).then("연체 중")
                .when(bookStatus.status.eq(4)).then("예약 중")
                .when(bookStatus.status.eq(5)).then("반납 완료")
                .otherwise("조회 불가");
    }
}
