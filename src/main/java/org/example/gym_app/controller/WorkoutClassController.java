package org.example.gym_app.controller;

import lombok.RequiredArgsConstructor;
import org.example.gym_app.model.WorkoutClass;
import org.example.gym_app.service.WorkoutClassService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/classes")
@RequiredArgsConstructor
public class WorkoutClassController {

    private final WorkoutClassService workoutClassService;

    @GetMapping
    public List<WorkoutClass> getAllClasses() {
        return workoutClassService.getAllClasses();
    }

    @PostMapping
    public WorkoutClass addClass(@RequestBody WorkoutClass workoutClass) {
        return workoutClassService.addClass(workoutClass);
    }
}
