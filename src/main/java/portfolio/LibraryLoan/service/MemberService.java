package portfolio.LibraryLoan.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
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

import java.time.LocalDateTime;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    private final BCryptPasswordEncoder encoder;

    private final RoleRepository roleRepository;

    public MemberService(MemberRepository memberRepository,
                         BCryptPasswordEncoder encoder,
                         RoleRepository roleRepository) {
        this.memberRepository = memberRepository;
        this.encoder = encoder;
        this.roleRepository = roleRepository;
    }

    @Transactional
    public void joinMember(JoinDto joinDto) {
        String username = joinDto.getUsername();
        String password = joinDto.getPassword();
        String email = joinDto.getEmail();
        int age = joinDto.getAge();

        Boolean isExist = memberRepository.existsByUsername(username);

        if (isExist) {
            throw new DuplicationUserException("사용할 수 없는 Username 입니다. 유저네임 : " + username);
        }

        Role userRole = roleRepository.findByRoleName(RoleType.ROLE_USER)
                .orElseThrow(() -> new IllegalArgumentException("해당 권한을 찾을 수 없습니다."));

        Member joinMember = Member.builder()
                .role(userRole)
                .username(username)
                .password(encoder.encode(password))
                .age(age)
                .email(email)
                .build();

        memberRepository.save(joinMember);
    }

    @Transactional(readOnly = true)
    public Member getMember(String username) {
        return memberRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다. 유저네임 : " + username));
    }

    @Transactional(readOnly = true)
    public MemberDto getMemberDto(String username) {
        Member findMember = getMember(username);

        return MemberDto.builder()
                .memberId(findMember.getMemberId())
                .username(findMember.getUsername())
                .age(findMember.getAge())
                .email(findMember.getEmail())
                .build();
    }

}
