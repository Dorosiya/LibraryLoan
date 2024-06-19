package portfolio.LibraryLoan.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class BookSearchCondDto {

    private String title;
    private String author;
    private String publisher;
    private String yearOfPublication;

}
