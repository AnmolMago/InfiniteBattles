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

	public static HashMap<String, Arena> getUnusedArenas(){
		return unusedArenas;
	}

	public static void addUnusedArena(Arena a){
		if(!isUnused(a))
			unusedArenas.put(a.getName(), a);
	}

	public static Arena startUsingArena(Arena a){
		if(isUnused(a)){
			Arena ar = unusedArenas.get(a.getName());
			unusedArenas.remove(a.getName());
			return ar;
		}

		return null;
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
		if(bt.equals(BattleType.Spleef)){// || bt.equals(BattleType.OTHERUNIQUETYPE)
			for(String key : unusedArenas.keySet()){
				if(unusedArenas.get(key).isUnique().equals(bt))
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

		return arenas.get(new Random().nextInt(arenas.size()));
	}

}
