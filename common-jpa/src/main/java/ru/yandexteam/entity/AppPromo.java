package ru.yandexteam.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Setter
@Getter
@EqualsAndHashCode(exclude = "id")
@Builder
@Entity
@Table(name = "app_promo")
@NoArgsConstructor
@AllArgsConstructor
public class AppPromo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String username;
    @Column(unique = true)
    private String promoValue;
    private Integer promoMonth;
    private Integer promoYear;
}
