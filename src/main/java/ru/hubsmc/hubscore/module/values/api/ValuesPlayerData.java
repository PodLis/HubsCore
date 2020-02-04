package ru.hubsmc.hubscore.module.values.api;

import org.bukkit.entity.Player;
import ru.hubsmc.hubscore.module.values.PlayerData;
import ru.hubsmc.hubscore.module.values.HubsValues;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ValuesPlayerData extends PlayerData {

    private static final String TABLE_NAME = "hubsval";
    private static final String C_PLAYER = "player";
    private static final String C_DOLLARS = "dollars";
    private static final String C_HUBIXES = "hubixes";
    private static final String C_MANA = "mana";
    private static final String C_MAX = "max";
    private static final String C_REGEN = "regen";

    private static final int START_MANA = HubsValues.START_MANA;
    private static final int START_MAX = HubsValues.START_MANA;
    private static final int START_REGEN = HubsValues.START_REGEN;
    private static final int START_DOLLARS = 0;
    private static final int START_HUBIXES = 0;

    private static Map<Player, ValueSet> valueSetMap;

    public ValuesPlayerData() {
        super(TABLE_NAME, C_PLAYER, C_DOLLARS, C_HUBIXES, C_MANA, C_MAX, C_REGEN);
        valueSetMap = new HashMap<>();
    }

    public void prepareToWork(String url, String user, String password) {
        super.prepareToWork(url, user, password, new String[] {"null"}, new int[] {START_DOLLARS, START_HUBIXES, START_MANA, START_MAX, START_REGEN}, new double[0]);
    }

    static boolean isPlayerOnline(Player player) {
        return valueSetMap.containsKey(player);
    }

    static int getManaFromMap(Player player) {
        return valueSetMap.get(player).getMana();
    }

    static int getMaxManaFromMap(Player player) {
        return valueSetMap.get(player).getMax();
    }

    static int getRegenManaFromMap(Player player) {
        return valueSetMap.get(player).getRegen();
    }

    static int getDollarsFromMap(Player player) {
        return valueSetMap.get(player).getDollars();
    }

    static void setManaToMap(Player player, int amount) {
        valueSetMap.get(player).setMana(amount);
    }

    static void setMaxManaToMap(Player player, int amount) {
        valueSetMap.get(player).setMax(amount);
    }

    static void setRegenManaToMap(Player player, int amount) {
        valueSetMap.get(player).setRegen(amount);
    }

    static void setDollarsToMap(Player player, int amount) {
        valueSetMap.get(player).setDollars(amount);
    }


    static int loadValue(String UUID, String valueType) {
        return selectIntValue(UUID, valueType);
    }

    static void saveAllMapValues(String UUID, int manaAmount, int maxAmount, int regenAmount, int dollarsAmount) {
        update(UUID, C_MANA, manaAmount);
        update(UUID, C_MAX, maxAmount);
        update(UUID, C_REGEN, regenAmount);
        update(UUID, C_DOLLARS, dollarsAmount);
    }

    static void increaseManaForAll() {
        updateIncreaseAll();
    }

    static boolean checkDataExist(String UUID) {
        return selectExist(UUID);
    }

    static void createAccount(String UUID, Player player) {
        createAccount(UUID);
        update(UUID, C_PLAYER, player.getDisplayName());
        loadPlayerValueSet(player, START_DOLLARS, START_MANA, START_MAX, START_REGEN);
    }

    static void loadPlayerValueSet(Player player, int dollars, int mana, int max, int regen) {
        valueSetMap.put(player, new ValueSet(dollars, mana, max, regen));
    }

    private static void updateIncreaseAll() {
        try {
            ResultSet rs = manager.Request("SELECT " + C_UUID + ", " + C_MANA + ", " + C_REGEN + " FROM " + TABLE_NAME);
            while (rs.next()) {
                update(rs.getString(C_UUID), C_MANA, rs.getInt(C_MANA) + rs.getInt(C_REGEN));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
