package net.ArtificialCraft.InfiniteBattles.Listeners;

import net.ArtificialCraft.InfiniteBattles.Contestant.Contestant;
import net.ArtificialCraft.InfiniteBattles.IBattle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

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

}
