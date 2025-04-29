package me.not_ryuzaki.setHome;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class SetHome extends JavaPlugin {
    public static Map<UUID, double[]> homes = new HashMap<>();

    @Override
    public void onEnable() {
        load();
        getCommand("sethome").setExecutor(new SetHomeCommand());
        getCommand("home").setExecutor(new HomeCommand());
        getServer().getPluginManager().registerEvents(new HomeGUIListener(), this); // register the listener ONCE
    }

    public void save(){
        for (UUID uuid : homes.keySet()) {
            double[] coords = homes.get(uuid);
            getConfig().set("homes." + uuid + ".x", coords[0]);
            getConfig().set("homes." + uuid + ".y", coords[1]);
            getConfig().set("homes." + uuid + ".z", coords[2]);
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
                homes.put(uuid, new double[]{x, y, z});
            }
        }
    }
}
