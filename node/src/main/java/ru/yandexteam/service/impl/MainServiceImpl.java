package ru.yandexteam.service.impl;

import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.yandexteam.dao.AppPromoDAO;
import ru.yandexteam.dao.AppUserDAO;
import ru.yandexteam.dao.RawDataDAO;
import ru.yandexteam.entity.*;
import ru.yandexteam.exceptions.UploadFileException;
import ru.yandexteam.service.MainService;
import ru.yandexteam.service.enums.ServiceCommands;

import java.time.LocalDate;

import static ru.yandexteam.entity.enums.UserState.*;
import static ru.yandexteam.service.enums.ServiceCommands.*;

@Service
@Log4j
public class MainServiceImpl implements MainService {

    private final RawDataDAO rawDataDAO;
    private final ProducerServiceImpl producerService;
    private final AppUserDAO appUserDAO;
    private final AppPromoDAO appPromoDAO;
    private final FileServiceImpl fileService;

    public MainServiceImpl(RawDataDAO rawDataDAO, ProducerServiceImpl producerService, AppUserDAO appUserDAO, AppPromoDAO appPromoDAO, FileServiceImpl fileService) {
        this.rawDataDAO = rawDataDAO;
        this.producerService = producerService;
        this.appUserDAO = appUserDAO;
        this.appPromoDAO = appPromoDAO;
        this.fileService = fileService;
    }

    @Override
    public void processTextMessage(Update update) {
        saveRawData(update);

        var appUser = findOrSaveAppUser(update);
        var userState = appUser.getState();
        var text = update.getMessage().getText();
        var output = "";

        var serviceCommand = ServiceCommands.fromValue(text);
        if (CANCEL.equals(serviceCommand)) {
            output = cancelProcess(appUser);
        } else if (BASIC_STATE.equals(userState)) {
            output = processServiceCommand(appUser, serviceCommand);
        } else if (WAITING_FOR_EMAIL_STATE.equals(userState)) {
            //TODO добавить обработку эмейла
        } else {
            log.error("Unknown user state: " + userState);
            output = "Невідома помилка. Введи /cancel і спробуй знову!";
        }

        var chatId = update.getMessage().getChatId();
        sendAnswer(output, chatId);
    }

    @Override
    public void processDocMessage(Update update) {
        saveRawData(update);
        var appuser = findOrSaveAppUser(update);
        var chatId = update.getMessage().getChatId();

        if (isNotAllowToSendContent(chatId, appuser)) {
            return;
        }

        try {
            AppDocument appDocument = fileService.processDoc(update.getMessage());
            var answer = "Документ успішно завантажено!" +
                    "посилання для скачування";
            sendAnswer(answer, chatId);
        } catch (UploadFileException ex) {
            log.error(ex);
            String error = "На жаль, завантаження не вдалося. Повторіть спробу пізніше.";
            sendAnswer(error, chatId);
        }
    }

    private boolean isNotAllowToSendContent(Long chatId, AppUser appuser) {
        var state = appuser.getState();
        if (!appuser.isActive()) {
            var error = "Зареєструйтесь або активуйте свій облік для надсилання контенту.";
            sendAnswer(error, chatId);
            return true;
        } else if (!BASIC_STATE.equals(state)) {
            var error = "Скасуйте поточну команду за допомогою /cancel для надсилання файлів.";
            sendAnswer(error, chatId);
            return true;
        }
        return false;
    }

    @Override
    public void processPhotoMessage(Update update) {
        saveRawData(update);
        var appuser = findOrSaveAppUser(update);
        var chatId = update.getMessage().getChatId();

        if (isNotAllowToSendContent(chatId, appuser)) {
            return;
        }

        try {
            AppPhoto photo = fileService.processPhoto(update.getMessage());
            //TODO добавить сохранение нескольких фото
            var answer = "Фото успішно завантажено! Посилання для скачування ...";
            sendAnswer(answer, chatId);
        } catch (UploadFileException exception) {
            log.error(exception);
            String error = "На жаль, завантаження не вдалося. Повторіть спробу пізніше.";
            sendAnswer(error, chatId);
        }
    }

    private void sendAnswer(String output, Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(output);
        producerService.produceAnswer(sendMessage);
    }

    private String processServiceCommand(AppUser appUser, ServiceCommands cmd) {
        if (REGISTRATION.equals(cmd)) {
            //TODO добавить регистрацию
            return "Тимчасово недоступна";
        } else if (HELP.equals(cmd)) {
            return help();
        } else if (START.equals(cmd)) {
            return "Привіт! Для перегляду доступних команд введи /help";
        } else if (GET_PROMO.equals(cmd)) {
            int currentMonth = LocalDate.now().getMonthValue();
            int currentYear = LocalDate.now().getYear();
            AppPromo promo = appPromoDAO.findAppPromoByUsernameAndPromoMonthAndPromoYear(
                    appUser.getUserName(), currentMonth, currentYear
            );
            if(promo!= null){
                return "Твій промокод " + promo.getPromoValue();
            }
            return "Нажаль, твій промокод не знайдено";

        } else {
            return "Невідома команда. Для перегляду доступних команд введи /help";
        }
    }

    private String help() {
        return "Список доступных команд:\n " +
                "/cancel - скасування виконання поточної команди;\n" +
                "/registration - регистрация пользователя;\n" +
                "/get_promo - видати промокод";
    }

    private String cancelProcess(AppUser appUser) {
        appUser.setState(BASIC_STATE);
        appUserDAO.save(appUser);
        return "Команда скасована.";
    }

    private AppUser findOrSaveAppUser(Update update) {
        User telegramUser = update.getMessage().getFrom();
        AppUser persistentUser = appUserDAO.findAppUserByTelegramUserId(telegramUser.getId());

        if (persistentUser == null) {
            AppUser transientUser = AppUser.builder()
                    .telegramUserId(telegramUser.getId())
                    .firstName(telegramUser.getFirstName())
                    .lastName(telegramUser.getLastName())
                    .userName(telegramUser.getUserName())
                    //TODO изменить значение по умолчанию после добавления регистрации
                    .isActive(true)
                    .state(BASIC_STATE)
                    .build();
            return appUserDAO.save(transientUser);
        }

        return persistentUser;
    }

    private void saveRawData(Update update) {
        RawData rawData = RawData.builder()
                .update(update)
                .build();

        rawDataDAO.save(rawData);
    }
}
