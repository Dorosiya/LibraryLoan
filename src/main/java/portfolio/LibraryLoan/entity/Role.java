package portfolio.LibraryLoan.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import portfolio.LibraryLoan.enums.RoleType;

@Getter
@NoArgsConstructor
@Table(name = "role")
@Entity
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roleId;

    @Enumerated(EnumType.STRING)
    private RoleType roleName;

    @Builder
    private Role(RoleType roleName) {
        this.roleName = roleName;
    }
}
