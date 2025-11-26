package org.example.gym_app.service;

import lombok.RequiredArgsConstructor;
import org.example.gym_app.model.JournalEntry;
import org.example.gym_app.model.JournalExercise;
import org.example.gym_app.model.User;
import org.example.gym_app.repository.JournalEntryRepository;
import org.example.gym_app.repository.JournalExerciseRepository;
import org.example.gym_app.repository.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JournalService {

    private final JournalEntryRepository journalEntryRepository;
    private final JournalExerciseRepository journalExerciseRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<JournalEntry> getEntriesForUser(Long userId, Sort.Direction direction) {
        Sort sort = Sort.by(direction, "sessionDate");
        return journalEntryRepository.findAllByUser_Id(userId, sort);
    }

    @Transactional
    public JournalEntry createEntry(Long userId, String title, LocalDate date, String notes) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Użytkownik nie istnieje"));

        JournalEntry entry = new JournalEntry();
        entry.setUser(user);
        entry.setTitle(title);
        entry.setSessionDate(date);
        entry.setNotes(notes);
        return journalEntryRepository.save(entry);
    }

    @Transactional(readOnly = true)
    public JournalEntry getEntryForUser(Long entryId, Long userId) {
        JournalEntry entry = journalEntryRepository.findById(entryId)
                .orElseThrow(() -> new RuntimeException("Wpis nie istnieje"));
        if (!entry.getUser().getId().equals(userId)) {
            throw new RuntimeException("Brak dostępu do wpisu");
        }
        return entry;
    }

    @Transactional
    public JournalEntry updateEntry(Long entryId, Long userId, String title, LocalDate date, String notes) {
        JournalEntry entry = getEntryForUser(entryId, userId);
        entry.setTitle(title);
        entry.setSessionDate(date);
        entry.setNotes(notes);
        return journalEntryRepository.save(entry);
    }

    @Transactional
    public void deleteEntry(Long entryId, Long userId) {
        JournalEntry entry = getEntryForUser(entryId, userId);
        journalEntryRepository.delete(entry);
    }

    @Transactional
    public JournalExercise addExercise(Long entryId, Long userId, JournalExercise payload) {
        JournalEntry entry = getEntryForUser(entryId, userId);
        payload.setEntry(entry);
        return journalExerciseRepository.save(payload);
    }

    @Transactional
    public JournalExercise updateExercise(Long exerciseId, Long userId, JournalExercise payload) {
        JournalExercise exercise = getExerciseForUser(exerciseId, userId);
        exercise.setName(payload.getName());
        exercise.setSetsCount(payload.getSetsCount());
        exercise.setReps(payload.getReps());
        exercise.setWeightKg(payload.getWeightKg());
        exercise.setNotes(payload.getNotes());
        return journalExerciseRepository.save(exercise);
    }

    @Transactional(readOnly = true)
    public JournalExercise getExerciseForUser(Long exerciseId, Long userId) {
        JournalExercise exercise = journalExerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new RuntimeException("Ćwiczenie nie istnieje"));
        if (!exercise.getEntry().getUser().getId().equals(userId)) {
            throw new RuntimeException("Brak dostępu");
        }
        return exercise;
    }

    @Transactional
    public void deleteExercise(Long exerciseId, Long userId) {
        JournalExercise exercise = getExerciseForUser(exerciseId, userId);
        journalExerciseRepository.delete(exercise);
    }
}

