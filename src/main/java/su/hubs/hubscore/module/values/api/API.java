package su.hubs.hubscore.module.values.api;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import su.hubs.hubscore.PluginUtils;

public class API {

    /**
     * Check if user has value data
     * @param UUID the UUID of the player to check
     * @return true if player has a value data, false otherwise
     */
    public static boolean checkDataExist(String UUID) {
        return ValuesPlayerData.checkDataExist(UUID);
    }

    /**
     * Get the amount of user value
     * @param offlinePlayer online or offline player
     * @param valueType name of the value (mana, max, regen, dollars, hubixes)
     * @return the amount of the value the user has
     */
    public static int getValueFromName(OfflinePlayer offlinePlayer, String valueType) {
        if (!(offlinePlayer instanceof Player)) {
            if (valueType.equals("mana")) {
                return Math.min(ValuesPlayerData.loadValue(offlinePlayer.getUniqueId().toString(), "mana"), ValuesPlayerData.loadValue(offlinePlayer.getUniqueId().toString(), "max"));
            }
            return ValuesPlayerData.loadValue(offlinePlayer.getUniqueId().toString(), valueType);
        }
        switch (valueType) {
            case "mana": {
                return ValuesPlayerData.getManaFromMap(offlinePlayer.getPlayer());
            }
            case "max": {
                return ValuesPlayerData.getMaxManaFromMap(offlinePlayer.getPlayer());
            }
            case "regen": {
                return ValuesPlayerData.getRegenManaFromMap(offlinePlayer.getPlayer());
            }
            case "dollars": {
                return ValuesPlayerData.getDollarsFromMap(offlinePlayer.getPlayer());
            }
            default: {
                return ValuesPlayerData.loadValue(offlinePlayer.getUniqueId().toString(), valueType);
            }
        }
    }

    /**
     * Set the new amount of user value
     * @param offlinePlayer online or offline player
     * @param valueType name of the value (mana, max, regen, dollars, hubixes)
     * @param amount the amount to set
     * @return true if data saved in memory (player online), false otherwise
     */
    public static boolean setValueFromName(OfflinePlayer offlinePlayer, String valueType, int amount) {
        if (!(offlinePlayer instanceof Player)) {
            ValuesPlayerData.saveHubsValue(offlinePlayer.getUniqueId().toString(), valueType, amount);
            return false;
        }
        switch (valueType) {
            case "mana": {
                ValuesPlayerData.setManaToMap(offlinePlayer.getPlayer(), amount);
                break;
            }
            case "max": {
                ValuesPlayerData.setMaxManaToMap(offlinePlayer.getPlayer(), amount);
                break;
            }
            case "regen": {
                ValuesPlayerData.setRegenManaToMap(offlinePlayer.getPlayer(), amount);
                break;
            }
            case "dollars": {
                ValuesPlayerData.setDollarsToMap(offlinePlayer.getPlayer(), amount);
                break;
            }
            default: {
                ValuesPlayerData.saveHubsValue(offlinePlayer.getUniqueId().toString(), valueType, amount);
                return false;
            }
        }
        return true;
    }

    /**
     * Load player mana, max, regen, dollars from database to map and create a HubsPlayer
     * @param player the player to load
     */
    public static void loadPlayerData(Player player) {
        String UUID = player.getUniqueId().toString();
        if (ValuesPlayerData.checkDataExist(UUID)) {
            ValuesPlayerData.recreateAccount(
                    player,
                    ValuesPlayerData.loadValue(UUID, "dollars"),
                    Math.min(ValuesPlayerData.loadValue(UUID, "mana"), ValuesPlayerData.loadValue(UUID, "max")),
                    ValuesPlayerData.loadValue(UUID, "max"),
                    ValuesPlayerData.loadValue(UUID, "regen")
            );
        } else {
            ValuesPlayerData.createAccount(player.getUniqueId().toString(), player);
        }
    }

    /**
     * Load player mana, max, regen, dollars from database to map
     * @param player the player to load
     */
    public static void reloadPlayerData(Player player) {
        String UUID = player.getUniqueId().toString();
        ValuesPlayerData.loadPlayerValueSet(
                player,
                ValuesPlayerData.loadValue(UUID, "dollars"),
                Math.min(ValuesPlayerData.loadValue(UUID, "mana"), ValuesPlayerData.loadValue(UUID, "max")),
                ValuesPlayerData.loadValue(UUID, "max"),
                ValuesPlayerData.loadValue(UUID, "regen")
        );
    }

