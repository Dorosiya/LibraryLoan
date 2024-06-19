package portfolio.LibraryLoan.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import portfolio.LibraryLoan.dto.request.ArticleSearchCond;
import portfolio.LibraryLoan.dto.response.ArticleDto;
import portfolio.LibraryLoan.entity.Article;
import portfolio.LibraryLoan.entity.Member;
import portfolio.LibraryLoan.entity.Role;
import portfolio.LibraryLoan.enums.RoleType;
import portfolio.LibraryLoan.exception.ArticleDeletionNotAllowedException;
import portfolio.LibraryLoan.exception.ArticleNotFoundException;
import portfolio.LibraryLoan.exception.NotAllowedException;
import portfolio.LibraryLoan.repository.ArticleRepository;
import portfolio.LibraryLoan.repository.MemberRepository;
import portfolio.LibraryLoan.repository.RoleRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
class ArticleServiceTest {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    ArticleService articleService;

    private Member testMember;
    private Member anotherMember;

    @BeforeEach
    void setup() {
        Role userRole = Role.builder()
                .roleName(RoleType.ROLE_USER)
                .build();

        roleRepository.save(userRole);

        testMember = Member.builder()
                .role(userRole)
                .username("NewUser")
                .password("2345")
                .age(31)
                .email("NewUser@newUSer.com")
                .build();

        memberRepository.save(testMember);

        anotherMember = Member.builder()
                .role(userRole)
                .username("AnotherUser")
                .password("password")
                .age(30)
                .email("anotheruser@example.com")
                .build();
        memberRepository.save(anotherMember);
    }

    @DisplayName("게시글 생성 테스트")
    @Test
    void createArticle() {
        // given
        String title = "Test Title";
        String content = "Test Content";

        // when
        articleService.createArticle(testMember.getUsername(), title, content);

        // then
        Article savedArticle = articleRepository.findAll().get(0);
        assertThat(savedArticle.getTitle()).isEqualTo(title);
        assertThat(savedArticle.getContent()).isEqualTo(content);
        assertThat(savedArticle.getMember().getUsername()).isEqualTo(testMember.getUsername());
    }

    @AfterEach
    void cleanUp() {
        articleRepository.deleteAll();
        memberRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @DisplayName("게시글들 조회 테스트")
    @Test
    void findArticles() {
        // given
        Article article1 = Article.builder()
                .member(testMember)
                .title("Test Title 1")
                .content("Test Content 1")
                .views(0L)
                .build();

        Article article2 = Article.builder()
                .member(testMember)
                .title("Test Title 2")
                .content("Test Content 2")
                .views(0L)
                .build();

        articleRepository.save(article1);
        articleRepository.save(article2);

        ArticleSearchCond cond = new ArticleSearchCond();
        cond.setTitle("Test Title");

        PageRequest pageable = PageRequest.of(0, 10);

        // when
        Page<ArticleDto> result = articleService.findArticles(cond, pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).extracting("title")
                .containsExactlyInAnyOrder("Test Title 1", "Test Title 2");
    }

    @DisplayName("게시글 조회 테스트")
    @Test
    void findArticle() {
        // given
        Article article = Article.builder()
                .member(testMember)
                .title("Test Title")
                .content("Test Content")
                .views(0L)
                .build();
        articleRepository.save(article);

        // when
        ArticleDto foundArticle = articleService.findArticle(article.getArticleId());

        // then
        assertThat(foundArticle.getTitle()).isEqualTo(article.getTitle());
        assertThat(foundArticle.getContent()).isEqualTo(article.getContent());
    }

    @DisplayName("게시글 수정 테스트")
    @Test
    void editArticle() {
        // given
        Article article = Article.builder()
                .member(testMember)
                .title("Test Title")
                .content("Test Content")
                .views(0L)
                .build();
        articleRepository.save(article);

        String newTitle = "Updated Title";
        String newContent = "Updated Content";

        // when
        articleService.editArticle(testMember.getUsername(), article.getArticleId(), newTitle, newContent);

        // then
        Article updatedArticle = articleRepository.findById(article.getArticleId()).get();
        assertThat(updatedArticle.getTitle()).isEqualTo(newTitle);
        assertThat(updatedArticle.getContent()).isEqualTo(newContent);
    }

    @DisplayName("게시글 삭제 테스트")
    @Test
    void deleteArticle() {
        // given
        Article article = Article.builder()
                .member(testMember)
                .title("Test Title")
                .content("Test Content")
                .views(0L)
                .build();
        articleRepository.save(article);

        // when
        articleService.deleteArticle(testMember.getUsername(), article.getArticleId());

        // then
        assertThrows(ArticleNotFoundException.class, () -> {
            articleService.findArticle(article.getArticleId());
        });
    }

    @DisplayName("게시글 다른 사용자 수정 테스트")
    @Test
    void editArticle_NotAllowed() {
        // given
        Article article = Article.builder()
                .member(testMember)
                .title("Test Title")
                .content("Test Content")
                .views(0L)
                .build();
        articleRepository.save(article);

        // when & then
        assertThrows(NotAllowedException.class, () -> {
            articleService.editArticle(anotherMember.getUsername(), article.getArticleId(), "New Title", "New Content");
        });
    }

    @DisplayName("게시글 다른 사용자 삭제 테스트")
    @Test
    void deleteArticle_NotAllowed() {
        // given
        Article article = Article.builder()
                .member(testMember)
                .title("Test Title")
                .content("Test Content")
                .views(0L)
                .build();
        articleRepository.save(article);

        // when & then
        assertThrows(ArticleDeletionNotAllowedException.class, () -> {
            articleService.deleteArticle(anotherMember.getUsername(), article.getArticleId());
        });
    }
}