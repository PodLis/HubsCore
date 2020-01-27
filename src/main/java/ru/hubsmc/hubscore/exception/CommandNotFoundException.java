package ru.hubsmc.hubscore.exception;

public class CommandNotFoundException extends Exception {

    public CommandNotFoundException(String msg) {
        super(msg);
    }

    public CommandNotFoundException(String msg, Throwable err) {
        super(msg, err);
    }

}
