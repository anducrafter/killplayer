package ch.andu.killMoney;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class KillMoney extends JavaPlugin  implements Listener {

    private static Economy econ = null;
    @Override
    public void onEnable() {
        addDefaults();
        if (!setupEconomy() ) {
            getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Plugin startup logic
        Bukkit.getPluginManager().registerEvents(this,this);

    }
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
    private void addDefaults(){

        saveConfig();

        getConfig().addDefault("moneay_amount",1);
        getConfig().addDefault("kill_message","&7Du hast 7 $ erhalten");
        getConfig().options().copyDefaults(true);
        saveConfig();

    }
    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
    @EventHandler
    public void onKill(PlayerDeathEvent event){
        if(!(event.getPlayer() instanceof Player && event.getPlayer().getKiller()  instanceof Player))return;
        Player killer = event.getPlayer().getKiller();
        int amount = getConfig().getInt("moneay_amount");
      EconomyResponse er = econ.depositPlayer(killer,amount);
    if(er.transactionSuccess()){
        killer.sendMessage(getConfig().getString("moneay_amount").replace("&","§"));
    }else{
        killer.sendMessage("§cError with Vault api, pleas contact anducrafter..");
    }



    }
}
