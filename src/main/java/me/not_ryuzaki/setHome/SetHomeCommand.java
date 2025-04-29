package me.not_ryuzaki.setHome;

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
        double x = player.getLocation().getX();
        double y = player.getLocation().getY();
        double z = player.getLocation().getZ();
        UUID uuid = player.getUniqueId();
        SetHome.homes.put(uuid, new double[]{x, y, z});
        player.sendMessage(ChatColor.GOLD + "Set home coordinates: " + x + ", " + y + ", " + z);
        JavaPlugin plugin = JavaPlugin.getPlugin(SetHome.class);
        ((SetHome) plugin).save();

        return true;
    }
}
