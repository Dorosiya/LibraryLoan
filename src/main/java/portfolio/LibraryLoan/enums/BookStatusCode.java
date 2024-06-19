package portfolio.LibraryLoan.enums;

public enum BookStatusCode {

    AVAILABLE(1),   // 도서가 대출이 가능 상태
    IN_LOAN(2),     // 도서가 대출이 된 상태
    OVERDUE(3),     // 도서가 대출이 되었으나 연체 된 상태
    RESERVED(4),    // 도서가 반납은 되었지만 예약인 상태(다른 사람이 대출불가)
    UNAVAILABLE(5);    // 도서가 사용이 불가하여 대출 불가인 상태

    private int value;
    private BookStatusCode(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

}
