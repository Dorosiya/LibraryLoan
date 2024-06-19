package portfolio.LibraryLoan.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import portfolio.LibraryLoan.dto.request.ArticleSearchCond;
import portfolio.LibraryLoan.dto.response.ArticleDto;

import java.util.Optional;

public interface ArticleRepositoryCustom {

    Page<ArticleDto> findArticleComplex(ArticleSearchCond cond, Pageable pageable);

    Optional<ArticleDto> findArticleByArticleId(Long articleId);

}
