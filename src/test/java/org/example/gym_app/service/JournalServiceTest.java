package org.example.gym_app.service;

import org.example.gym_app.model.JournalEntry;
import org.example.gym_app.model.JournalExercise;
import org.example.gym_app.model.User;
import org.example.gym_app.repository.JournalEntryRepository;
import org.example.gym_app.repository.JournalExerciseRepository;
import org.example.gym_app.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class JournalServiceTest {

    @Autowired
    private JournalService journalService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JournalEntryRepository journalEntryRepository;

    @Autowired
    private JournalExerciseRepository journalExerciseRepository;

    private User user;

    @BeforeEach
    void setup() {
        journalExerciseRepository.deleteAll();
        journalEntryRepository.deleteAll();
        userRepository.deleteAll();

        User baseUser = new User();
        baseUser.setUsername("journal");
        baseUser.setPassword("secret");
        baseUser.setRole("USER");
        user = userRepository.save(baseUser);
    }

    @Test
    @DisplayName("Użytkownik może tworzyć, edytować i usuwać wpis dziennika")
    void shouldHandleEntryLifecycle() {
        JournalEntry entry = journalService.createEntry(user.getId(), "Push day", LocalDate.now(), "ciężko");
        assertThat(entry.getId()).isNotNull();

        journalService.updateEntry(entry.getId(), user.getId(), "Push day updated",
                entry.getSessionDate(), "lżejszy trening");

        JournalEntry fetched = journalService.getEntryForUser(entry.getId(), user.getId());
        assertThat(fetched.getTitle()).isEqualTo("Push day updated");

        journalService.deleteEntry(entry.getId(), user.getId());
        assertThat(journalEntryRepository.count()).isZero();
    }

    @Test
    @DisplayName("Ćwiczenia można dodawać, edytować i usuwać w obrębie wpisu")
    void shouldManageExercises() {
        JournalEntry entry = journalService.createEntry(user.getId(), "Leg day", LocalDate.now(), null);

        JournalExercise exercise = new JournalExercise();
        exercise.setName("Przysiad");
        exercise.setSetsCount(4);
        exercise.setReps(8);
        exercise.setWeightKg(90);

        JournalExercise saved = journalService.addExercise(entry.getId(), user.getId(), exercise);
        assertThat(saved.getId()).isNotNull();

        JournalExercise updatePayload = new JournalExercise();
        updatePayload.setName("Przysiad tylni");
        updatePayload.setSetsCount(5);
        updatePayload.setReps(6);
        updatePayload.setWeightKg(100);
        updatePayload.setNotes("Ciężko");

        journalService.updateExercise(saved.getId(), user.getId(), updatePayload);

        JournalExercise fetched = journalService.getExerciseForUser(saved.getId(), user.getId());
        assertThat(fetched.getName()).isEqualTo("Przysiad tylni");
        assertThat(fetched.getWeightKg()).isEqualTo(100);

        journalService.deleteExercise(saved.getId(), user.getId());
        assertThat(journalExerciseRepository.count()).isZero();
    }

    @Test
    @DisplayName("Nie można edytować wpisu innego użytkownika")
    void shouldRejectForeignAccess() {
        JournalEntry entry = journalService.createEntry(user.getId(), "FBW", LocalDate.now(), null);

        User stranger = new User();
        stranger.setUsername("stranger");
        stranger.setPassword("pwd");
        stranger.setRole("USER");
        stranger = userRepository.save(stranger);

        User finalStranger = stranger;
        assertThatThrownBy(() -> journalService.getEntryForUser(entry.getId(), finalStranger.getId()))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Lista wpisów może być sortowana po dacie")
    void shouldReturnEntriesInSelectedOrder() {
        JournalEntry older = journalService.createEntry(user.getId(), "Stary", LocalDate.now().minusDays(2), null);
        JournalEntry newer = journalService.createEntry(user.getId(), "Nowy", LocalDate.now(), null);

        var desc = journalService.getEntriesForUser(user.getId(), Sort.Direction.DESC);
        var asc = journalService.getEntriesForUser(user.getId(), Sort.Direction.ASC);

        assertThat(desc.get(0).getId()).isEqualTo(newer.getId());
        assertThat(asc.get(0).getId()).isEqualTo(older.getId());
    }
}

