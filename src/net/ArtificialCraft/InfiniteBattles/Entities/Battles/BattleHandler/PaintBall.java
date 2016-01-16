package net.ArtificialCraft.InfiniteBattles.Entities.Battles.BattleHandler;

import net.ArtificialCraft.InfiniteBattles.Entities.Arena.LocationType;
import net.ArtificialCraft.InfiniteBattles.Entities.Battles.Battle;
import net.ArtificialCraft.InfiniteBattles.Entities.Battles.Status;
import net.ArtificialCraft.InfiniteBattles.Entities.Contestant.Contestant;
import net.ArtificialCraft.InfiniteBattles.IBattle;
import net.ArtificialCraft.InfiniteBattles.Misc.Util;
import net.ArtificialCraft.InfiniteBattles.ScoreBoard.ScoreboardHandler;
import org.bukkit.Bukkit;
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
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
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
import java.util.concurrent.ConcurrentHashMap;

/**
 * Enclosed in project InfiniteBattles for Aurora Enterprise.
 * Author: Josh Aurora
 * Date: 2013-04-29
 */
public class PaintBall extends IBattleHandler{

	Team redTeam, blueTeam;
	Scoreboard board = getBattle().getScoreboard();
	ConcurrentHashMap<Location, String> mines = new ConcurrentHashMap<Location, String>();
	HashMap<String, Integer> shieldCount = new HashMap<String, Integer>();
	HashMap<String, Integer> armourCount = new HashMap<String, Integer>();
	HashMap<String, Long> reload = new HashMap<String, Long>();
	int redpool, bluepool;

	public PaintBall(Battle b){
		super(b);
	}

