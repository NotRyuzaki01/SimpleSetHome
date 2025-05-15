package me.not_ryuzaki.setHome;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

public class HomeGUIListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();
        ItemStack clicked = event.getCurrentItem();

        if (clicked == null || clicked.getType() == Material.AIR || !clicked.hasItemMeta()) return;
        ItemMeta meta = clicked.getItemMeta();

        // --- HOME GUI ---
        if (title.equals("Your Home")) {
            event.setCancelled(true); // Prevent all item movement

            Location playerLoc = player.getLocation().clone();

            // --- DELETE HOME ---
            if (clicked.getType() == Material.BLUE_DYE && meta.getDisplayName().equals("§cDelete Home")) {
                Inventory gui = Bukkit.createInventory(null, 27, "Delete Home");

                ItemStack green = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
                ItemMeta greenMeta = green.getItemMeta();
                greenMeta.setDisplayName("§aConfirm");
                green.setItemMeta(greenMeta);

                ItemStack red = new ItemStack(Material.RED_STAINED_GLASS_PANE);
                ItemMeta redMeta = red.getItemMeta();
                redMeta.setDisplayName("§cCancel");
                red.setItemMeta(redMeta);

                ItemStack bed = new ItemStack(Material.BLUE_BED);
                ItemMeta bedMeta = bed.getItemMeta();
                bedMeta.setDisplayName("Your Home");
                bed.setItemMeta(bedMeta);

                gui.setItem(11, red);
                gui.setItem(13, bed);
                gui.setItem(15, green);

                player.openInventory(gui);
            }

            // --- TELEPORT HOME ---
            else if (meta.getDisplayName().equals("§x§0§0§9§4§F§FGo Home")) {
                Object[] data = SetHome.homes.get(player.getUniqueId());
                if (data == null) {
                    player.sendMessage("§cYou don't have a home set!");
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
                    player.closeInventory();
                    return;
                }

                double x = (double) data[0];
                double y = (double) data[1];
                double z = (double) data[2];
                String worldName = (String) data[3];

                World world = Bukkit.getWorld(worldName);
                if (world == null) {
                    player.sendMessage("§cYour home world is missing!");
                    return;
                }

                Location homeLoc = new Location(world, x, y, z);

                new BukkitRunnable() {
                    int countdown = 5;
                    final Location originalLoc = playerLoc;

                    @Override
                    public void run() {
                        if (hasMoved(originalLoc, player.getLocation())) {
                            player.sendMessage("§cTeleport cancelled because you moved!");
                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§cTeleport cancelled because you moved!"));
                            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
                            cancel();
                            return;
                        }

                        if (countdown > 0) {
                            TextComponent message = new TextComponent("Teleporting in ");
                            message.setColor(ChatColor.WHITE);

                            TextComponent seconds = new TextComponent(String.valueOf(countdown));
                            seconds.setColor(ChatColor.of("#0094FF"));

                            TextComponent suffix = new TextComponent("s");
                            suffix.setColor(ChatColor.of("#0094FF"));

                            message.addExtra(seconds);
                            message.addExtra(suffix);

                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, message);
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f);
                            countdown--;
                        } else {
                            player.teleport(homeLoc);
                            player.sendMessage("§aTeleported to your home!");
                            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
                            cancel();
                        }
                    }
                }.runTaskTimer(SetHome.getInstance(), 0L, 20L);

                player.closeInventory();
            }

            // --- SET HOME ---
            else if (meta.getDisplayName().equals("§fClick To Set Home")) {
                Location loc = player.getLocation();
                double x = loc.getX(), y = loc.getY(), z = loc.getZ();
                String worldName = loc.getWorld().getName();

                SetHome.homes.put(player.getUniqueId(), new Object[]{x, y, z, worldName});
                SetHome.getInstance().getConfig().set("homes." + player.getUniqueId() + ".x", x);
                SetHome.getInstance().getConfig().set("homes." + player.getUniqueId() + ".y", y);
                SetHome.getInstance().getConfig().set("homes." + player.getUniqueId() + ".z", z);
                SetHome.getInstance().getConfig().set("homes." + player.getUniqueId() + ".world", worldName);
                SetHome.getInstance().saveConfig();

                player.sendMessage("§aHome has been set at your current location!");
                player.playSound(loc, Sound.BLOCK_BEACON_ACTIVATE, 1f, 1f);

                HomeGUI.openHomeGUI(player);
            }
        }

        // --- DELETE HOME GUI ---
        else if (title.equals("Delete Home")) {
            event.setCancelled(true); // Cancel any item interaction

            if (!meta.hasDisplayName()) return;
            String displayName = meta.getDisplayName();

            if (clicked.getType() == Material.LIME_STAINED_GLASS_PANE && displayName.equals("§aConfirm")) {
                SetHome.homes.remove(player.getUniqueId());
                SetHome.getInstance().getConfig().set("homes." + player.getUniqueId(), null);
                SetHome.getInstance().saveConfig();

                player.sendMessage("§cYour home has been deleted!");
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1f, 1f);

                HomeGUI.openHomeGUI(player);
            } else if (clicked.getType() == Material.RED_STAINED_GLASS_PANE && displayName.equals("§cCancel")) {
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
                player.closeInventory();
                HomeGUI.openHomeGUI(player);
            }
        }
    }

    private boolean hasMoved(Location original, Location current) {
        return original.getBlockX() != current.getBlockX()
                || original.getBlockY() != current.getBlockY()
                || original.getBlockZ() != current.getBlockZ();
    }
}
