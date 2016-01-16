package net.ArtificialCraft.InfiniteBattles.Entities.Battles;

import net.ArtificialCraft.InfiniteBattles.Entities.Arena.LocationType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Enclosed in project InfiniteBattles for Aurora Enterprise.
 * Author: Josh Aurora
 * Date: 2013-04-27
 */
public enum BattleType{

	Capture_The_Flag(0, Arrays.asList("ctf", "flag", "capture"), Arrays.asList(LocationType.first, LocationType.second, LocationType.redflag, LocationType.blueflag)),
	Duck_Hunt(0, Arrays.asList("duckhunt", "dh", "duck"), true),//done
	Free_For_All(3, Arrays.asList("freeforall", "ffa", "normal", "default")),//done
	Infection(0, Arrays.asList("infection", "infect", "zombie"), Arrays.asList(LocationType.first, LocationType.second)),
	InvPick(3, Arrays.asList("invpick", "inv", "chooseinv")),
	PaintBall(0, Arrays.asList("paintball", "paint", "pb", "pball"), Arrays.asList(LocationType.first, LocationType.second)),
	Role_Play(3, Arrays.asList("rp", "roleplay")),//done
	Spleef(0, Arrays.asList("spleef"), true);//done

	public int lives = 1;
	public List<String> aliases;
	public List<LocationType> locations;
	public boolean unique;

	private BattleType(int lives, List<String> aliases){
		this(lives, aliases, false);
	}

	private BattleType(int lives, List<String> aliases, boolean unique){
		this.aliases = aliases;
		this.lives = lives;
		this.locations = new ArrayList<LocationType>(Arrays.asList(LocationType.first, LocationType.second, LocationType.third, LocationType.fourth, LocationType.pitstop, LocationType.spectator));
		this.unique = unique;
	}

	private BattleType(int lives, List<String> aliases, List<LocationType> locations){
		this.aliases = aliases;
		this.lives = lives;
		this.locations = new ArrayList<LocationType>(locations);
		this.locations.add(LocationType.pitstop);
		this.locations.add(LocationType.spectator);
		unique = true;
	}

	public List<String> getAliases(){
		return aliases;
	}

	public int getLives(){
		return lives;
	}

	public String getName(){
		return name().replace("_", " ");
	}

	public String getTypableName(){
		return getAliases().get(0);
	}

	public boolean isType(String s){
		s = s.toLowerCase();
		return aliases.contains(s) || s.equalsIgnoreCase(name());
	}

	public static BattleType getByName(String s){
		for(BattleType type : BattleType.values()){
			if(type.isType(s))
				return type;
		}
		return null;
	}

	public boolean isUnique(){
		return unique;
	}

}
