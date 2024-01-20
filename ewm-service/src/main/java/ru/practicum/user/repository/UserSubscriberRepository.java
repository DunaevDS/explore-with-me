package ru.practicum.user.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.user.model.UserSubscriber;

import java.util.List;

@Repository
public interface UserSubscriberRepository extends JpaRepository<UserSubscriber, Long> {
    UserSubscriber findByUserIdAndSubscriberId(Long userId, Long subscriberId);

    List<UserSubscriber> findByUserId(Long userId);
}