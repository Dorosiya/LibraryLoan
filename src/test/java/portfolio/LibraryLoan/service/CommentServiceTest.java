package portfolio.LibraryLoan.service;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import portfolio.LibraryLoan.dto.response.CommentDto;
import portfolio.LibraryLoan.entity.Article;
import portfolio.LibraryLoan.entity.Comment;
import portfolio.LibraryLoan.entity.Member;
import portfolio.LibraryLoan.entity.Role;
import portfolio.LibraryLoan.enums.RoleType;
import portfolio.LibraryLoan.exception.CommentNotFoundException;
import portfolio.LibraryLoan.exception.NotAllowedException;
import portfolio.LibraryLoan.repository.ArticleRepository;
import portfolio.LibraryLoan.repository.CommentRepository;
import portfolio.LibraryLoan.repository.MemberRepository;
import portfolio.LibraryLoan.repository.RoleRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class CommentServiceTest {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CommentService commentService;

    @Autowired
    private EntityManager entityManager;

    private Role userRole;
    private Member testMember;
    private Member anotherMember;
    private Article testArticle;

    @BeforeEach
    void setup() {
        userRole = Role.builder()
                .roleName(RoleType.ROLE_USER)
                .build();
        roleRepository.save(userRole);

        testMember = Member.builder()
                .role(userRole)
                .username("TestUser" + System.currentTimeMillis())
                .password("password")
                .age(25)
                .email("testuser" + System.currentTimeMillis() + "@example.com")
                .build();
        memberRepository.save(testMember);

        anotherMember = Member.builder()
                .role(userRole)
                .username("AnotherUser" + System.currentTimeMillis())
                .password("password")
                .age(30)
                .email("anotheruser" + System.currentTimeMillis() + "@example.com")
                .build();
        memberRepository.save(anotherMember);

        testArticle = Article.builder()
                .member(testMember)
                .title("Test Title")
                .content("Test Content")
                .views(0L)
                .build();
        articleRepository.save(testArticle);
    }

    @AfterEach
    void cleanUp() {
        commentRepository.deleteAll();
        articleRepository.deleteAll();
        memberRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @DisplayName("게시글에 딸려있는 댓글 조회 테스트")
    @Test
    void findCommentInArticle() {
        // given
        Comment comment = Comment.builder()
                .article(testArticle)
                .member(testMember)
                .content("Test Comment")
                .build();
        commentRepository.save(comment);

        // when
        List<CommentDto> comments = commentService.findCommentInArticle(testArticle.getArticleId());

        // then
        assertThat(comments).hasSize(1);
        assertThat(comments.get(0).getContent()).isEqualTo("Test Comment");
    }

    @DisplayName("댓글 추가 테스트")
    @Test
    void createComment() {
        // given
        String content = "New Comment";

        // when
        commentService.createComment(testMember.getUsername(), testArticle.getArticleId(), content);

        // then
        List<Comment> comments = commentRepository.findAll();
        assertThat(comments).hasSize(1);
        assertThat(comments.get(0).getContent()).isEqualTo(content);
    }

    @DisplayName("댓글 수정 테스트")
    @Test
    void editComment() {
        // given
        Comment comment = Comment.builder()
                .article(testArticle)
                .member(testMember)
                .content("Original Comment")
                .build();
        commentRepository.save(comment);

        String updatedContent = "Updated Comment";

        // when
        commentService.editComment(testMember.getUsername(), comment.getCommentId(), updatedContent);

        // then
        Comment updatedComment = commentRepository.findById(comment.getCommentId())
                .orElseThrow(() -> new CommentNotFoundException("댓글을 찾을 수 없습니다. 댓글 ID: " + comment.getCommentId()));
        assertThat(updatedComment.getContent()).isEqualTo(updatedContent);
    }

    @DisplayName("댓글 다른 사용자 수정 테스트")
    @Test
    void editComment_NotAllowed() {
        // given
        Comment comment = Comment.builder()
                .article(testArticle)
                .member(testMember)
                .content("Original Comment")
                .build();
        commentRepository.save(comment);

        String updatedContent = "Updated Comment";

        // when & then
        assertThrows(NotAllowedException.class, () -> {
            commentService.editComment(anotherMember.getUsername(), comment.getCommentId(), updatedContent);
        });
    }

    @DisplayName("댓글 삭제 테스트")
    @Test
    void deleteComment() {
        // given
        Comment comment = Comment.builder()
                .article(testArticle)
                .member(testMember)
                .content("Test Comment")
                .build();
        commentRepository.save(comment);

        // when
        commentService.deleteComment(testMember.getUsername(), comment.getCommentId());

        entityManager.flush();
        entityManager.clear();

        // then
        assertThrows(CommentNotFoundException.class, () -> {
            commentRepository.findById(comment.getCommentId())
                    .orElseThrow(() -> new CommentNotFoundException("댓글을 찾을 수 없습니다. 댓글 ID: " + comment.getCommentId()));
        });
    }

    @Test
    void deleteComment_NotAllowed() {
        // given
        Comment comment = Comment.builder()
                .article(testArticle)
                .member(testMember)
                .content("Test Comment")
                .build();
        commentRepository.save(comment);

        // when & then
        assertThrows(NotAllowedException.class, () -> {
            commentService.deleteComment(anotherMember.getUsername(), comment.getCommentId());
        });
    }

}