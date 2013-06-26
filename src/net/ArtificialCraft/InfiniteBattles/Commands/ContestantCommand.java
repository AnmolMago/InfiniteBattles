package net.ArtificialCraft.InfiniteBattles.Commands;

import net.ArtificialCraft.InfiniteBattles.Collections.IError;
import net.ArtificialCraft.InfiniteBattles.Entities.Contestant.Contestant;
import net.ArtificialCraft.InfiniteBattles.Entities.Battles.BattleType;
import net.ArtificialCraft.InfiniteBattles.IBattle;
import org.bukkit.Bukkit;
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
		Contestant c = IBattle.getContestant(Bukkit.getOfflinePlayer(name).getName());
		if(c == null)
			return IError.invalidContestant;
		sender.sendMessage(ChatColor.DARK_GREEN + "Statistics:");
		sender.sendMessage(ChatColor.DARK_RED + "     Wins: " + ChatColor.BLUE + c.getWins() + ChatColor.DARK_GREEN
				+ "     |     " + ChatColor.DARK_RED + "Losses: " + ChatColor.BLUE + c.getLosses() + ChatColor.DARK_GREEN
				+ "     |     " + ChatColor.DARK_RED + "Streak: " + ChatColor.BLUE + c.parseStreak());
		sender.sendMessage(ChatColor.DARK_GREEN + "Games Played: " + ChatColor.DARK_RED + (c.getTotalPlayed() == 0 ? "None" : c.getTotalPlayed()));
		for(BattleType b : c.getCount().keySet())
			sender.sendMessage(ChatColor.DARK_RED + "      " + b.name() + ": " + ChatColor.BLUE + c.getCount().get(b));
		return null;
	}
}
