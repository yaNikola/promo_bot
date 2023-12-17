package ru.yandexteam.entity;

import lombok.*;

import javax.persistence.*;

@Setter
@Getter
@EqualsAndHashCode(exclude = "id")
@Builder
@Entity
@Table(name = "app_document")
@NoArgsConstructor
@AllArgsConstructor
public class AppDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String telegramFileId;
    private String docName;
    @OneToOne
    private BinaryContent binaryContent;
    private String mimType;
    private Long fileSize;
}
