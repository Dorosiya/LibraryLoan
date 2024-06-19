package portfolio.LibraryLoan.dto.request;

import lombok.Data;

@Data
public class CommentCreateDto {

    private Long articleId;

    private String content;

}
