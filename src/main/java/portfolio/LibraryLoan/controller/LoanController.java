package portfolio.LibraryLoan.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import portfolio.LibraryLoan.dto.request.BookIdDto;
import portfolio.LibraryLoan.dto.response.LoanBookDto;
import portfolio.LibraryLoan.security.CustomUserDetails;
import portfolio.LibraryLoan.service.LoanService;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class LoanController {

    private final LoanService loanService;

    @GetMapping("/loan")
    public ResponseEntity<List<LoanBookDto>> getLoan(@AuthenticationPrincipal CustomUserDetails customUserDetails) {

        List<LoanBookDto> LoanBookDto = loanService.getLoanBooks(customUserDetails.getUsername());

        return new ResponseEntity<>(LoanBookDto, HttpStatus.OK);
    }

    @PostMapping("/loan")
    public ResponseEntity<Map<String, String>> createLoan(
            @RequestBody BookIdDto dto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {

        loanService.createLoan(customUserDetails.getUsername(), dto.getBookId());

        return ResponseEntity.ok(Map.of("message", "성공"));
    }

    @PatchMapping("/loan")
    public ResponseEntity<Map<String, String>> returnLoan(@RequestBody BookIdDto bookIdDto,
                                                          @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        loanService.returnBook(customUserDetails.getUsername(), bookIdDto.getBookId());

        return new ResponseEntity<>(Map.of("message", "Ok"), HttpStatus.OK);
    }

}
