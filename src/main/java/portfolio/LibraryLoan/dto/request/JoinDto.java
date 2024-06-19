package portfolio.LibraryLoan.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

@Data
@AllArgsConstructor
public class JoinDto {

    @NotNull(message = "username을 입력해 주세요")
    @Size(min = 5, message = "최소 5글자 이상 입력해 주세요.")
    private String username;

    @NotNull(message = "password를 입력해 주세요")
    @Size(min = 4, message = "4글자 이상 입력해 주세요")
    private String password;

    private String email;

    @Range(min = 0, max = 150)
    private int age;

}
