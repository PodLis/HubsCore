package su.hubs.hubscore.module.chesterton.internal;

import org.bukkit.entity.Player;
import su.hubs.hubscore.module.chesterton.internal.menu.ChestertonMenu;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MenuUtils {

    private static Map<String, ChestertonMenu> menuMap;

    static {
        menuMap = new HashMap<>();
    }

    public static void registerMenu(ChestertonMenu menu, String name) {
        menuMap.put(name, menu);
    }

    public static boolean menuExists(String menu) {
        return menuMap.containsKey(menu);
    }

    public static void openMenu(Player player, String menu) {
        menuMap.get(menu).open(player);
    }

    public static Set<String> getAllMenuSet() {
        return menuMap.keySet();
    }

}
