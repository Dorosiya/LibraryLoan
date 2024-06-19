package portfolio.LibraryLoan.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class BookDto {

    private Long bookId;
    private String title;
    private String author;
    private String publisher;
    private String yearOfPublication;
    private String loanStatus;

}
