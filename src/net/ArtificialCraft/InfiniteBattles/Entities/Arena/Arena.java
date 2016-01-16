package net.ArtificialCraft.InfiniteBattles.Entities.Arena;

import net.ArtificialCraft.InfiniteBattles.Entities.Battles.BattleType;
import net.ArtificialCraft.InfiniteBattles.Misc.Formatter;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Enclosed in project InfiniteBattles for Aurora Enterprise.
 * Author: Josh Aurora
 * Date: 2013-04-27
 */
public class Arena{

	String name = null;
	BattleType type = null;
	HashMap<LocationType, Location> locations = new HashMap<LocationType, Location>();

	public Arena(String name, HashMap<LocationType, Location> locations, BattleType type){
		name = name.replaceAll(",", "");
		this.name = name;
		this.locations = locations;
		this.type = type;
	}

	public String getName(){
		return name;
	}

	public Location getLocation(LocationType type){
		return locations.get(type);
	}

	public void setLocation(LocationType type, Location l){
		locations.put(type, l);
	}

	public Location getRandomLocation(){
		return getLocations().get(new Random().nextInt(getLocations().size()));
	}

	public boolean hasType(LocationType type){
		return locations.containsKey(type);
	}

	public List<Location> getLocations(){
		List<Location> l = new ArrayList<Location>();
		for(LocationType type : locations.keySet()){
			if(!type.isSpecial())
				l.add(locations.get(type));
		}
		return l;
	}

	public BattleType isUnique(){
		return type;
	}

	public String toString(){
		String types = "";
		for(LocationType type : locations.keySet())
			types += "!" + type.name() + "," + Formatter.configLoc(locations.get(type));
		return name + "!" + (type == null ? "null" : type.name()) + types ;
	}
}
