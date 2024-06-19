package portfolio.LibraryLoan.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import portfolio.LibraryLoan.dto.request.CommentCreateDto;
import portfolio.LibraryLoan.dto.request.CommentDeleteDto;
import portfolio.LibraryLoan.dto.request.CommentUpdateDto;
import portfolio.LibraryLoan.security.CustomUserDetails;
import portfolio.LibraryLoan.service.CommentService;

import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class CommentController {

    private final CommentService commentService;

    // 댓글 추가
    @PostMapping("/comment")
    public ResponseEntity<Map<String, String>> createComment(
            @RequestBody CommentCreateDto addDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        String username = customUserDetails.getUsername();
        Long articleId = addDto.getArticleId();
        String content = addDto.getContent();

        commentService.createComment(username, articleId, content);

        return new ResponseEntity<>(Map.of("message", "Ok"), HttpStatus.OK);
    }

    // 댓글 수정
    @PatchMapping("/comment/{commentId}")
    public ResponseEntity<Map<String, String>> editComment(@PathVariable("commentId") Long commentId,
                                                           @AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                           @RequestBody CommentUpdateDto dto) {
        String username = customUserDetails.getUsername();
        String content = dto.getContent();

        commentService.editComment(username, commentId, content);

        return new ResponseEntity<>(Map.of("message", "Ok"), HttpStatus.OK);
    }

    // 댓글 삭제
    @DeleteMapping("/comment/{commentId}")
    public ResponseEntity<Map<String, String>> deleteComment(@PathVariable("commentId") Long commentId,
                                                             @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        commentService.deleteComment(customUserDetails.getUsername(), commentId);

        return new ResponseEntity<>(Map.of("message", "Ok"), HttpStatus.OK);
    }

}
