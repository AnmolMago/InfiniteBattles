package net.ArtificialCraft.InfiniteBattles.Listeners;

import net.ArtificialCraft.InfiniteBattles.Entities.Contestant.Contestant;
import net.ArtificialCraft.InfiniteBattles.Entities.Battles.Battle;
import net.ArtificialCraft.InfiniteBattles.IBattle;
import net.ArtificialCraft.InfiniteBattles.Misc.Util;
import net.ArtificialCraft.InfiniteBattles.ScoreBoard.ScoreboardHandler;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Enclosed in project InfiniteBattles for Aurora Enterprise.
 * Author: Josh Aurora
 * Date: 2013-04-28
 */
public class PlayerListener implements Listener{

	@EventHandler
	public void onLogin(PlayerJoinEvent e){
		if(!IBattle.doesContestantExist(e.getPlayer().getName()))
			IBattle.addContestant(new Contestant(e.getPlayer()));
	}

	@EventHandler
	public void onCommandPre(PlayerCommandPreprocessEvent e){
		if(e.getMessage().toLowerCase().startsWith("/create") || e.getMessage().toLowerCase().startsWith("/join"))
			e.setMessage(e.getMessage().replaceFirst("\\/", "/ibattle "));
	}

	@EventHandler
	public void onLogout(PlayerQuitEvent e){
		String name = e.getPlayer().getName();
		if(IBattle.isPlayerPlaying(name) != null){
			Battle b = IBattle.isPlayerPlaying(name);
			b.removeContestant(IBattle.getContestant(name));
			b.warnUsers(name + " has logged out and been kicked from the battle!");
		}
		if(e.getPlayer().getScoreboard() != null)
			e.getPlayer().setScoreboard(ScoreboardHandler.getSBM().getNewScoreboard());
	}

	@EventHandler
	public void onExplode(EntityExplodeEvent e){
		if(e.getEntity().getWorld().getName().equalsIgnoreCase("Warfare")){
			e.blockList().clear();
		}
	}

	@EventHandler
	public void onInteact(PlayerInteractEvent e){
		if(IBattle.isPlayerPlaying(e.getPlayer().getName()) != null && e.getItem() != null && e.getItem().getType().equals(Material.COMPASS) && (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK))){
			Battle b = IBattle.isPlayerPlaying(e.getPlayer().getName());
			Player target = null;
			double dist = -1;
			for(Contestant c : b.getContestants()){
				if(target != null && dist > 0){
					double d = c.getPlayer().getLocation().distanceSquared(e.getPlayer().getLocation());
					if(d < dist){
						target = c.getPlayer();
						dist = d;
					}
				}else{
					target = c.getPlayer();
				}
			}
			if(target != null){
				e.getPlayer().setCompassTarget(target.getLocation());
				Util.msg(e.getPlayer(), "You target has been set to §4" + target.getName() + "§6!");
			}else{
				Util.error(e.getPlayer(), "We could not find a player to target!");
			}
			e.setCancelled(true);
		}
	}

}
