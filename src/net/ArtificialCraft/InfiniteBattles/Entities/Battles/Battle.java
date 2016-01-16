package net.ArtificialCraft.InfiniteBattles.Entities.Battles;

import net.ArtificialCraft.InfiniteBattles.Entities.Arena.Arena;
import net.ArtificialCraft.InfiniteBattles.Entities.Arena.ArenaHandler;
import net.ArtificialCraft.InfiniteBattles.Entities.Arena.LocationType;
import net.ArtificialCraft.InfiniteBattles.Entities.Battles.BattleHandler.*;
import net.ArtificialCraft.InfiniteBattles.Entities.Contestant.Contestant;
import net.ArtificialCraft.InfiniteBattles.Events.BattleStartEvent;
import net.ArtificialCraft.InfiniteBattles.IBattle;
import net.ArtificialCraft.InfiniteBattles.Misc.Util;
import net.ArtificialCraft.InfiniteBattles.ScoreBoard.ScoreboardHandler;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.kitteh.vanish.staticaccess.VanishNoPacket;
import org.kitteh.vanish.staticaccess.VanishNotLoadedException;
import uk.co.tggl.pluckerpluck.multiinv.MultiInvAPI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

/**
 * Enclosed in project InfiniteBattles for Aurora Enterprise.
 * Author: Josh Aurora
 * Date: 2013-04-27
 */
public class Battle{

	long time;
	String Bname, creator;
	Arena a;
	Status status = Status.Joinable;
	BattleType bt;
	IBattleHandler handler;
	private List<String> contestants = new ArrayList<String>();
	private HashMap<String, Location> locations = new HashMap<String, Location>();
	private List<String> spectators = new ArrayList<String>();
	public HashMap<String, Integer> lives = new HashMap<String, Integer>();
	Scoreboard scoreboard;
	MultiInvAPI miapi = IBattle.getMiAPI();

	public Battle(String name, Long time, String creator, BattleType bt, Arena a){
		this.creator = creator;
		this.bt = bt;
		this.a = a;
		this.time = time;
		this.Bname = name;
	}

	public void setStatus(Status s){
		status = s;
		if(s.equals(Status.Started))
			msgUsers(ChatColor.RED + "You are now fighting, good luck!");
	}

	public void setName(String bname){
		Bname = bname;
	}

	public String getName(){
		return Bname;
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

	public boolean isStarted(){
		return status == Status.Started;
	}

	public void startAcceptingContestants(){
		Util.broadcast(ChatColor.DARK_AQUA + Bname + ChatColor.DARK_RED + " {" + ChatColor.GOLD + bt.getName() + ChatColor.DARK_RED + "} is now accepting contestants! Please type " + ChatColor.DARK_AQUA + "\"/join " + Bname + "\"" + ChatColor.DARK_RED + " to join!");
	}

	public void setUp(){
		if(Bukkit.getWorld("Warfare").getTime() > 10000){
			Bukkit.getWorld("Warfare").setTime(1000);
		}
		scoreboard = ScoreboardHandler.getNewScoreBoard(this);
		handler = IBattle.getBattleHandler(bt, this);
		status = Status.Preparing;
		for(Contestant c : getContestants()){
			Player p = c.getPlayer();
			locations.put(c.getName(), p.getLocation());
			//MIInventory datinv = miapi.getPlayerInventory(p.getName(), p.getWorld().getName(), GameMode.SURVIVAL);
			//miapi.setPlayerInventory(p.getName(), "Warfare", GameMode.SURVIVAL, datinv);
			p.teleport(a.getLocation(LocationType.pitstop));
			c.clearInv();
			p.setGameMode(GameMode.SURVIVAL);
			for(PotionEffect pe : c.getPlayer().getActivePotionEffects())
				c.getPlayer().removePotionEffect(pe.getType());
			p.setScoreboard(scoreboard);
			p.setHealth(p.getMaxHealth());
			p.setFoodLevel(20);
		}
		handler.load();
		new BattleStartEvent(getContestants()).callEvent();
		Util.broadcast(ChatColor.DARK_AQUA + Bname + ChatColor.DARK_RED + " {" + ChatColor.GOLD + bt.getName() + ChatColor.DARK_RED + "} has been started! Please type " + ChatColor.DARK_AQUA + "\"/spectate " + Bname + "\"" + ChatColor.DARK_RED + " to spectate the battle!");
	}

	public void onContestantDeath(Player p){
		Contestant c = IBattle.getContestant(p.getName());
		StringBuilder sb = new StringBuilder();
		if(getType().equals(BattleType.PaintBall))
			return;
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
			if(scoreboard.getObjective("Lives") != null)
				scoreboard.getObjective("Lives").getScore(p).setScore(getLivesLeft(c));
		}else{
			if(handler instanceof Infection){
				if(((Infection)handler).isHuman(p)){
					sb.append(", but has risen as the undead");
				}else{
					sb.append(", but has come back for more");
				}
			}
			sb.append("!");
		}
		msgUsers(sb.toString());
	}

