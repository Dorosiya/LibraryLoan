package portfolio.LibraryLoan.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ComplexCountDto { // 대여와 예약 카운트 응답을 위한 DTO

    private int loanCount;
    private int reservationCount;

}
