package ru.hubsmc.hubscore.exception;

public class ServerErrorException extends Exception {

    public ServerErrorException(String msg) {
        super(msg);
    }

    public ServerErrorException(String msg, Throwable err) {
        super(msg, err);
    }

}
