package net.ArtificialCraft.InfiniteBattles.Entities.Battles.BattleHandler;

import net.ArtificialCraft.InfiniteBattles.Entities.Contestant.Contestant;
import net.ArtificialCraft.InfiniteBattles.Entities.Battles.Battle;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Random;

/**
 * Enclosed in project InfiniteBattles for Aurora Enterprise.
 * Author: Josh Aurora
 * Date: 2013-04-29
 */
public class Spleef extends IBattleHandler{

	boolean started;

	public Spleef(Battle b){
		super(b);
	}

	@Override
	public void load(){
		for(Contestant c : getBattle().getContestants()){
			Player p = c.getPlayer();
			if(p == null){continue;}
			PlayerInventory inv = p.getInventory();
			inv.clear();
			inv.addItem(new ItemStack(Material.DIAMOND_SPADE));
			p.updateInventory();
		}
		start();
	}

	@Override
	public void start(){
		started = true;
		for(Contestant c : getBattle().getContestants()){
			Player p = c.getPlayer();
			if(p == null){continue;}
			p.teleport(getBattle().getArena().getSpawns().get(new Random().nextInt(3)));
		}
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
		if(!isBattleEvent(e.getPlayer())){return;}
		if(e.getBlock().getType().equals(Material.SNOW_BLOCK)){
			e.getBlock().getDrops().clear();
		}else{
			e.setCancelled(true);
		}
	}

}
