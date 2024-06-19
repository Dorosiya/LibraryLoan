package portfolio.LibraryLoan.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "article")
@Entity
public class Article extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long articleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String title;

    private String content;

    private Long views;

    @OneToMany(mappedBy = "article", cascade = CascadeType.REMOVE)
    private List<Comment> comment = new ArrayList<>();

    @Builder
    private Article(Member member, String title, String content, Long views) {
        this.member = member;
        this.title = title;
        this.content = content;
        this.views = views;
    }

    public void plusViews() {
        this.views++;
    }

    public void editTitleAndContent(String title, String content) {
        this.title = title;
        this.content = content;
    }

}
