package portfolio.LibraryLoan.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Table(name = "loan")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long loanId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;

    private LocalDate loanDate;

    private LocalDate dueDate;

    private LocalDate returnDate;

    private int status;

    @Builder
    private Loan(Long loanId, Member member, Book book, LocalDate loanDate, LocalDate dueDate, LocalDate returnDate, int status) {
        this.loanId = loanId;
        this.member = member;
        this.book = book;
        this.loanDate = loanDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.status = status;
    }

    public void changeStatusAndReturnDate(int status, LocalDate returnDate) {
        this.status = status;
        this.returnDate = returnDate;
    }

}
