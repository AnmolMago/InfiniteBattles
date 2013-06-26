package net.ArtificialCraft.InfiniteBattles.Entities.Battles.BattleHandler;

import net.ArtificialCraft.InfiniteBattles.Entities.Battles.Battle;
import net.ArtificialCraft.InfiniteBattles.Entities.Battles.Status;
import net.ArtificialCraft.InfiniteBattles.Entities.Contestant.Contestant;
import net.ArtificialCraft.InfiniteBattles.IBattle;
import net.ArtificialCraft.InfiniteBattles.Misc.Util;
import net.ArtificialCraft.InfiniteBattles.ScoreBoard.ScoreboardHandler;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
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
 * Date: 2013-04-29
 */
public class PaintBall extends IBattleHandler{

	Team redTeam, blueTeam;
	Scoreboard board = getBattle().getScoreboard();
	HashMap<Location, Block> mines = new HashMap<Location, Block>();
	HashMap<String, Integer> shieldCount = new HashMap<String, Integer>();
	HashMap<String, Integer> armourCount = new HashMap<String, Integer>();
	HashMap<String, Long> reload = new HashMap<String, Long>();

	public PaintBall(Battle b){
		super(b);
	}

	@Override
	public void load(){
		redTeam = ScoreboardHandler.createTeam(board, "redTeam");
		blueTeam = ScoreboardHandler.createTeam(board, "blueTeam");
		List<Contestant> teams = new ArrayList<Contestant>(getBattle().getContestants());
		int half = teams.size() / 2;
		for(int i = 0; i <= half; i++)
			redTeam.addPlayer(teams.remove(i).getPlayer());

		for(Contestant c : teams)
			blueTeam.addPlayer(c.getPlayer());
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
			inv.addItem(new ItemStack(Material.SNOW_BALL, 64));
			p.updateInventory();
			p.setScoreboard(board);
		}
		start();
	}

	@Override
	public void start(){
		getBattle().setStatus(Status.Started);
		for(OfflinePlayer op : redTeam.getPlayers())
			IBattle.getContestant(op.getName()).teleport(getBattle().getArena().getFirstSpawn());

		for(OfflinePlayer op : blueTeam.getPlayers())
			IBattle.getContestant(op.getName()).teleport(getBattle().getArena().getSecondSpawn());

		getBattle().msgUsers("The battle has started! Good luck! :P");
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e){
		if(!isBattleEvent(e)){return;}
		Player p = e.getPlayer();
		Block below = p.getLocation().add(0,-1,0).getBlock();
		if(below.getType().equals(Material.WOOL)){
			if(mines.containsKey(below.getLocation()) && (below.getData() == 11 && redTeam.hasPlayer(p) || below.getData() == 14 && blueTeam.hasPlayer(p))){
				p.getWorld().createExplosion(below.getLocation(), 4, false);
				if(mines.containsKey(below.getLocation())){
					Block old = mines.get(below.getLocation());
					below.setTypeIdAndData(old.getTypeId(), old.getData(), false);
					mines.remove(below.getLocation());
				}
			}
		}
	}

	@EventHandler
	public void placeMine(PlayerInteractEvent e){
		if(!isBattleEvent(e) || e.getItem() == null || e.getClickedBlock() == null || !e.getItem().getType().equals(Material.WOOL)){
			return;
		}
		if(isCloseEnoughTo(e.getClickedBlock().getLocation(), getBattle().getArena().getFirstSpawn()) ||
				isCloseEnoughTo(e.getClickedBlock().getLocation(), getBattle().getArena().getSecondSpawn())){
			Util.error(e.getPlayer(), "You cannot place a mine here because it is too close to the player spawns!");
			return;
		}
		mines.put(e.getClickedBlock().getLocation(), e.getClickedBlock());
		e.getClickedBlock().setTypeIdAndData(e.getItem().getTypeId(), e.getItem().getData().getData(), false);
		Util.msg(e.getPlayer(), "Your mine has been placed!");
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void throwBall(PlayerInteractEvent e){
		if(!isBattleEvent(e)){return;}
		Player p = e.getPlayer();
		if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getAction().equals(Action.RIGHT_CLICK_AIR)){
			if(e.getItem().getType().equals(Material.BOW)){
				if(p.getInventory().contains(Material.SNOW_BALL)){
					p.throwSnowball();
					p.getItemInHand().setType(Material.BOW);
					p.getInventory().removeItem(new ItemStack(Material.SNOW_BALL, 1));
					p.updateInventory();
				}
			}else if(e.getItem().getType().equals(Material.SNOW_BALL)){
				Util.error(p, "Right click with a bow to throw the paintballs!");
				e.setCancelled(true);
			}
		}else{
			if(reload.containsKey(p.getName()) && reload.get(p.getName()) + 60000 < System.currentTimeMillis()){
				Util.error(p, "You still have to wait " + (System.currentTimeMillis() - reload.get(p.getName()))/1000 + " seconds before you can reload!");
			}else{
				p.getInventory().addItem(new ItemStack(Material.SNOW_BALL, 64));
				Util.msg(p, "Reload complete!");
			}
		}
	}

	@EventHandler
	public void onHit(EntityDamageByEntityEvent e){
		if(!isBattleEvent(e)){return;}
		if(e.getDamager() instanceof Snowball){
			Player p = (Player) e.getEntity();
			if(p.getItemInHand() != null && p.getItemInHand().getType().equals(Material.IRON_DOOR_BLOCK)){
				if(!shieldCount.containsKey(p.getName())){
					shieldCount.put(p.getName(), 1);
				}else{
					shieldCount.put(p.getName(), shieldCount.get(p.getName()) + 1);
					if(shieldCount.get(p.getName()) >= 50){
						if(p.getInventory().contains(Material.IRON_DOOR_BLOCK)){
							p.getInventory().remove(Material.IRON_DOOR_BLOCK);
							Util.error(p, "Your shield has been broken!");
							shieldCount.put(p.getName(), 999);
						}
					}
				}
			}else{
				if(!armourCount.containsKey(p.getName())){
					armourCount.put(p.getName(), 1);
				}else{
					armourCount.put(p.getName(), armourCount.get(p.getName()) + 1);
					int count = armourCount.get(p.getName());
					PlayerInventory pi = p.getInventory();
					if(count > 5 && pi.getBoots() != null){
						pi.setBoots(null);
						p.getWorld().playSound(p.getLocation(), Sound.ITEM_BREAK, 10, 1);
					}else if(count > 10 && pi.getHelmet() != null){
						pi.setHelmet(null);
						p.getWorld().playSound(p.getLocation(), Sound.ITEM_BREAK, 10, 1);
					}else if(count > 15 && pi.getLeggings() != null){
						pi.setLeggings(null);
						p.getWorld().playSound(p.getLocation(), Sound.ITEM_BREAK, 10, 1);
					}else if(count > 20 && pi.getChestplate() != null){
						pi.setChestplate(null);
						p.getWorld().playSound(p.getLocation(), Sound.ITEM_BREAK, 10, 1);
					}else if(count > 23){
						e.setDamage(10000);
					}
				}
			}
		}
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent e){
		if(!isBattleEvent(e)){return;}
		e.getDrops().clear();
		Player p = e.getEntity();
		getBattle().onContestantDeath(p);
		Contestant c = IBattle.getContestant(p.getName());
		if(getBattle().getType().getLives() <= getBattle().lives.get(p.getName())){
			if(redTeam.hasPlayer(p)){
				redTeam.removePlayer(p);
				if(redTeam.getPlayers().size() == 0)
					getBattle().end(blueTeam);
			}else if(blueTeam.hasPlayer(p)){
				blueTeam.removePlayer(p);
				if(blueTeam.getPlayers().size() == 0)
					getBattle().end(redTeam);
			}
			getBattle().addSpectator(c);
		}
	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent e){
		Player p = e.getPlayer();
		if(redTeam.hasPlayer(p)){
			e.setRespawnLocation(getBattle().getArena().getFirstSpawn());
		}else{
			e.setRespawnLocation(getBattle().getArena().getSecondSpawn());
		}
		PlayerInventory inv = p.getInventory();
		Color teamColor = board.getPlayerTeam(p).getName().equalsIgnoreCase("redTeam") ? Color.RED : Color.BLUE;
		inv.setHelmet(colorrize(new ItemStack(Material.LEATHER_HELMET), teamColor));
		inv.setChestplate(colorrize(new ItemStack(Material.LEATHER_CHESTPLATE), teamColor));
		inv.setLeggings(colorrize(new ItemStack(Material.LEATHER_LEGGINGS), teamColor));
		inv.setBoots(colorrize(new ItemStack(Material.LEATHER_BOOTS), teamColor));
		inv.addItem(new ItemStack(Material.SNOW_BALL, 64));
		p.updateInventory();
	}

	public static boolean isCloseEnoughTo(Location p, Location l){
		return p.getX() >= l.getX() - 10 && p.getX() <= l.getX() + 10 && p.getY() >= l.getY() - 10 && p.getY() <= l.getY() + 10;
	}

	public ItemStack colorrize(ItemStack item, Color color) {
		LeatherArmorMeta meta = (LeatherArmorMeta)item.getItemMeta();
		meta.setColor(color);
		item.setItemMeta(meta);
		return item;
	}
}
