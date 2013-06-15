package net.ArtificialCraft.InfiniteBattles.Entities.Battles.BattleHandler;

import net.ArtificialCraft.InfiniteBattles.Entities.Battles.Battle;
import net.ArtificialCraft.InfiniteBattles.IBattle;
import net.ArtificialCraft.InfiniteBattles.Misc.Config;
import net.ArtificialCraft.InfiniteBattles.Misc.Util;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.vehicle.VehicleEvent;

/**
 * Enclosed in project InfiniteBattles for Aurora Enterprise.
 * Author: Josh Aurora
 * Date: 2013-04-27
 */
public abstract class IBattleHandler implements Listener{

	protected ConfigurationSection config;
	protected String battle;

	public IBattleHandler(Battle b){
		this.battle = b.getName();
		Bukkit.getPluginManager().registerEvents(this, IBattle.getPlugin());
		String path = "Handlers." + this.getClass().getSimpleName();
		if(!Config.getConfig().isConfigurationSection(path)){
			Config.getConfig().createSection(path);
		}
		config = Config.getConfig().getConfigurationSection(path);
	}

	public void unregisterHandler(){
		HandlerList.unregisterAll(this);
	}

	protected Battle getBattle(){
		return IBattle.getBattle(battle);
	}

	protected boolean isBattleEvent(Player p){
		return getBattle().hasContestant(IBattle.getContestant(p.getName()));
	}

	protected boolean isBattleEvent(Event e){
		if(e instanceof EntityEvent){
			EntityEvent ee = (EntityEvent) e;
			if(ee.getEntity() instanceof Player){
				Player p = (Player)ee.getEntity();
				return p.getWorld().getName().equalsIgnoreCase("Warfare") && getBattle().hasContestant(IBattle.getContestant(p.getName()));
			}
		}else if(e instanceof VehicleEvent){
			VehicleEvent ve = (VehicleEvent) e;
			Player p = (Player) ve.getVehicle().getPassenger();
			return p != null && p.getWorld().getName().equalsIgnoreCase("Warfare") && getBattle().hasContestant(IBattle.getContestant(p.getName()));
		}else if(e instanceof PlayerEvent){
			PlayerEvent pe = (PlayerEvent)e;
			return pe.getPlayer().getWorld().getName().equalsIgnoreCase("Warfare") && getBattle().hasContestant(IBattle.getContestant(pe.getPlayer().getName()));
		}else if(e instanceof BlockEvent){
			Block b = ((BlockEvent)e).getBlock();
			Player p;
			if(e instanceof BlockPlaceEvent){
				p = ((BlockPlaceEvent)e).getPlayer();
			}else if(e instanceof BlockBreakEvent){
				p = ((BlockBreakEvent)e).getPlayer();
			}else{
				return false;
			}
			return p.getWorld().getName().equalsIgnoreCase("Warfare") && getBattle().hasContestant(IBattle.getContestant(p.getName()));
		}
		return false;
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent e){
		Util.broadcast(e.getEntity().getName());
		getBattle().onContestantDeath(e.getEntity());
	}

	public abstract void load();

	public abstract void start();

}
