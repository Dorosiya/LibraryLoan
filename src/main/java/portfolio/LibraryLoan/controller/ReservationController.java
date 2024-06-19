package portfolio.LibraryLoan.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import portfolio.LibraryLoan.dto.request.BookIdDto;
import portfolio.LibraryLoan.dto.response.ReservationDto;
import portfolio.LibraryLoan.security.CustomUserDetails;
import portfolio.LibraryLoan.service.ReservationService;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping("/reservation")
    public ResponseEntity<Map<String, String>> ReservationBook(
            @RequestBody BookIdDto bookId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        reservationService.reservation(customUserDetails.getUsername(), bookId.getBookId());

        return new ResponseEntity<>(Map.of("message", "Ok"), HttpStatus.OK);
    }

    @GetMapping("/reservation")
    public ResponseEntity<List<ReservationDto>> getReservationBook(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        List<ReservationDto> reservationBook = reservationService.getReservations(customUserDetails.getUsername());

        return new ResponseEntity<>(reservationBook, HttpStatus.OK);
    }

}
