package net.ArtificialCraft.InfiniteBattles.Misc;

import net.ArtificialCraft.InfiniteBattles.Entities.Contestant.Contestant;
import net.ArtificialCraft.InfiniteBattles.Entities.Arena.Arena;
import net.ArtificialCraft.InfiniteBattles.Entities.Battles.BattleType;
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
		String name = null;
		Location bluespawn, redspawn, greenspawn, purplespawn, spectatorspawn, pitstop, pastepoint;
		BattleType unique = null;
		String[] peices = s.split("!");
		String[] info = peices[0].split(",");
		String[] locs = peices[1].split(",");
		name = info[0];
		if(!info[1].equalsIgnoreCase("null")){
			try{
				unique = BattleType.valueOf(info[2]);
			}catch(IllegalArgumentException x){}
		}
		bluespawn = parseLoc(locs[0]);
		redspawn = parseLoc(locs[1]);
		greenspawn = parseLoc(locs[2]);
		purplespawn = parseLoc(locs[3]);
		spectatorspawn = parseLoc(locs[4]);
		pitstop = parseLoc(locs[5]);
		Arena a = new Arena(name, bluespawn, redspawn, greenspawn, purplespawn, spectatorspawn, pitstop);
		if(unique != null)
			a.setUnique(unique);
		return a;
	}

	public static String configLoc(Location l){
		return l.getX() + "|" + l.getY() + "|" + l.getZ();
	}

	public static Location parseLoc(String l){
		String[] locs = l.split("\\|");
		try{
			double x = Double.parseDouble(locs[0]);
			double y = Double.parseDouble(locs[1]);
			double z = Double.parseDouble(locs[2]);
			return new Location(Bukkit.getServer().getWorld("Warfare"), x, y, z);
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
