package com.marshall.sportbot.service;

import com.marshall.sportbot.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public void validateUser();
}
