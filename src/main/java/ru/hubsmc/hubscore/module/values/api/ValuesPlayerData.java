package ru.hubsmc.hubscore.module.values.api;

import org.bukkit.entity.Player;
import ru.hubsmc.hubscore.PluginUtils;
import ru.hubsmc.hubscore.module.values.PlayerData;
import ru.hubsmc.hubscore.module.values.HubsValues;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ValuesPlayerData extends PlayerData {

    private static final String TABLE_NAME = "hubsval";
    private static final String C_PLAYER = "player";
    private static final String C_DOLLARS = "dollars";
    private static final String C_HUBIXES = "hubixes";
    private static final String C_MANA = "mana";
    private static final String C_MAX = "max";
    private static final String C_REGEN = "regen";

    private static ValuesPlayerData instance;

    private static int START_MANA;
    private static int START_MAX;
    private static int START_REGEN;
    private static final int START_DOLLARS = 0;
    private static final int START_HUBIXES = 0;

    public ValuesPlayerData() {
        super(TABLE_NAME, C_PLAYER, C_DOLLARS, C_HUBIXES, C_MANA, C_MAX, C_REGEN);
        instance = this;
        START_MANA = HubsValues.START_MANA;
        START_MAX = HubsValues.START_MANA;
        START_REGEN = HubsValues.START_REGEN;
        super.prepareToWork(new String[] {"null"}, new int[] {START_DOLLARS, START_HUBIXES, START_MANA, START_MAX, START_REGEN}, new double[0]);
    }

    boolean isPlayerOnlineF(Player player) {
        return PluginUtils.isPlayerOnHubs(player);
    }

    int getManaFromMapF(Player player) {
        return PluginUtils.getHubsPlayer(player).getMana();
    }

    int getMaxManaFromMapF(Player player) {
        return PluginUtils.getHubsPlayer(player).getMax();
    }

    int getRegenManaFromMapF(Player player) {
        return PluginUtils.getHubsPlayer(player).getRegen();
    }

    int getDollarsFromMapF(Player player) {
        return PluginUtils.getHubsPlayer(player).getDollars();
    }

    void setManaToMapF(Player player, int amount) {
        PluginUtils.getHubsPlayer(player).setMana(amount);
    }

    void setMaxManaToMapF(Player player, int amount) {
        PluginUtils.getHubsPlayer(player).setMax(amount);
    }

    void setRegenManaToMapF(Player player, int amount) {
        PluginUtils.getHubsPlayer(player).setRegen(amount);
    }

    void setDollarsToMapF(Player player, int amount) {
        PluginUtils.getHubsPlayer(player).setDollars(amount);
    }


    int loadValueF(String UUID, String valueType) {
        return selectIntValue(UUID, valueType);
    }

    void saveAllMapValuesF(String UUID, int manaAmount, int maxAmount, int regenAmount, int dollarsAmount) {
        update(UUID, C_MANA, manaAmount);
        update(UUID, C_MAX, maxAmount);
        update(UUID, C_REGEN, regenAmount);
        update(UUID, C_DOLLARS, dollarsAmount);
    }

    void increaseManaForAllF() {
        updateIncreaseAll();
    }

    boolean checkDataExistF(String UUID) {
        return selectExist(UUID);
    }

    void createAccountF(String UUID, Player player) {
        createAccount(UUID);
        update(UUID, C_PLAYER, player.getDisplayName());
        PluginUtils.loadPlayerAsHubsPlayer(player, START_DOLLARS, START_MANA, START_MAX, START_REGEN);
    }

    void recreateAccountF(Player player, int dollars, int mana, int max, int regen) {
        PluginUtils.loadPlayerAsHubsPlayer(player, dollars, mana, max, regen);
    }

    void loadPlayerValueSetF(Player player, int dollars, int mana, int max, int regen) {
        PluginUtils.getHubsPlayer(player).setValues(dollars, mana, max, regen);
    }

    private void updateIncreaseAll() {
        try {
            ResultSet rs = manager.Request("SELECT " + C_UUID + ", " + C_MANA + ", " + C_REGEN + " FROM " + TABLE_NAME);
            while (rs.next()) {
                update(rs.getString(C_UUID), C_MANA, rs.getInt(C_MANA) + rs.getInt(C_REGEN));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    static  boolean isPlayerOnline(Player player) {
        return instance.isPlayerOnlineF(player);
    }

    static int getManaFromMap(Player player) {
        return instance.getManaFromMapF(player);
    }

    static int getMaxManaFromMap(Player player) {
        return instance.getMaxManaFromMapF(player);
    }

    static int getRegenManaFromMap(Player player) {
        return instance.getRegenManaFromMapF(player);
    }

    static int getDollarsFromMap(Player player) {
        return instance.getDollarsFromMapF(player);
    }

    static void setManaToMap(Player player, int amount) {
        instance.setManaToMapF(player, amount);
    }

    static void setMaxManaToMap(Player player, int amount) {
        instance.setMaxManaToMapF(player, amount);
    }

    static void setRegenManaToMap(Player player, int amount) {
        instance.setRegenManaToMapF(player, amount);
    }

    static void setDollarsToMap(Player player, int amount) {
        instance.setDollarsToMapF(player, amount);
    }

    static int loadValue(String UUID, String valueType) {
        return instance.loadValueF(UUID, valueType);
    }

    static void saveAllMapValues(String UUID, int manaAmount, int maxAmount, int regenAmount, int dollarsAmount) {
        instance.saveAllMapValuesF(UUID, manaAmount, maxAmount, regenAmount, dollarsAmount);
    }

    static void increaseManaForAll() {
        instance.increaseManaForAllF();
    }

    static boolean checkDataExist(String UUID) {
        return instance.checkDataExistF(UUID);
    }

    static void createAccount(String UUID, Player player) {
        instance.createAccountF(UUID, player);
    }

    static void recreateAccount(Player player, int dollars, int mana, int max, int regen) {
        instance.recreateAccountF(player, dollars, mana, max, regen);
    }

    static void loadPlayerValueSet(Player player, int dollars, int mana, int max, int regen) {
        instance.loadPlayerValueSetF(player, dollars, mana, max, regen);
    }

    static void saveHubsValue(String UUID, String valueType, int valueAmount) {
        instance.saveValue(UUID, valueType, valueAmount);
    }

}
