package org.example.gym_app.controller;

import lombok.RequiredArgsConstructor;
import org.example.gym_app.model.JournalEntry;
import org.example.gym_app.model.JournalExercise;
import org.example.gym_app.model.User;
import org.example.gym_app.repository.UserRepository;
import org.example.gym_app.service.JournalService;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/journal")
@RequiredArgsConstructor
public class JournalController {

    private final JournalService journalService;
    private final UserRepository userRepository;

    @GetMapping
    public String listEntries(@RequestParam(name = "sort", defaultValue = "DESC") String sort,
                              Model model) {
        Long userId = currentUserId();
        Sort.Direction direction = "ASC".equalsIgnoreCase(sort) ? Sort.Direction.ASC : Sort.Direction.DESC;
        model.addAttribute("sortDirection", direction.name());
        model.addAttribute("entries", journalService.getEntriesForUser(userId, direction));
        return "journal/list";
    }

    @GetMapping("/new")
    public String newEntryForm(Model model) {
        model.addAttribute("entry", new JournalEntry());
        model.addAttribute("today", LocalDate.now());
        return "journal/entry-form";
    }

    @GetMapping("/{id}/edit")
    public String editEntryForm(@PathVariable Long id, Model model) {
        Long userId = currentUserId();
        JournalEntry entry = journalService.getEntryForUser(id, userId);
        model.addAttribute("entry", entry);
        model.addAttribute("today", entry.getSessionDate());
        return "journal/entry-form";
    }

    @PostMapping
    public String saveEntry(@ModelAttribute("entry") JournalEntry form,
                            RedirectAttributes redirectAttributes) {
        Long userId = currentUserId();
        if (form.getId() == null) {
            journalService.createEntry(userId, form.getTitle(), form.getSessionDate(), form.getNotes());
            redirectAttributes.addFlashAttribute("successMessage", "Dodano wpis dziennika.");
        } else {
            journalService.updateEntry(form.getId(), userId, form.getTitle(), form.getSessionDate(), form.getNotes());
            redirectAttributes.addFlashAttribute("successMessage", "Zaktualizowano wpis.");
        }
        return "redirect:/journal";
    }

    @GetMapping("/{id}")
    public String viewEntry(@PathVariable Long id,
                            @RequestParam(value = "editExerciseId", required = false) Long editExerciseId,
                            Model model) {
        Long userId = currentUserId();
        JournalEntry entry = journalService.getEntryForUser(id, userId);
        model.addAttribute("entry", entry);
        JournalExercise exercise = (editExerciseId != null)
                ? journalService.getExerciseForUser(editExerciseId, userId)
                : new JournalExercise();
        model.addAttribute("exercise", exercise);
        return "journal/entry-detail";
    }

    @PostMapping("/{id}/delete")
    public String deleteEntry(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        journalService.deleteEntry(id, currentUserId());
        redirectAttributes.addFlashAttribute("successMessage", "Usunięto wpis.");
        return "redirect:/journal";
    }

    @PostMapping("/{entryId}/exercises")
    public String addExercise(@PathVariable Long entryId,
                              @ModelAttribute("exercise") JournalExercise exercise,
                              RedirectAttributes redirectAttributes) {
        journalService.addExercise(entryId, currentUserId(), exercise);
        redirectAttributes.addFlashAttribute("successMessage", "Dodano ćwiczenie.");
        return "redirect:/journal/" + entryId;
    }

    @PostMapping("/{entryId}/exercises/{exerciseId}/update")
    public String updateExercise(@PathVariable Long entryId,
                                 @PathVariable Long exerciseId,
                                 @ModelAttribute("exercise") JournalExercise exercise,
                                 RedirectAttributes redirectAttributes) {
        exercise.setId(exerciseId);
        journalService.updateExercise(exerciseId, currentUserId(), exercise);
        redirectAttributes.addFlashAttribute("successMessage", "Zaktualizowano ćwiczenie.");
        return "redirect:/journal/" + entryId;
    }

    @PostMapping("/{entryId}/exercises/{exerciseId}/delete")
    public String deleteExercise(@PathVariable Long entryId,
                                 @PathVariable Long exerciseId,
                                 RedirectAttributes redirectAttributes) {
        journalService.deleteExercise(exerciseId, currentUserId());
        redirectAttributes.addFlashAttribute("successMessage", "Usunięto ćwiczenie.");
        return "redirect:/journal/" + entryId;
    }

    private Long currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono użytkownika: " + username));
        return user.getId();
    }
}

