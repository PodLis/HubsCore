package su.hubs.hubscore.util;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import su.hubs.hubscore.HubsCommand;
import su.hubs.hubscore.GlobalPermission;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static su.hubs.hubscore.PluginUtils.getFileToSaveParse;

public class UtilsCommand extends HubsCommand {

    public UtilsCommand() {
        super("utils", GlobalPermission.UTILS, true, 0);
    }

    @Override
    public boolean onHubsCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (args.length == 0) {
            sendPlaceholderMessage(sender, "help.header");
            sendPlaceholderMessage(sender, "help.parse");
            sendPlaceholderMessage(sender, "help.rename");
            return true;
        }

        switch (args[0].toLowerCase()) {

            case "parse":
                Player player = (Player) sender;

                if (args.length < 2) {
                    MessageUtils.sendWrongUsageMessage(player, "parse <filename-part-1> [filename-part-2] ...");
                    return true;
                }

                Block block = player.getTargetBlock(null, 3);

                if (!block.getType().equals(Material.CHEST) && !block.getType().equals(Material.TRAPPED_CHEST)) {
                    sendPlaceholderMessage(sender, "parse.not-a-chest");
                    return true;
                }

                if (block.getType().equals(Material.TRAPPED_CHEST)) {
                    sendPlaceholderMessage(sender, "parse.trap-detected");
                }

                sendPlaceholderMessage(sender, "parse.start-parsing");

                StringBuilder fileName = new StringBuilder();
                for (String arg : args) {
                    fileName.append(arg).append("-");
                }
                fileName.deleteCharAt(fileName.length() - 1);
                File dataFile = getFileToSaveParse(fileName.toString());
                FileConfiguration data = YamlConfiguration.loadConfiguration(dataFile);

                Chest chest = (Chest) block.getState();
                InventoryHolder holder = chest.getInventory().getHolder();
                Inventory inventory = holder.getInventory();

                for (int i = 0; i < inventory.getSize(); i++) {
                    ItemStack itemStack = inventory.getItem(i);
                    if (itemStack != null && !itemStack.getType().isAir()) {

                        // Type
                        Material material = itemStack.getType();
                        data.set(i + ".type", material.name());

                        // Enchantments
                        for (Enchantment enchantment : itemStack.getEnchantments().keySet()) {
                            data.set(i + ".enchantments." + enchantment.getName(), itemStack.getEnchantmentLevel(enchantment));
                        }

                        // Potion effects
                        if (material.equals(Material.POTION) || material.equals(Material.SPLASH_POTION) ||
                                material.equals(Material.LINGERING_POTION) || material.equals(Material.TIPPED_ARROW)) {

                            List<String> customEffects = new ArrayList<>();
                            PotionMeta potionMeta = (PotionMeta) itemStack.getItemMeta();

                            if (potionMeta.hasCustomEffects()) {

                                // Custom effects
                                for (PotionEffect effect : (potionMeta).getCustomEffects()) {
                                    customEffects.add(effect.getType().getName() + ":" +
                                            effect.getAmplifier() + ":" +
                                            effect.getDuration() + ":" +
                                            effect.hasParticles());
                                }
                                if (customEffects.size() > 0) {
                                    data.set(i + ".custom-potion.name", potionMeta.getDisplayName());
                                    data.set(i + ".custom-potion.color", potionMeta.getColor().toString());
                                    data.set(i + ".custom-potion.effects", customEffects);
                                }

                            } else {
                                // Normal effects
                                data.set(i + ".potion.effect", potionMeta.getBasePotionData().getType().name());
                                data.set(i + ".potion.extended", potionMeta.getBasePotionData().isExtended());
                                data.set(i + ".potion.upgraded", potionMeta.getBasePotionData().isUpgraded());
                            }

                        }

                        // Enchanted book
                        if (material.equals(Material.ENCHANTED_BOOK)) {
                            EnchantmentStorageMeta storageMeta = (EnchantmentStorageMeta) itemStack.getItemMeta();
                            for (Enchantment enchantment : storageMeta.getStoredEnchants().keySet()) {
                                data.set(i + ".storage." + enchantment.getName(), storageMeta.getStoredEnchantLevel(enchantment));
                            }
                        }

                    }
                }

                try {
                    data.save(dataFile);
                    sendPlaceholderMessage(sender, "parse.success");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return true;

            case "broadcast":
            case "bc": {
                String[] newArgs = new String[args.length - 1];
                System.arraycopy(args, 1, newArgs, 0, newArgs.length);
                ServerUtils.broadcastMessage(StringUtils.replaceColor(String.join(" ", newArgs)));
                return true;
            }

            case "rename": {
                Player player1 = (Player) sender;
                ItemMeta itemMeta = player1.getInventory().getItemInMainHand().getItemMeta();
                String[] newArgs = new String[args.length - 1];
                System.arraycopy(args, 1, newArgs, 0, newArgs.length);
                if (itemMeta == null) {
                    sendPlaceholderMessage(sender, "rename");
                    return true;
                }
                itemMeta.setDisplayName(StringUtils.replaceColor(String.join(" ", newArgs)));
                player1.getInventory().getItemInMainHand().setItemMeta(itemMeta);
                return true;
            }

            default:
                MessageUtils.sendUnknownCommandMessage(sender, args[0]);
                return true;

        }
    }

    @Override
    public List<String> onHubsComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completionList = new ArrayList<>();
        String partOfCommand;
        List<String> cmds = new ArrayList<>();

        switch (args.length) {
            case 1:
                cmds = new ArrayList<>(Arrays.asList("parse", "rename", "broadcast"));
                partOfCommand = args[0];

                StringUtil.copyPartialMatches(partOfCommand, cmds, completionList);
                Collections.sort(completionList);
                return completionList;

            default:
                return null;
        }

    }

}
