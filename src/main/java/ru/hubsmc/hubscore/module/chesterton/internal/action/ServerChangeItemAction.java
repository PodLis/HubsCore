package ru.hubsmc.hubscore.module.chesterton.internal.action;

import org.bukkit.entity.Player;
import ru.hubsmc.hubscore.PluginUtils;
import ru.hubsmc.hubscore.exception.ServerErrorException;
import ru.hubsmc.hubscore.util.ServerUtils;

import java.io.IOException;

import static ru.hubsmc.hubscore.util.MessageUtils.sendAlreadyThatServerMessage;

public class ServerChangeItemAction extends ItemAction {

    private String server;

    public ServerChangeItemAction(String server) {
        this.server = server;
    }

    @Override
    public void execute(Player player) {
        try {
            try {
                if (ServerUtils.changeServer(player, server)) {
                    sendAlreadyThatServerMessage(player);
                }
            } catch (IOException e) {
                throw new ServerErrorException(PluginUtils.getBungeeServerName(), server);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
