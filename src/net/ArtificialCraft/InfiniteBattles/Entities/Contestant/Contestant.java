package net.ArtificialCraft.InfiniteBattles.Entities.Contestant;

import net.ArtificialCraft.InfiniteBattles.Entities.Battles.BattleType;
import net.ArtificialCraft.InfiniteBattles.IBattle;
import net.ArtificialCraft.InfiniteBattles.Misc.Util;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;

/**
 * Enclosed in project InfiniteBattles for Aurora Enterprise.
 * Author: Josh Aurora
 * Date: 2013-04-28
 */
public class Contestant{

	String name;
	int wins, losses, streak;
	HashMap<BattleType, Integer> count = new HashMap<BattleType, Integer>();

	public Contestant(Player p){
		name = p.getName();
	}

	public Contestant(String name, int wins, int losses, int streak, HashMap<BattleType, Integer> count){
		this.name = name;
		this.wins = wins;
		this.losses = losses;
		this.streak = streak;
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
		if(streak < 0)
			streak = 0;
		streak++;
		wins++;
	}

	private void addLoss(){
		if(streak > 0)
			streak = 0;
		streak--;
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
		if(Bukkit.getPlayerExact(name) == null && IBattle.isPlayerPlaying(name) != null){
				IBattle.isPlayerPlaying(name).removeContestant(this);
		}
		return Bukkit.getPlayerExact(name);
	}

	public Contestant clearInv(){
		if(getPlayer() == null || !getPlayer().getWorld().getName().equalsIgnoreCase("Warfare"))
			return this;
		getPlayer().getInventory().clear();
		getPlayer().getInventory().setArmorContents(null);
		return this;
	}

	public boolean teleport(Location l){
		if(l == null)
			Util.debug("Location is null u biatch");
		return getPlayer() != null && l != null && getPlayer().teleport(l);
	}

	public String parseStreak(){
		if(streak > 0){
			return streak + " win" + (streak > 1 ? "s" : "");
		}else if(streak < 0){
			return (0 - streak) + " loss" + (streak < -1 ? "es" : "");
		}else{
			return "None";
		}
	}

	public String toString(){
		return name + "|" + wins + "|" + losses + "|" + streak + "!" + countToString();
	}

	private String countToString(){
		StringBuilder sb = new StringBuilder();
		for(BattleType bt : count.keySet())
			sb.append(bt.name() + "," + count.get(bt) + "|");

		return sb.toString().substring(0, sb.toString().length() == 0 ? 0 : sb.toString().length() - 1);
	}

	/*public int hashCode(){
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
	} */

}
