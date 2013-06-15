package net.ArtificialCraft.InfiniteBattles.Entities.Battles.BattleHandler;

import net.ArtificialCraft.InfiniteBattles.Entities.Contestant.Contestant;
import net.ArtificialCraft.InfiniteBattles.Entities.Battles.Battle;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

/**
 * Enclosed in project InfiniteBattles for Aurora Enterprise.
 * Author: Josh Aurora
 * Date: 2013-05-08
 */
public class LMS extends IBattleHandler{

	public LMS(Battle b){
		super(b);
	}

	@Override
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
			if(c.getPlayer() != null){
				c.getPlayer().teleport(getBattle().getArena().getSpawns().get(new Random().nextInt(3)));
			}
		}
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent e){
		if(!isBattleEvent(e)){return;}
		getBattle().onContestantDeath(e.getEntity());
		if(getBattle().getContestants().size() == 1)
			getBattle().end(getBattle().getContestants().get(0));
	}
}
