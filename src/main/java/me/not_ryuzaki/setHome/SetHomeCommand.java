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
import org.bukkit.plugin.java.JavaPlugin;

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
            player.sendMessage("§cYou can't set a home while in combat!");
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§cYou're in combat!"));
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
            return true;
        }

        UUID uuid = player.getUniqueId();
        Map<String, Object[]> playerHomes = SetHome.homes.computeIfAbsent(uuid, k -> new HashMap<>());

        String homeName;
        if (args.length > 0) {
            if (args[0].equals("1") || args[0].equals("2")) {
                homeName = "home" + args[0];
            } else {
                player.sendMessage(ChatColor.RED + "Usage: /sethome [1|2]");
                return true;
            }
        } else {
            homeName = playerHomes.containsKey("home1") ? "home2" : "home1";
        }

        if (playerHomes.containsKey(homeName)) {
            String homeNumber = homeName.substring(4);
            player.sendMessage(ChatColor.RED + "Home " + homeNumber + " is already set!");

            TextComponent message = new TextComponent("Home " + homeNumber + " is already set!");
            message.setColor(ChatColor.RED);
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, message);

            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
            return true;
        }

        double x = player.getLocation().getX();
        double y = player.getLocation().getY();
        double z = player.getLocation().getZ();
        String worldName = player.getWorld().getName();

        playerHomes.put(homeName, new Object[]{x, y, z, worldName});

        SetHome plugin = JavaPlugin.getPlugin(SetHome.class);
        plugin.getConfig().set("homes." + uuid + "." + homeName + ".x", x);
        plugin.getConfig().set("homes." + uuid + "." + homeName + ".y", y);
        plugin.getConfig().set("homes." + uuid + "." + homeName + ".z", z);
        plugin.getConfig().set("homes." + uuid + "." + homeName + ".world", worldName);
        plugin.saveConfig();

        String homeNumber = homeName.substring(4);
        TextComponent homePart = new TextComponent("Home " + homeNumber);
        homePart.setColor(ChatColor.of("#0094FF"));

        TextComponent setPart = new TextComponent(" set");
        setPart.setColor(ChatColor.WHITE);

        homePart.addExtra(setPart);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, homePart);
        player.sendMessage("§x§0§0§9§4§F§FHome " + homeNumber + " §fset");

        return true;
    }
}
