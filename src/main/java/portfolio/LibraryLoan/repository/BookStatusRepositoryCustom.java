package portfolio.LibraryLoan.repository;

import java.util.List;

public interface BookStatusRepositoryCustom {

    void updateBookStatusesInBatches(List<Long> loanItemIds, int batchSize, int overdueCode);

}
