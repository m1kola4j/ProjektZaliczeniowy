package org.example.gym_app.controller;

import lombok.RequiredArgsConstructor;
import org.example.gym_app.model.WorkoutClass;
import org.example.gym_app.repository.BookingRepository;
import org.example.gym_app.repository.UserRepository;
import org.example.gym_app.service.WorkoutClassService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final WorkoutClassService workoutClassService;

    // Dashboard
    @GetMapping
    public String adminHome(Model model) {
        long usersCount = userRepository.count();
        long classesCount = workoutClassService.getAllClasses().size();
        long bookingsCount = bookingRepository.count();

        model.addAttribute("usersCount", usersCount);
        model.addAttribute("classesCount", classesCount);
        model.addAttribute("bookingsCount", bookingsCount);

        return "admin/index";
    }

    // Lista zajęć
    @GetMapping("/classes")
    public String listClasses(Model model) {
        List<WorkoutClass> classes = workoutClassService.getAllClasses();
        model.addAttribute("classes", classes);
        return "admin/classes";
    }

    // Formularz dodawania
    @GetMapping("/classes/new")
    public String newClassForm(Model model) {
        model.addAttribute("workoutClass", new WorkoutClass());
        model.addAttribute("formTitle", "Dodaj nowe zajęcia");
        return "admin/class-form";
    }

    // Formularz edycji
    @GetMapping("/classes/{id}/edit")
    public String editClassForm(@PathVariable Long id, Model model) {
        WorkoutClass workoutClass = workoutClassService.getClassById(id);
        model.addAttribute("workoutClass", workoutClass);
        model.addAttribute("formTitle", "Edytuj zajęcia");
        return "admin/class-form";
    }

    // Jeden endpoint do zapisu (dodawanie + edycja)
    @PostMapping("/classes/save")
    public String saveClass(@ModelAttribute("workoutClass") WorkoutClass form,
                            RedirectAttributes redirectAttributes) {

        if (form.getId() != null) {
            // edycja istniejących zajęć
            WorkoutClass existing = workoutClassService.getClassById(form.getId());
            existing.setName(form.getName());
            existing.setDescription(form.getDescription());
            existing.setStartTime(form.getStartTime());
            existing.setCapacity(form.getCapacity());

            workoutClassService.addClass(existing); // save/update
            redirectAttributes.addFlashAttribute("successMessage", "Zajęcia zostały zaktualizowane.");
        } else {
            // nowe zajęcia
            workoutClassService.addClass(form);
            redirectAttributes.addFlashAttribute("successMessage", "Zajęcia zostały dodane.");
        }

        return "redirect:/admin/classes";
    }

    // Usuwanie zajęć
    @PostMapping("/classes/{id}/delete")
    public String deleteClass(@PathVariable Long id,
                              RedirectAttributes redirectAttributes) {
        workoutClassService.deleteClass(id);
        redirectAttributes.addFlashAttribute("successMessage", "Zajęcia zostały usunięte.");
        return "redirect:/admin/classes";
    }
}
