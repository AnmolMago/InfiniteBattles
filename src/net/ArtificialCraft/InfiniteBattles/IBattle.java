package net.ArtificialCraft.InfiniteBattles;

import net.ArtificialCraft.InfiniteBattles.Commands.CommandManager;
import net.ArtificialCraft.InfiniteBattles.Commands.ICommand;
import net.ArtificialCraft.InfiniteBattles.Contestant.Contestant;
import net.ArtificialCraft.InfiniteBattles.Entities.Arena.Arena;
import net.ArtificialCraft.InfiniteBattles.Entities.Battles.Battle;
import net.ArtificialCraft.InfiniteBattles.Entities.Battles.BattleHandler.*;
import net.ArtificialCraft.InfiniteBattles.Entities.Battles.BattleType;
import net.ArtificialCraft.InfiniteBattles.Listeners.PlayerListener;
import net.ArtificialCraft.InfiniteBattles.Listeners.SignListener;
import net.ArtificialCraft.InfiniteBattles.Misc.Config;
import net.ArtificialCraft.InfiniteBattles.Misc.Util;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import uk.co.tggl.pluckerpluck.multiinv.MultiInv;
import uk.co.tggl.pluckerpluck.multiinv.MultiInvAPI;

import java.util.HashMap;

/**
 * Enclosed in project InfiniteBattles for Aurora Enterprise.
 * Author: Josh Aurora
 * Date: 2013-04-27
 */
public class IBattle extends JavaPlugin{

	private FileConfiguration config;
	private static IBattle plugin;
	private static MultiInvAPI miapi;

	private static HashMap<String, Arena> arenas = new HashMap<String, Arena>();
	private static HashMap<String, Battle> currentBattles = new HashMap<String, Battle>();

	private static HashMap<String, Contestant> contestants = new HashMap<String, Contestant>();

	public IBattle(){
		plugin = this;
	}

	public void onDisable(){
		Config.saveYamls();
		getServer().getScheduler().cancelTasks(this);
		Util.debug("[InfiniteBattles] Plugin disabled!");
	}

	public void onEnable(){
		Config.init(getDataFolder(), getResource("config.yml"));
		config = Config.getConfig();

		Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
		Bukkit.getPluginManager().registerEvents(new SignListener(), this);

		miapi = new MultiInvAPI((MultiInv)getServer().getPluginManager().getPlugin("MultiInv"));

		Util.debug("[InfiniteBattles] Plugin enabled!");
	}

	public static IBattle getPlugin(){
		return plugin;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		CommandManager.execute(sender, cmd.getName(), args);
		return true;
	}

	public static void help(ICommand cmd){

	}

	public static Arena getArena(String s){
		return arenas.get(s);
	}

	public static void addArena(Arena s){
		arenas.put(s.getName(), s);
	}

	public static void addBattle(Battle b){
		if(!currentBattles.containsKey(b.getName()))
			currentBattles.put(b.getName(), b);
	}

	public static Battle getBattle(String s){
		return currentBattles.get(s);
	}

	public static HashMap<String, Battle> getCurrentBattles(){
		return currentBattles;
	}

	public static HashMap<String, Arena> getArenas(){
		return arenas;
	}

	public static void addContestant(Contestant c){
		contestants.put(c.getName().toLowerCase(), c);
	}

	public static boolean doesContestantExist(String c){
		return contestants.containsKey(c.toLowerCase());
	}

	public static Contestant getContestant(String s){
		if(contestants.containsKey(s.toLowerCase()))
			return contestants.get(s.toLowerCase());

		return null;
	}

	public static IBattleHandler getBattleHandler(BattleType bt, Battle b){
		switch(bt){
			case Capture_The_Flag:
				return new CaptureTheFlag(b);
			case Spleef:
				return new Spleef(b);
			case PaintBall:
				return new PaintBall(b);
			case One_Hit_Ko:
				return new OneHitKO(b);
			case Role_Play:
				return new RolePlay(b);
			default:
				return new FreeForAll(b);
		}
	}

	public static Battle isPlayerPlaying(String p){
		if(currentBattles.get("Battle1") != null && currentBattles.get("Battle1").hasContestant(contestants.get(p.toLowerCase()))){
			return currentBattles.get("Battle1");
		}else if(currentBattles.get("Battle2") != null && currentBattles.get("Battle2").hasContestant(contestants.get(p.toLowerCase()))){
			return currentBattles.get("Battle2");
		}
		return null;
	}

	public static MultiInvAPI getMiAPI(){
		return miapi;
	}

}
