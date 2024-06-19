package portfolio.LibraryLoan.enums;

public enum LoanStatusCode {

    IN_LOAN(1),     // 도서 대출 상태
    OVERDUE(2),     // 도서 연체 상태
    RETURNED(3);    // 도서 반납 상태

    private int value;
    private LoanStatusCode(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

}
