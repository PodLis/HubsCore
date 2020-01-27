package ru.hubsmc.hubscore.exception;

public class IncorrectConfigurationException extends Exception {

    public IncorrectConfigurationException(String msg) {
        super(msg);
    }

    public IncorrectConfigurationException(String msg, Throwable err) {
        super(msg, err);
    }

}
