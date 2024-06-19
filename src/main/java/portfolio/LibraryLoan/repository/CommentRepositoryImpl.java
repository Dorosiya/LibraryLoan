package portfolio.LibraryLoan.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import portfolio.LibraryLoan.dto.response.CommentDto;
import portfolio.LibraryLoan.entity.QArticle;
import portfolio.LibraryLoan.entity.QComment;
import portfolio.LibraryLoan.entity.QMember;

import java.util.List;

import static portfolio.LibraryLoan.entity.QArticle.article;
import static portfolio.LibraryLoan.entity.QComment.comment;
import static portfolio.LibraryLoan.entity.QMember.member;

public class CommentRepositoryImpl implements CommentRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public CommentRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<CommentDto> findCommentByArticleId(Long articleId) {
        return queryFactory
                .select(Projections.constructor(CommentDto.class,
                        comment.commentId,
                        comment.article.articleId,
                        comment.member.username,
                        comment.content,
                        comment.lastModifiedDate))
                .from(comment)
                .join(comment.article, article)
                .join(comment.member, member)
                .where(comment.article.articleId.eq(articleId))
                .fetch();
    }

    @Override
    public void deleteComment(Long articleId, Long commentId) {
        queryFactory
                .delete(comment)
                .where(comment.article.articleId.eq(articleId),
                        comment.commentId.eq(commentId))
                .execute();
    }
}
