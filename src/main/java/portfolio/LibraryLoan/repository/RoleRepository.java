package portfolio.LibraryLoan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import portfolio.LibraryLoan.entity.Role;
import portfolio.LibraryLoan.enums.RoleType;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByRoleName(RoleType roleName);

}
