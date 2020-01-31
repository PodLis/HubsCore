package ru.hubsmc.hubscore.module.chesterton.internal.parser;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import ru.hubsmc.hubscore.module.chesterton.internal.ActionClickHandler;
import ru.hubsmc.hubscore.module.chesterton.internal.action.ItemAction;
import ru.hubsmc.hubscore.module.chesterton.internal.item.CustomItem;
import ru.hubsmc.hubscore.module.chesterton.internal.item.ExtendedItem;
import ru.hubsmc.hubscore.module.chesterton.internal.item.PlayerHeadItem;
import ru.hubsmc.hubscore.module.chesterton.internal.menu.ChestMenu;

import java.util.LinkedList;
import java.util.List;

public class ItemParser {

    static CustomItem parseCustomItem(ConfigurationSection section, ChestMenu menu) {

        CustomItem customItem;
        String type = section.getString("type");
        Material material;
        if (type == null) {
            material = Material.BEDROCK;
        } else {
            material = Material.getMaterial(type);
            if (material == null) {
                material = Material.BEDROCK;
            }
        }

        switch (material) {
            case POTION:
            case SPLASH_POTION:
            case LINGERING_POTION:
            case TIPPED_ARROW:
            case LEATHER_BOOTS:
            case LEATHER_LEGGINGS:
            case LEATHER_CHESTPLATE:
            case LEATHER_HELMET:
            {
                customItem = new CustomItem(material);
                String color = section.getString("color");
                if (color != null) {
                    customItem.setColor(SubParser.parseColor(color));
                }
                break;
            }
            case PLAYER_HEAD: {
                customItem = new PlayerHeadItem();
                String base = section.getString("base");
                if (base != null) {
                    ((PlayerHeadItem) customItem).setBase64(base);
                }
                break;
            }
            default: {
                customItem = new CustomItem(material);
                break;
            }
        }

        if (section.getString("name") != null)
            customItem.setName(section.getString("name"));
        customItem.setLore(section.getStringList("lore"));
        customItem.setEnchanted(section.getBoolean("enchanted"));

        List<ItemAction> actions = new LinkedList<>();
        for (String s : section.getStringList("on-click")) {
            ItemAction action = SubParser.parseAction(s, menu);
            actions.add(action);
        }
        customItem.setClickHandler(new ActionClickHandler(actions));

        return customItem;
    }

    static ExtendedItem parseExtendedItem(ConfigurationSection section) {

        ExtendedItem extendedItem;
        String type = section.getString("type");
        Material material;
        if (type == null) {
            material = Material.BEDROCK;
        } else {
            material = Material.getMaterial(type);
        }
        extendedItem = new ExtendedItem(material);

        switch (material) {
            case POTION:
            case SPLASH_POTION:
            case LINGERING_POTION:
            case TIPPED_ARROW:
            {
                if (section.getConfigurationSection("potion") != null)
                    extendedItem.setPotionData(SubParser.parsePotionData(section.getConfigurationSection("potion")));
                break;
            }
            case ENCHANTED_BOOK:
            {
                if (section.getConfigurationSection("storage") != null)
                    extendedItem.setStoredEnchantments(SubParser.parseEnchantments(section.getConfigurationSection("storage")));
                break;
            }
            default:
            {
                if (section.getConfigurationSection("enchantments") != null)
                    extendedItem.setEnchantments(SubParser.parseEnchantments(section.getConfigurationSection("enchantments")));
                break;
            }
        }

        return extendedItem;
    }
/*
    static ShopItem parseShopItem(ConfigurationSection section) {

        ShopItem shopItem;
        String type = section.getString("type");
        Material material;
        if (type == null) {
            material = Material.BEDROCK;
        } else {
            material = Material.getMaterial(type);
        }

        switch (material) {
            case POTION:
            case SPLASH_POTION:
            case LINGERING_POTION:
            {
                if (section.getConfigurationSection("potion") != null) {
                    PotionData potionData = SubParser.parsePotionData(section.getConfigurationSection("potion"));
                    shopItem = new ShopItem(material, getPotionPrice(potionData, material));
                    shopItem.setPotionData(potionData);
                    return shopItem;
                }
            }
            case TIPPED_ARROW:
            {
                if (section.getConfigurationSection("potion") != null) {
                    PotionData potionData = SubParser.parsePotionData(section.getConfigurationSection("potion"));
                    shopItem = new ShopItem(material, getArrowPrice(potionData));
                    shopItem.setPotionData(potionData);
                    return shopItem;
                }
            }
            case ENCHANTED_BOOK:
            {
                if (section.getConfigurationSection("storage") != null) {
                    Map<Enchantment, Integer> enchantments = SubParser.parseEnchantments(section.getConfigurationSection("storage"));
                    shopItem = new ShopItem(material, getEnchantedBookPrice(enchantments));
                    shopItem.setStoredEnchantments(enchantments);
                    return shopItem;
                }
            }
            default:
            {
                return new ShopItem(material, getItemPrice(material));
            }
        }
    }*/

}
