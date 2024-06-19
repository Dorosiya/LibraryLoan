package portfolio.LibraryLoan.enums;

public enum ReservationStatusCode {

    RESERVED(1),    // 사용자가 예약을 하고 대출을 기다리는 상태
    AVAILABLE(2),   // 도서가 반납되어 첫 번째 예약자에게 대출이 가능한 상태
    EXPIRED(3),     // 예약 기간이 지나 예약이 더 이상 유효하지 않은 상태
    CANCELLED(4),   // 예약이 취소된 상태
    COMPLETED(5);   // 예약된 도서가 대출된 상태

    private int value;
    private ReservationStatusCode(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

}
