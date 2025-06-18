package com.marshall.sportbot.repository;

import com.marshall.sportbot.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findByUserId(Long id);

    List<UserEntity> findAllByShouldSendPushUpNotificationsTrue();
}
