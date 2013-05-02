package net.ArtificialCraft.InfiniteBattles.Commands;

import net.ArtificialCraft.InfiniteBattles.Collections.IError;
import net.ArtificialCraft.InfiniteBattles.Misc.Util;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

/**
 * Enclosed in project InfiniteBattles for Aurora Enterprise.
 * Author: Josh Aurora
 * Date: 2013-04-27
 */
public class CommandManager{

	public static void execute(CommandSender sender, String cmd, String[] args){
		ICommand exec = null;
		for(Commands c : Commands.values()){
			if(c.isCmd(cmd)){
				exec = c.getExec();
				break;
			}
		}
		if(exec != null){
			Util.error(sender, exec.execute(sender, args));
		}else{
			Util.error(sender, IError.invalidCommand);
		}
	}

	public enum Commands{

		battle("ibattle", new BattleCommand(), new String[]{"", "d"}),
		join("join", new JoinCommand(), new String[]{"", "d"});

		String cmd;
		ICommand exec;
		List<String> aliases = null;

		Commands(String cmd, ICommand exec){
			this.cmd = cmd;
			this.exec = exec;
		}

		Commands(String cmd, ICommand exec, String[] aliases){
			this.cmd = cmd;
			this.exec = exec;
			this.aliases = Arrays.asList(aliases);
		}

		public ICommand getExec(){
			return exec;
		}

		public boolean isCmd(String s){
			return cmd.equalsIgnoreCase(s) || aliases.contains(s);
		}
	}
}
