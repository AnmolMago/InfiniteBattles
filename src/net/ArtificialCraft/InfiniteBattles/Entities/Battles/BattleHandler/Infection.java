package net.ArtificialCraft.InfiniteBattles.Entities.Battles.BattleHandler;

import net.ArtificialCraft.InfiniteBattles.Entities.Arena.LocationType;
import net.ArtificialCraft.InfiniteBattles.Entities.Battles.Battle;
import net.ArtificialCraft.InfiniteBattles.Entities.Battles.Status;
import net.ArtificialCraft.InfiniteBattles.Entities.Contestant.Contestant;
import net.ArtificialCraft.InfiniteBattles.IBattle;
import net.ArtificialCraft.InfiniteBattles.ScoreBoard.ScoreboardHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.kitteh.tag.PlayerReceiveNameTagEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Enclosed in project InfiniteBattles for Aurora Enterprise.
 * Author: Josh Aurora
 * Date: 2013-05-08
 */
public class Infection extends IBattleHandler{

	Team zombies, humans;
	Scoreboard board = getBattle().getScoreboard();
	BukkitTask task;

	public Infection(Battle b){
		super(b);
	}

	@Override
	public void load(){
		int zc = (int)Math.ceil(getBattle().getContestants().size() * 0.2);
		zombies = ScoreboardHandler.createTeam(board, "Zombies");
		humans = ScoreboardHandler.createTeam(board, "Humans");
		List<Contestant> teams = new ArrayList<Contestant>(getBattle().getContestants());
		for(int i = 0; i < zc; i++){
			int rand = new Random().nextInt(getBattle().getContestants().size());
			zombies.addPlayer(teams.remove(rand).getPlayer());
		}

		for(Contestant c : teams)
			humans.addPlayer(c.getPlayer());
		start();
	}

