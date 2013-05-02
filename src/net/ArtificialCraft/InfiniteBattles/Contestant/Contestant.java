package net.ArtificialCraft.InfiniteBattles.Contestant;

import net.ArtificialCraft.InfiniteBattles.Entities.Battles.BattleType;
import net.ArtificialCraft.InfiniteBattles.IBattle;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;

/**
 * Enclosed in project InfiniteBattles for Aurora Enterprise.
 * Author: Josh Aurora
 * Date: 2013-04-28
 */
public class Contestant{

	String name;
	boolean onstreak = true;
	int wins, losses, streak;
	HashMap<BattleType, Integer> count = new HashMap<BattleType, Integer>();

	public Contestant(Player p){
		name = p.getName();
	}

	public Contestant(String name, int wins, int losses, int streak, boolean onstreak, HashMap<BattleType, Integer> count){
		this.name = name;
		this.wins = wins;
		this.losses = losses;
		this.streak = streak;
		this.onstreak = onstreak;
		this.count = count;
	}

	public String getName(){
		return name;
	}

	public int getWins(){
		return wins;
	}

	public int getLosses(){
		return losses;
	}

	public int getPlayed(BattleType bt){
		if(count.containsKey(bt))
			return count.get(bt);

		return 0;
	}

	public int getTotalPlayed(){
		int total = 0;
		for(Integer i : count.values())
			total += i;

		return total;
	}

	public int getStreak(){
		return streak;
	}

	private void addWin(){
		if(onstreak)
			streak++;

		wins++;
		onstreak = true;
	}

	private void addLoss(){
		onstreak = false;
		losses++;
	}

	public void onBattlePlayed(BattleType type, boolean win){
		if(win){
			addWin();
		}else{
			addLoss();
		}
		if(count.containsKey(type)){
			count.put(type, count.get(type) + 1);
		}else{
			count.put(type, 1);
		}
	}

	public HashMap<BattleType, Integer> getCount(){
		return count;
	}

	public Player getPlayer(){
		if(Bukkit.getPlayerExact(name) == null && (IBattle.isPlayerPlaying(name) != null)){
				IBattle.isPlayerPlaying(name).removeContestant(this);
		}
		return Bukkit.getPlayerExact(name);
	}

	public String toString(){
		return name + "|" + wins + "|" + losses + "|" + streak + "|" + onstreak + "!" + countToString();
	}

	private String countToString(){
		StringBuilder sb = new StringBuilder();
		for(BattleType bt : count.keySet())
			sb.append(bt.name() + "," + count.get(bt) + "|");

		return sb.toString().substring(0, sb.toString().length() - 1);
	}

	public int hashCode(){
		return new HashCodeBuilder(17, 31).append(getName()).toHashCode();
	}

	public boolean equals(Object obj){
		if(obj == null)
			return false;
		if(obj == this)
			return true;
		if(!(obj instanceof Contestant))
			return false;

		Contestant c = (Contestant) obj;
		return new EqualsBuilder().append(getName(), c.getName()).isEquals();
	}

}
