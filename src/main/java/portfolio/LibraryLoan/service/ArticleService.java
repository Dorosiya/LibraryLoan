package portfolio.LibraryLoan.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import portfolio.LibraryLoan.dto.request.ArticleSearchCond;
import portfolio.LibraryLoan.dto.response.ArticleDto;
import portfolio.LibraryLoan.entity.Article;
import portfolio.LibraryLoan.entity.Member;
import portfolio.LibraryLoan.exception.ArticleDeletionNotAllowedException;
import portfolio.LibraryLoan.exception.ArticleNotFoundException;
import portfolio.LibraryLoan.exception.NotAllowedException;
import portfolio.LibraryLoan.repository.ArticleRepository;

@Slf4j
@RequiredArgsConstructor
@Service
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final MemberService memberService;

    // 게시글 생성
    @Transactional
    public void createArticle(String username, String title, String content) {
        Member findMember = memberService.getMember(username);

        Article createArticle = Article.builder()
                .member(findMember)
                .title(title)
                .content(content)
                .views(0L)
                .build();

        articleRepository.save(createArticle);
    }

    // 검색 조건으로 게시글 조회
    @Transactional(readOnly = true)
    public Page<ArticleDto> findArticles(ArticleSearchCond cond, Pageable pageable) {
        return articleRepository.findArticleComplex(cond, pageable);
    }

    // 게시글 조회
    @Transactional
    public ArticleDto findArticle(Long articleId) {
        return articleRepository.findArticleByArticleId(articleId)
                .orElseThrow(() -> new ArticleNotFoundException("게시글을 찾을 수 없습니다. 게시글 ID: " + articleId));
    }

    @Transactional
    public void editArticle(String username, Long articleId, String title, String content) {
        Member findMember = memberService.getMember(username);

        Article findArticle = articleRepository.findById(articleId)
                .orElseThrow(() -> new ArticleNotFoundException("게시글을 찾을 수 없습니다. 게시글 ID: " + articleId));

        if (!findMember.getUsername().equals(findArticle.getMember().getUsername()) &&
                !findArticle.getMember().getMemberId().equals(findMember.getMemberId())) {
            throw new NotAllowedException("게시글을 수정할 수 없습니다.게시글 ID: " + articleId);
        }

        findArticle.editTitleAndContent(title, content);
    }

    // 게시글 삭제
    @Transactional
    public void deleteArticle(String requestUsername, Long articleId) {
        Member findMember = memberService.getMember(requestUsername);

        Article findArticle = articleRepository.findById(articleId)
                .orElseThrow(() -> new ArticleNotFoundException("게시글을 찾을 수 없습니다. 게시글 ID: " + articleId));

        if (!findMember.getUsername().equals(findArticle.getMember().getUsername()) &&
                !findMember.getMemberId().equals(findArticle.getMember().getMemberId())) {
            throw new ArticleDeletionNotAllowedException("게시글 삭제가 불가능 합니다. 게시글 ID: " + articleId);
        }

        log.info("User : {} deleted article : {}", requestUsername, articleId);

        articleRepository.deleteById(articleId);
    }

}
