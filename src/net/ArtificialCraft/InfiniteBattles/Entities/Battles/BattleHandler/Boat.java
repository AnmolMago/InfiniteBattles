package net.ArtificialCraft.InfiniteBattles.Entities.Battles.BattleHandler;

import net.ArtificialCraft.InfiniteBattles.Entities.Battles.Battle;
import net.ArtificialCraft.InfiniteBattles.Entities.Battles.Status;
import net.ArtificialCraft.InfiniteBattles.Entities.Contestant.Contestant;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;

/**
 * Enclosed in project InfiniteBattles for Aurora Enterprise.
 * Author: Josh Aurora
 * Date: 2013-05-07
 */
public class Boat extends IBattleHandler{

	HashMap<String, Integer> damage = new HashMap<String, Integer>();

	public Boat(Battle b){
		super(b);
	}

	@Override
	public void load(){
		ItemStack bow = new ItemStack(Material.BOW, 1);
		bow.addEnchantment(Enchantment.ARROW_INFINITE, 1);
		ItemStack helm = new ItemStack(Material.DIAMOND_HELMET);
		helm.addEnchantment(Enchantment.PROTECTION_PROJECTILE, 2);
		ItemStack chest = new ItemStack(Material.DIAMOND_CHESTPLATE);
		chest.addEnchantment(Enchantment.PROTECTION_PROJECTILE, 2);
		ItemStack legs = new ItemStack(Material.DIAMOND_LEGGINGS);
		legs.addEnchantment(Enchantment.PROTECTION_PROJECTILE, 2);
		ItemStack boots = new ItemStack(Material.DIAMOND_BOOTS);
		helm.addEnchantment(Enchantment.PROTECTION_PROJECTILE, 2);
		for(Contestant c : getBattle().getContestants()){
			Player p = c.getPlayer();
			if(p == null){continue;}
			PlayerInventory inv = p.getInventory();
			inv.setArmorContents(new ItemStack[]{helm, chest, legs, boots});
			inv.addItem(new ItemStack(Material.BOAT, 1), bow, new ItemStack(Material.ARROW, 3));
		}
	}

	@Override
	public void start(){
		getBattle().setStatus(Status.Started);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDamage(EntityDamageByEntityEvent e){
		if(!isBattleEvent(e)){return;}
		if(!getBattle().isStarted()){
			e.setCancelled(true);
			return;
		}
		if(e.getDamager() instanceof Arrow && e.getEntity() instanceof Player){
			e.setCancelled(true);
			if(e.getEntity().getVehicle() != null){
				Bukkit.getPluginManager().callEvent(new VehicleDamageEvent((Vehicle)e.getEntity().getVehicle(), e.getDamager(), 0));
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onVehicleDamage(VehicleDamageEvent e){
		if(!isBattleEvent(e) || !(e.getVehicle() instanceof Boat)){
			return;
		}
		if(e.getVehicle().getPassenger() == null || !(e.getVehicle().getPassenger() instanceof Player)){
			return;
		}
		Player p = (Player) e.getVehicle().getPassenger();
		String name = p.getName();
		if(!damage.containsKey(name)){
			damage.put(name, 1);
			e.setCancelled(true);
		}else if(damage.get(name) > 10){
			e.setDamage(100);
			damage.remove(name);
			p.setHealth(0);
		}else{
			damage.put(name, damage.get(name) + 1);
			e.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onVehicleEnter(VehicleEnterEvent e){
		if(!isBattleEvent(e)){return;}
		if(!getBattle().isStarted())
			e.getVehicle().teleport(getBattle().getArena().getRandomLocation());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onVehicleMove(VehicleMoveEvent e){
		if(!isBattleEvent(e)){return;}
		if(!getBattle().isStarted()){
			e.getVehicle().teleport(e.getFrom());
			if(e.getVehicle().getPassenger() instanceof Player)
				((Player)e.getVehicle().getPassenger()).sendMessage(ChatColor.RED + "You cannot start moving until everyone has joined!");
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onVehicleExit(VehicleExitEvent e){
		if(!isBattleEvent(e)){return;}
		e.setCancelled(true);
	}

}

