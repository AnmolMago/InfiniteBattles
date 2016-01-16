package net.ArtificialCraft.InfiniteBattles.Misc;

import net.ArtificialCraft.InfiniteBattles.Entities.Arena.Arena;
import net.ArtificialCraft.InfiniteBattles.Entities.Arena.LocationType;
import net.ArtificialCraft.InfiniteBattles.Entities.Battles.BattleType;
import net.ArtificialCraft.InfiniteBattles.Entities.Contestant.Contestant;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.HashMap;

/**
 * Enclosed in project InfiniteBattles for Aurora Enterprise.
 * Author: Josh Aurora
 * Date: 2013-04-28
 */
public class Formatter{

	public static Contestant parseContestant(String c){
		String[] parts = c.split("!");
		String[] info = parts[0].split("\\|");
		String[] counts = null;
		if(parts.length > 1){
			counts = parts[1].split("\\|");
		}
		String name;
		int wins, losses, streak;
		HashMap<BattleType, Integer> count = new HashMap<BattleType, Integer>();
		name = info[0];
		wins = Integer.parseInt(info[1]);
		losses = Integer.parseInt(info[2]);
		streak = Integer.parseInt(info[3]);
		if(counts != null){
			for(String s : counts){
				String[] split = s.split(",");
				count.put(BattleType.valueOf(split[0]), Integer.parseInt(split[1]));
			}
		}
		return new Contestant(name, wins, losses, streak, count);
	}

	public static Arena parseArena(String s){
		String[] parts = s.split("!");
		BattleType type = null;
		String name = parts[0];
		if(!parts[1].equalsIgnoreCase("null")){
			try{
				type = BattleType.valueOf(parts[1]);
			}catch(Exception e){}
		}
		HashMap<LocationType, Location> spawns = new HashMap<LocationType, Location>();
		for(int c = 2; c < parts.length; c++){
			String[] toparse = parts[c].split(",");
			LocationType ltype;
			try{
				ltype = LocationType.valueOf(toparse[0]);
				Location loc = parseLoc(toparse[1]);
				if(loc != null)
					spawns.put(ltype, loc);
			}catch(Exception e){
				Util.debug(parts[c] + " IS INVALID!!! - IBattle:Formatter.java:60");
			}
		}
		return new Arena(name, spawns, type);
	}

	public static String configLoc(Location l){
		return l.getX() + "|" + l.getY() + "|" + l.getZ() + "|" + l.getYaw() + "|" + l.getPitch();
	}

	public static Location parseLoc(String l){
		if(!l.contains("|")){
			return null;
		}
		String[] locs = l.split("\\|");
		try{
			double x = Double.parseDouble(locs[0]);
			double y = Double.parseDouble(locs[1]);
			double z = Double.parseDouble(locs[2]);
			if(locs.length == 3)
				return new Location(Bukkit.getServer().getWorld("Warfare"), x, y, z);
			float yaw = Float.parseFloat(locs[3]);
			float pitch = Float.parseFloat(locs[4]);
			return new Location(Bukkit.getServer().getWorld("Warfare"), x, y, z, yaw, pitch);
		}catch(NumberFormatException ex){
			return null;
		}
	}

	public static String formatTimeSpan(long time){
		String formatted = "";
		if(time >= 3600L){
			int h = (int)Math.floor(time / 3600L);
			formatted = formatted + h + " hours";
			time -= h * 3600;
		}
		if(time >= 60L){
			int m = (int)Math.floor(time / 60L);
			formatted = formatted + (formatted.length() > 0 ? ", " : "") + m + " minutes";
			time -= m * 60;
		}
		if(formatted.length() == 0 || time > 0L)
			formatted = formatted + (formatted.length() > 0 ? ", " : "") + time + " seconds";
		return formatted;
	}

}
