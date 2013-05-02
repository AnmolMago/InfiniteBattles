package net.ArtificialCraft.InfiniteBattles.Entities.Battles.BattleHandler;

import net.ArtificialCraft.InfiniteBattles.Entities.Battles.Battle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

/**
 * Enclosed in project InfiniteBattles for Aurora Enterprise.
 * Author: Josh Aurora
 * Date: 2013-04-29
 */
public class Spleef extends IBattleHandler{

	public Spleef(Battle b){
		super(b);
	}

	@Override
	public void load(){

	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDamage(EntityDamageEvent e){
		if(!isBattleEvent(e)){return;}
		if(!e.getCause().equals(DamageCause.LAVA)){
			e.setCancelled(true);
		}
	}

}
