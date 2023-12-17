package ru.yandexteam.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandexteam.entity.AppUser;

public interface AppUserDAO extends JpaRepository<AppUser, Long> {
    AppUser findAppUserByTelegramUserId(Long id);
}
