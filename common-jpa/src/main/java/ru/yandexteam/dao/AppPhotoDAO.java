package ru.yandexteam.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandexteam.entity.AppDocument;
import ru.yandexteam.entity.AppPhoto;

public interface AppPhotoDAO extends JpaRepository<AppPhoto, Long> {
}
