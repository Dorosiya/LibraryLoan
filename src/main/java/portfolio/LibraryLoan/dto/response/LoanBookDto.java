package portfolio.LibraryLoan.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class LoanBookDto {

    private Long bookId;
    private String title;
    private String author;
    private String publisher;
    private String yearOfPublication;
    private LocalDate loanDate;
    private LocalDate dueDate;

}
