package com.marshall.sportbot.service;

import com.marshall.sportbot.entity.UserEntity;
import com.marshall.sportbot.repository.ExerciseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ExerciseService {
    private final ExerciseRepository exerciseRepository;

    public void validatePushUpGoals(UserEntity user) {

    }
}
