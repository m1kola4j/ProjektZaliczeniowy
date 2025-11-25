package org.example.gym_app.controller;

import lombok.RequiredArgsConstructor;
import org.example.gym_app.service.BookingService;
import org.example.gym_app.service.WorkoutClassService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class WebController {

    private final WorkoutClassService workoutClassService;
    private final BookingService bookingService;

    // 🔹 STRONA GŁÓWNA ("index.html")
    @GetMapping("/")
    public String home() {
        return "index";
    }

    // 🔹 LISTA ZAJĘĆ
    @GetMapping("/classes")
    public String showClasses(Model model) {
        model.addAttribute("classes", workoutClassService.getAllClasses());
        return "classes";
    }

    // 🔹 ZAPIS Z WIDOKU "ZAJĘCIA"
    @PostMapping("/bookings/create")
    public String createBookingFromView(@RequestParam("classId") Long workoutClassId,
                                        RedirectAttributes redirectAttributes) {
        Long userId = 1L; // na sztywno

        try {
            bookingService.createBooking(userId, workoutClassId);
            redirectAttributes.addFlashAttribute("successMessage", "Zapisano na zajęcia!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/classes";
    }

    // 🔹 LISTA REZERWACJI USERA
    @GetMapping("/bookings")
    public String showBookings(Model model) {
        Long userId = 1L; // na sztywno
        model.addAttribute("bookings", bookingService.getBookingsForUser(userId));
        return "bookings";
    }

    // 🔹 ANULOWANIE REZERWACJI
    @PostMapping("/bookings/cancel/{bookingId}")
    public String cancelBooking(@PathVariable Long bookingId,
                                RedirectAttributes redirectAttributes) {
        Long userId = 1L; // na sztywno

        try {
            bookingService.cancelBooking(bookingId, userId);
            redirectAttributes.addFlashAttribute("successMessage", "Rezerwacja została anulowana.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/bookings";
    }
}
