package me.not_ryuzaki.setHome;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class HomeGUI {

    public static void openHomeGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 36, "Your Homes");

        Map<String, Object[]> playerHomes = SetHome.homes.get(player.getUniqueId());
        boolean hasHome = playerHomes != null && !playerHomes.isEmpty();
        int maxHomes = SetHome.getMaxHomes(player);

        int[] bedSlots = {11, 12, 13, 14, 15};
        int[] deleteSlots = {20, 21, 22, 23, 24};

        for (int i = 1; i <= 5; i++) {
            String homeKey = "home" + i;
            boolean owned = hasHome && playerHomes.containsKey(homeKey);

            ItemStack bed = new ItemStack(owned ? Material.BLUE_BED : Material.GRAY_BED);
            ItemMeta bedMeta = bed.getItemMeta();

            if (i <= maxHomes) {
                bedMeta.setDisplayName(owned ? "§x§0§0§9§4§F§FHome " + i : "§fClick To Set Home " + i);
            } else {
                bedMeta.setDisplayName("§cNo Permission");
                bedMeta.setLore(List.of("§7Higher rank required"));
            }

            bed.setItemMeta(bedMeta);
            gui.setItem(bedSlots[i - 1], bed);

            ItemStack dye = new ItemStack(owned ? Material.BLUE_DYE : Material.GRAY_DYE);
            ItemMeta dyeMeta = dye.getItemMeta();

            if (i <= maxHomes && owned) {
                dyeMeta.setDisplayName("§cDelete Home " + i);
            } else {
                dyeMeta.setDisplayName("§7No Home To Delete");
            }

            dye.setItemMeta(dyeMeta);
            gui.setItem(deleteSlots[i - 1], dye);
        }

        player.openInventory(gui);
    }
}
