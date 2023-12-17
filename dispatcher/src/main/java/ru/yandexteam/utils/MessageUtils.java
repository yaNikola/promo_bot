package ru.yandexteam.utils;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class MessageUtils {
    public SendMessage generateSendMessageWithText(Update update, String text) {
        var message = update.getMessage();
        var response = new SendMessage();
        response.setChatId(message.getChatId().toString());
        response.setText(text);
        return response;
    }
}