	public int getLivesLeft(Contestant c){
		if(status.equals(Status.Joinable))
			return getType().getLives();
		return getType().getLives() - lives.get(c.getName());
	}

	public void end(String reason){
		Util.broadcast(ChatColor.DARK_RED + "The battle " + ChatColor.DARK_AQUA + Bname + ChatColor.DARK_RED + " was cancelled because " + reason + "!");
		endAll();
	}

	public void end(Contestant c){
		Util.broadcast(ChatColor.DARK_RED + "The battle " + ChatColor.DARK_AQUA + Bname + ChatColor.DARK_RED + " has been won by " + ChatColor.DARK_AQUA + c.getName() + "!");
		c.onBattlePlayed(getType(), true);
		endAll();
	}

	public void end(Team team, Team losers){
		Util.broadcast(ChatColor.DARK_RED + "The battle " + ChatColor.DARK_AQUA + Bname + ChatColor.DARK_RED + " was won by the " + ChatColor.DARK_AQUA + team.getName() + ChatColor.DARK_RED + "!");
		for(OfflinePlayer p : team.getPlayers()){
			IBattle.getContestant(p.getName()).onBattlePlayed(getType(), true);
		}
		for(OfflinePlayer p : losers.getPlayers()){
			IBattle.getContestant(p.getName()).onBattlePlayed(getType(), false);
		}
		endAll();
	}

	private void endAll(){
		if(handler instanceof Spleef){
			((Spleef)handler).restoreBlocks();
		}else if(handler instanceof RolePlay){
			((RolePlay)handler).clearWolves();
		}
		if(handler != null)
			handler.unregisterHandler();

		if(a != null)
			ArenaHandler.addUnusedArena(a);

		for(Contestant c : getContestants()){
			c.clearInv();
            IBattle.workaround.put(c.getName(), locations.get(c.getName()));
			if(c.getPlayer() == null){
				continue;
			}
            if(!c.getPlayer().isDead())
                c.getPlayer().chat("/mvtp survival");
			c.getPlayer().setScoreboard(ScoreboardHandler.getSBM().getNewScoreboard());
			for(PotionEffect pe : c.getPlayer().getActivePotionEffects())
				c.getPlayer().removePotionEffect(pe.getType());
		}

		for(String name : spectators){
			Contestant c = IBattle.getContestant(name);
			Player p = c.getPlayer();
			try{
				if(VanishNoPacket.getManager().isVanished(p)){
					VanishNoPacket.getManager().toggleVanishQuiet(p);
				}
			}catch(VanishNotLoadedException ex){
				for(Player guy : Bukkit.getOnlinePlayers()){
					guy.showPlayer(p);
				}
			}
			p.setFlying(false);
			p.setFlySpeed(p.getFlySpeed() / 2);
			c.clearInv();
            IBattle.workaround.put(c.getName(), locations.get(c.getName()));
            c.getPlayer().chat("/mvtp survival");
		}

		contestants = null;
		spectators = null;
		locations = null;

		IBattle.endBattle(this);
	}

	public boolean addContestant(Contestant c){
		return c != null && contestants.add(c.getName());
	}

	public void removeContestant(Contestant c){
        if(contestants == null)
            return;
		if(contestants.contains(c.getName()))
			contestants.remove(c.getName());
		if(c.getPlayer() != null)
			c.getPlayer().setScoreboard(ScoreboardHandler.getSBM().getNewScoreboard());
	}

	public List<Contestant> getContestants(){
		List<Contestant> cons = new ArrayList<Contestant>();
		if(contestants == null || contestants.isEmpty())
			return cons;
		for(String name : contestants)
			cons.add(IBattle.getContestant(name));
		return cons;
	}

