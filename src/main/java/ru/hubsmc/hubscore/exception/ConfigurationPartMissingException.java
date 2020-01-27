package ru.hubsmc.hubscore.exception;

public class ConfigurationPartMissingException extends Exception {

    public ConfigurationPartMissingException(String msg) {
        super(msg);
    }

    public ConfigurationPartMissingException(String msg, Throwable err) {
        super(msg, err);
    }

}
