package portfolio.LibraryLoan.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import portfolio.LibraryLoan.dto.request.JoinDto;
import portfolio.LibraryLoan.dto.response.MemberDto;
import portfolio.LibraryLoan.entity.Member;
import portfolio.LibraryLoan.entity.Role;
import portfolio.LibraryLoan.enums.RoleType;
import portfolio.LibraryLoan.exception.DuplicationUserException;
import portfolio.LibraryLoan.exception.UserNotFoundException;
import portfolio.LibraryLoan.repository.MemberRepository;
import portfolio.LibraryLoan.repository.RoleRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private MemberService memberService;

    @Autowired
    private BCryptPasswordEncoder encoder;

    private Role userRole;

    @BeforeEach
    void setup() {
        userRole = Role.builder()
                .roleName(RoleType.ROLE_USER)
                .build();
        roleRepository.save(userRole);
    }

    @AfterEach
    void cleanUp() {
        memberRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @DisplayName("멤버 회원가입 테스트")
    @Test
    void joinMember() {
        // given
        JoinDto joinDto = new JoinDto("TestUser", "password", "testuser@example.com", 25);

        // when
        memberService.joinMember(joinDto);

        // then
        Member member = memberRepository.findByUsername("TestUser")
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
        assertThat(member.getUsername()).isEqualTo("TestUser");
        assertThat(encoder.matches("password", member.getPassword())).isTrue();
        assertThat(member.getEmail()).isEqualTo("testuser@example.com");
        assertThat(member.getAge()).isEqualTo(25);
        assertThat(member.getRole().getRoleName()).isEqualTo(RoleType.ROLE_USER);
    }

    @DisplayName("중복 회원 가입 테스트")
    @Test
    void joinMember_DuplicateUsername() {
        // given
        JoinDto joinDto = new JoinDto("TestUser", "password", "testuser@example.com", 25);
        memberService.joinMember(joinDto);

        // when & then
        assertThrows(DuplicationUserException.class, () -> {
            memberService.joinMember(joinDto);
        });
    }

    @DisplayName("회원 조회 테스트")
    @Test
    void getMember() {
        // given
        JoinDto joinDto = new JoinDto("TestUser", "password", "testuser@example.com", 25);
        memberService.joinMember(joinDto);

        // when
        Member member = memberService.getMember("TestUser");

        // then
        assertThat(member.getUsername()).isEqualTo("TestUser");
        assertThat(member.getEmail()).isEqualTo("testuser@example.com");
        assertThat(member.getAge()).isEqualTo(25);
    }

    @DisplayName("존재하지 않는 회원 조회 테스트")
    @Test
    void getMember_UserNotFound() {
        // when & then
        assertThrows(UserNotFoundException.class, () -> {
            memberService.getMember("NonUser");
        });
    }
}