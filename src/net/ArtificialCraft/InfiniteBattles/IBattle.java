package net.ArtificialCraft.InfiniteBattles;

import net.ArtificialCraft.InfiniteBattles.Commands.CommandManager;
import net.ArtificialCraft.InfiniteBattles.Entities.Battles.Battle;
import net.ArtificialCraft.InfiniteBattles.Entities.Battles.BattleHandler.*;
import net.ArtificialCraft.InfiniteBattles.Entities.Battles.BattleType;
import net.ArtificialCraft.InfiniteBattles.Entities.Contestant.Contestant;
import net.ArtificialCraft.InfiniteBattles.Entities.QueueHandler;
import net.ArtificialCraft.InfiniteBattles.Listeners.PlayerListener;
import net.ArtificialCraft.InfiniteBattles.Listeners.SignListener;
import net.ArtificialCraft.InfiniteBattles.Misc.Config;
import net.ArtificialCraft.InfiniteBattles.Misc.Util;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import uk.co.tggl.pluckerpluck.multiinv.MultiInv;
import uk.co.tggl.pluckerpluck.multiinv.MultiInvAPI;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Enclosed in project InfiniteBattles for Aurora Enterprise.
 * Author: Josh Aurora
 * Date: 2013-04-27
 */
public class IBattle extends JavaPlugin implements Listener {

	private static IBattle plugin;
	private static MultiInvAPI miapi;
	private static Location rolepicker, invpicker;

	private static ConcurrentHashMap<String, Battle> currentBattles = new ConcurrentHashMap<String, Battle>();
	private static HashMap<String, Contestant> contestants = new HashMap<String, Contestant>();
    public static HashMap<String, Location> workaround = new HashMap<String, Location>();

	public IBattle(){
		plugin = this;
	}

	public void onDisable(){
		Config.saveYamls();
		Config.saveContestants();
		getServer().getScheduler().cancelTasks(this);
		for(Battle b : currentBattles.values())
			b.end("the plugin is being debugged!");

		Util.debug("[InfiniteBattles] Plugin disabled!");
	}

	public void onEnable(){
		Config.init(getDataFolder(), getResource("config.yml"));

		Config.loadArenas();
		Config.loadContestants();

		Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        Bukkit.getPluginManager().registerEvents(new SignListener(), this);
        Bukkit.getPluginManager().registerEvents(this, this);

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

	public static void addBattle(Battle b){
		if(!currentBattles.containsKey(b.getName().toLowerCase()))
			currentBattles.put(b.getName().toLowerCase(), b);
	}

	public static void endBattle(Battle b){
		if(currentBattles.containsKey(b.getName().toLowerCase())){
			currentBattles.remove(b.getName().toLowerCase());

			if(QueueHandler.getQueue().peek() != null){
				Battle newB = QueueHandler.getQueue().poll();
				String name = IBattle.getCurrentBattles().containsKey("battle1") ? "Battle2" : "Battle1";
				newB.setName(name);
				IBattle.startBattle(newB);
			}
		}
	}

	public static Battle getBattle(String s){
		if(!currentBattles.containsKey(s.toLowerCase()))
			return null;
		return currentBattles.get(s.toLowerCase());
	}

	public static ConcurrentHashMap<String, Battle> getCurrentBattles(){
		return currentBattles;
	}

	public static void addContestant(Contestant c){
		contestants.put(c.getName().toLowerCase(), c);
	}

	public static boolean doesContestantExist(String c){
		return contestants.containsKey(c.toLowerCase());
	}

	public static Contestant getContestant(String s){
		if(!contestants.containsKey(s.toLowerCase()) && Bukkit.getPlayerExact(s) != null)
			contestants.put(s.toLowerCase(), new Contestant(Bukkit.getPlayerExact(s)));

		return contestants.get(s.toLowerCase());
	}

	public static HashMap<String, Contestant> getContestants(){
		return contestants;
	}

	public static IBattleHandler getBattleHandler(BattleType bt, Battle b){
		switch(bt){
			case Capture_The_Flag:
				return new CaptureTheFlag(b);
			case Duck_Hunt:
				return new DuckHunt(b);
			case Infection:
				return new Infection(b);
			case InvPick:
				return new InvPick(b);
			case PaintBall:
				return new PaintBall(b);
			case Role_Play:
				return new RolePlay(b);
			case Spleef:
				return new Spleef(b);
			default:
				return new FreeForAll(b);
		}
	}

	public static Battle isPlayerPlaying(String p){
		if(currentBattles.get("battle1") != null && currentBattles.get("battle1").hasContestant(contestants.get(p.toLowerCase()))){
			return currentBattles.get("battle1");
		}else if(currentBattles.get("battle2") != null && currentBattles.get("battle2").hasContestant(contestants.get(p.toLowerCase()))){
			return currentBattles.get("battle2");
		}
		return null;
	}

    public static Battle isPlayerSpectating(String p){
        if(currentBattles.get("battle1") != null && currentBattles.get("battle1").hasSpectator(contestants.get(p.toLowerCase()))){
            return currentBattles.get("battle1");
        }else if(currentBattles.get("battle2") != null && currentBattles.get("battle2").hasSpectator(contestants.get(p.toLowerCase()))){
            return currentBattles.get("battle2");
        }
        return null;
    }

	public static void startBattle(Battle b){
		final String name = b.getName();
		b.startAcceptingContestants();
		if(Bukkit.getPlayer(b.getCreator()) != null){
			if(isPlayerPlaying(b.getCreator()) == null){
				b.addContestant(IBattle.getContestant(b.getCreator()));
			}
		}
		addBattle(b);
		Bukkit.getScheduler().runTaskLater(IBattle.getPlugin(), new Runnable(){
			public void run(){
				Battle b = IBattle.getBattle(name);
				if(b == null)
					return;
				if(b.getContestants().size() > 1){
					b.setUp();
				}else{
					b.end("there were not enough players");
				}
			}
		}, 400);
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

    @EventHandler
    public void onSurvivalEnter(PlayerChangedWorldEvent e){
        if(e.getPlayer().getWorld().getName().startsWith("Survival") && workaround.containsKey(e.getPlayer().getName())){
            final Location loc = workaround.get(e.getPlayer().getName());
	        final String name = e.getPlayer().getName();
	        Util.debug("change: " + e.getPlayer().getName() + " | " + loc);
            if(loc != null){
	            Bukkit.getScheduler().runTaskLater(this, new Runnable(){
		            @Override
		            public void run(){
			            if(Bukkit.getPlayer(name) != null)
			                Bukkit.getPlayer(name).teleport(loc);
			            Util.debug("IBteleported: " + name);
		            }
	            }, 100);
            }
            workaround.remove(e.getPlayer().getName());
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e){
        if(workaround.containsKey(e.getPlayer().getName())){
            final String name = e.getPlayer().getName();
            Bukkit.getScheduler().runTaskLater(this, new Runnable() {
                @Override
                public void run() {
                    Player p = Bukkit.getPlayerExact(name);
                    if(p != null)
                        p.chat("/mvtp survival");
                }
            }, 100);
        }
    }

}
