package com.gmail.filoghost.healthbar;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.MetricsLite;

import com.gmail.filoghost.healthbar.utils.Debug;
import com.gmail.filoghost.healthbar.utils.PlayerBarUtils;
import com.gmail.filoghost.healthbar.utils.Utils;


public class HealthBar extends JavaPlugin {
    
    public static HealthBar plugin;
    public static Logger logger;
    
    private static DamageListener damageListener;
    private static DeathListener deathListener;
    private static MiscListeners miscListeners;

    @Override
    public void onEnable() {
        
        plugin = this;
        logger = getLogger();
        
        damageListener = new DamageListener();
        deathListener = new DeathListener();
        miscListeners = new MiscListeners();
        
        //to check if I've forgot the debug on :)
        Debug.color("�c[HealthBar] Debug ON");        
        
        //create the folder and the file
        if (getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
        Utils.loadFile("config.yml", this);
            
        //register events
        getServer().getPluginManager().registerEvents(damageListener, this);
        getServer().getPluginManager().registerEvents(deathListener, this);
        getServer().getPluginManager().registerEvents(miscListeners, this);
        
        //other files
        reloadConfigFromDisk();
            
        //setup for command executor
        getCommand("healthbar").setExecutor(new Commands(this));
            
        //metrics
        try {
            MetricsLite metrics = new MetricsLite(this);
            metrics.start();
        } catch (Exception e) {}
            
            
//end of onEnable
    }    

    @Override
    public void onDisable() {
        PlayerBarUtils.removeAllHealthbarTeams(Bukkit.getScoreboardManager().getMainScoreboard());
        PlayerBar.removeBelowObj();
        DamageListener.removeAllMobHealthBars();
        System.out.println("HealthBar disabled, all the health bars have been removed.");
    }
    
    
    public void reloadConfigFromDisk() {
        
        reloadConfig();
        //Utils.checkDefaultNodes(getConfig(), this);
        Configuration.checkConfigYML();
        
        Utils.loadFile("custom-mob-bar.yml", this);
        Utils.loadFile("custom-player-bar.yml", this);
        Utils.loadFile("locale.yml", this);
        Utils.loadFile("config.yml", this);
        
        //forces to generate translations, if missing
        Utils.getTranslationMap(this);
        
        DamageListener.loadConfiguration();
        DeathListener.loadConfiguration();
        PlayerBar.loadConfiguration();
        MiscListeners.loadConfiguration();
    }

    public static MiscListeners getLoginListenerInstance() {
        return miscListeners;
    }
    
    public static File getPluginFile() {
        return plugin.getFile();
    }

//end of the class
}