    /**
     * Save player mana, max, regen, dollars from map to database
     * @param player the player to save
     */
    public static void savePlayerData(Player player) {
        ValuesPlayerData.saveAllMapValues(player.getUniqueId().toString(), ValuesPlayerData.getManaFromMap(player), ValuesPlayerData.getMaxManaFromMap(player), ValuesPlayerData.getRegenManaFromMap(player), ValuesPlayerData.getDollarsFromMap(player));
    }

    /**
     * Check if player is online (have data in memory)
     * @param player the player to check
     */
    public static boolean isPlayerOnline(Player player) {
        return ValuesPlayerData.isPlayerOnline(player);
    }



    /**
     * Add the mana to all online players
     * (all online players who activate a scoreboard)
     */
    public static void increaseAllOnlineMana() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (isPlayerOnline(player))
                ValuesPlayerData.setManaToMap(player, Math.min(ValuesPlayerData.getManaFromMap(player) + ValuesPlayerData.getRegenManaFromMap(player), ValuesPlayerData.getMaxManaFromMap(player)));
        }
    }

    /**
     * Add the mana to all offline players
     */
    public static void increaseAllOfflineMana() {
        ValuesPlayerData.increaseManaForAll();
    }



    /**
     * Get the amount of user mana
     * @param offlinePlayer online or offline player
     * @return the amount of the mana the user has
     */
    public static int getMana(OfflinePlayer offlinePlayer) {
        if (!(offlinePlayer instanceof Player)) {
            return Math.min(ValuesPlayerData.loadValue(offlinePlayer.getUniqueId().toString(), "mana"), ValuesPlayerData.loadValue(offlinePlayer.getUniqueId().toString(), "max"));
        }
        return ValuesPlayerData.getManaFromMap(offlinePlayer.getPlayer());
    }

    /**
     * Set the new mana value for that player
     * @param offlinePlayer online or offline player
     * @param amount the amount to set
     * @return true if data saved in memory (player online), false otherwise
     */
    public static boolean setMana(OfflinePlayer offlinePlayer, int amount) {
        if (!(offlinePlayer instanceof Player)) {
            ValuesPlayerData.saveHubsValue(offlinePlayer.getUniqueId().toString(), "mana", amount);
            return false;
        }
        ValuesPlayerData.setManaToMap(offlinePlayer.getPlayer(), amount);
        return true;
    }

    /**
     * Add the mana to that player not exceeding the maximum value
     * @param offlinePlayer online or offline player
     * @param amount the amount to add
     * @return true if data saved in memory (player online), false otherwise
     */
    public static boolean addMana(OfflinePlayer offlinePlayer, int amount) {
        if (!(offlinePlayer instanceof Player)) {
            String UUID = offlinePlayer.getUniqueId().toString();
            ValuesPlayerData.saveHubsValue(offlinePlayer.getUniqueId().toString(), "mana", amount + ValuesPlayerData.loadValue(UUID, "mana"));
            return false;
        }
        Player player = offlinePlayer.getPlayer();
        ValuesPlayerData.setManaToMap(player, Math.min(amount + ValuesPlayerData.getManaFromMap(player), ValuesPlayerData.getMaxManaFromMap(player)));
        return true;
    }

    /**
     * Add the mana to that player with the probability to exceeding the maximum value
     * @param offlinePlayer online or offline player
     * @param amount the amount to add
     * @return true if data saved in memory (player online), false otherwise
     */
    public static boolean overflowMana(OfflinePlayer offlinePlayer, int amount) {
        if (!(offlinePlayer instanceof Player)) {
            String UUID = offlinePlayer.getUniqueId().toString();
            ValuesPlayerData.saveHubsValue(UUID, "mana", amount + ValuesPlayerData.loadValue(UUID, "mana"));
            return false;
        }
        Player player = offlinePlayer.getPlayer();
        ValuesPlayerData.setManaToMap(player, amount + ValuesPlayerData.getManaFromMap(player));
        return true;
    }

    /**
     * Remove the mana from that player not going for zero
     * This feature does CHANGE player mana if taking amount more then original player mana
     * If you need to not change player mana in that cause, use takeMana
     * @param offlinePlayer online or offline player
     * @param amount the amount to take
     * @return true if data saved in memory (player online), false otherwise
     * @see #takeMana(OfflinePlayer, int)
     */
    public static boolean removeMana(OfflinePlayer offlinePlayer, int amount) {
        if (!(offlinePlayer instanceof Player)) {
            String UUID = offlinePlayer.getUniqueId().toString();
            ValuesPlayerData.saveHubsValue(UUID, "mana", Math.max(ValuesPlayerData.loadValue(UUID, "mana") - amount, 0));
            return false;
        }
        Player player = offlinePlayer.getPlayer();
        ValuesPlayerData.setManaToMap(player, Math.max(ValuesPlayerData.getManaFromMap(player) - amount, 0));
        return true;
    }

    /**
     * Take the mana from that player not going for zero
     * This feature does NOT change player mana if taking amount more then original player mana
     * If you need to change player mana in that cause, use removeMana
     * @param offlinePlayer online or offline player
     * @param amount the amount to take
     * @return 1 if data saved in memory (player online, enough mana), 0 if data not saved (not enough mana), -1 otherwise
     * @see #removeMana(OfflinePlayer, int)
     */
    public static byte takeMana(OfflinePlayer offlinePlayer, int amount) {
        if (!(offlinePlayer instanceof Player)) {
            String UUID = offlinePlayer.getUniqueId().toString();
            if (ValuesPlayerData.loadValue(UUID, "mana") >= amount) {
                ValuesPlayerData.saveHubsValue(UUID, "mana", ValuesPlayerData.loadValue(UUID, "mana") - amount);
                return -1;
            }
            return 0;
        }
        Player player = offlinePlayer.getPlayer();
        if (ValuesPlayerData.getManaFromMap(player) >= amount) {
            ValuesPlayerData.setManaToMap(player, ValuesPlayerData.getManaFromMap(player) - amount);
            return 1;
        }
        return 0;
    }

    /**
     * Fill the mana to that player to the maximum
     * @param offlinePlayer online or offline player
     * @return true if data saved in memory (player online), false otherwise
     */
    public static boolean fillMana(OfflinePlayer offlinePlayer) {
        if (!(offlinePlayer instanceof Player)) {
            String UUID = offlinePlayer.getUniqueId().toString();
            ValuesPlayerData.saveHubsValue(UUID, "mana", ValuesPlayerData.loadValue(UUID, "max"));
            return false;
        }
        Player player = offlinePlayer.getPlayer();
        ValuesPlayerData.setManaToMap(player, ValuesPlayerData.getMaxManaFromMap(player));
        return true;
    }

    /**
     * Use up the mana to that player (set to zero if player have maximum mana)
     * That feature allowed only to online player
     * @param player online player
     * @return true if player had maximum mana (and mana value changed), false otherwise
     */
    public static boolean useUpMana(Player player) {
        if (ValuesPlayerData.getManaFromMap(player) == ValuesPlayerData.getMaxManaFromMap(player)) {
            ValuesPlayerData.setManaToMap(player, 0);
            return true;
        }
        return false;
    }



    /**
     * Get the amount of user maximum mana
     * @param offlinePlayer online or offline player
     * @return the amount of the maximum mana the user has
     */
    public static int getMaxMana(OfflinePlayer offlinePlayer) {
        if (!(offlinePlayer instanceof Player)) {
            return ValuesPlayerData.loadValue(offlinePlayer.getUniqueId().toString(), "max");
        }
        return ValuesPlayerData.getMaxManaFromMap(offlinePlayer.getPlayer());
    }

    /**
     * Set the new maximum mana value for that player
     * @param offlinePlayer online or offline player
     * @param amount the amount to set
     * @return true if data saved in memory (player online), false otherwise
     */
    public static boolean setMaxMana(OfflinePlayer offlinePlayer, int amount) {
        if (!(offlinePlayer instanceof Player)) {
            ValuesPlayerData.saveHubsValue(offlinePlayer.getUniqueId().toString(), "max", amount);
            return false;
        }
        ValuesPlayerData.setMaxManaToMap(offlinePlayer.getPlayer(), amount);
        return true;
    }

    /**
     * Increase the maximum mana value to that player
     * @param offlinePlayer online or offline player
     * @param amount the amount to increase
     * @return true if data saved in memory (player online), false otherwise
     */
    public static boolean increaseMaxMana(OfflinePlayer offlinePlayer, int amount) {
        if (!(offlinePlayer instanceof Player)) {
            String UUID = offlinePlayer.getUniqueId().toString();
            ValuesPlayerData.saveHubsValue(UUID, "max", amount + ValuesPlayerData.loadValue(UUID, "max"));
            return false;
        }
        Player player = offlinePlayer.getPlayer();
        ValuesPlayerData.setMaxManaToMap(player, amount + ValuesPlayerData.getMaxManaFromMap(player));
        return true;
    }



    /**
     * Get the amount of user mana regeneration
     * @param offlinePlayer online or offline player
     * @return the amount of the mana regeneration the user has
     */
    public static int getRegenMana(OfflinePlayer offlinePlayer) {
        if (!(offlinePlayer instanceof Player)) {
            return ValuesPlayerData.loadValue(offlinePlayer.getUniqueId().toString(), "regen");
        }
        return ValuesPlayerData.getRegenManaFromMap(offlinePlayer.getPlayer());
    }

    /**
     * Set the new mana regeneration value for that player
     * @param offlinePlayer online or offline player
     * @param amount the amount to set
     * @return true if data saved in memory (player online), false otherwise
     */
    public static boolean setRegenMana(OfflinePlayer offlinePlayer, int amount) {
        if (!(offlinePlayer instanceof Player)) {
            ValuesPlayerData.saveHubsValue(offlinePlayer.getUniqueId().toString(), "regen", amount);
            return false;
        }
        ValuesPlayerData.setRegenManaToMap(offlinePlayer.getPlayer(), amount);
        return true;
    }

    /**
     * Increase the mana regeneration value to that player
     * @param offlinePlayer online or offline player
     * @param amount the amount to increase
     * @return true if data saved in memory (player online), false otherwise
     */
    public static boolean increaseRegenMana(OfflinePlayer offlinePlayer, int amount) {
        if (!(offlinePlayer instanceof Player)) {
            String UUID = offlinePlayer.getUniqueId().toString();
            ValuesPlayerData.saveHubsValue(UUID, "regen", amount + ValuesPlayerData.loadValue(UUID, "regen"));
            return false;
        }
        Player player = offlinePlayer.getPlayer();
        ValuesPlayerData.setRegenManaToMap(player, amount + ValuesPlayerData.getRegenManaFromMap(player));
        return true;
    }



    /**
     * Get the amount of user dollars
     * @param offlinePlayer online or offline player
     * @return the amount of the dollars the user has
     */
    public static int getDollars(OfflinePlayer offlinePlayer) {
        if (!(offlinePlayer instanceof Player)) {
            return ValuesPlayerData.loadValue(offlinePlayer.getUniqueId().toString(), "dollars");
        }
        return ValuesPlayerData.getDollarsFromMap(offlinePlayer.getPlayer());
    }

    /**
     * Set the new dollars value for that player
     * @param offlinePlayer online or offline player
     * @param amount the amount to set
     * @return true if data saved in memory (player online), false otherwise
     */
    public static boolean setDollars(OfflinePlayer offlinePlayer, int amount) {
        if (!(offlinePlayer instanceof Player)) {
            ValuesPlayerData.saveHubsValue(offlinePlayer.getUniqueId().toString(), "dollars", amount);
            return false;
        }
        ValuesPlayerData.setDollarsToMap(offlinePlayer.getPlayer(), amount);
        return true;
    }

    /**
     * Add the dollars to that player
     * @param offlinePlayer online or offline player
     * @param amount the amount to add
     * @return true if data saved in memory (player online), false otherwise
     */
    public static boolean addDollars(OfflinePlayer offlinePlayer, int amount) {
        if (!(offlinePlayer instanceof Player)) {
            String UUID = offlinePlayer.getUniqueId().toString();
            ValuesPlayerData.saveHubsValue(UUID, "dollars", amount + ValuesPlayerData.loadValue(UUID, "dollars"));
            return false;
        }
        Player player = offlinePlayer.getPlayer();
        ValuesPlayerData.setDollarsToMap(player, amount + ValuesPlayerData.getDollarsFromMap(player));
        return true;
    }

    /**
     * Remove the dollars from that player not going for zero
     * This feature does CHANGE player dollars if taking amount more then original player dollars
     * If you need to not change player dollars in that cause, use takeDollars
     * @param offlinePlayer online or offline player
     * @param amount the amount to take
     * @return true if data saved in memory (player online), false otherwise
     * @see #takeDollars(OfflinePlayer, int)
     */
    public static boolean removeDollars(OfflinePlayer offlinePlayer, int amount) {
        if (!(offlinePlayer instanceof Player)) {
            String UUID = offlinePlayer.getUniqueId().toString();
            ValuesPlayerData.saveHubsValue(UUID, "dollars", Math.max(ValuesPlayerData.loadValue(UUID, "dollars") - amount, 0));
            return false;
        }
        Player player = offlinePlayer.getPlayer();
        ValuesPlayerData.setDollarsToMap(player, Math.max(ValuesPlayerData.getDollarsFromMap(player) - amount, 0));
        return true;
    }

    /**
     * Take the dollars from that player not going for zero
     * This feature does NOT change player dollars if taking amount more then original player dollars
     * If you need to change player dollars in that cause, use removeDollars
     * @param offlinePlayer online or offline player
     * @param amount the amount to take
     * @return 1 if data saved in memory (player online, enough dollars), 0 if data not saved (not enough dollars), -1 otherwise
     * @see #removeDollars(OfflinePlayer, int)
     */
    public static byte takeDollars(OfflinePlayer offlinePlayer, int amount) {
        if (!(offlinePlayer instanceof Player)) {
            String UUID = offlinePlayer.getUniqueId().toString();
            if (ValuesPlayerData.loadValue(UUID, "dollars") >= amount) {
                ValuesPlayerData.saveHubsValue(UUID, "dollars", ValuesPlayerData.loadValue(UUID, "dollars") - amount);
                return -1;
            }
            return 0;
        }
        Player player = offlinePlayer.getPlayer();
        if (ValuesPlayerData.getDollarsFromMap(player) >= amount) {
            ValuesPlayerData.setDollarsToMap(player, ValuesPlayerData.getDollarsFromMap(player) - amount);
            return 1;
        }
        return 0;
    }



    /**
     * Get the amount of user hubixes
     * @param offlinePlayer online or offline player
     * @return the amount of the hubixes the user has
     */
    public static int getHubixes(OfflinePlayer offlinePlayer) {
        return ValuesPlayerData.loadValue(offlinePlayer.getUniqueId().toString(), "hubixes");
    }

    /**
     * Set the new hubixes value for that player
     * @param offlinePlayer online or offline player
     * @param amount the amount to set
     */
    public static void setHubixes(OfflinePlayer offlinePlayer, int amount) {
        ValuesPlayerData.saveHubsValue(offlinePlayer.getUniqueId().toString(), "hubixes", amount);
        if (offlinePlayer instanceof Player)
            PluginUtils.getHubsPlayer((Player) offlinePlayer).updateNormalVars();
    }

    /**
     * Add the hubixes to that player
     * @param offlinePlayer online or offline player
     * @param amount the amount to add
     */
    public static void addHubixes(OfflinePlayer offlinePlayer, int amount) {
        String UUID = offlinePlayer.getUniqueId().toString();
        ValuesPlayerData.saveHubsValue(UUID, "hubixes", amount + ValuesPlayerData.loadValue(UUID, "hubixes"));
        if (offlinePlayer instanceof Player)
            PluginUtils.getHubsPlayer((Player) offlinePlayer).updateNormalVars();
    }

    /**
     * Remove the hubixes from that player not going for zero
     * This feature does CHANGE player hubixes if taking amount more then original player hubixes
     * If you need to not change player hubixes in that cause, use takeHubixes
     * @param offlinePlayer online or offline player
     * @param amount the amount to take
     * @see #takeHubixes(OfflinePlayer, int)
     */
    public static void removeHubixes(OfflinePlayer offlinePlayer, int amount) {
        String UUID = offlinePlayer.getUniqueId().toString();
        ValuesPlayerData.saveHubsValue(UUID, "hubixes", Math.max(ValuesPlayerData.loadValue(UUID, "hubixes") - amount, 0));
        if (offlinePlayer instanceof Player)
            PluginUtils.getHubsPlayer((Player) offlinePlayer).updateNormalVars();
    }

    /**
     * Take the hubixes from that player not going for zero
     * This feature does NOT change player hubixes if taking amount more then original player hubixes
     * If you need to change player hubixes in that cause, use removeHubixes
     * @param offlinePlayer online or offline player
     * @param amount the amount to take
     * @return true if enough hubixes, false otherwise
     * @see #removeHubixes(OfflinePlayer, int)
     */
    public static boolean takeHubixes(OfflinePlayer offlinePlayer, int amount) {
        String UUID = offlinePlayer.getUniqueId().toString();
        if (ValuesPlayerData.loadValue(UUID, "hubixes") >= amount) {
            ValuesPlayerData.saveHubsValue(UUID, "hubixes", ValuesPlayerData.loadValue(UUID, "hubixes") - amount);
            if (offlinePlayer instanceof Player)
                PluginUtils.getHubsPlayer((Player) offlinePlayer).updateNormalVars();
            return true;
        }
        return false;
    }

}
