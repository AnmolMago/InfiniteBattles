package net.ArtificialCraft.InfiniteBattles.Listeners;

import net.ArtificialCraft.InfiniteBattles.Contestant.Contestant;
import net.ArtificialCraft.InfiniteBattles.Entities.Battles.Battle;
import net.ArtificialCraft.InfiniteBattles.IBattle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
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
	}

}
