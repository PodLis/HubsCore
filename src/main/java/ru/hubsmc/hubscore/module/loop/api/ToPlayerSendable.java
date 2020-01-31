package ru.hubsmc.hubscore.module.loop.api;

import org.bukkit.entity.Player;

import java.util.Collection;

public interface ToPlayerSendable {
    void send(Collection<? extends Player> players);
}
