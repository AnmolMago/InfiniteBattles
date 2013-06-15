package net.ArtificialCraft.InfiniteBattles.Events;

import net.ArtificialCraft.InfiniteBattles.Entities.Contestant.Contestant;

import java.util.List;

/**
 * Enclosed in project InfiniteBattles for Aurora Enterprise.
 * Author: Josh Aurora
 * Date: 2013-04-27
 */
public class BattleStartEvent extends IEvent{

	List<Contestant> players;

	public BattleStartEvent(List<Contestant> players){
		this.players = players;
	}

	public List<Contestant> getPlayers(){
		return players;
	}
}
