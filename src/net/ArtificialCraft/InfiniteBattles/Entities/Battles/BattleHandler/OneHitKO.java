package net.ArtificialCraft.InfiniteBattles.Entities.Battles.BattleHandler;

import net.ArtificialCraft.InfiniteBattles.Entities.Battles.Battle;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Enclosed in project InfiniteBattles for Aurora Enterprise.
 * Author: Josh Aurora
 * Date: 2013-04-29
 */
public class OneHitKO extends IBattleHandler{

	public OneHitKO(Battle b){
		super(b);
	}

	@Override
	public void load(){

	}

	@Override
	public void start(){

	}

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e){
		if(!isBattleEvent(e)){return;}
		if(e.getDamager() instanceof Arrow && e.getDamager() instanceof Player && e.getEntity() instanceof Player){
			e.getEntity().getLastDamageCause().setDamage(20);
			((Player)e.getDamager()).getInventory().addItem(new ItemStack(Material.ARROW, 1));
		}
	}
}
