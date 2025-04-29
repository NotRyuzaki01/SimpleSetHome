package me.not_ryuzaki.setHome;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class HomeGUI {

    public static void openHomeGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 9, "Your Home");

        ItemStack bed = new ItemStack(Material.RED_BED);
        ItemMeta meta = bed.getItemMeta();
        meta.setDisplayName("§6Go Home");
        bed.setItemMeta(meta);

        gui.setItem(4, bed); // Middle slot

        player.openInventory(gui);
    }
}
