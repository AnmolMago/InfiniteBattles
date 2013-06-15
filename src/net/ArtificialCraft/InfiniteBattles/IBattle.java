package net.ArtificialCraft.InfiniteBattles;

import net.ArtificialCraft.InfiniteBattles.Commands.CommandManager;
import net.ArtificialCraft.InfiniteBattles.Commands.ICommand;
import net.ArtificialCraft.InfiniteBattles.Entities.Contestant.Contestant;
import net.ArtificialCraft.InfiniteBattles.Entities.Arena.Arena;
import net.ArtificialCraft.InfiniteBattles.Entities.Battles.Battle;
import net.ArtificialCraft.InfiniteBattles.Entities.Battles.BattleHandler.*;
import net.ArtificialCraft.InfiniteBattles.Entities.Battles.BattleType;
import net.ArtificialCraft.InfiniteBattles.Listeners.PlayerListener;
import net.ArtificialCraft.InfiniteBattles.Listeners.SignListener;
import net.ArtificialCraft.InfiniteBattles.Misc.Config;
import net.ArtificialCraft.InfiniteBattles.Misc.Util;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
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

	private static IBattle plugin;
	private static MultiInvAPI miapi;
	private static Location rolepicker, invpicker;

	private static HashMap<String, Arena> arenas = new HashMap<String, Arena>();
	private static HashMap<String, Battle> currentBattles = new HashMap<String, Battle>();
	private static HashMap<String, Contestant> contestants = new HashMap<String, Contestant>();

	public IBattle(){
		plugin = this;
	}

	public void onDisable(){
		Config.saveYamls();
		Config.saveContestants();
		getServer().getScheduler().cancelTasks(this);
		Util.debug("[InfiniteBattles] Plugin disabled!");
	}

	public void onEnable(){
		Config.init(getDataFolder(), getResource("config.yml"));

		Config.loadArenas();
		Config.loadContestants();

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
			currentBattles.put(b.getName().toLowerCase(), b);
	}

	public static void endBattle(Battle b){
		if(currentBattles.containsKey(b.getName().toLowerCase()))
			currentBattles.remove(b.getName().toLowerCase());
	}

	public static Battle getBattle(String s){
		return currentBattles.get(s.toLowerCase());
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
		if(!contestants.containsKey(s.toLowerCase()))
			contestants.put(s.toLowerCase(), new Contestant(Bukkit.getPlayerExact(s)));

		return contestants.get(s.toLowerCase());
	}

	public static HashMap<String, Contestant> getContestants(){
		return contestants;
	}

	public static IBattleHandler getBattleHandler(BattleType bt, Battle b){
		switch(bt){
			case Boat_Wars:
				return new Boat(b);
			case Capture_The_Flag:
				return new CaptureTheFlag(b);
			case Cars:
				return new Cars(b);
			case InvPick:
				return new InvPick(b);
			case Last_Man_Standing:
				return new LMS(b);
			case One_Hit_Ko:
				return new OneHitKO(b);
			case PaintBall:
				return new PaintBall(b);
			case Role_Play:
				return new RolePlay(b);
			case Spleef:
				return new Spleef(b);
		}
		return null;
	}

	public static Battle isPlayerPlaying(String p){
		if(currentBattles.get("battle1") != null && currentBattles.get("battle1").hasContestant(contestants.get(p.toLowerCase()))){
			Util.debug("1");
			return currentBattles.get("battle1");
		}else if(currentBattles.get("battle2") != null && currentBattles.get("battle2").hasContestant(contestants.get(p.toLowerCase()))){
			Util.debug("2");
			return currentBattles.get("battle2");
		}
		return null;
	}

	public static void startBattle(Battle b){
		final String name = b.getName();
		b.startAcceptingContestants();
		if(Bukkit.getPlayer(b.getCreator()) != null)
			b.addContestant(IBattle.getContestant(b.getCreator()));
		Bukkit.getScheduler().runTaskLaterAsynchronously(IBattle.getPlugin(), new Runnable(){
			public void run(){
				Battle b = IBattle.getBattle(name);
				if(b.getContestants().size() > 1){
					b.setUp();
				}else{
					Util.broadcast(b.getContestants().size() + "");
					b.end("there were not enough players");
				}
			}
		}, 200);
	}

	public static MultiInvAPI getMiAPI(){
		return miapi;
	}

	public static Location getRolepicker(){
		return rolepicker;
	}
	public static void setRolepicker(Location rp){
		rolepicker = rp;
	}
	public static Location getInvpicker(){
		return invpicker;
	}
	public static void setInvpicker(Location ip){
		invpicker = ip;
	}
}
