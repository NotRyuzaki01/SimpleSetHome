package me.not_ryuzaki.setHome;

import org.bukkit.plugin.java.JavaPlugin;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class SetHome extends JavaPlugin {
    public static Map<UUID, Map<String, Object[]>> homes = new HashMap<>();
    private static SetHome instance;

    @Override
    public void onEnable() {
        instance = this;
        load();
        getCommand("sethome").setExecutor(new SetHomeCommand());
        getCommand("home").setExecutor(new HomeCommand());
        getServer().getPluginManager().registerEvents(new HomeGUIListener(), this);
    }

    public void save() {
        for (UUID uuid : homes.keySet()) {
            Map<String, Object[]> playerHomes = homes.get(uuid);
            for (String homeName : playerHomes.keySet()) {
                Object[] data = playerHomes.get(homeName);
                getConfig().set("homes." + uuid + "." + homeName + ".x", data[0]);
                getConfig().set("homes." + uuid + "." + homeName + ".y", data[1]);
                getConfig().set("homes." + uuid + "." + homeName + ".z", data[2]);
                getConfig().set("homes." + uuid + "." + homeName + ".world", data[3]);
            }
        }
        saveConfig();
    }

    public void load() {
        if (getConfig().isConfigurationSection("homes")) {
            for (String uuidStr : getConfig().getConfigurationSection("homes").getKeys(false)) {
                UUID uuid = UUID.fromString(uuidStr);
                Map<String, Object[]> playerHomes = new HashMap<>();
                for (String homeName : getConfig().getConfigurationSection("homes." + uuidStr).getKeys(false)) {
                    double x = getConfig().getDouble("homes." + uuidStr + "." + homeName + ".x");
                    double y = getConfig().getDouble("homes." + uuidStr + "." + homeName + ".y");
                    double z = getConfig().getDouble("homes." + uuidStr + "." + homeName + ".z");
                    String world = getConfig().getString("homes." + uuidStr + "." + homeName + ".world");
                    playerHomes.put(homeName, new Object[]{x, y, z, world});
                }
                homes.put(uuid, playerHomes);
            }
        }
    }

    public static int getMaxHomes(org.bukkit.entity.Player player) {
        if (player.hasPermission("sethome.slots.5")) return 5;
        if (player.hasPermission("sethome.slots.4")) return 4;
        return 2;
    }

    public static SetHome getInstance() {
        return instance;
    }
}
