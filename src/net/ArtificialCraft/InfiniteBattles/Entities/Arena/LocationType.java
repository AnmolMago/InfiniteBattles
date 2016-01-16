package net.ArtificialCraft.InfiniteBattles.Entities.Arena;

/**
 * Enclosed in project InfiniteBattles for Aurora Enterprise.
 * Author: Josh Aurora
 * Date: 2013-06-26
 */
public enum LocationType{

	first,
	second,
	third,
	fourth,
	fifth,
	spectator(true),
	pitstop(true),
	blueflag(true),
	redflag(true);

	boolean special = false;

	LocationType(){}

	LocationType(boolean b){
		special = b;
	}

	public boolean isSpecial(){
		return special;
	}

}
