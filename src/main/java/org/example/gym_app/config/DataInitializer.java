package org.example.gym_app.config;

import lombok.RequiredArgsConstructor;
import org.example.gym_app.model.User;
import org.example.gym_app.model.WorkoutClass;
import org.example.gym_app.repository.UserRepository;
import org.example.gym_app.repository.WorkoutClassRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final WorkoutClassRepository workoutClassRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        // jeśli nie ma usera o nazwie "user" -> tworzymy
        if (!userRepository.existsByUsername("user")) {
            User user = new User();
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setRole("USER");
            userRepository.save(user);
        }

        // jeśli nie ma usera "admin" -> tworzymy admina
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ADMIN");
            userRepository.save(admin);
        }

        // jeśli nie ma żadnych zajęć -> tworzymy jedne przykładowe
        if (workoutClassRepository.count() == 0) {
            WorkoutClass cardio = new WorkoutClass();
            cardio.setName("Cardio");
            cardio.setDescription("Trening wytrzymałościowy");
            cardio.setStartTime(LocalDateTime.of(2025, 1, 10, 10, 0));
            cardio.setCapacity(5);
            workoutClassRepository.save(cardio);

            System.out.println(">>> Dodano przykładowe zajęcia: Cardio (id=" + cardio.getId() + ")");
        }

        System.out.println(">>> Inicjalizacja danych zakończona.");
    }
}
