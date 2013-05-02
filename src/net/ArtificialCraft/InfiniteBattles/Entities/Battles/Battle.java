package net.ArtificialCraft.InfiniteBattles.Entities.Battles;

import net.ArtificialCraft.InfiniteBattles.Contestant.Contestant;
import net.ArtificialCraft.InfiniteBattles.Entities.Arena.Arena;
import net.ArtificialCraft.InfiniteBattles.Entities.Battles.BattleHandler.IBattleHandler;
import net.ArtificialCraft.InfiniteBattles.IBattle;
import net.ArtificialCraft.InfiniteBattles.Misc.Util;
import net.ArtificialCraft.InfiniteBattles.ScoreBoard.ScoreboardHandler;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import uk.co.tggl.pluckerpluck.multiinv.MultiInvAPI;
import uk.co.tggl.pluckerpluck.multiinv.inventory.MIInventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Enclosed in project InfiniteBattles for Aurora Enterprise.
 * Author: Josh Aurora
 * Date: 2013-04-27
 */
public class Battle{

	long time;
	String name, creator;
	Arena a;
	Status status = Status.Joinable;
	BattleType bt;
	IBattleHandler handler;
	Set<Contestant> contestants = new HashSet<Contestant>();
	HashMap<Contestant, Location> locations = new HashMap<Contestant, Location>();
	HashMap<Contestant, MIInventory> inventories = new HashMap<Contestant, MIInventory>();
	Scoreboard scoreboard;
	MultiInvAPI miapi = IBattle.getMiAPI();

	public Battle(String name, Long time, String creator, BattleType bt, Arena a){
		this.creator = creator;
		this.bt = bt;
		this.a = a;
		this.time = time;
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public String getCreator(){
		return creator;
	}

	public Arena getArena(){
		return a;
	}

	public BattleType getType(){
		return bt;
	}

	public Long getTime(){
		return time;
	}

	public Scoreboard getScoreboard(){
		return scoreboard;
	}

	public boolean isJoinable(){
		return status == Status.Joinable;
	}

	public boolean hasStarted(){
		return status == Status.Started;
	}

	public void startAcceptingContestants(){
		Util.broadcast(ChatColor.DARK_AQUA + name + ChatColor.DARK_RED + " {" + ChatColor.GOLD + bt.getName() + ChatColor.DARK_RED + "} is now accepting contestants! Please type " + ChatColor.DARK_AQUA + "\"/join " + name + "\"" + ChatColor.DARK_RED + " to join!");
	}

	public void start(){
		Util.broadcast(ChatColor.DARK_RED + "The battle " + ChatColor.DARK_AQUA + name + ChatColor.DARK_RED + " has started, you may type " + ChatColor.DARK_AQUA + "\"/spectate Battle1\"" + ChatColor.DARK_RED + " to watch the battle!");
		handler = IBattle.getBattleHandler(bt, this);
		scoreboard = ScoreboardHandler.getNewScoreBoard();
		for(Contestant c : contestants){
			Player p = c.getPlayer();
			locations.put(c, p.getLocation());
			inventories.put(c, miapi.getPlayerInventory(c.getName(), p.getWorld().getName(), GameMode.SURVIVAL));
		}
		if(bt.equals(BattleType.Pick_Inv)){

		}else if(bt.equals(BattleType.Role_Play) || bt.equals(BattleType.Team_Role_Play)){

		}
	}

	public void end(String reason){
		handler.unregisterHandler();
		Util.broadcast(ChatColor.DARK_RED + "The battle " + ChatColor.DARK_AQUA + name + ChatColor.DARK_RED + " was cancelled because " + reason + "!");
	}

	public void end(Contestant c){
		handler.unregisterHandler();
	}

	public boolean addContestant(Contestant c){
		return contestants.add(c);
	}

	public void removeContestant(Contestant c){
		contestants.remove(c);
		locations.remove(c);
		inventories.remove(c);
	}

	public List<Contestant> getContestants(){
		return new ArrayList<Contestant>(locations.keySet());
	}

	public boolean hasContestant(Contestant c){
		return contestants.contains(c);
	}

	public void teleportUsersToArenaTeam(){   //make scoreboard teams too
		for(Contestant c : contestants){
			Player p = c.getPlayer();
			if(p != null){

			}else{
				removeContestant(c);
			}
		}
	}

	public void teleportUsersToArena(){
		for(Contestant c : contestants){
			Player p = c.getPlayer();
			if(p != null){
				p.teleport(a.getSpawns().get(new Random().nextInt(a.getSpawns().size()-1)));
			}else{
				removeContestant(c);
			}
		}
	}

	public void warnUsers(String msg){
		for(Contestant c : contestants)
			if(c.getPlayer() != null)
				Util.error(c.getPlayer(), msg);
	}

}
