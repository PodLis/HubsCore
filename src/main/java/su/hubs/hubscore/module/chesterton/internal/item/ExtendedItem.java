package su.hubs.hubscore.module.chesterton.internal.item;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import su.hubs.hubscore.util.StringUtils;

import java.util.List;
import java.util.Map;

public class ExtendedItem extends ChestertonItem {

    protected List<String> lore;
    protected boolean loreIsUpdated;
    protected Map<Enchantment, Integer> enchantments;
    protected Map<Enchantment, Integer> storedEnchantments;
    protected PotionData potionData;
    protected String name = null;

    public ExtendedItem(Material material) {
        super(material);
        this.loreIsUpdated = false;
    }

    public void setLore(List<String> lore) {
        this.lore = StringUtils.replaceColor(lore);
    }

    public void addLore(String subLore) {
        if (lore != null) {
            this.lore.add(StringUtils.replaceColor(subLore));
        }
    }

    public void setLoreIsUpdated(boolean loreIsUpdated) {
        this.loreIsUpdated = loreIsUpdated;
    }

    public void setEnchantments(Map<Enchantment, Integer> enchantments) {
        this.enchantments = enchantments;
    }

    public Map<Enchantment, Integer> getStoredEnchantments() {
        return storedEnchantments;
    }

    public void setStoredEnchantments(Map<Enchantment, Integer> storedEnchantments) {
        this.storedEnchantments = storedEnchantments;
    }

    public PotionData getPotionData() {
        return potionData;
    }

    public void setPotionData(PotionData potionData) {
        this.potionData = potionData;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    protected ItemStack setItemData(ItemStack itemStack, Player player) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return itemStack;
        }

        if (name != null)
            meta.setDisplayName(name);

        if (lore != null) {
            if (!loreIsUpdated && meta.getLore() != null) {
                lore.addAll(meta.getLore());
                loreIsUpdated = true;
            }
            meta.setLore(lore);
        }

        if (meta instanceof PotionMeta && potionData != null) {

            PotionMeta potionMeta = (PotionMeta) meta;
            potionMeta.setBasePotionData(potionData);
            itemStack.setItemMeta(potionMeta);

        } else if (meta instanceof EnchantmentStorageMeta && storedEnchantments != null) {

            EnchantmentStorageMeta storageMeta = (EnchantmentStorageMeta) meta;
            for (Map.Entry<Enchantment, Integer> entry : storedEnchantments.entrySet()) {
                storageMeta.addStoredEnchant(entry.getKey(), entry.getValue(), true);
            }
            itemStack.setItemMeta(storageMeta);

        } else {

            itemStack.setItemMeta(meta);

        }

        if (enchantments != null) {
            itemStack.addUnsafeEnchantments(enchantments);
        }

        return itemStack;
    }

}
