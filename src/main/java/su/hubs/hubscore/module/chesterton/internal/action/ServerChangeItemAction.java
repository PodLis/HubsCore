package su.hubs.hubscore.module.chesterton.internal.action;

import org.bukkit.entity.Player;
import su.hubs.hubscore.PluginUtils;
import su.hubs.hubscore.exception.ServerErrorException;
import su.hubs.hubscore.module.chesterton.internal.item.ChestertonItem;
import su.hubs.hubscore.util.ServerUtils;
import su.hubs.hubscore.util.MessageUtils;

import java.io.IOException;

public class ServerChangeItemAction extends ItemAction {

    private String server;

    public ServerChangeItemAction(String server) {
        this.server = server;
    }

    @Override
    public void execute(Player player, ChestertonItem item) {
        try {
            try {
                if (ServerUtils.changeServer(player, server)) {
                    MessageUtils.sendAlreadyThatServerMessage(player);
                }
            } catch (IOException e) {
                throw new ServerErrorException(PluginUtils.getBungeeServerName(), server);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
