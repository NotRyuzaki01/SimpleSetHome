package me.not_ryuzaki.setHome;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.entity.Player;
import org.bukkit.Location;

public class HomeGUIListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("Your Home")) {
            event.setCancelled(true);

            ItemStack clicked = event.getCurrentItem();
            if (clicked == null || !clicked.hasItemMeta()) return;

            ItemMeta meta = clicked.getItemMeta();
            if (meta.hasDisplayName() && meta.getDisplayName().equals("§6Go Home")) {
                Player player = (Player) event.getWhoClicked();
                double[] coords = SetHome.homes.get(player.getUniqueId());
                if (coords != null) {
                    player.teleport(new Location(player.getWorld(), coords[0], coords[1], coords[2]));
                    player.sendMessage("§aTeleported to your home!");
                } else {
                    player.sendMessage("§cYou don't have a home set!");
                }
                player.closeInventory();
            }
        }
    }
}
