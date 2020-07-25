package su.hubs.hubscore.module.chesterton.internal.item;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class PlayerHeadItem extends CustomItem {

    private String base64;

    public PlayerHeadItem() {
        super(Material.PLAYER_HEAD);
    }

    public void setBase64(String base64) {
        this.base64 = base64;
    }

    public String getBase64() {
        return base64;
    }

    @Override
    protected ItemStack setItemData(ItemStack itemStack, Player player) {
        return super.setItemData(itemWithBase64(itemStack), player);
    }

    /**
     * Applies the base64 string to the ItemStack.
     *
     * @param item The ItemStack to put the base64 onto
     * @return The head with a custom texture
     */
    private ItemStack itemWithBase64(ItemStack item) {
        UUID hashAsId = new UUID(base64.hashCode(), base64.hashCode());
        return Bukkit.getUnsafe().modifyItemStack(item,
                "{SkullOwner:{Id:\"" + hashAsId + "\",Properties:{textures:[{Value:\"" + base64 + "\"}]}}}"
        );
    }

}
