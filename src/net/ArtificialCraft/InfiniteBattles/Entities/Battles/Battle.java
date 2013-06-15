package net.ArtificialCraft.InfiniteBattles.Entities.Battles;

import net.ArtificialCraft.InfiniteBattles.Entities.Arena.Arena;
import net.ArtificialCraft.InfiniteBattles.Entities.Arena.ArenaHandler;
import net.ArtificialCraft.InfiniteBattles.Entities.Battles.BattleHandler.IBattleHandler;
import net.ArtificialCraft.InfiniteBattles.Entities.Contestant.Contestant;
import net.ArtificialCraft.InfiniteBattles.Entities.QueueHandler;
import net.ArtificialCraft.InfiniteBattles.Events.BattleStartEvent;
import net.ArtificialCraft.InfiniteBattles.IBattle;
import net.ArtificialCraft.InfiniteBattles.Misc.Util;
import net.ArtificialCraft.InfiniteBattles.ScoreBoard.ScoreboardHandler;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import uk.co.tggl.pluckerpluck.multiinv.MultiInvAPI;
import uk.co.tggl.pluckerpluck.multiinv.inventory.MIInventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
	private List<String> contestants = new ArrayList<String>();
	private HashMap<String, Location> locations = new HashMap<String, Location>();
	private List<String> spectators = new ArrayList<String>();
	private HashMap<String, Integer> lives = new HashMap<String, Integer>();
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

	public IBattleHandler getHandler(){
		return handler;
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

	public void setUp(){
		handler = IBattle.getBattleHandler(bt, this);
		scoreboard = ScoreboardHandler.getNewScoreBoard(this);
		status = Status.Started;
		for(Contestant c : getContestants()){
			Player p = c.getPlayer();
			locations.put(c.getName(), p.getLocation());
			MIInventory datinv = miapi.getPlayerInventory(p.getName(), p.getWorld().getName(), GameMode.SURVIVAL);
			miapi.setPlayerInventory(p.getName(), "Warfare", GameMode.SURVIVAL, datinv);
			p.teleport(a.getPitstop());
			p.setScoreboard(scoreboard);
		}
		handler.load();
		new BattleStartEvent(getContestants()).callEvent();
	}

	public void onContestantDeath(Player p){
		Contestant c = IBattle.getContestant(p.getName());
		StringBuilder sb = new StringBuilder();
		sb.append(ChatColor.GOLD + c.getName() + ChatColor.BLUE + " has been ");
		if(p.getKiller() != null){
			sb.append("slaughtered by " + ChatColor.DARK_RED + p.getKiller().getName() + ChatColor.BLUE);
		}else{
			sb.append("killed");
		}
		if(lives.containsKey(p.getName())){
			lives.put(p.getName(), lives.get(p.getName()) + 1);
		}else{
			lives.put(p.getName(), 1);
		}
		if((getType().getLives() - lives.get(p.getName())) > 0){
			sb.append(", but has "  + ChatColor.DARK_RED + getLivesLeft(c) + ChatColor.BLUE + " lives left!");
			scoreboard.getObjective("Lives").getScore(p).setScore(getLivesLeft(c));
		}else{
			sb.append("!");
		}
		warnUsers(sb.toString());
		if(getType().getLives() <= lives.get(p.getName())){
			if(contestants.contains(c.getName()))
				contestants.remove(c.getName());
			addSpectator(c);
			c.onBattlePlayed(getType(), false);
		}
		if(contestants.size() == 1){
			end(IBattle.getContestant(contestants.get(0)));
		}
	}

	public int getLivesLeft(Contestant c){
		return getType().getLives() - lives.get(c.getName());
	}

	public void end(String reason){
		warnUsers(ChatColor.DARK_RED + "The battle " + ChatColor.DARK_AQUA + name + ChatColor.DARK_RED + " was cancelled because " + reason + "!");
		endAll();
	}

	public void end(Contestant c){
		Util.broadcast(ChatColor.DARK_RED + "The battle " + ChatColor.DARK_AQUA + name + ChatColor.DARK_RED + " has been won by " + c.getName() + "!");
		c.onBattlePlayed(getType(), true);
		endAll();
	}

	public void end(Team team){
		Util.broadcast(ChatColor.DARK_RED + "The battle " + ChatColor.DARK_AQUA + name + ChatColor.DARK_RED + " was won by the " + (team.getName().contains("red") ? "red team" : "blue team") + "!");
		for(OfflinePlayer p : team.getPlayers()){
			IBattle.getContestant(p.getName()).onBattlePlayed(getType(), true);
		}
		endAll();
	}

	private void endAll(){
		if(handler != null)
			handler.unregisterHandler();

		if(a != null)
			ArenaHandler.addUnusedArena(a);

		for(String name : contestants)
			IBattle.getContestant(name).clearInv().teleport(locations.get(name));

		for(String name : spectators)
			IBattle.getContestant(name).clearInv().teleport(locations.get(name));

		if(QueueHandler.getQueue().peek() != null)
			IBattle.startBattle(QueueHandler.getQueue().poll());

		IBattle.endBattle(this);
	}

	public boolean addContestant(Contestant c){
		return c != null && contestants.add(c.getName());
	}

	public void removeContestant(Contestant c){
		contestants.remove(c.getName());
		locations.remove(c.getName());
		if(contestants.size() == 1){
			end(contestants.get(0));
		}
	}

	public List<Contestant> getContestants(){
		List<Contestant> cons = new ArrayList<Contestant>();
		for(String name : contestants)
			cons.add(IBattle.getContestant(name));
		return cons;
	}

	public boolean hasContestant(Contestant c){
		for(Contestant con : getContestants())
			Util.debug(con.getName());
		return getContestants().contains(c);
	}

	public void warnUsers(String msg){
		for(Contestant c : getContestants())
			if(c.getPlayer() != null)
				Util.error(c.getPlayer(), msg);
	}

	public void addSpectator(Contestant c){
		Player p = c.getPlayer();
		if(contestants.contains(c.getName())){
			removeContestant(c);
		}else if(p != null){
			locations.put(p.getName(), p.getLocation());
		}
		if(p != null){
			spectators.add(c.getName());
			p.teleport(a.getSpectatorspawn());
		}
	}

}
