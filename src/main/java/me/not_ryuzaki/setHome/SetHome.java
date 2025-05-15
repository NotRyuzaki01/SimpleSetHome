package me.not_ryuzaki.setHome;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class SetHome extends JavaPlugin {
    public static Map<UUID, Object[]> homes = new HashMap<>();
    private static SetHome instance;

    @Override
    public void onEnable() {
        instance = this;
        load();
        getCommand("sethome").setExecutor(new SetHomeCommand());
        getCommand("home").setExecutor(new HomeCommand());
        getServer().getPluginManager().registerEvents(new HomeGUIListener(), this); // register the listener ONCE
    }

    public void save(){
        for (UUID uuid : homes.keySet()) {
            Object[] data = homes.get(uuid);
            getConfig().set("homes." + uuid + ".x", data[0]);
            getConfig().set("homes." + uuid + ".y", data[1]);
            getConfig().set("homes." + uuid + ".z", data[2]);
            getConfig().set("homes." + uuid + ".world", data[3]);
        }
        saveConfig();
    }

    public void load(){
        if (getConfig().isConfigurationSection("homes")) {
            for (String key : getConfig().getConfigurationSection("homes").getKeys(false)) {
                UUID uuid = UUID.fromString(key);
                double x = getConfig().getDouble("homes." + key + ".x");
                double y = getConfig().getDouble("homes." + key + ".y");
                double z = getConfig().getDouble("homes." + key + ".z");
                String world = getConfig().getString("homes." + key + ".world");
                homes.put(uuid, new Object[]{x, y, z, world});
            }
        }
    }

    public static SetHome getInstance() {
        return instance;
    }
}
