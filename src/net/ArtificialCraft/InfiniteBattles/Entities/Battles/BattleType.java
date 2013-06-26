package net.ArtificialCraft.InfiniteBattles.Entities.Battles;

import java.util.Arrays;
import java.util.List;

/**
 * Enclosed in project InfiniteBattles for Aurora Enterprise.
 * Author: Josh Aurora
 * Date: 2013-04-27
 */
public enum BattleType{

	Boat_Wars(2, new String[]{"boats", "boatwar", "boatwars"}),
	Capture_The_Flag(3, new String[]{"ctf", "flag", "capture"}),
	Cars(1, new String[]{"cars", "carwar"}),
	Duck_Hunt(1, new String[]{"duckhunt", "dh", "duck"}),//done
	Free_For_All(3, new String[]{"freeforall", "ffa"}),//done
	Infection(999, new String[]{"infection", "infect", "zombie"}),
	InvPick(3, new String[]{"invpick", "inv", "chooseinv"}),
	PaintBall(3, new String[]{"paintball", "paint"}),
	Role_Play(3, new String[]{"rp", "roleplay"}),//done
	Spleef(1, new String[]{"spleef"});//done

	public int lives = 1;
	public List<String> aliases;

	private BattleType(int lives, String[] aliases){
		this.aliases = Arrays.asList(aliases);
		this.lives = lives;
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
		return aliases.contains(s);
	}

}
