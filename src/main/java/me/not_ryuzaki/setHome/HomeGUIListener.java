package me.not_ryuzaki.setHome;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

public class HomeGUIListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("Your Home")) {
            event.setCancelled(true);

            ItemStack clicked = event.getCurrentItem();
            if (clicked == null || !clicked.hasItemMeta()) return;
            Player player = (Player) event.getWhoClicked();
            ItemMeta meta = clicked.getItemMeta();

            // --- DELETE HOME ---
            if (clicked.getType() == Material.BLUE_DYE && meta.hasDisplayName() && meta.getDisplayName().equals("§6Delete Home")) {
                SetHome.homes.remove(player.getUniqueId());
                SetHome.getInstance().getConfig().set("homes." + player.getUniqueId(), null);
                SetHome.getInstance().saveConfig();

                player.sendMessage("§cYour home has been deleted!");
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1f, 1f);

                HomeGUI.openHomeGUI(player); // refresh gui
                return;
            }

            // --- TELEPORT HOME ---
            if (meta.hasDisplayName() && meta.getDisplayName().equals("§6Go Home")) {
                double[] coords = SetHome.homes.get(player.getUniqueId());
                if (coords == null) {
                    player.sendMessage("§cYou don't have a home set!");
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
                    player.closeInventory();
                    return;
                }

                new BukkitRunnable() {
                    int countdown = 5;
                    @Override
                    public void run() {
                        if (countdown > 0) {
                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§eTeleporting in §c" + countdown + "§e seconds..."));
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f);
                            countdown--;
                        } else {
                            player.teleport(new Location(player.getWorld(), coords[0], coords[1], coords[2]));
                            player.sendMessage("§aTeleported to your home!");
                            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
                            cancel();
                        }
                    }
                }.runTaskTimer(SetHome.getInstance(), 0L, 20L);

                player.closeInventory();
                return;
            }

            // --- SET HOME ---
            if (meta.hasDisplayName() && meta.getDisplayName().equals("§6Right Click To Set Home")) {
                double x = player.getLocation().getX();
                double y = player.getLocation().getY();
                double z = player.getLocation().getZ();

                SetHome.homes.put(player.getUniqueId(), new double[]{x, y, z});
                SetHome.getInstance().getConfig().set("homes." + player.getUniqueId() + ".x", x);
                SetHome.getInstance().getConfig().set("homes." + player.getUniqueId() + ".y", y);
                SetHome.getInstance().getConfig().set("homes." + player.getUniqueId() + ".z", z);
                SetHome.getInstance().saveConfig();

                player.sendMessage("§aHome has been set at your current location!");
                player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1f, 1f);

                HomeGUI.openHomeGUI(player); // refresh gui
            }
        }
    }
}
