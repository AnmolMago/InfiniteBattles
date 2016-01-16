package net.ArtificialCraft.InfiniteBattles.Events;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Enclosed in project InfiniteBattles for Aurora Enterprise.
 * Author: Josh Aurora
 * Date: 2013-04-27
 */
public class IEvent extends Event{

	private static final HandlerList handlers = new HandlerList();

	public IEvent getEvent(){
		return this;
	}

	public void callEvent(){
		Bukkit.getPluginManager().callEvent(this);
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
}
