package portfolio.LibraryLoan.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "book")
@Getter
@Entity
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookId;

    @OneToOne(mappedBy = "book", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private BookStatus bookStatus;

    private String title;

    private String author;

    private String publisher;

    private String yearOfPublication;

    private int stock;

    private int unitPrice;

    private int price;

    @Builder
    private Book(BookStatus bookStatus, String title, String author, String publisher, String yearOfPublication, int stock, int unitPrice, int price) {
        this.bookStatus = bookStatus;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.yearOfPublication = yearOfPublication;
        this.stock = stock;
        this.unitPrice = unitPrice;
        this.price = price;
    }

    public void linkBookStatus(BookStatus status) {
        this.bookStatus = status;
        status.setBook(this);
    }

}
