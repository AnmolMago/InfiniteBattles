package net.ArtificialCraft.InfiniteBattles.Entities.Battles.BattleHandler;

import net.ArtificialCraft.InfiniteBattles.Entities.Arena.LocationType;
import net.ArtificialCraft.InfiniteBattles.Entities.Battles.Battle;
import net.ArtificialCraft.InfiniteBattles.Entities.Battles.Status;
import net.ArtificialCraft.InfiniteBattles.Entities.Contestant.Contestant;
import net.ArtificialCraft.InfiniteBattles.IBattle;
import net.ArtificialCraft.InfiniteBattles.Misc.Util;
import net.ArtificialCraft.InfiniteBattles.ScoreBoard.ScoreboardHandler;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
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
	String blueHolder = null, redHolder = null;
	Team redTeam, blueTeam;
	Scoreboard board = getBattle().getScoreboard();
	HashMap<String, Integer> points = new HashMap<String, Integer>();

	public CaptureTheFlag(Battle b){
		super(b);
		redLoc = getBattle().getArena().getLocation(LocationType.redflag);
		blueLoc = getBattle().getArena().getLocation(LocationType.blueflag);
		redBlock = redLoc.getWorld().getBlockAt(redLoc);
		redBlock.setTypeIdAndData(35, (byte)14, false);
		redLoc = redBlock.getLocation();
		blueBlock = blueLoc.getWorld().getBlockAt(blueLoc);
		blueBlock.setTypeIdAndData(35, (byte)11, false);
		blueLoc = blueBlock.getLocation();
		redTeam = ScoreboardHandler.createTeam(board, "redTeam");
		blueTeam = ScoreboardHandler.createTeam(board, "blueTeam");
		List<Contestant> teams = new ArrayList<Contestant>(getBattle().getContestants());
		int half = teams.size() / 2;
		for(int i = 0; i < half; i++)
			redTeam.addPlayer(teams.remove(i).getPlayer());

		for(Contestant c : teams)
			blueTeam.addPlayer(c.getPlayer());

		Objective ob = board.registerNewObjective("Score", "dummy");
		ob.setDisplaySlot(DisplaySlot.SIDEBAR);
		ob.setDisplayName("Team Points");
		ob.getScore(Bukkit.getOfflinePlayer("RedTeam")).setScore(0);
		ob.getScore(Bukkit.getOfflinePlayer("BlueTeam")).setScore(0);
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
			inv.addItem(new ItemStack(Material.STONE_SWORD, 1), new ItemStack(Material.BOW, 1), new ItemStack(Material.ARROW, 128));
			p.updateInventory();
			p.setScoreboard(board);
		}
		start();
	}

	@Override
	public void start(){
		for(OfflinePlayer offp : redTeam.getPlayers())
			IBattle.getContestant(offp.getName()).teleport(getBattle().getArena().getLocation(LocationType.second));

		for(OfflinePlayer offp : blueTeam.getPlayers())
			IBattle.getContestant(offp.getName()).teleport(getBattle().getArena().getLocation(LocationType.first));

		getBattle().setStatus(Status.Started);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInteract(PlayerInteractEvent e){
		if(!isBattleEvent(e)){return;}
		Player p = e.getPlayer();
		if(e.getClickedBlock() == null){return;}
		if(e.getClickedBlock().equals(redBlock)){
			if(board.getPlayerTeam(p).equals(redTeam)){
				if(redBlock.getLocation().equals(redLoc)){
					Util.error(p, "Your flag is safe!");
				}else{
					e.getClickedBlock().setType(Material.AIR);
					redLoc.getWorld().getBlockAt(redLoc).setTypeIdAndData(35, (byte)14, false);
					getBattle().msgUsers("The red team has recovered their flag!");
					redBlock = redLoc.getWorld().getBlockAt(redLoc);
				}
			}else if(board.getPlayerTeam(p).equals(blueTeam)){
				redHolder = p.getName();
				e.getClickedBlock().setType(Material.AIR);
				e.getPlayer().getInventory().addItem(new ItemStack(Material.WOOL, 1, (byte)14));
				redBlock = null;
				getBattle().msgUsers(ChatColor.BLUE + e.getPlayer().getName() + ChatColor.RED + " has captured the flag of the red team!");
			}
		}else if(e.getClickedBlock().equals(blueBlock)){
			if(board.getPlayerTeam(p).equals(blueTeam)){
				if(blueBlock.getLocation().equals(blueLoc)){
					Util.error(p, "Your flag is safe!");
				}else{
					e.getClickedBlock().setType(Material.AIR);
					blueLoc.getWorld().getBlockAt(blueLoc).setTypeIdAndData(35, (byte)11, false);
					getBattle().msgUsers("The blue team has recovered their flag!");
					blueBlock = blueLoc.getWorld().getBlockAt(blueLoc);
				}
			}else if(board.getPlayerTeam(p).equals(redTeam)){
				blueHolder = p.getName();
				e.getClickedBlock().setType(Material.AIR);
				e.getPlayer().getInventory().addItem(new ItemStack(Material.WOOL, 1, (byte)11));
				blueBlock = null;
				getBattle().msgUsers(ChatColor.BLUE + e.getPlayer().getName() + ChatColor.RED + " has captured the flag of the blue team!");
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDeath2(PlayerDeathEvent e){
		if(!isBattleEvent(e)){return;}
			e.getDrops().clear();
		if(e.getEntity().getName().equalsIgnoreCase(redHolder)){
			Block bl = e.getEntity().getWorld().getBlockAt(e.getEntity().getLocation());
			bl.setTypeIdAndData(35, (byte)14, false);
			redBlock = bl;
			redHolder = null;
			bl.getWorld().strikeLightningEffect(redBlock.getLocation());
		}else if(e.getEntity().getName().equalsIgnoreCase(blueHolder)){
			Block bl = e.getEntity().getWorld().getBlockAt(e.getEntity().getLocation());
			bl.setTypeIdAndData(35, (byte)11, false);
			blueBlock = bl;
			blueHolder = null;
			bl.getWorld().strikeLightningEffect(blueBlock.getLocation());
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onLogout(PlayerQuitEvent e){
		if(!isBattleEvent(e)){return;}
		if(e.getPlayer().getName().equalsIgnoreCase(redHolder)){
			e.getPlayer().getInventory().remove(Material.WOOL);
			Block bl = e.getPlayer().getWorld().getBlockAt(e.getPlayer().getLocation());
			bl.setTypeIdAndData(35, (byte)14, false);
			redBlock = bl;
		}else if(e.getPlayer().getName().equalsIgnoreCase(blueHolder)){
			e.getPlayer().getInventory().remove(Material.WOOL);
			Block bl = e.getPlayer().getWorld().getBlockAt(e.getPlayer().getLocation());
			bl.setTypeIdAndData(35, (byte)11, false);
			blueBlock = bl;
		}
	}

	@EventHandler
	public void onPlace(BlockPlaceEvent e){
		if(!isBattleEvent(e)){return;}
		e.setCancelled(true);
		e.getPlayer().updateInventory();
	}

	@EventHandler
	public void onBreak(BlockBreakEvent e){
		if(!isBattleEvent(e)){return;}
		e.setCancelled(true);
		e.getPlayer().updateInventory();
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onMove(PlayerMoveEvent e){
		if(!isBattleEvent(e)){return;}
		Player p = e.getPlayer();
		if(p.getName().equalsIgnoreCase(redHolder) || p.getName().equalsIgnoreCase(blueHolder)){
			if(e.getPlayer().isSprinting())
				e.getPlayer().setSprinting(false);
			p.getWorld().playEffect(p.getLocation(), Effect.MOBSPAWNER_FLAMES, 0);
			if(redHolder != null && blueHolder != null)
				return;
			Location homeblock = p.getName().equalsIgnoreCase(redHolder) ? blueLoc : redLoc;
			if(isCloseEnoughTo(p.getLocation(), homeblock)){
				if(p.getName().equalsIgnoreCase(redHolder)){
					getBattle().msgUsers("The blue team just scored a point!");
					redHolder = null;
				}else{
					getBattle().msgUsers("The red team just scored a point!");
					blueHolder = null;
				}
				addPoint(p);
			}
		}
	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent e){
		if(!isBattleEvent(e)){return;}
		Player p = e.getPlayer();
		PlayerInventory inv = p.getInventory();
		inv.clear();
		Color teamColor = board.getPlayerTeam(p).getName().equalsIgnoreCase("redTeam") ? Color.RED : Color.BLUE;
		inv.setHelmet(colorrize(new ItemStack(Material.LEATHER_HELMET), teamColor));
		inv.setChestplate(colorrize(new ItemStack(Material.LEATHER_CHESTPLATE), teamColor));
		inv.setLeggings(colorrize(new ItemStack(Material.LEATHER_LEGGINGS), teamColor));
		inv.setBoots(colorrize(new ItemStack(Material.LEATHER_BOOTS), teamColor));
		inv.addItem(new ItemStack(Material.STONE_SWORD, 1), new ItemStack(Material.BOW, 1), new ItemStack(Material.ARROW, 128));
		p.updateInventory();
		if(redTeam.hasPlayer(p)){
			e.setRespawnLocation(getBattle().getArena().getLocation(LocationType.second));
		}else{
			e.setRespawnLocation(getBattle().getArena().getLocation(LocationType.first));
		}
	}

	public static boolean isCloseEnoughTo(Location p, Location l){
		return p.getX() >= l.getX() - 3 && p.getX() <= l.getX() + 3 && p.getY() >= l.getY() - 3 && p.getY() <= l.getY() + 3 && p.getZ() >= l.getZ() - 3 && p.getZ() <= l.getZ() + 3;
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
		Objective ob = board.getObjective("Score");
		if(winner == redTeam){
			ob.getScore(Bukkit.getOfflinePlayer("RedTeam")).setScore(points.get(winner.getName()));
		}else{
			ob.getScore(Bukkit.getOfflinePlayer("BlueTeam")).setScore(points.get(winner.getName()));
		}
		if(points.get(winner.getName()) >= 5){
			declareWinner(winner);
		}
	}

	public void declareWinner(Team t){
		for(OfflinePlayer op : t.getPlayers()){
			Contestant c = IBattle.getContestant(op.getName());
			Player p = c.getPlayer();
			if(p != null){
				Util.msg(p, "Your team has won the battle!");
			}
		}
		Team otherTeam = t.equals(redTeam) ? blueTeam : redTeam;
		for(OfflinePlayer op : otherTeam.getPlayers()){
			Contestant c = IBattle.getContestant(op.getName());
			Player p = c.getPlayer();
			if(p != null){
				Util.msg(p, "Your team has lost the battle!");
			}
		}
		getBattle().end(t, otherTeam);
	}

	public ItemStack colorrize(ItemStack item, Color color) {
		LeatherArmorMeta meta = (LeatherArmorMeta)item.getItemMeta();
		meta.setColor(color);
		item.setItemMeta(meta);
		return item;
	}

	public void goHome(Contestant c){
		if(redTeam.hasPlayer(c.getPlayer())){
			redTeam.removePlayer(c.getPlayer());
			if(redTeam.getPlayers().size() == 0)
				declareWinner(blueTeam);
		}else if(blueTeam.hasPlayer(c.getPlayer())){
			blueTeam.removePlayer(c.getPlayer());
			if(blueTeam.getPlayers().size() == 0)
				declareWinner(redTeam);
		}
		if(c.getPlayer().getName().equalsIgnoreCase(redHolder)){
			Block bl = c.getPlayer().getWorld().getBlockAt(c.getPlayer().getLocation());
			bl.setTypeIdAndData(35, (byte)14, false);
			redBlock = bl;
			redHolder = null;
			bl.getWorld().strikeLightningEffect(redBlock.getLocation());
		}else if(c.getPlayer().getName().equalsIgnoreCase(blueHolder)){
			Block bl = c.getPlayer().getWorld().getBlockAt(c.getPlayer().getLocation());
			bl.setTypeIdAndData(35, (byte)11, false);
			blueBlock = bl;
			blueHolder = null;
			bl.getWorld().strikeLightningEffect(blueBlock.getLocation());
		}
	}

}
