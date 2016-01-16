package net.ArtificialCraft.InfiniteBattles.Commands;

import net.ArtificialCraft.InfiniteBattles.Collections.IError;
import net.ArtificialCraft.InfiniteBattles.Misc.Util;
import org.bukkit.command.CommandSender;

import java.util.HashMap;

/**
 * Enclosed in project InfiniteBattles for Aurora Enterprise.
 * Author: Josh Aurora
 * Date: 2013-04-27
 */
public class CommandManager{

	public static HashMap<String, ICommand> cmds = new HashMap<String, ICommand>();
	private static boolean init = false;

	public static void init(){
		init = true;
		cmds.put("ibattle", new BattleCommand());
		cmds.put("iarena", new ArenaCommand());
		cmds.put("contestant", new ContestantCommand());
		cmds.put("spectate", new SpectateCommand());
	}

	public static void execute(CommandSender sender, String cmd, String[] args){
		if(!init)
			init();
		cmd = cmd.toLowerCase();
		if(cmds.containsKey(cmd)){
			String s = cmds.get(cmd).execute(sender, args);
			if(s != null)
				Util.error(sender, s);
		}else{
			Util.error(sender, IError.invalidCommand + " | " + cmd);
		}
	}
}
