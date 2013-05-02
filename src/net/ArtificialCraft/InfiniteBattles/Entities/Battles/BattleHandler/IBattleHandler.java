package net.ArtificialCraft.InfiniteBattles.Entities.Battles.BattleHandler;

import net.ArtificialCraft.InfiniteBattles.Entities.Battles.Battle;
import net.ArtificialCraft.InfiniteBattles.IBattle;
import net.ArtificialCraft.InfiniteBattles.Misc.Config;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityEvent;

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

	protected boolean isBattleEvent(Event e){
		if(e instanceof EntityEvent){
			EntityEvent ee = (EntityEvent) e;
			if(ee.getEntity() instanceof Player){
				Player p = (Player)ee.getEntity();
				return getBattle().hasContestant(IBattle.getContestant(p.getName()));
			}
		}
		return false;
	}

	public abstract void load();

}
