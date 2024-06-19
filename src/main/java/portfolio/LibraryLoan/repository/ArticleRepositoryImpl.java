package portfolio.LibraryLoan.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import portfolio.LibraryLoan.dto.request.ArticleSearchCond;
import portfolio.LibraryLoan.dto.response.ArticleDto;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static portfolio.LibraryLoan.entity.QArticle.article;
import static portfolio.LibraryLoan.entity.QMember.member;

public class ArticleRepositoryImpl implements ArticleRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ArticleRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<ArticleDto> findArticleComplex(ArticleSearchCond cond, Pageable pageable) {
        List<ArticleDto> articles = queryFactory
                .select(Projections.constructor(ArticleDto.class,
                        article.articleId,
                        article.title,
                        article.member.username,
                        article.content,
                        article.views,
                        article.createdDate))
                .from(article)
                .join(article.member, member)
                .where(
                        titleOfContains(cond.getTitle()),
                        contentOfContains(cond.getContent()),
                        usernameOfContains(cond.getUsername())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> articleCount = queryFactory
                .select(article.count())
                .from(article)
                .join(article.member, member)
                .where(
                        titleOfContains(cond.getTitle()),
                        contentOfContains(cond.getContent()),
                        usernameOfContains(cond.getUsername())
                );

        return PageableExecutionUtils.getPage(articles, pageable, articleCount::fetchOne);
    }

    private BooleanExpression titleOfContains(String title) {
        return title == null ? null : article.title.contains(title);
    }

    private BooleanExpression contentOfContains(String content) {
        return content == null ? null : article.content.contains(content);
    }

    private BooleanExpression usernameOfContains(String username) {
        return username == null ? null : article.member.username.contains(username);
    }

    @Override
    public Optional<ArticleDto> findArticleByArticleId(Long articleId) {
        ArticleDto articleDto = queryFactory
                .select(Projections.constructor(ArticleDto.class,
                        article.articleId,
                        article.title,
                        article.member.username,
                        article.content,
                        article.views,
                        article.createdDate)
                )
                .from(article)
                .join(article.member, member)
                .where(article.articleId.eq(articleId))
                .fetchOne();
        return Optional.ofNullable(articleDto);
    }
}
