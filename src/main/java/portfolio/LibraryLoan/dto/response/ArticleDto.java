package portfolio.LibraryLoan.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@Data
public class ArticleDto {

    private Long articleId;

    private String title;

    private String username;

    private String content;

    private Long views;

    private LocalDate createdDate;

    public ArticleDto(Long articleId, String title, String username, String content, Long views, LocalDateTime createdDate) {
        this.articleId = articleId;
        this.title = title;
        this.username = username;
        this.content = content;
        this.views = views;
        this.createdDate = createdDate.toLocalDate();
    }
}
