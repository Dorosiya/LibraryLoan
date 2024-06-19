package portfolio.LibraryLoan.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import portfolio.LibraryLoan.dto.response.ComplexCountDto;
import portfolio.LibraryLoan.security.CustomUserDetails;
import portfolio.LibraryLoan.service.ComplexDataService;

@RequestMapping("/api")
@RequiredArgsConstructor
@Controller
public class ComplexDataController {

    private final ComplexDataService complexDataService;

    @GetMapping("/basic")
    public ResponseEntity<ComplexCountDto> findComplex(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        ComplexCountDto complex = complexDataService.getLoanAndReservationCount(customUserDetails.getUsername());

        return new ResponseEntity<>(complex, HttpStatus.OK);
    }

}
