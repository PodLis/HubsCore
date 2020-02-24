package ru.hubsmc.hubscore.exception;

public class ServerErrorException extends Exception {

    public ServerErrorException(String from, String to) {
        super("Problems to change server from '" + from + "' to '" + to + "'");
    }

    public ServerErrorException(String from, String to, Throwable err) {
        super("Problems to change server from '" + from + "' to '" + to + "'", err);
    }

}
