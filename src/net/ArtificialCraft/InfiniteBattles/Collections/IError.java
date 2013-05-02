package net.ArtificialCraft.InfiniteBattles.Collections;

import net.ArtificialCraft.InfiniteBattles.Misc.Util;

/**
 * Enclosed in project InfiniteBattles for Aurora Enterprise.
 * Author: Josh Aurora
 * Date: 2013-04-27
 */
public class IError{
	public static final String playerOnly = "You cannot use this command unless you are a player!";
	public static final String noPerm = "You do not have permission to use this command!";
	public static final String invalidCommand = "This is an invalid command! Please try again!";
	public static final String noMoreThanOne = "You cannot create more than one battle at a time!";
	public static final String invalidBattleType = "This is an invalid battletype! Please choose from: " + Util.getBattleTypes();
	public static final String outOfArenas = "No arenas are available for this battletype! Please try again later!";
	public static final String alreadyInBattle = "You are already fighting in a battle!";
}
