package portfolio.LibraryLoan.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "book_status")
@Getter
@Entity
public class BookStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookStatusId;

    @OneToOne
    @JoinColumn(name = "book_id")
    private Book book;

    private int status;

    @Builder
    private BookStatus(Book book, int status) {
        this.book = book;
        this.status = status;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public void changeStatus(int status) {
        this.status = status;
    }

}