	@Override
	public void load(){
		redTeam = ScoreboardHandler.createTeam(board, "redTeam");
		blueTeam = ScoreboardHandler.createTeam(board, "blueTeam");
		List<Contestant> teams = new ArrayList<Contestant>(getBattle().getContestants());
		int half = teams.size() / 2;
		for(int i = 0; i < half; i++)
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
			inv.addItem(new ItemStack(Material.BOW, 1), new ItemStack(Material.IRON_DOOR_BLOCK, 1), new ItemStack(Material.SNOW_BALL, 64));
			if(teamColor.equals(Color.RED)){
				inv.addItem(new ItemStack(Material.WOOL, 1, (byte)14));
			}else{
				inv.addItem(new ItemStack(Material.WOOL, 1, (byte)11));
			}
			p.updateInventory();
			p.setScoreboard(board);
			reload.put(p.getName(), System.currentTimeMillis());
		}
		start();
	}

	@Override
	public void start(){
		for(OfflinePlayer op : redTeam.getPlayers())
			IBattle.getContestant(op.getName()).teleport(getBattle().getArena().getLocation(LocationType.first));

		for(OfflinePlayer op : blueTeam.getPlayers())
			IBattle.getContestant(op.getName()).teleport(getBattle().getArena().getLocation(LocationType.second));

		int i = blueTeam.getPlayers().size() * 3;
		if(i > 30)
			i = 30;
		redpool = i;
		bluepool = i;
		Objective lives = board.registerNewObjective("LivesLeft", "dummy");
		lives.setDisplayName("Life Pool");
		lives.setDisplaySlot(DisplaySlot.SIDEBAR);
		lives.getScore(Bukkit.getOfflinePlayer("blueTeam")).setScore(i);
		lives.getScore(Bukkit.getOfflinePlayer("redTeam")).setScore(i);
		getBattle().setStatus(Status.Started);
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e){
		if(!isBattleEvent(e)){return;}
		Player p = e.getPlayer();
		Block below = p.getLocation().add(0, -1, 0).getBlock();
		if(below.getType().equals(Material.WOOL)){
			if(mines.containsKey(below.getLocation()) && (below.getData() == 11 && redTeam.hasPlayer(p) || below.getData() == 14 && blueTeam.hasPlayer(p))){
				p.getWorld().createExplosion(below.getX(), below.getY(), below.getZ(), 8, false, false);
				double h = p.getHealth() - 12;
				if(h < 1)
					h = 1;
				p.setHealth(h);
				String[] old = mines.get(below.getLocation()).split("\\|");
				int id = Integer.parseInt(old[0]);
				byte data = Byte.parseByte(old[1]);
				below.setTypeIdAndData(id, data, true);
				mines.remove(below.getLocation());
			}
		}
	}

	@EventHandler
	public void placeMine(PlayerInteractEvent e){
		if(!isBattleEvent(e) || e.getItem() == null || e.getClickedBlock() == null || !e.getItem().getType().equals(Material.WOOL)){
			return;
		}
		e.setCancelled(true);
		if(isCloseEnoughTo(e.getClickedBlock().getLocation(), getBattle().getArena().getLocation(LocationType.first)) || isCloseEnoughTo(e.getClickedBlock().getLocation(), getBattle().getArena().getLocation(LocationType.second))){
			Util.error(e.getPlayer(), "You cannot place a mine here because it is too close to the player spawns!");
			e.getPlayer().updateInventory();
			return;
		}
		mines.put(e.getClickedBlock().getLocation(), e.getClickedBlock().getTypeId() + "|" + e.getClickedBlock().getData());
		e.getClickedBlock().setTypeIdAndData(e.getItem().getTypeId(), e.getItem().getData().getData(), false);
		e.getPlayer().getInventory().removeItem(new ItemStack(e.getItem().getType(), 1, e.getItem().getDurability()));
		Util.msg(e.getPlayer(), "Your mine has been placed!");
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void throwBall(PlayerInteractEvent e){
		if(!isBattleEvent(e)){return;}
		Player p = e.getPlayer();
		if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getAction().equals(Action.RIGHT_CLICK_AIR)){
			if(e.getItem() == null)
				return;
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
			if(reload.containsKey(p.getName()) && reload.get(p.getName()) + 45000 > System.currentTimeMillis()){
				Util.error(p, "You still have to wait " + (reload.get(p.getName()) + 45000 - System.currentTimeMillis()) / 1000 + " seconds before you can reload!");
			}else{
				if(p.getInventory().contains(Material.SNOW_BALL)){
					Util.error(p, "You still have paintballs!");
					return;
				}
				reload.put(p.getName(), System.currentTimeMillis());
				p.getInventory().addItem(new ItemStack(Material.SNOW_BALL, 64));
				Util.msg(p, "Reload complete!");
			}
		}
	}

	@EventHandler
	public void onHit(EntityDamageByEntityEvent e){
		if(!isBattleEvent(e)){return;}
		if(e.getDamager() instanceof Snowball){
			Player p = (Player)e.getEntity();
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
						if(shieldCount.containsKey(p.getName()))
							shieldCount.remove(p.getName());
						armourCount.remove(p.getName());
						e.setDamage(10000);
					}
				}
			}
		}
	}

	@EventHandler
	public void onDeath2(PlayerDeathEvent e){
		if(!isBattleEvent(e)){return;}
		e.getDrops().clear();
		Player p = e.getEntity();
		Contestant c = IBattle.getContestant(p.getName());
		Objective lives = board.getObjective("LivesLeft");
		if(redTeam.hasPlayer(p)){
			redpool--;
			if(redpool == 0){
				fixBlocks();
				getBattle().end(blueTeam, redTeam);
				return;
			}
			lives.getScore(Bukkit.getOfflinePlayer("redTeam")).setScore(redpool);
			if(p.getKiller() != null)
				p.getKiller().getInventory().addItem(new ItemStack(Material.WOOL, 1, (byte)11));
		}else if(blueTeam.hasPlayer(p)){
			bluepool--;
			if(bluepool == 0){
				fixBlocks();
				getBattle().end(redTeam, blueTeam);
				return;
			}
			lives.getScore(Bukkit.getOfflinePlayer("blueTeam")).setScore(bluepool);
			if(p.getKiller() != null)
				p.getKiller().getInventory().addItem(new ItemStack(Material.WOOL, 1, (byte)14));
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

	@EventHandler
	public void onRespawn(PlayerRespawnEvent e){
		if(!isBattleEvent(e)){return;}
		Player p = e.getPlayer();
		if(redTeam.hasPlayer(p)){
			e.setRespawnLocation(getBattle().getArena().getLocation(LocationType.first));
		}else{
			e.setRespawnLocation(getBattle().getArena().getLocation(LocationType.second));
		}
		PlayerInventory inv = p.getInventory();
		Color teamColor = board.getPlayerTeam(p).getName().equalsIgnoreCase("redTeam") ? Color.RED : Color.BLUE;
		inv.setHelmet(colorrize(new ItemStack(Material.LEATHER_HELMET), teamColor));
		inv.setChestplate(colorrize(new ItemStack(Material.LEATHER_CHESTPLATE), teamColor));
		inv.setLeggings(colorrize(new ItemStack(Material.LEATHER_LEGGINGS), teamColor));
		inv.setBoots(colorrize(new ItemStack(Material.LEATHER_BOOTS), teamColor));
		inv.addItem(new ItemStack(Material.BOW, 1), new ItemStack(Material.IRON_DOOR_BLOCK, 1), new ItemStack(Material.SNOW_BALL, 64));
		p.updateInventory();
	}

	public static boolean isCloseEnoughTo(Location p, Location l){
		return p.getX() >= l.getX() - 10 && p.getX() <= l.getX() + 10 && p.getY() >= l.getY() - 10 && p.getY() <= l.getY() + 10;
	}

	public ItemStack colorrize(ItemStack item, Color color){
		LeatherArmorMeta meta = (LeatherArmorMeta)item.getItemMeta();
		meta.setColor(color);
		item.setItemMeta(meta);
		return item;
	}

	public void fixBlocks(){
		for(Location l : mines.keySet()){
			String[] old = mines.get(l).split("\\|");
			int id = Integer.parseInt(old[0]);
			byte data = Byte.parseByte(old[1]);
			l.getBlock().setTypeIdAndData(id, data, true);
			mines.remove(l);
		}
	}

	public void goHome(Contestant c){
		Objective lives = board.getObjective("LivesLeft");
		if(redTeam.hasPlayer(c.getPlayer())){
			redTeam.removePlayer(c.getPlayer());
			redpool -= 3;
			if(redpool <= 0){
				fixBlocks();
				getBattle().end(blueTeam, redTeam);
				return;
			}
			lives.getScore(Bukkit.getOfflinePlayer("redTeam")).setScore(redpool);
			if(redTeam.getPlayers().size() == 0)
				getBattle().end(blueTeam, redTeam);
		}else if(blueTeam.hasPlayer(c.getPlayer())){
			blueTeam.removePlayer(c.getPlayer());
			redpool -= 3;
			if(bluepool <= 0){
				fixBlocks();
				getBattle().end(redTeam, blueTeam);
				return;
			}
			lives.getScore(Bukkit.getOfflinePlayer("blueTeam")).setScore(bluepool);
			if(blueTeam.getPlayers().size() == 0)
				getBattle().end(redTeam, blueTeam);
		}
	}
}
