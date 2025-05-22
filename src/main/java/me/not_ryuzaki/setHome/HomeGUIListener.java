package me.not_ryuzaki.setHome;

import me.not_ryuzaki.mainScorePlugin.Combat;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class HomeGUIListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();
        ItemStack clicked = event.getCurrentItem();

        if (clicked == null || clicked.getType() == Material.AIR || !clicked.hasItemMeta()) return;
        ItemMeta meta = clicked.getItemMeta();

        if (title.equals("Your Homes")) {
            event.setCancelled(true);

            if (Combat.isInCombat(player)) {
                player.sendMessage("§cYou can't teleport while in combat!");
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§cYou're in combat!"));
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
                player.closeInventory();
                return;
            }

            Location playerLoc = player.getLocation().clone();
            Map<String, Object[]> playerHomes = SetHome.homes.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>());

            if (clicked.getType() == Material.BLUE_BED && meta.getDisplayName().equals("§x§0§0§9§4§F§FHome 1")) {
                teleportToHome(player, playerLoc, "home1");
            } else if (clicked.getType() == Material.GRAY_BED && meta.getDisplayName().equals("§fClick To Set Home 1")) {
                setHome(player, "home1");
            } else if (clicked.getType() == Material.BLUE_BED && meta.getDisplayName().equals("§x§0§0§9§4§F§FHome 2")) {
                teleportToHome(player, playerLoc, "home2");
            } else if (clicked.getType() == Material.GRAY_BED && meta.getDisplayName().equals("§fClick To Set Home 2")) {
                setHome(player, "home2");
            } else if (clicked.getType() == Material.BLUE_DYE && meta.getDisplayName().equals("§cDelete Home 1")) {
                openDeleteConfirmationGUI(player, "home1");
            } else if (clicked.getType() == Material.BLUE_DYE && meta.getDisplayName().equals("§cDelete Home 2")) {
                openDeleteConfirmationGUI(player, "home2");
            }
        } else if (title.startsWith("Delete Home ")) {
            event.setCancelled(true);

            if (!meta.hasDisplayName()) return;
            String displayName = meta.getDisplayName();

            String homeNumber = title.substring("Delete Home ".length());
            String homeName = "home" + homeNumber;

            if (clicked.getType() == Material.LIME_STAINED_GLASS_PANE && displayName.equals("§aConfirm")) {
                SetHome.homes.get(player.getUniqueId()).remove(homeName);
                SetHome.getInstance().getConfig().set("homes." + player.getUniqueId() + "." + homeName, null);
                SetHome.getInstance().saveConfig();

                player.sendMessage("§cYour Home " + homeNumber + " has been deleted!");
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§cYour Home " + homeNumber + " has been deleted!"));
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1f, 1f);

                HomeGUI.openHomeGUI(player);
            } else if (clicked.getType() == Material.RED_STAINED_GLASS_PANE && displayName.equals("§cCancel")) {
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
                player.closeInventory();
                HomeGUI.openHomeGUI(player);
            }
        }
    }

    private void teleportToHome(Player player, Location playerLoc, String homeName) {
        if (me.not_ryuzaki.mainScorePlugin.Combat.isInCombat(player)) {
            player.sendMessage("§cYou can't teleport while in combat!");
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§cYou're in combat!"));
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
            player.closeInventory();
            return;
        }

        Map<String, Object[]> playerHomes = SetHome.homes.get(player.getUniqueId());
        if (playerHomes == null || !playerHomes.containsKey(homeName)) {
            player.sendMessage("§cYou don't have a " + homeName + " set!");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
            player.closeInventory();
            return;
        }

        Object[] data = playerHomes.get(homeName);
        double x = (double) data[0];
        double y = (double) data[1];
        double z = (double) data[2];
        String worldName = (String) data[3];

        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            player.sendMessage("§cYour " + homeName + " world is missing!");
            return;
        }

        Location homeLoc = new Location(world, x, y, z);
        player.closeInventory();

        BukkitRunnable task = new BukkitRunnable() {
            int countdown = 5;
            final Location originalLoc = playerLoc;

            @Override
            public void run() {
                if (!player.isOnline()) {
                    cancel();
                    return;
                }

                if (me.not_ryuzaki.mainScorePlugin.Combat.isInCombat(player)) {
                    player.sendMessage("§cTeleport cancelled — you entered combat!");
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§cTeleport cancelled — in combat!"));
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
                    me.not_ryuzaki.mainScorePlugin.Combat.unregisterTeleportCallback(player.getUniqueId());
                    cancel();
                    return;
                }

                if (hasMoved(originalLoc, player.getLocation())) {
                    player.sendMessage("§cTeleport cancelled because you moved!");
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§cTeleport cancelled because you moved!"));
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
                    me.not_ryuzaki.mainScorePlugin.Combat.unregisterTeleportCallback(player.getUniqueId());
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
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
                    countdown--;
                } else {
                    player.teleport(homeLoc);
                    player.sendMessage("§aTeleported to your home!");
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§aTeleported to your home!"));
                    player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
                    me.not_ryuzaki.mainScorePlugin.Combat.unregisterTeleportCallback(player.getUniqueId());
                    cancel();
                }
            }
        };

        task.runTaskTimer(SetHome.getInstance(), 0L, 20L);

        me.not_ryuzaki.mainScorePlugin.Combat.registerTeleportCancelCallback(player.getUniqueId(), () -> {
            task.cancel();
            player.sendMessage("§cTeleport cancelled — you entered combat!");
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§cTeleport cancelled — in combat!"));
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
        });
    }


    private void setHome(Player player, String homeName) {
        Location loc = player.getLocation();
        double x = loc.getX(), y = loc.getY(), z = loc.getZ();
        String worldName = loc.getWorld().getName();

        Map<String, Object[]> playerHomes = SetHome.homes.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>());
        playerHomes.put(homeName, new Object[]{x, y, z, worldName});

        SetHome.getInstance().getConfig().set("homes." + player.getUniqueId() + "." + homeName + ".x", x);
        SetHome.getInstance().getConfig().set("homes." + player.getUniqueId() + "." + homeName + ".y", y);
        SetHome.getInstance().getConfig().set("homes." + player.getUniqueId() + "." + homeName + ".z", z);
        SetHome.getInstance().getConfig().set("homes." + player.getUniqueId() + "." + homeName + ".world", worldName);
        SetHome.getInstance().saveConfig();

        player.sendMessage("§x§0§0§9§4§F§FHome " + homeName.charAt(4) + "§f set");
        String homeNumber = homeName.substring(4);

        TextComponent homePart = new TextComponent("Home " + homeNumber);
        homePart.setColor(ChatColor.of("#0094FF"));

        TextComponent setPart = new TextComponent(" set");
        setPart.setColor(ChatColor.WHITE);

        homePart.addExtra(setPart);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, homePart);
        player.playSound(loc, Sound.BLOCK_BEACON_ACTIVATE, 1f, 1f);

        HomeGUI.openHomeGUI(player);
    }

    private void openDeleteConfirmationGUI(Player player, String homeName) {
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
        String homeNumber = homeName.substring(4);
        Inventory gui = Bukkit.createInventory(null, 27, "Delete Home " + homeNumber);

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
        bedMeta.setDisplayName("Home " + homeNumber);
        bed.setItemMeta(bedMeta);

        gui.setItem(11, red);
        gui.setItem(13, bed);
        gui.setItem(15, green);

        player.openInventory(gui);
    }

    private boolean hasMoved(Location original, Location current) {
        return original.getBlockX() != current.getBlockX()
                || original.getBlockY() != current.getBlockY()
                || original.getBlockZ() != current.getBlockZ();
    }
}
