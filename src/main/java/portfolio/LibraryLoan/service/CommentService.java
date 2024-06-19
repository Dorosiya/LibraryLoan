package portfolio.LibraryLoan.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import portfolio.LibraryLoan.dto.response.CommentDto;
import portfolio.LibraryLoan.entity.Article;
import portfolio.LibraryLoan.entity.Comment;
import portfolio.LibraryLoan.entity.Member;
import portfolio.LibraryLoan.exception.ArticleNotFoundException;
import portfolio.LibraryLoan.exception.CommentNotFoundException;
import portfolio.LibraryLoan.exception.NotAllowedException;
import portfolio.LibraryLoan.repository.ArticleRepository;
import portfolio.LibraryLoan.repository.CommentRepository;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class CommentService {

    private final MemberService memberService;
    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public List<CommentDto> findCommentInArticle(Long articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ArticleNotFoundException("공지사항을 찾을 수 없습니다. 공지사항 ID : " + articleId));

        Long findArticleId = article.getArticleId();

        article.plusViews();

        return commentRepository.findCommentByArticleId(findArticleId);
    }

    // 댓글 생성
    @Transactional
    public void createComment(String username, Long articleId, String content) {
        Member findMember = memberService.getMember(username);

        Article findArticle = articleRepository.findById(articleId)
                .orElseThrow(() -> new ArticleNotFoundException("공지사항을 찾을 수 없습니다. 공지사항 ID : " + articleId));

        Comment createComment = Comment.builder()
                .article(findArticle)
                .member(findMember)
                .content(content)
                .build();

        commentRepository.save(createComment);
    }

    @Transactional
    public void editComment(String username, Long commentId, String content) {
        Member findMember = memberService.getMember(username);

        Comment findComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("댓글을 찾을 수 없습니다. 댓글 ID: " + commentId));

        if (!findMember.getUsername().equals(findComment.getMember().getUsername()) &&
                !findComment.getMember().getMemberId().equals(findMember.getMemberId())) {
            throw new NotAllowedException("댓글을 수정할 수 없습니다. 댓글 ID: " + commentId);
        }

        findComment.editCommentContent(content);
    }

    // 댓글 삭제
    @Transactional
    public void deleteComment(String username, Long commentId) {
        Member findMember = memberService.getMember(username);

        Comment findComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("댓글을 찾을 수 없습니다. 댓글 ID : " + commentId));

        Article findArticle = articleRepository.findById(findComment.getArticle().getArticleId())
                .orElseThrow(() -> new ArticleNotFoundException("게시글을 찾을 수 없습니다."));
        Long findArticleId = findArticle.getArticleId();

        // 댓글 소유자 확인
        if (!findMember.getMemberId().equals(findComment.getMember().getMemberId())) {
            throw new NotAllowedException("댓글의 작성자만 삭제할 수 있습니다. 댓글 ID: " + commentId);
        }

        // 댓글이 해당 게시글에 속하는지 확인
        if (!findComment.getArticle().getArticleId().equals(findArticleId)) {
            throw new NotAllowedException("댓글은 지정된 게시글에 속하지 않습니다. 게시글 ID: " + findArticleId + ", 댓글 ID: " + commentId);
        }

        log.info("User : {} deleted comment : {}", findMember.getUsername(), commentId);

        commentRepository.deleteComment(findArticleId, commentId);
    }

}
