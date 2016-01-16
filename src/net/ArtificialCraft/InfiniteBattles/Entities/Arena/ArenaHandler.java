package net.ArtificialCraft.InfiniteBattles.Entities.Arena;

import net.ArtificialCraft.InfiniteBattles.Entities.Battles.BattleType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Enclosed in project InfiniteBattles for Aurora Enterprise.
 * Author: Josh Aurora
 * Date: 2013-04-27
 */
public class ArenaHandler{

	private static HashMap<String, Arena> unusedArenas = new HashMap<String, Arena>();

	/*public static void createOld(Player p, String[] args, boolean set){
		String pname = p.getName();
		if(!set){
			if(status.containsKey(pname)){
				Util.error(p, ChatColor.RED + "You are already in creation mode ... please type /iBattle set to select your next spawn point!");
				return;
			}
			if(IBattle.getArena(args[1]) != null){
				Util.error(p, ChatColor.RED + "There is already an existing arena by this name!");
				return;
			}
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("name", args[1]);
			creation.put(pname, map);
			status.put(pname, "first");
			Util.msg(p, ChatColor.RED + "Please type §3\"/iBattle set\"§c to set the first spawn to your location!");
		}else{// /iBattle set command was used
			if(status.get(pname).equalsIgnoreCase("first")){
				creation.get(pname).put("first", Formatter.configLoc(p.getLocation()));
				status.put(pname, "second");
				Util.msg(p, ChatColor.RED + "Please type §3\"/iBattle set\"§c to set the second spawn to your location!");
			}else if(status.get(pname).equalsIgnoreCase("second")){
				creation.get(pname).put("second", Formatter.configLoc(p.getLocation()));
				status.put(pname, "third");
				Util.msg(p, ChatColor.RED + "Please type §3\"/iBattle set\"§c to set the third spawn to your location!");
			}else if(status.get(pname).equalsIgnoreCase("third")){
				creation.get(pname).put("third", Formatter.configLoc(p.getLocation()));
				status.put(pname, "fourth");
				Util.msg(p, ChatColor.RED + "Please type §3\"/iBattle set\"§c to set the fourth spawn to your location!");
			}else if(status.get(pname).equalsIgnoreCase("fourth")){
				creation.get(pname).put("fourth", Formatter.configLoc(p.getLocation()));
				status.put(pname, "pitstop");
				Util.msg(p, ChatColor.RED + "Please type §3\"/iBattle set\"§c to set the pitstop to your location!");
			}else if(status.get(pname).equalsIgnoreCase("pitstop")){
				creation.get(pname).put("pitstop", Formatter.configLoc(p.getLocation()));
				status.put(pname, "pastepoint");
				Util.msg(p, ChatColor.RED + "Please type §3\"/iBattle set\"§c to set the pastepoint to your location!");
			}else if(status.get(pname).equalsIgnoreCase("pastepoint")){
				creation.get(pname).put("pastepoint", Formatter.configLoc(p.getLocation()));
				status.put(pname, "spectator");
				Util.msg(p, ChatColor.RED + "Please type §3\"/iBattle set\"§c to set the spectator spawn to your location!");
			}else if(status.get(pname).equalsIgnoreCase("spectator")){
				creation.get(pname).put("spectator", Formatter.configLoc(p.getLocation()));
				Config.saveYamls();
				//create new arena
				Location firstspawn, secondSpawn, thirdSpawn, fourthSpawn, spectatorspawn, pitstop;
				String name = creation.get(pname).get("name");
				firstspawn = Formatter.parseLoc(creation.get(pname).get("first"));
				secondSpawn = Formatter.parseLoc(creation.get(pname).get("second"));
				thirdSpawn = Formatter.parseLoc(creation.get(pname).get("third"));
				fourthSpawn = Formatter.parseLoc(creation.get(pname).get("fourth"));
				spectatorspawn = Formatter.parseLoc(creation.get(pname).get("spectator"));
				pitstop = Formatter.parseLoc(creation.get(pname).get("pitstop"));
				Arena a = new Arena(name, firstspawn, secondSpawn, thirdSpawn, fourthSpawn, spectatorspawn, pitstop);
				Config.addArena(a);
				IBattle.addArena(a);
				Util.msg(p, ChatColor.DARK_PURPLE + "Congratulations you have successfully made a new arena called §3" + name + "§5!");
				status.remove(pname);
			}
		}
	}

	public static void cancel(Player p){
		if(status.containsKey(p.getName())){
			status.remove(p.getName());
			creation.remove(p.getName());
			Util.msg(p, ChatColor.RED + "You have been removed from the creation process and your changes have been discarded.");
		}else{
			Util.error(p, "You were not creating an arena!");
		}
	}  */
	public static HashMap<String, Arena> getUnusedArenas(){
		return unusedArenas;
	}

	public static void addUnusedArena(Arena a){
		if(!isUnused(a) && a != null)
			unusedArenas.put(a.getName(), a);
	}

	public static Arena getUnusedArena(String s){
		if(unusedArenas.containsKey(s))
			return unusedArenas.get(s);

		return null;
	}

	public static boolean isUnused(Arena a){
		return unusedArenas.containsKey(a.getName());
	}

	public static Arena getArenaFor(BattleType bt){
		List<Arena> arenas = new ArrayList<Arena>();
		if(bt.isUnique()){
			for(String key : unusedArenas.keySet()){
				if(bt.equals(unusedArenas.get(key).isUnique()))
					arenas.add(unusedArenas.get(key));
			}
		}else{
			for(String key : unusedArenas.keySet()){
				if(unusedArenas.get(key).isUnique() == null)
					arenas.add(unusedArenas.get(key));
			}
		}
		if(arenas.size() == 0)
			return null;

		Arena a = arenas.get(new Random().nextInt(arenas.size()));
		unusedArenas.remove(a.getName());

		return a;
	}

}
