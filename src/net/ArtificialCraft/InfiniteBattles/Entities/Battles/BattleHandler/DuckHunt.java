package net.ArtificialCraft.InfiniteBattles.Entities.Battles.BattleHandler;

import net.ArtificialCraft.InfiniteBattles.Entities.Battles.Battle;
import net.ArtificialCraft.InfiniteBattles.Entities.Contestant.Contestant;
import net.ArtificialCraft.InfiniteBattles.IBattle;
import net.ArtificialCraft.InfiniteBattles.Misc.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Enclosed in project InfiniteBattles for Aurora Enterprise.
 * Author: Josh Aurora
 * Date: 2013-04-29
 */
public class DuckHunt extends IBattleHandler{

	public CopyOnWriteArrayList<Entity> chickens = new CopyOnWriteArrayList<Entity>();
	Scoreboard sb = getBattle().getScoreboard();
	public static int c = 1;

	public DuckHunt(Battle b){
		super(b);
	}

	@Override
	public void load(){
		ItemStack head = new ItemStack(Material.LEATHER_HELMET), chest = new ItemStack(Material.LEATHER_CHESTPLATE), legs = new ItemStack(Material.LEATHER_LEGGINGS), boots = new ItemStack(Material.LEATHER_BOOTS), bow = new ItemStack(Material.BOW), arrow = new ItemStack(Material.ARROW);
		bow.addEnchantment(Enchantment.ARROW_INFINITE, 1);
		Objective side = sb.registerNewObjective("ibattle", "dummy");
		side.setDisplaySlot(DisplaySlot.SIDEBAR);
		side.setDisplayName(ChatColor.GOLD + getBattle().getType().getName());
		for(Contestant c : getBattle().getContestants()){
			if(c.getPlayer() != null){
				c.getPlayer().getInventory().setArmorContents(new ItemStack[]{head, chest, legs, boots});
				c.getPlayer().getInventory().addItem(bow, arrow);
				side.getScore(c.getPlayer()).setScore(0);
			}
		}
		start();
	}

	@Override
	public void start(){
		BukkitTask bt = Bukkit.getScheduler().runTaskTimer(IBattle.getPlugin(), new Runnable(){
			@Override
			public void run(){
				if(!chickens.isEmpty()){
					for(Entity e : chickens)
						e.remove();
				}
				for(int i = 0; i < 5 * c; i++){
					Location spawn = getBattle().getArena().getRandomSpawn();
					chickens.add(spawn.getWorld().spawnEntity(spawn, EntityType.CHICKEN));
					Util.broadcast("yolo " + c);
				}
				c++;
			}
		}, 300, 300);
		final int id = bt.getTaskId();
		Bukkit.getScheduler().runTaskLaterAsynchronously(IBattle.getPlugin(), new Runnable(){
			@Override
			public void run(){
				Bukkit.getScheduler().cancelTask(id);
				getBattle().end(highScore());
			}
		}, 2400);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDamage(EntityDamageByEntityEvent e){
		if(!isBattleEvent(e)){return;}
		Util.broadcast("1");
		if(e.getEntityType().equals(EntityType.CHICKEN) && e.getDamager().getType().equals(EntityType.ARROW)){
			Util.broadcast("2");
			Arrow a = (Arrow)e.getDamager();
			Score s = sb.getObjective(DisplaySlot.SIDEBAR).getScore((Player) a.getShooter());
			s.setScore(s.getScore() + 250);
			a.remove();
		}else if(e.getEntityType().equals(EntityType.PLAYER)){
			Player p = (Player)e.getEntity();
			p.setHealth(p.getMaxHealth());
		}
	}

	public Contestant highScore(){
		Contestant c = null;
		int score = 0;
		for(Contestant p : getBattle().getContestants()){
			if(sb.getObjective(DisplaySlot.SIDEBAR).getScore(p.getPlayer()).getScore() > score)
				c = p;
		}
		return c;
	}
}
