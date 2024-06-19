package portfolio.LibraryLoan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import portfolio.LibraryLoan.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryCustom {
}
