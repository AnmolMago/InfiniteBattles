package net.ArtificialCraft.InfiniteBattles.Entities.Battles.BattleHandler;

import net.ArtificialCraft.InfiniteBattles.Entities.Battles.Battle;
import net.ArtificialCraft.InfiniteBattles.Entities.Battles.Status;
import net.ArtificialCraft.InfiniteBattles.Entities.Contestant.Contestant;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.List;

/**
 * Enclosed in project InfiniteBattles for Aurora Enterprise.
 * Author: Josh Aurora
 * Date: 2013-04-29
 */
public class Spleef extends IBattleHandler{

	List<Location> blocks = new ArrayList<Location>();

	public Spleef(Battle b){
		super(b);
	}

	@Override
	public void load(){
		ItemStack head = new ItemStack(Material.LEATHER_HELMET), chest = new ItemStack(Material.LEATHER_CHESTPLATE), legs = new ItemStack(Material.LEATHER_LEGGINGS), boots = new ItemStack(Material.LEATHER_BOOTS);
		for(Contestant c : getBattle().getContestants()){
			Player p = c.getPlayer();
			if(p == null){continue;}
			PlayerInventory inv = p.getInventory();
			inv.clear();
			inv.setArmorContents(new ItemStack[]{head, chest, legs, boots});
			inv.addItem(new ItemStack(Material.DIAMOND_SPADE));
			p.updateInventory();
		}
		start();
	}

	@Override
	public void start(){
		for(Contestant c : getBattle().getContestants()){
			Player p = c.getPlayer();
			if(p == null){continue;}
			p.teleport(getBattle().getArena().getRandomLocation());
		}
		getBattle().setStatus(Status.Started);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDamage(EntityDamageEvent e){
		if(!isBattleEvent(e)){return;}
		if(!e.getCause().equals(DamageCause.LAVA)){
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onSnowBreak(BlockBreakEvent e){
		if(!isBattleEvent(e)){return;}
		if(e.getBlock().getType().equals(Material.SNOW_BLOCK)){
			blocks.add(e.getBlock().getLocation());
			e.setCancelled(true);
			e.getBlock().setType(Material.AIR);
		}else if(!e.getBlock().getType().equals(Material.SNOW)){
			e.setCancelled(true);
		}
	}

	public void restoreBlocks(){
		for(Location l : blocks)
			l.getBlock().setType(Material.SNOW_BLOCK);
	}
}
