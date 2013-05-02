package net.ArtificialCraft.InfiniteBattles.Commands;

import net.ArtificialCraft.InfiniteBattles.Collections.IError;
import net.ArtificialCraft.InfiniteBattles.Entities.Battles.Battle;
import net.ArtificialCraft.InfiniteBattles.IBattle;
import net.ArtificialCraft.InfiniteBattles.Misc.Util;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Enclosed in project InfiniteBattles for Aurora Enterprise.
 * Author: Josh Aurora
 * Date: 2013-04-27
 */
public class JoinCommand implements ICommand{

	@Override
	public String execute(CommandSender sender, String[] args){
		if(!(sender instanceof Player)){return IError.playerOnly;}
		Player p = (Player) sender;
		if(args.length >= 1){
			if(IBattle.isPlayerPlaying(p.getName()) != null){
				return IError.alreadyInBattle;
			}
			Battle b = IBattle.getBattle(args[0]);
			if(b != null && b.isJoinable()){
				b.addContestant(IBattle.getContestant(p.getName()));
				Util.msg(p, "You have been added to " + b.getName() + "!");
			}else{
				String running;
				if(IBattle.getCurrentBattles().keySet().size() == 0){
					running = "There are currently no battles available, type \"/battle create\" if you wish to create a battle!";
				}else{
					StringBuilder sb = new StringBuilder();
					String b1 = "", b2 = "";
					int count = 0;
					if(IBattle.getCurrentBattles().containsKey("Battle1") && IBattle.getBattle("Battle1").isJoinable()){//You can join the battle "Battle1"
						count++;
						b1 = " \"Battle1\"";
					}
					if(IBattle.getCurrentBattles().containsKey("Battle1") && IBattle.getBattle("Battle2").isJoinable()){//You can join the battles "Battle1" and "Battle2"
						if(count == 1)
							b1 = "s" + b1 + " and ";

						b2 = "\"Battle2\"";
					}
					sb.append("To join a battle type \"/join {Battle Name}\"! You can join the battle" + b1 + b2 + "!");
					running = sb.toString();
				}
				return "This battle is not running! " + running;
			}
		}
		return null;
	}
}
