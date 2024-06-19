package portfolio.LibraryLoan.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CommentDto {

    private Long commentId;

    private Long ArticleId;

    private String username;

    private String content;

    private LocalDateTime lastModifiedDate;

}
