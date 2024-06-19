package portfolio.LibraryLoan.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import portfolio.LibraryLoan.entity.BookStatus;
import portfolio.LibraryLoan.enums.BookStatusCode;
import portfolio.LibraryLoan.enums.LoanStatusCode;
import portfolio.LibraryLoan.exception.BookNotFoundException;
import portfolio.LibraryLoan.exception.BookUnavailableException;
import portfolio.LibraryLoan.repository.BookStatusRepository;
import portfolio.LibraryLoan.repository.LoanRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class BookStatusService {

    private final BookStatusRepository bookStatusRepository;
    private final LoanRepository loanRepository;

    private static final int LOAN_STATUS_OVERDUE = LoanStatusCode.OVERDUE.getValue();
    private static final int BOOK_STATUS_AVAILABLE = BookStatusCode.AVAILABLE.getValue();
    private static final int BOOK_STATUS_RESERVED = BookStatusCode.RESERVED.getValue();
    private static final int BOOK_STATUS_OVERDUE = BookStatusCode.OVERDUE.getValue();


    @Transactional(readOnly = true)
    public void validNonLoanableBook(Long bookId) {
        BookStatus findBookStatus = bookStatusRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("도서의 상태를 찾을 수 없습니다. 도서 ID : " + bookId));

        int status = findBookStatus.getStatus();

        if (status != BOOK_STATUS_AVAILABLE && status != BOOK_STATUS_RESERVED) {
            throw new BookUnavailableException("해당 도서는 대여가 불가능 합니다. 도서 ID : " + bookId);
        }
    }

    @Transactional
    public void updateOverdueBookStatus() {
        // 업데이트 된 상태(연체)의 대출 ID를 조회합니다.
        List<Long> loanBookId = loanRepository.findLoanByOverdueCode(LOAN_STATUS_OVERDUE);

        // 도서 상태를 연체 상태로 변경
        bookStatusRepository.updateBookStatusesInBatches(loanBookId, 500, BOOK_STATUS_OVERDUE);
    }

}
