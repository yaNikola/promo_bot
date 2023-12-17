package ru.yandexteam.service;

import org.telegram.telegrambots.meta.api.objects.Message;
import ru.yandexteam.entity.AppDocument;
import ru.yandexteam.entity.AppPhoto;

public interface FileService {
    AppDocument processDoc(Message telegramMessage);
    AppPhoto processPhoto(Message telegramMessage);
}
