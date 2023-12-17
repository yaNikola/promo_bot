package ru.yandexteam.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandexteam.entity.BinaryContent;

public interface BinaryContentDAO extends JpaRepository<BinaryContent, Long> {
}
