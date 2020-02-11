package ru.hubsmc.hubscore.exception;

import ru.hubsmc.hubscore.util.StringUtils;

public class WorldNotFoundException extends Exception {

    public static final String msg = "World with name '%world%' was not found!";

    public WorldNotFoundException(String world) {
        super(StringUtils.setPlaceholders(msg, "%world%", world));
    }

    public WorldNotFoundException(String world, Throwable err) {
        super(StringUtils.setPlaceholders(msg, "%world%", world), err);
    }

}
