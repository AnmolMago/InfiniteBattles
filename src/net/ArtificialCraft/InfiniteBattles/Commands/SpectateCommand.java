package net.ArtificialCraft.InfiniteBattles.Commands;

import org.bukkit.command.CommandSender;

/**
 * Enclosed in project InfiniteBattles for Aurora Enterprise.
 * Author: Josh Aurora
 * Date: 2013-08-13
 */
public class SpectateCommand implements ICommand{

	@Override
	public String execute(CommandSender sender, String[] args){
		return "Spectating has been disabled for now! To create your own game type \"/ IBattle create {GameType}\". ";
		/*if(args.length > 0){
			if(!(sender instanceof Player))
				return "You must be a player dimwit";
			if(IBattle.getBattle(args[0]) == null)
				return "This battle is currently not running!";

			IBattle.getBattle(args[0]).addSpectator(IBattle.getContestant(sender.getName()));
		}else{
			return "You must type the command as such \"/spectate {Battle}\"";
		}
		return null;*/
	}
}
