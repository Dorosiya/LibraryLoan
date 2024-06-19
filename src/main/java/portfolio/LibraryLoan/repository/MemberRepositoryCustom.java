package portfolio.LibraryLoan.repository;

import portfolio.LibraryLoan.entity.Member;

import java.util.Optional;

public interface MemberRepositoryCustom {

    Optional<Member> findByUsername(String username);

    Boolean existsByUsername(String username);
}
