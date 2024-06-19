package portfolio.LibraryLoan.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ArticleRequestDto {

    @NotEmpty(message = "제목은 필수 입력 값 입니다.")
    private String title;

    @NotEmpty(message = "내용은 필수 입력 값 입니다.")
    private String content;

}
