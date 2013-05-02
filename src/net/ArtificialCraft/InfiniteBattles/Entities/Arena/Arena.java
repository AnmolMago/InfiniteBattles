package net.ArtificialCraft.InfiniteBattles.Entities.Arena;

import net.ArtificialCraft.InfiniteBattles.Entities.Battles.BattleType;
import net.ArtificialCraft.InfiniteBattles.Misc.Formatter;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Enclosed in project InfiniteBattles for Aurora Enterprise.
 * Author: Josh Aurora
 * Date: 2013-04-27
 */
public class Arena{

	String name = null;
	Location bluespawn, redspawn, greenspawn, purplespawn, spectatorspawn, pitstop, pastepoint;
	BattleType unique = null;
	int max = 0;

	public Arena(String name, Location bluespawn, Location redspawn, Location greenspawn, Location purplespawn, Location spectatorspawn, Location pitstop, Location pastepoint, int max){
		this.name = name;
		this.bluespawn = bluespawn;
		this.redspawn = redspawn;
		this.greenspawn = greenspawn;
		this.purplespawn = purplespawn;
		this.spectatorspawn = spectatorspawn;
		this.pitstop = pitstop;
		this.pastepoint = pastepoint;
		this.max = max;
	}

	public String getName(){
		return name;
	}

	public Location getBlueSpawn(){
		return bluespawn;
	}

	public Location getRedSpawn(){
		return redspawn;
	}

	public Location getGreenSpawn(){
		return greenspawn;
	}

	public Location getPurpleSpawn(){
		return purplespawn;
	}

	public Location getPitStop(){
		return pitstop;
	}

	public void setBlueSpawn(Location spawn){
		bluespawn = spawn;
	}

	public void setRedSpawn(Location spawn){
		redspawn = spawn;
	}

	public void setGreenSpawn(Location spawn){
		greenspawn = spawn;
	}

	public void setPurpleSpawn(Location spawn){
		purplespawn = spawn;
	}

	public void setPitStop(Location stop){
		pitstop = stop;
	}

	public Location getPastepoint(){
		return pastepoint;
	}

	public void setPastepoint(Location pastepoint){
		this.pastepoint = pastepoint;
	}

	public int getmax(){
		return max;
	}

	public List<Location> getSpawns(){
		List<Location> l = new ArrayList<Location>();
		l.add(bluespawn);
		l.add(redspawn);
		l.add(greenspawn);
		l.add(purplespawn);
		return l;
	}

	public void setUnique(BattleType b){
		unique = b;
	}

	public BattleType isUnique(){
		return unique;
	}

	public String toString(){
		return name + "," + max + "," + (unique == null ? "null" : unique.name()) + "!" + Formatter.configLoc(bluespawn) + "," + Formatter.configLoc(redspawn) + "," + Formatter.configLoc(greenspawn) + "," + Formatter.configLoc(purplespawn);
	}
}
