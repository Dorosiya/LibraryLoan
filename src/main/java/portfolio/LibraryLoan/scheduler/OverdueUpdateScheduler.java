package portfolio.LibraryLoan.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import portfolio.LibraryLoan.service.BookStatusService;
import portfolio.LibraryLoan.service.LoanService;
import portfolio.LibraryLoan.service.ReservationService;

@Slf4j
@RequiredArgsConstructor
@Component
public class OverdueUpdateScheduler {

    private final LoanService loanService;
    private final ReservationService reservationService;
    private final BookStatusService bookStatusService;

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void updateOverdue() {
        try {
            loanService.updateOverdueLoan();
            bookStatusService.updateOverdueBookStatus();
            reservationService.updateOverdueReservation();
            log.info("모든 연체 상태 업데이트 작업이 성공적으로 완료 되었습니다.");
        } catch (Exception e) {
            log.error("연체 업데이트 작업 중 오류 발생, 모든 작업 롤백 예정", e);
        }
    }

    /*@Scheduled(cron = "0 0 0 * * ?")
    public void updateOverdueLoan() {
        try {
            loanService.updateOverdueLoan();
        } catch (Exception e) {
            log.error("스케줄러 updateOverdueLoan 작업 실패", e);
        }
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void updateOverdueBookStatus() {
        try {
            bookStatusService.updateOverdueBookStatus();
        } catch (Exception e) {
            log.error("스케줄러 updateOverdueBookStatus 작업 실패", e);
        }
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void updateOverdueReservation() {
        try {
            reservationService.updateOverdueReservation();
        } catch (Exception e) {
            log.error("스케줄러 updateOverdueReservation 작업 실패", e);
        }
    }*/

}
