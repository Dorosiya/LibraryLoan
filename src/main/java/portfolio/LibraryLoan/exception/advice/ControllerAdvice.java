package portfolio.LibraryLoan.exception.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import portfolio.LibraryLoan.dto.response.ExResponseDto;
import portfolio.LibraryLoan.exception.*;

@Slf4j
@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(CommentNotFoundException.class)
    public ResponseEntity<ExResponseDto> CommentNotFoundExHandler(CommentNotFoundException e) {
        log.error("CommentNotFoundException : {}", e.getMessage(), e);
        ExResponseDto exDto = new ExResponseDto("fail", e.getMessage());

        return new ResponseEntity<>(exDto, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(NotAllowedException.class)
    public ResponseEntity<ExResponseDto> NotAllowedExHandler(NotAllowedException e) {
        log.error("NotAllowedException : {}", e.getMessage(), e);
        ExResponseDto exDto = new ExResponseDto("fail", e.getMessage());

        return new ResponseEntity<>(exDto, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ArticleDeletionNotAllowedException.class)
    public ResponseEntity<ExResponseDto> ArticleDeletionNotAllowedExHandler(ArticleDeletionNotAllowedException e) {
        log.error("ArticleDeletionNotAllowedException : {}", e.getMessage(), e);
        ExResponseDto exDto = new ExResponseDto("fail", e.getMessage());

        return new ResponseEntity<>(exDto, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ArticleNotFoundException.class)
    public ResponseEntity<ExResponseDto> ArticleNotFoundExHandler(ArticleNotFoundException e) {
        log.error("ArticleNotFoundException : {}", e.getMessage(), e);
        ExResponseDto exDto = new ExResponseDto("fail", e.getMessage());

        return new ResponseEntity<>(exDto, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ReservationNotFoundException.class)
    public ResponseEntity<ExResponseDto> ReservationNotFoundExHandler(ReservationNotFoundException e) {
        log.error("ReservationNotFoundException : {}", e.getMessage(), e);
        ExResponseDto exDto = new ExResponseDto("fail", e.getMessage());

        return new ResponseEntity<>(exDto, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(LoanNotFoundException.class)
    public ResponseEntity<ExResponseDto> LoanNotFoundExHandler(LoanNotFoundException e) {
        log.error("LoanNotFoundException : {}", e.getMessage(), e);
        ExResponseDto exDto = new ExResponseDto("fail", e.getMessage());

        return new ResponseEntity<>(exDto, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicationUserException.class)
    public ResponseEntity<ExResponseDto> DuplicationUserExHandler(DuplicationUserException e) {
        log.error("DuplicationUserException : {}", e.getMessage(), e);
        ExResponseDto exDto = new ExResponseDto("fail", e.getMessage());

        return new ResponseEntity<>(exDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<ExResponseDto> BookNotFoundExHandler(BookNotFoundException e) {
        log.error("BookNotFoundException : {}", e.getMessage(), e);
        ExResponseDto exDto = new ExResponseDto("fail", e.getMessage());

        return new ResponseEntity<>(exDto, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ExResponseDto> UserNotFoundExHandler(UserNotFoundException e) {
        log.error("UserNotFoundException : {}", e.getMessage(), e);
        ExResponseDto exDto = new ExResponseDto("fail", e.getMessage());

        return new ResponseEntity<>(exDto, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ExResponseDto> dataAccessExHandler(DataAccessException e) {
        log.error("Database error : {}", e.getMessage(), e);
        ExResponseDto exDto = new ExResponseDto("error", "데이터베이스 오류가 발생했습니다.");

        return new ResponseEntity<>(exDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExResponseDto> globalExHandler(Exception e) {
        log.error("Unexpected error : {}", e.getMessage(), e);
        ExResponseDto exDto = new ExResponseDto("error", "예상치 못한 오류가 발생했습니다.");

        return new ResponseEntity<>(exDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
