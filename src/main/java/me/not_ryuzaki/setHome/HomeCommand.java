package me.not_ryuzaki.setHome;

import me.not_ryuzaki.mainScorePlugin.Combat;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HomeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players!");
            return false;
        }

        if (Combat.isInCombat(player)) {
            player.sendMessage("§cYou can't use /home while in combat!");
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§cYou're in combat!"));
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
            return true;
        }

        HomeGUI.openHomeGUI(player);
        return true;
    }

}