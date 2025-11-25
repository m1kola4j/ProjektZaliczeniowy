package org.example.gym_app.controller;

import lombok.RequiredArgsConstructor;
import org.example.gym_app.service.BookingService;
import org.example.gym_app.service.WorkoutClassService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class WebController {

    private final WorkoutClassService workoutClassService;
    private final BookingService bookingService;

    @GetMapping("/classes")
    public String showClasses(Model model) {
        model.addAttribute("classes", workoutClassService.getAllClasses());
        return "classes";
    }

    @GetMapping("/bookings")
    public String showBookings(Model model) {
        Long userId = 1L;
        model.addAttribute("bookings", bookingService.getBookingsForUser(userId));
        return "bookings";
    }

    @PostMapping("/bookings/create")
    public String createBookingFromClasses(@RequestParam("classId") Long classId,
                                           RedirectAttributes redirectAttributes) {
        Long userId = 1L; // na sztywno

        try {
            bookingService.createBooking(userId, classId);
            // komunikat sukcesu pokażemy na stronie rezerwacji
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Zapisano na wybrane zajęcia."
            );
            return "redirect:/bookings";
        } catch (RuntimeException e) {
            // np. "Już jesteś zapisany na te zajęcia!"
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    e.getMessage()
            );
            // wracamy na listę zajęć z ładnym komunikatem zamiast błędu 500
            return "redirect:/classes";
        }
    }
}
