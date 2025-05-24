package me.not_ryuzaki.setHome;

import me.not_ryuzaki.mainScorePlugin.Combat;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SetHomeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players!");
            return false;
        }

        if (Combat.isInCombat(player)) {
            send(player, "§cYou can't set a home while in combat!");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
            return true;
        }

        UUID uuid = player.getUniqueId();
        Map<String, Object[]> playerHomes = SetHome.homes.computeIfAbsent(uuid, k -> new HashMap<>());

        int maxHomes = SetHome.getMaxHomes(player);
        long currentCount = playerHomes.keySet().stream().filter(k -> k.startsWith("home")).count();

        if (currentCount >= maxHomes) {
            send(player, "§cYou have reached your home limit.");
            return true;
        }

        String homeName;
        if (args.length > 0) {
            if (args[0].matches("[1-5]")) {
                int homeSlot = Integer.parseInt(args[0]);
                homeName = "home" + homeSlot;

                if (homeSlot > maxHomes) {
                    send(player, "§cYou don't have permission to set Home " + homeSlot);
                    return true;
                }

                if (playerHomes.containsKey(homeName)) {
                    send(player, "§cHome " + homeSlot + " is already set!");
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
                    return true;
                }

            } else {
                send(player, "§cUsage: /sethome [1-5]");
                return true;
            }
        } else {
            homeName = null;
            for (int i = 1; i <= maxHomes; i++) {
                if (!playerHomes.containsKey("home" + i)) {
                    homeName = "home" + i;
                    break;
                }
            }
            if (homeName == null) {
                send(player, "§cYou have no available home slots.");
                return true;
            }
        }

        double x = player.getLocation().getX();
        double y = player.getLocation().getY();
        double z = player.getLocation().getZ();
        String worldName = player.getWorld().getName();

        playerHomes.put(homeName, new Object[]{x, y, z, worldName});
        SetHome plugin = SetHome.getInstance();
        plugin.getConfig().set("homes." + uuid + "." + homeName + ".x", x);
        plugin.getConfig().set("homes." + uuid + "." + homeName + ".y", y);
        plugin.getConfig().set("homes." + uuid + "." + homeName + ".z", z);
        plugin.getConfig().set("homes." + uuid + "." + homeName + ".world", worldName);
        plugin.saveConfig();

        String homeNumber = homeName.substring(4);
        String message = "§x§0§0§9§4§F§FHome " + homeNumber + " §fset";
        send(player, message);
        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1f, 1f);

        return true;
    }

    private void send(Player player, String message) {
        player.sendMessage(message);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
    }
}
