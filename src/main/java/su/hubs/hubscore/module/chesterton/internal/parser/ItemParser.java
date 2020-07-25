package su.hubs.hubscore.module.chesterton.internal.parser;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import su.hubs.hubscore.module.chesterton.HubsChesterton;
import su.hubs.hubscore.module.chesterton.internal.ActionClickHandler;
import su.hubs.hubscore.module.chesterton.internal.item.CustomItem;
import su.hubs.hubscore.module.chesterton.internal.item.ExtendedItem;
import su.hubs.hubscore.module.chesterton.internal.item.PlayerHeadItem;
import su.hubs.hubscore.module.chesterton.internal.menu.ChestMenu;

public class ItemParser {

    static CustomItem parseCustomItem(ConfigurationSection section, ChestMenu menu) {

        CustomItem customItem;
        String type = section.getString("type");
        Material material;
        if (type == null) {
            material = Material.BEDROCK;
        } else {
            if (type.equals("RETURN_BUTTON")) {
                return HubsChesterton.getReturnButton(menu);
            }
            material = Material.getMaterial(type.toUpperCase());
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

        String name = section.getString("name");
        if (name != null)
            customItem.setName(name);
        customItem.setLore(section.getStringList("lore"));
        customItem.setEnchanted(section.getBoolean("enchanted"));

        if (section.contains("amount"))
            customItem.setAmount(section.getInt("amount"));

        String action = section.getString("on-click");
        boolean close = section.getBoolean("close");
        if (action != null)
            customItem.setClickHandler(new ActionClickHandler(SubParser.parseAction(action, menu), close));

        return customItem;
    }

    public static ExtendedItem parseExtendedItem(ConfigurationSection section) {

        ExtendedItem extendedItem;
        String type = section.getString("type");
        Material material;
        if (type == null) {
            material = Material.BEDROCK;
        } else {
            material = Material.getMaterial(type.toUpperCase());
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
