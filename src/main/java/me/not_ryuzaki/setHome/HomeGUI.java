package me.not_ryuzaki.setHome;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class HomeGUI {

    public static void openHomeGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 36, "Your Home");

        boolean hasHome = SetHome.homes.containsKey(player.getUniqueId()); // ✅ CHECK FROM MEMORY, NOT CONFIG

        Material bedMaterial = hasHome ? Material.BLUE_BED : Material.RED_BED;
        Material dyeMaterial = hasHome ? Material.BLUE_DYE : Material.GRAY_DYE;

        ItemStack bed = new ItemStack(bedMaterial);
        ItemMeta meta = bed.getItemMeta();

        ItemStack dye = new ItemStack(dyeMaterial);
        ItemMeta metaDye = dye.getItemMeta();

        if (hasHome) {
            meta.setDisplayName("§6Go Home");
            metaDye.setDisplayName("§6Delete Home");
        } else {
            meta.setDisplayName("§6Right Click To Set Home");
            metaDye.setDisplayName("§7No Home To Delete");
        }

        bed.setItemMeta(meta);
        dye.setItemMeta(metaDye);

        gui.setItem(13, bed); // Bed in center
        gui.setItem(22, dye); // Dye below

        player.openInventory(gui);
    }
}
