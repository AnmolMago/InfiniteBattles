package net.ArtificialCraft.InfiniteBattles.Commands;

import net.ArtificialCraft.InfiniteBattles.Collections.IError;
import net.ArtificialCraft.InfiniteBattles.Contestant.Contestant;
import net.ArtificialCraft.InfiniteBattles.Entities.Battles.BattleType;
import net.ArtificialCraft.InfiniteBattles.IBattle;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Enclosed in project InfiniteBattles for Aurora Enterprise.
 * Author: Josh Aurora
 * Date: 2013-05-02
 */
public class ContestantCommand implements ICommand{

	public String execute(CommandSender sender, String[] args){
		String name;
		if(args.length > 0){
			name = args[0].toLowerCase();
		}else{
			name = sender.getName().toLowerCase();
		}
		Contestant c = IBattle.getContestant(name);
		if(c == null)
			return IError.invalidContestant;
		sender.sendMessage(ChatColor.DARK_GREEN + "Stats:");
		sender.sendMessage(ChatColor.RED + "Wins: " + ChatColor.BLUE + c.getWins() + ChatColor.DARK_GREEN
				+ " | " + ChatColor.RED + "Losses: " + ChatColor.BLUE + c.getLosses() + ChatColor.DARK_GREEN
				+ " | " + ChatColor.RED + "Streak: " + ChatColor.BLUE + c.getStreak());
		sender.sendMessage(ChatColor.DARK_GREEN + "Games Played:");
		for(BattleType b : c.getCount().keySet())
			sender.sendMessage(ChatColor.RED + b.name() + ": " + ChatColor.BLUE + c.getCount().get(b));
		return null;
	}
}
