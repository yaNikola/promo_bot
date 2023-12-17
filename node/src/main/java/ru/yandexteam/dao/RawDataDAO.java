package ru.yandexteam.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandexteam.entity.RawData;

public interface RawDataDAO extends JpaRepository<RawData, Long> {
}
