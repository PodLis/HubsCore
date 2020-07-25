package su.hubs.hubscore.module.chesterton.internal.menu;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import su.hubs.hubscore.PluginUtils;
import su.hubs.hubscore.exception.ConfigurationPartMissingException;
import su.hubs.hubscore.module.chesterton.internal.ActionClickHandler;
import su.hubs.hubscore.module.chesterton.internal.action.ReturnItemAction;
import su.hubs.hubscore.module.chesterton.internal.item.ChestertonItem;
import su.hubs.hubscore.module.chesterton.internal.item.CustomItem;
import su.hubs.hubscore.module.values.ValueType;
import su.hubs.hubscore.util.StringUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

public class ConfirmMenu extends ChestMenu {

    public ConfirmMenu(Material material, String nameOfProduct, ValueType valueType, int amount, BiFunction<Player, ChestertonItem, Boolean> onAccept) {
        super(PluginUtils.getStringsConfig().getString("menus.confirm.title"), 6);

        ConfigurationSection section;
        try {
            section = PluginUtils.getStringsConfig().getConfigurationSection("menus.confirm");
            if (section == null) {
                throw new ConfigurationPartMissingException("'menus.confirm' section in strings.yml is missing");
            }
        } catch (ConfigurationPartMissingException e) {
            e.printStackTrace();
            return;
        }

        CustomItem infoItem = new CustomItem(material != null ? material : Material.END_CRYSTAL);
        String formattedAmount = valueType != null ? valueType.getFormattedValue(amount) : ValueType.HUBIXES.getFormattedValue(amount);
        infoItem.setName(section.getString("info.name"));
        List<String> list = new LinkedList<>();
        for (String s : section.getStringList("info.lore")) {
            list.add(StringUtils.setPlaceholders(s, "product_name", nameOfProduct, "amount", formattedAmount));
        }
        infoItem.setLore(list);

        CustomItem acceptItem = new CustomItem(Material.getMaterial(Objects.requireNonNull(section.getString("accept.material"))));
        acceptItem.setName(section.getString("accept.name"));
        acceptItem.setClickHandler(onAccept::apply);

        CustomItem declineItem = new CustomItem(Material.getMaterial(Objects.requireNonNull(section.getString("decline.material"))));
        declineItem.setName(section.getString("decline.name"));
        declineItem.setClickHandler(new ActionClickHandler(new ReturnItemAction(this)));

        CustomItem fillItem = new CustomItem(Material.getMaterial(Objects.requireNonNull(section.getString("fill.material"))));
        fillItem.setName(section.getString("fill.name"));

        for (int i = 0; i < 54; i++) {
            switch (i) {
                case 4:
                    setItem(i, infoItem);
                    break;
                case 19:
                case 20:
                case 21:
                case 28:
                case 29:
                case 30:
                case 37:
                case 38:
                case 39:
                    setItem(i, acceptItem);
                    break;
                case 23:
                case 24:
                case 25:
                case 32:
                case 33:
                case 34:
                case 41:
                case 42:
                case 43:
                    setItem(i, declineItem);
                    break;
                default:
                    setItem(i, fillItem);
            }
        }

    }

}