	public boolean hasContestant(Contestant c){
		return getContestants().contains(c);
	}

	public void warnUsers(String msg){
		if(getContestants().isEmpty())
			return;
		for(Contestant c : getContestants())
			if(c.getPlayer() != null)
				Util.error(c.getPlayer(), msg);
	}

	public void msgUsers(String msg){
		if(getContestants().isEmpty())
			return;
		for(Contestant c : getContestants())
			if(c.getPlayer() != null)
				Util.msg(c.getPlayer(), msg);
	}

    public boolean hasSpectator(Contestant c){
        return spectators.contains(c.getName());
    }

	public void addSpectator(Contestant c){
		Player p = c.getPlayer();
		if(p != null && spectators.contains(p.getName())){
			p.sendMessage(ChatColor.RED + "You are already spectating this battle!");
			return;
		}
		if(contestants.contains(c.getName())){
			removeContestant(c);
			//Util.error(p, "Spectating has been disabled so you are being returned to your original location!");
			c.clearInv();
			IBattle.workaround.put(c.getName(), locations.get(c.getName()));
			c.getPlayer().chat("/mvtp survival");
			locations.remove(c.getName());
			return;
		}else if(p != null){
			locations.put(p.getName(), p.getLocation());
		}
		if(p != null){
			spectators.add(c.getName());
			if(p.teleport(a.getLocation(LocationType.spectator))){
				try{
					if(!VanishNoPacket.getManager().isVanished(p)){
						VanishNoPacket.getManager().toggleVanishQuiet(p);
					}
				}catch(VanishNotLoadedException ex){
					p.sendMessage(ChatColor.RED + "Error: We could not let you spectate ... you are now going home!");
					goHome(c);
					return;
				}
				p.setFlying(true);
				p.setFlySpeed(p.getFlySpeed() * 2);
			}
			c.clearInv();
			p.sendMessage(ChatColor.GOLD + "You are now spectating: " + ChatColor.DARK_AQUA + getName() + ChatColor.GOLD + "! Do not get in the way of players or there will be consequences!");
		}
	}

	public boolean goHome(Contestant c){
		if(contestants.contains(c.getName())){
			if(status == Status.Joinable){
				removeContestant(c);
				end("the creator quit!");
				return true;
			}
			warnUsers(c.getName() + " has left the battle!");
			if(getType().equals(BattleType.Capture_The_Flag)){
				CaptureTheFlag ctf = (CaptureTheFlag) handler;
				ctf.goHome(IBattle.getContestant(contestants.get(0)));
			}else if(getType().equals(BattleType.PaintBall)){
				PaintBall pb = (PaintBall) handler;
				pb.goHome(IBattle.getContestant(contestants.get(0)));
			}else if(getType().equals(BattleType.Infection)){
				Infection in = (Infection) handler;
				in.goHome(IBattle.getContestant(contestants.get(0)));
			}
			removeContestant(c);
            c.clearInv();
			if(locations == null)
				return true;
			if(IBattle.workaround == null)
				Util.debug(Level.SEVERE, "workaround is actually null...");
            IBattle.workaround.put(c.getName(), locations.get(c.getName()));
            c.getPlayer().chat("/mvtp survival");
			locations.remove(c.getName());
			c.getPlayer().setScoreboard(ScoreboardHandler.getSBM().getNewScoreboard());
			for(PotionEffect pe : c.getPlayer().getActivePotionEffects())
				c.getPlayer().removePotionEffect(pe.getType());

			if(contestants.size() == 1){
				end(IBattle.getContestant(contestants.get(0)));
			}
		}else if(spectators.contains(c.getName())){
			Player p = c.getPlayer();
			if(p == null)
				return false;
			try{
				if(VanishNoPacket.getManager().isVanished(p)){
					VanishNoPacket.getManager().toggleVanishQuiet(p);
				}
			}catch(VanishNotLoadedException ex){
				for(Player guy : Bukkit.getOnlinePlayers()){
					guy.showPlayer(p);
				}
			}
			p.setFlying(false);
			p.setFlySpeed(p.getFlySpeed() / 2);
            c.clearInv();
            IBattle.workaround.put(c.getName(), locations.get(c.getName()));
            c.getPlayer().chat("/mvtp survival");
			locations.remove(c.getName());
		}else{
			return false;
		}
		return true;
	}

}