	@Override
	public void start(){
		ItemStack head = new ItemStack(Material.DIAMOND_HELMET), chest = new ItemStack(Material.DIAMOND_CHESTPLATE), legs = new ItemStack(Material.DIAMOND_LEGGINGS), boots = new ItemStack(Material.DIAMOND_BOOTS), sword = new ItemStack(Material.IRON_SWORD), bow = new ItemStack(Material.BOW), zhead = new ItemStack(Material.IRON_HELMET), zchest = new ItemStack(Material.IRON_CHESTPLATE), zlegs = new ItemStack(Material.IRON_LEGGINGS), zboots = new ItemStack(Material.IRON_BOOTS), zsword = new ItemStack(Material.DIAMOND_SWORD);
		sword.addEnchantment(Enchantment.DAMAGE_ALL, 1);
		zsword.addEnchantment(Enchantment.KNOCKBACK, 1);
		bow.addEnchantment(Enchantment.ARROW_INFINITE, 1);
		for(Contestant c : getBattle().getContestants()){
			PlayerInventory inv = c.getPlayer().getInventory();
			if(c.getPlayer() == null){return;}
			if(zombies.hasPlayer(c.getPlayer())){
				inv.setArmorContents(new ItemStack[]{zhead, zchest, zboots, zlegs});
				inv.addItem(zsword, bow, new ItemStack(Material.ARROW));
				c.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0), true);
				c.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 0), true);
				c.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, Integer.MAX_VALUE, 0), true);
				c.teleport(getBattle().getArena().getLocation(LocationType.second));
			}else{
				inv.setArmorContents(new ItemStack[]{head, chest, boots, legs});
				inv.addItem(sword, bow, new ItemStack(Material.ARROW));
				c.teleport(getBattle().getArena().getLocation(LocationType.first));
			}
		}
		getBattle().setStatus(Status.Started);
		task = Bukkit.getScheduler().runTaskLater(IBattle.getPlugin(), new Runnable(){
			@Override
			public void run(){
				getBattle().end(humans, zombies);
			}
		}, humans.getSize() * 1000 > 5000 ? 5000 : humans.getSize() * 1000);
	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent e){
		if(!isBattleEvent(e)){return;}
		Player p = e.getPlayer();
		if(humans.hasPlayer(p)){
			humans.removePlayer(p);
			zombies.addPlayer(p);
		}
		if(humans.getPlayers().size() == 0){
			getBattle().end(zombies, humans);
			task.cancel();
			return;
		}
		PlayerInventory inv = p.getInventory();
		ItemStack bow = new ItemStack(Material.BOW), zhead = new ItemStack(Material.IRON_HELMET), zchest = new ItemStack(Material.IRON_CHESTPLATE), zlegs = new ItemStack(Material.IRON_LEGGINGS), zboots = new ItemStack(Material.IRON_BOOTS), zsword = new ItemStack(Material.DIAMOND_SWORD);
		zsword.addEnchantment(Enchantment.KNOCKBACK, 1);
		bow.addEnchantment(Enchantment.ARROW_INFINITE, 1);
		inv.setArmorContents(new ItemStack[]{zhead, zchest, zboots, zlegs});
		inv.addItem(zsword, bow, new ItemStack(Material.ARROW));
		p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0), true);
		p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 0), true);
		p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, Integer.MAX_VALUE, 0), true);
		e.setRespawnLocation(getBattle().getArena().getLocation(LocationType.second));
	}

	@EventHandler
	public void nameTag(PlayerReceiveNameTagEvent e){
		if(zombies.hasPlayer(e.getNamedPlayer())){
			e.setTag(ChatColor.RED + e.getNamedPlayer().getName());
		}else if(humans.hasPlayer(e.getNamedPlayer())){
			e.setTag(ChatColor.AQUA + e.getNamedPlayer().getName());
		}
	}

	public boolean isHuman(OfflinePlayer s){
		return humans.getPlayers().contains(s);
	}

	public void goHome(Contestant c){
		Player p = c.getPlayer();
		if(humans.hasPlayer(p)){
			humans.removePlayer(p);
		}else{
			zombies.removePlayer(p);
		}
		if(humans.getPlayers().size() == 0){
			getBattle().end(zombies, humans);
			task.cancel();
		}else if(zombies.getPlayers().size() == 0){
			getBattle().end(humans, zombies);
			task.cancel();
		}
	}

	/*@Override
	public void load(){
		ItemStack head = new ItemStack(Material.DIAMOND_HELMET), chest = new ItemStack(Material.DIAMOND_CHESTPLATE), legs = new ItemStack(Material.DIAMOND_LEGGINGS), boots = new ItemStack(Material.DIAMOND_BOOTS);
		head.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
		head.addEnchantment(Enchantment.DURABILITY, 3);
		head.addEnchantment(Enchantment.OXYGEN, 3);
		head.addEnchantment(Enchantment.WATER_WORKER, 1);
		chest.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
		chest.addEnchantment(Enchantment.DURABILITY, 3);
		chest.addEnchantment(Enchantment.THORNS, 3);
		legs.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
		legs.addEnchantment(Enchantment.DURABILITY, 3);
		legs.addEnchantment(Enchantment.THORNS, 3);
		boots.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
		boots.addEnchantment(Enchantment.DURABILITY, 3);
		boots.addEnchantment(Enchantment.PROTECTION_FALL, 3);
		ItemStack sword = new ItemStack(Material.DIAMOND_SWORD), bow = new ItemStack(Material.BOW);
		sword.addEnchantment(Enchantment.DAMAGE_ALL, 5);
		sword.addEnchantment(Enchantment.LOOT_BONUS_MOBS, 3);
		sword.addEnchantment(Enchantment.FIRE_ASPECT, 2);
		bow.addEnchantment(Enchantment.DURABILITY, 3);
		bow.addEnchantment(Enchantment.ARROW_INFINITE, 1);
		bow.addEnchantment(Enchantment.ARROW_FIRE, 1);
		bow.addEnchantment(Enchantment.ARROW_DAMAGE, 5);
		bow.addEnchantment(Enchantment.ARROW_KNOCKBACK, 2);
		for(Contestant c : getBattle().getContestants()){
			if(c.getPlayer() != null){
				c.getPlayer().getInventory().setArmorContents(new ItemStack[]{head, chest, legs, boots});
				c.getPlayer().getInventory().addItem(sword, bow, new ItemStack(Material.ARROW, 1));
			}
		}
		start();
	}

	@Override
	public void start(){
		for(Contestant c : getBattle().getContestants()){
			c.teleport(getBattle().getArena().getRandomLocation());
		}
	}

	@EventHandler
	public void onDeath2(PlayerDeathEvent e){
		if(!isBattleEvent(e)){return;}
		getBattle().onContestantDeath(e.getEntity());
		if(getBattle().getContestants().size() == 1)
			getBattle().end(getBattle().getContestants().get(0));
	} */
}
