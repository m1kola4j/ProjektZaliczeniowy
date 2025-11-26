package org.example.gym_app.controller;

import lombok.RequiredArgsConstructor;
import org.example.gym_app.model.Booking;
import org.example.gym_app.model.User;
import org.example.gym_app.model.WorkoutClass;
import org.example.gym_app.repository.UserRepository;
import org.example.gym_app.service.BookingService;
import org.example.gym_app.service.WorkoutClassService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class WebController {

    private final WorkoutClassService workoutClassService;
    private final BookingService bookingService;
    private final UserRepository userRepository;

    // ---------- STRONA GŁÓWNA ----------
    @GetMapping("/")
    public String home() {
        return "index";
    }

    // ---------- LISTA ZAJĘĆ ----------
    @GetMapping("/classes")
    public String showClasses(Model model) {
        List<WorkoutClass> classes = workoutClassService.getAllClasses();

        Map<Long, Integer> freeSpots = new HashMap<>();
        for (WorkoutClass wc : classes) {
            int free = bookingService.getFreeSpotsForClass(wc.getId());
            freeSpots.put(wc.getId(), free);
        }

        model.addAttribute("classes", classes);
        model.addAttribute("freeSpots", freeSpots);

        return "classes";
    }

    // ---------- ZAPIS NA ZAJĘCIA ----------
    @PostMapping("/classes/{id}/book")
    public String bookClass(@PathVariable("id") Long classId,
                            RedirectAttributes redirectAttributes) {

        Long userId = getCurrentUserId();

        try {
            bookingService.createBooking(userId, classId);
            redirectAttributes.addFlashAttribute("successMessage", "Zapisano na zajęcia!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/classes";
    }

    // ---------- LISTA REZERWACJI ----------
    @GetMapping("/bookings")
    public String showBookings(Model model) {
        Long userId = getCurrentUserId();

        List<Booking> bookings = bookingService.getBookingsForUser(userId);

        Map<Long, Integer> freeSpots = new HashMap<>();
        for (Booking b : bookings) {
            Long classId = b.getWorkoutClass().getId();
            int free = bookingService.getFreeSpotsForClass(classId);
            freeSpots.put(classId, free);
        }

        model.addAttribute("bookings", bookings);
        model.addAttribute("freeSpots", freeSpots);

        return "bookings";
    }

    // ---------- ANULOWANIE REZERWACJI ----------
    @PostMapping("/bookings/cancel/{bookingId}")
    public String cancelBooking(@PathVariable Long bookingId,
                                RedirectAttributes redirectAttributes) {

        Long userId = getCurrentUserId();

        try {
            bookingService.cancelBooking(bookingId, userId);
            redirectAttributes.addFlashAttribute("successMessage", "Rezerwacja została anulowana.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/bookings";
    }

    // ---------- pomocnicze: id aktualnie zalogowanego usera ----------
    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono użytkownika: " + username));

        return user.getId();
    }
}
