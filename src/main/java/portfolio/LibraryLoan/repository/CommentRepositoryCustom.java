package portfolio.LibraryLoan.repository;

import org.springframework.data.repository.query.Param;
import portfolio.LibraryLoan.dto.response.CommentDto;

import java.util.List;

public interface CommentRepositoryCustom {

    List<CommentDto> findCommentByArticleId(Long articleId);

    void deleteComment(Long articleId, Long commentId);
}
