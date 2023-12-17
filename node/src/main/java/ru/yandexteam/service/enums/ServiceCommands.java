package ru.yandexteam.service.enums;

public enum ServiceCommands {
    HELP("/help"),
    REGISTRATION("/registration"),
    CANCEL("/cancel"),
    START("/start"),
    GET_PROMO("/get_promo");

    private final String value;

    ServiceCommands(String cmd) {
        this.value = cmd;
    }


    @Override
    public String toString() {
        return value;
    }

    public static ServiceCommands fromValue(String v) {
        for(ServiceCommands c : ServiceCommands.values()){
            if (c.value.equals(v)){
                return c;
            }
        }
        return null;
    }
}
