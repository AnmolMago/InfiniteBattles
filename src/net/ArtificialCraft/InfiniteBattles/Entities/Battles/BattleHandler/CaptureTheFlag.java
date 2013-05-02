package net.ArtificialCraft.InfiniteBattles.Entities.Battles.BattleHandler;

import net.ArtificialCraft.InfiniteBattles.Contestant.Contestant;
import net.ArtificialCraft.InfiniteBattles.Entities.Battles.Battle;
import net.ArtificialCraft.InfiniteBattles.Entities.Battles.BattleType;
import net.ArtificialCraft.InfiniteBattles.IBattle;
import net.ArtificialCraft.InfiniteBattles.Misc.Formatter;
import net.ArtificialCraft.InfiniteBattles.Misc.Util;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.List;

/**
 * Enclosed in project InfiniteBattles for Aurora Enterprise.
 * Author: Josh Aurora
 * Date: 2013-04-27
 */
public class CaptureTheFlag extends IBattleHandler{

	Block redBlock, blueBlock;
	Location redLoc, blueLoc;
	String blueHolder, redHolder;
	Team redTeam, blueTeam;
	Scoreboard board = getBattle().getScoreboard();
	HashMap<String, Integer> points = new HashMap<String, Integer>();

	public CaptureTheFlag(Battle b){
		super(b);
		redLoc = Formatter.parseLoc(config.getString("redBlock"));
		blueLoc = Formatter.parseLoc(config.getString("blueBlock"));
		redBlock = Bukkit.getWorld(redLoc.getWorld().getName()).getBlockAt(redLoc);
		redBlock.setTypeIdAndData(35, (byte)14, false);
		blueBlock = Bukkit.getWorld(blueLoc.getWorld().getName()).getBlockAt(blueLoc);
		blueBlock.setTypeIdAndData(35, (byte)11, false);
		redTeam = getBattle().getScoreboard().registerNewTeam("redTeam");
		redTeam.setCanSeeFriendlyInvisibles(true);
		redTeam.setAllowFriendlyFire(false);
		blueTeam = getBattle().getScoreboard().registerNewTeam("blueTeam");
		blueTeam.setCanSeeFriendlyInvisibles(true);
		blueTeam.setAllowFriendlyFire(false);
		List<Contestant> teams = getBattle().getContestants();
		int half = teams.size() / 2;
		for(int i = 0; i < half; i++)
			redTeam.addPlayer(teams.remove(i).getPlayer());

		for(Contestant c : teams)
			blueTeam.addPlayer(c.getPlayer());

	}

	@Override
	public void load(){

	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInteract(PlayerInteractEvent e){
		if(!isBattleEvent(e)){return;}
		Player p = e.getPlayer();
		if(e.getClickedBlock().equals(redBlock)){
			if(board.getPlayerTeam(p).equals(redTeam)){
				if(redBlock.getLocation().equals(redLoc)){
					Util.error(p, "Your flag is safe!");
				}else{
					e.getClickedBlock().setType(Material.AIR);
					redLoc.getWorld().getBlockAt(redLoc).setTypeIdAndData(35, (byte)14, false);
				}
			}else if(board.getPlayerTeam(p).equals(blueTeam)){
				redHolder = p.getName();
				e.getClickedBlock().setType(Material.AIR);
				e.getPlayer().getInventory().addItem(new ItemStack(Material.WOOL, 1, (byte)14));
			}
		}else if(e.getClickedBlock().equals(blueBlock)){
			if(board.getPlayerTeam(p).equals(blueTeam)){
				if(blueBlock.getLocation().equals(blueLoc)){
					Util.error(p, "Your flag is safe!");
				}else{
					e.getClickedBlock().setType(Material.AIR);
					blueLoc.getWorld().getBlockAt(blueLoc).setTypeIdAndData(35, (byte)11, false);
				}
			}else if(board.getPlayerTeam(p).equals(redTeam)){
				blueHolder = p.getName();
				e.getClickedBlock().setType(Material.AIR);
				e.getPlayer().getInventory().addItem(new ItemStack(Material.WOOL, 1, (byte)11));
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDeath(PlayerDeathEvent e){
		if(!isBattleEvent(e)){return;}
		e.getDrops().clear();
		if(e.getEntity().getName().equalsIgnoreCase(redHolder)){
			e.getEntity().getWorld().getBlockAt(e.getEntity().getLocation()).setTypeIdAndData(35, (byte)14, false);
		}else if(e.getEntity().getName().equalsIgnoreCase(blueHolder)){
			e.getEntity().getWorld().getBlockAt(e.getEntity().getLocation()).setTypeIdAndData(35, (byte)11, false);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onLogout(PlayerQuitEvent e){
		if(!isBattleEvent(e)){return;}
		if(e.getPlayer().getName().equalsIgnoreCase(redHolder)){
			e.getPlayer().getInventory().remove(Material.WOOL);
			e.getPlayer().getWorld().getBlockAt(e.getPlayer().getLocation()).setTypeIdAndData(35, (byte)14, false);
		}else if(e.getPlayer().getName().equalsIgnoreCase(blueHolder)){
			e.getPlayer().getInventory().remove(Material.WOOL);
			e.getPlayer().getWorld().getBlockAt(e.getPlayer().getLocation()).setTypeIdAndData(35, (byte)11, false);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onMove(PlayerMoveEvent e){
		if(!isBattleEvent(e)){return;}
		Player p = e.getPlayer();
		if(p.getName().equalsIgnoreCase(redHolder) || p.getName().equalsIgnoreCase(blueHolder)){
			p.getWorld().playEffect(p.getLocation(), Effect.MOBSPAWNER_FLAMES, 0, 10);
			Location homeblock = p.getName().equalsIgnoreCase(redHolder) ? redLoc : blueLoc;
			if(isCloseEnoughTo(p.getLocation(), homeblock)){
				addPoint(p);
			}
		}
	}

	public static boolean isCloseEnoughTo(Location p, Location l){
		return p.getX() >= l.getX() - 5 && p.getX() <= l.getX() + 5 && p.getY() >= l.getY() - 5 && p.getY() <= l.getY() + 5;
	}

	public void addPoint(Player p){
		Team winner = board.getPlayerTeam(p).equals(blueTeam) ? blueTeam : redTeam;
		if(!points.containsKey(winner.getName()))
			points.put(winner.getName(), 0);

		points.put(winner.getName(), points.get(winner.getName()) + 1);
		if(points.get(winner.getName()) > 3){
			declareWinner(winner);
		}
	}

	public void declareWinner(Team t){
		for(OfflinePlayer op : t.getPlayers()){
			Contestant c = IBattle.getContestant(op.getName());
			Player p = c.getPlayer();
			if(p != null){
				Util.msg(p, "Your team has won the battle!");
				c.onBattlePlayed(BattleType.Capture_The_Flag, true);
			}
		}
		Team otherTeam = t.equals(redTeam) ? blueTeam : redTeam;
		for(OfflinePlayer op : otherTeam.getPlayers()){
			Contestant c = IBattle.getContestant(op.getName());
			Player p = c.getPlayer();
			if(p != null){
				Util.msg(p, "Your team has lost the battle!");
				c.onBattlePlayed(BattleType.Capture_The_Flag, false);
			}
		}
		getBattle().end(t);
	}

}
