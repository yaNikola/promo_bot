package ru.yandexteam.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandexteam.entity.AppDocument;
import ru.yandexteam.entity.AppPromo;

public interface AppPromoDAO extends JpaRepository<AppPromo, Long> {
    AppPromo findAppPromoByUsernameAndPromoMonthAndPromoYear(String username,
                                                             Integer promoMonth, Integer promoYear);
}
