package portfolio.LibraryLoan.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Data
public class MemberDto {

    private Long memberId;

    private String username;

    private int age;

    private String email;

    @Builder
    private MemberDto(Long memberId, String username, int age, String email, LocalDateTime joinDate) {
        this.memberId = memberId;
        this.username = username;
        this.age = age;
        this.email = email;
    }

}
