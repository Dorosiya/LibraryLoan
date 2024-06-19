package portfolio.LibraryLoan.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import portfolio.LibraryLoan.dto.request.ArticleRequestDto;
import portfolio.LibraryLoan.dto.request.ArticleSearchCond;
import portfolio.LibraryLoan.dto.response.ArticleDto;
import portfolio.LibraryLoan.dto.response.CommentDto;
import portfolio.LibraryLoan.security.CustomUserDetails;
import portfolio.LibraryLoan.service.ArticleService;
import portfolio.LibraryLoan.service.CommentService;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class ArticleController {

    private final ArticleService articleService;
    private final CommentService commentService;

    // 게시글 생성
    @PostMapping("/article")
    public ResponseEntity<Map<String, String>> createArticle(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                             @RequestBody ArticleRequestDto articleCreateDto) {

        String username = customUserDetails.getUsername();

        String createTitle = articleCreateDto.getTitle();
        String createContent = articleCreateDto.getContent();

        articleService.createArticle(username, createTitle, createContent);

        return ResponseEntity.ok(Map.of("message", "성공"));
    }

    // 게시글 여러개 조회
    @GetMapping("/article")
    public ResponseEntity<Page<ArticleDto>> SearchArticles(ArticleSearchCond cond, Pageable pageable) {

        Page<ArticleDto> findArticleDto = articleService.findArticles(cond, pageable);

        return ResponseEntity.ok(findArticleDto);
    }

    // 특정 게시글 조회
    @GetMapping("/article/{articleId}")
    public ResponseEntity<searchArticleWithCommentsData> searchArticleWithComments(@PathVariable("articleId") Long articleId) {
        ArticleDto articleSearchDto = articleService.findArticle(articleId);
        List<CommentDto> commentSearchDto = commentService.findCommentInArticle(articleId);

        searchArticleWithCommentsData articleData = new searchArticleWithCommentsData(
                articleSearchDto, commentSearchDto);

        return new ResponseEntity<>(articleData, HttpStatus.OK);
    }

    // 게시글 수정
    @PatchMapping("/article/{articleId}")
    public ResponseEntity<Map<String, String>> editArticle(@PathVariable("articleId") Long articleId,
                                                             @AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                             @RequestBody ArticleRequestDto dto) {
        String findUsername = customUserDetails.getUsername();

        articleService.editArticle(findUsername, articleId, dto.getTitle(), dto.getContent());
        return new ResponseEntity<>(Map.of("message", "Ok"), HttpStatus.OK);
    }

    // 게시글 삭제
    @DeleteMapping("/article/{articleId}")
    public ResponseEntity<Map<String, String>> deleteArticle(@PathVariable("articleId") Long articleId
            , @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        String requestUsername = customUserDetails.getUsername();

        articleService.deleteArticle(requestUsername, articleId);

        return new ResponseEntity<>(Map.of("message", "ok"), HttpStatus.OK);
    }

    @Data
    @AllArgsConstructor
    static class searchArticleWithCommentsData {
        private ArticleDto articleDto;
        private List<CommentDto> commentDto;
    }
}
