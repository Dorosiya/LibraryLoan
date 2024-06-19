package portfolio.LibraryLoan.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum RoleType {

    ROLE_USER("유저"),
    ROLE_ADMIN("관리자");

    private final String roleName;
}
