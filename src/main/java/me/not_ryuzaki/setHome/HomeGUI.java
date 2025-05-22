package me.not_ryuzaki.setHome;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;

public class HomeGUI {

    public static void openHomeGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 36, "Your Homes");

        Map<String, Object[]> playerHomes = SetHome.homes.get(player.getUniqueId());
        boolean hasHome = playerHomes != null && !playerHomes.isEmpty();

        // Create home 1 item
        ItemStack home1 = new ItemStack(hasHome && playerHomes.containsKey("home1") ? Material.BLUE_BED : Material.GRAY_BED);
        ItemMeta home1Meta = home1.getItemMeta();
        home1Meta.setDisplayName(hasHome && playerHomes.containsKey("home1") ? "§x§0§0§9§4§F§FHome 1" : "§fClick To Set Home 1");
        home1.setItemMeta(home1Meta);

        // Create home 2 item
        ItemStack home2 = new ItemStack(hasHome && playerHomes.containsKey("home2") ? Material.BLUE_BED : Material.GRAY_BED);
        ItemMeta home2Meta = home2.getItemMeta();
        home2Meta.setDisplayName(hasHome && playerHomes.containsKey("home2") ? "§x§0§0§9§4§F§FHome 2" : "§fClick To Set Home 2");
        home2.setItemMeta(home2Meta);

        // Create delete items
        ItemStack delete1 = new ItemStack(hasHome && playerHomes.containsKey("home1") ? Material.BLUE_DYE : Material.GRAY_DYE);
        ItemMeta delete1Meta = delete1.getItemMeta();
        delete1Meta.setDisplayName(hasHome && playerHomes.containsKey("home1") ? "§cDelete Home 1" : "§7No Home To Delete");
        delete1.setItemMeta(delete1Meta);

        ItemStack delete2 = new ItemStack(hasHome && playerHomes.containsKey("home2") ? Material.BLUE_DYE : Material.GRAY_DYE);
        ItemMeta delete2Meta = delete2.getItemMeta();
        delete2Meta.setDisplayName(hasHome && playerHomes.containsKey("home2") ? "§cDelete Home 2" : "§7No Home To Delete");
        delete2.setItemMeta(delete2Meta);

        gui.setItem(12, home1); // Home 1 on left
        gui.setItem(14, home2); // Home 2 on right
        gui.setItem(21, delete1); // Delete 1 below
        gui.setItem(23, delete2); // Delete 2 below

        player.openInventory(gui);
    }
}