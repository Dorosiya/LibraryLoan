package portfolio.LibraryLoan.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import portfolio.LibraryLoan.dto.request.JoinDto;
import portfolio.LibraryLoan.dto.response.MemberDto;
import portfolio.LibraryLoan.security.CustomUserDetails;
import portfolio.LibraryLoan.service.MemberService;

import java.util.Map;

@Slf4j
@RequestMapping("/api")
@RestController
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/member")
    public ResponseEntity<Map<String, String>> joinProcess(@Valid @RequestBody JoinDto  joinDto) {
        log.info("join Proceeding");
        memberService.joinMember(joinDto);
        return ResponseEntity.ok().body(Map.of("message", "ok"));
    }

    @GetMapping("/member")
    public ResponseEntity<MemberDto> getMember(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        MemberDto memberDto = memberService.getMemberDto(customUserDetails.getUsername());

        return new ResponseEntity<>(memberDto, HttpStatus.OK);
    }

}
