package portfolio.LibraryLoan.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@AllArgsConstructor
@Data
public class ReservationDto {

    private Long reservationId;
    private Long bookId;
    private String title;
    private String author;
    private String publisher;
    private String yearOfPublication;
    private LocalDate reservationDate;
    private LocalDate expireDate;
    private int status;

}
