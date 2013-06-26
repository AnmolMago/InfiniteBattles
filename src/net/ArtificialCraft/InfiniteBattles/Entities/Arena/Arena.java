package net.ArtificialCraft.InfiniteBattles.Entities.Arena;

import net.ArtificialCraft.InfiniteBattles.Entities.Battles.BattleType;
import net.ArtificialCraft.InfiniteBattles.Misc.Formatter;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Enclosed in project InfiniteBattles for Aurora Enterprise.
 * Author: Josh Aurora
 * Date: 2013-04-27
 */
public class Arena{

	String name = null;
	Location firstspawn, secondspawn, thirdspawn, fourthspawn, spectatorspawn, pitstop;
	BattleType unique = null;

	public Arena(String name, Location firstspawn, Location secondspawn, Location thirdspawn, Location fourthspawn, Location spectatorspawn, Location pitstop){
		this.name = name;
		this.firstspawn = firstspawn;
		this.secondspawn = secondspawn;
		this.thirdspawn = thirdspawn;
		this.fourthspawn = fourthspawn;
		this.spectatorspawn = spectatorspawn;
		this.pitstop = pitstop;
	}

	public String getName(){
		return name;
	}

	public Location getFirstSpawn(){
		return firstspawn;
	}

	public Location getSecondSpawn(){
		return secondspawn;
	}

	public Location getThirdSpawn(){
		return thirdspawn;
	}

	public Location getFourthSpawn(){
		return fourthspawn;
	}

	public Location getSpectatorspawn(){
		return spectatorspawn;
	}

	public Location getRandomSpawn(){
		return getSpawns().get(new Random().nextInt(getSpawns().size()));
	}

	public Location getPitstop(){
		return pitstop;
	}
	public void setFirstSpawn(Location spawn){
		firstspawn = spawn;
	}

	public void setSecondSpawn(Location spawn){
		secondspawn = spawn;
	}

	public void setThirdSpawn(Location spawn){
		thirdspawn = spawn;
	}

	public void setFourthSpawn(Location spawn){
		fourthspawn = spawn;
	}

	public void setPitStop(Location stop){
		pitstop = stop;
	}

	public void setSpectatorspawn(Location spectatorspawn){
		this.spectatorspawn = spectatorspawn;
	}

	public void setPitstop(Location pitstop){
		this.pitstop = pitstop;
	}

	public List<Location> getSpawns(){
		List<Location> l = new ArrayList<Location>();
		l.add(firstspawn);
		l.add(secondspawn);
		l.add(thirdspawn);
		l.add(fourthspawn);
		return l;
	}

	public void setUnique(BattleType b){
		unique = b;
	}

	public BattleType isUnique(){
		return unique;
	}

	public String toString(){
		return name + "," + (unique == null ? "null" : unique.name()) + "!" + Formatter.configLoc(firstspawn) + "," + Formatter.configLoc(secondspawn) + "," + Formatter.configLoc(thirdspawn) + "," + Formatter.configLoc(fourthspawn) + "," + Formatter.configLoc(spectatorspawn) + "," + Formatter.configLoc(pitstop);
	}
}
