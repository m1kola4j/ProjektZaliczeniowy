package org.example.gym_app.controller;

import lombok.RequiredArgsConstructor;
import org.example.gym_app.service.BookingService;
import org.example.gym_app.service.WorkoutClassService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class WebController {

    private final WorkoutClassService workoutClassService;
    private final BookingService bookingService;

    // STRONA ZAJĘĆ
    @GetMapping("/classes")
    public String showClasses(Model model) {
        model.addAttribute("classes", workoutClassService.getAllClasses());
        return "classes"; // templates/classes.html
    }

    // MOJE REZERWACJE (userId = 1 na sztywno)
    @GetMapping("/bookings")
    public String showBookings(Model model) {
        Long userId = 1L;
        model.addAttribute("bookings", bookingService.getBookingsForUser(userId));
        return "bookings"; // templates/bookings.html
    }

    // ZAPIS NA ZAJĘCIA (kliknięcie "Zapisz się")
    @PostMapping("/classes/{id}/book")
    public String bookClass(@PathVariable("id") Long workoutClassId,
                            RedirectAttributes redirectAttributes) {

        Long userId = 1L; // później z zalogowanego

        try {
            bookingService.createBooking(userId, workoutClassId);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Zostałeś zapisany na zajęcia.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/classes";
    }
}
