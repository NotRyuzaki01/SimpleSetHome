package me.not_ryuzaki.setHome;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class SetHomeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players!");
            return false;
        }

        UUID uuid = player.getUniqueId();
        if (SetHome.homes.containsKey(uuid)) {
            player.sendMessage(ChatColor.RED + "Home is already set!");
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§cHome is already set!"));
            return true;
        }

        double x = player.getLocation().getX();
        double y = player.getLocation().getY();
        double z = player.getLocation().getZ();
        String worldName = player.getWorld().getName();

        // Save to memory
        SetHome.homes.put(uuid, new Object[]{x, y, z, worldName});

        // Save to config
        SetHome plugin = JavaPlugin.getPlugin(SetHome.class);
        plugin.getConfig().set("homes." + uuid + ".x", x);
        plugin.getConfig().set("homes." + uuid + ".y", y);
        plugin.getConfig().set("homes." + uuid + ".z", z);
        plugin.getConfig().set("homes." + uuid + ".world", worldName);
        plugin.saveConfig();

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§7Home set"));

        return true;
    }
}
