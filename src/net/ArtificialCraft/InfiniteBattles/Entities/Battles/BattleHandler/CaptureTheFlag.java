package net.ArtificialCraft.InfiniteBattles.Entities.Battles.BattleHandler;

import net.ArtificialCraft.InfiniteBattles.Entities.Contestant.Contestant;
import net.ArtificialCraft.InfiniteBattles.Entities.Battles.Battle;
import net.ArtificialCraft.InfiniteBattles.Entities.Battles.BattleType;
import net.ArtificialCraft.InfiniteBattles.IBattle;
import net.ArtificialCraft.InfiniteBattles.Misc.Formatter;
import net.ArtificialCraft.InfiniteBattles.Misc.Util;
import net.ArtificialCraft.InfiniteBattles.ScoreBoard.ScoreboardHandler;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
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
		redTeam = ScoreboardHandler.createTeam(board, "redTeam");
		blueTeam = ScoreboardHandler.createTeam(board, "blueTeam");
		List<Contestant> teams = new ArrayList<Contestant>(getBattle().getContestants());
		int half = teams.size() / 2;
		for(int i = 0; i <= half; i++)
			redTeam.addPlayer(teams.remove(i).getPlayer());

		for(Contestant c : teams)
			blueTeam.addPlayer(c.getPlayer());
	}

	@Override
	public void load(){
		for(Contestant c : getBattle().getContestants()){
			Player p = c.getPlayer();
			if(p == null){continue;}
			PlayerInventory inv = p.getInventory();
			inv.clear();
			Color teamColor = board.getPlayerTeam(c.getPlayer()).getName().equalsIgnoreCase("redTeam") ? Color.RED : Color.BLUE;
			inv.setHelmet(colorrize(new ItemStack(Material.LEATHER_HELMET), teamColor));
			inv.setChestplate(colorrize(new ItemStack(Material.LEATHER_CHESTPLATE), teamColor));
			inv.setLeggings(colorrize(new ItemStack(Material.LEATHER_LEGGINGS), teamColor));
			inv.setBoots(colorrize(new ItemStack(Material.LEATHER_BOOTS), teamColor));
			p.updateInventory();
			p.setScoreboard(board);
		}
		start();
	}

	@Override
	public void start(){
		for(OfflinePlayer offp : redTeam.getPlayers()){
			Player p = offp.getPlayer();
			if(p != null)
				p.teleport(getBattle().getArena().getSecondSpawn());
		}
		for(OfflinePlayer offp : blueTeam.getPlayers()){
			Player p = offp.getPlayer();
			if(p != null)
				p.teleport(getBattle().getArena().getFirstSpawn());
		}
		Util.broadcast(ChatColor.DARK_RED + "The battle " + ChatColor.DARK_AQUA + getBattle().getName() + ChatColor.DARK_RED + " has started, you may type " + ChatColor.DARK_AQUA + "\"/spectate " + getBattle().getName() + "\"" + ChatColor.DARK_RED + " to watch the battle!");
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
				blueBlock = null;
				getBattle().warnUsers(ChatColor.BLUE + e.getPlayer().getName() + ChatColor.RED + " has captured the flag of the red team!");
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
				blueBlock = null;
				getBattle().warnUsers(ChatColor.BLUE + e.getPlayer().getName() + ChatColor.RED + " has captured the flag of the blue team!");
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDeath(PlayerDeathEvent e){
		if(!isBattleEvent(e)){return;}
			e.getDrops().clear();
		if(e.getEntity().getName().equalsIgnoreCase(redHolder)){
			Block bl = e.getEntity().getWorld().getBlockAt(e.getEntity().getLocation());
			bl.setTypeIdAndData(35, (byte)14, false);
			blueBlock = bl;
		}else if(e.getEntity().getName().equalsIgnoreCase(blueHolder)){
			Block bl = e.getEntity().getWorld().getBlockAt(e.getEntity().getLocation());
			bl.setTypeIdAndData(35, (byte)11, false);
			redBlock = bl;
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onLogout(PlayerQuitEvent e){
		if(!isBattleEvent(e)){return;}
		if(e.getPlayer().getName().equalsIgnoreCase(redHolder)){
			e.getPlayer().getInventory().remove(Material.WOOL);
			Block bl = e.getPlayer().getWorld().getBlockAt(e.getPlayer().getLocation());
			bl.setTypeIdAndData(35, (byte)14, false);
			blueBlock = bl;
		}else if(e.getPlayer().getName().equalsIgnoreCase(blueHolder)){
			e.getPlayer().getInventory().remove(Material.WOOL);
			Block bl = e.getPlayer().getWorld().getBlockAt(e.getPlayer().getLocation());
			bl.setTypeIdAndData(35, (byte)11, false);
			redBlock = bl;
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
		p.getInventory().remove(Material.WOOL);
		Block bl = p.getWorld().getBlockAt(winner == redTeam ? blueLoc : redLoc);
		if(winner.equals(redTeam)){
			bl.setTypeIdAndData(35, (byte)11, false);
			blueBlock = bl;
		}else{
			bl.setTypeIdAndData(35, (byte)14, false);
			redBlock = bl;
		}
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

	public ItemStack colorrize(ItemStack item, Color color) {
		LeatherArmorMeta meta = (LeatherArmorMeta)item.getItemMeta();
		meta.setColor(color);
		item.setItemMeta(meta);
		return item;
	}

}
