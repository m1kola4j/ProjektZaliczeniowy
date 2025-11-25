package org.example.gym_app.controller;

import lombok.RequiredArgsConstructor;
import org.example.gym_app.model.Booking;
import org.example.gym_app.service.BookingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public Booking createBooking(@RequestParam Long userId,
                                 @RequestParam Long workoutClassId) {
        return bookingService.createBooking(userId, workoutClassId);
    }

    @GetMapping("/user/{userId}")
    public List<Booking> getBookingsForUser(@PathVariable Long userId) {
        return bookingService.getBookingsForUser(userId);
    }
}
