package portfolio.LibraryLoan.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "reservation")
@Entity
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationId;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    private LocalDate reservationDate;

    private LocalDate expireDate;

    private int status;

    @Builder
    private Reservation(Member member, Book book, LocalDate reservationDate, LocalDate expireDate, int status) {
        this.member = member;
        this.book = book;
        this.reservationDate = reservationDate;
        this.expireDate = expireDate;
        this.status = status;
    }

    public void changeStatus(int status) {
        this.status = status;
    }

    public void changeExpireDate(LocalDate expireDate) {
        this.expireDate = expireDate;
    }

}
