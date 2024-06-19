package portfolio.LibraryLoan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import portfolio.LibraryLoan.entity.Article;

public interface ArticleRepository extends JpaRepository<Article, Long>, ArticleRepositoryCustom {
}
