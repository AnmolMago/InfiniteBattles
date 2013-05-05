package net.ArtificialCraft.InfiniteBattles.Commands;

import net.ArtificialCraft.InfiniteBattles.Collections.IError;
import net.ArtificialCraft.InfiniteBattles.Entities.Arena.Arena;
import net.ArtificialCraft.InfiniteBattles.Entities.Arena.ArenaHandler;
import net.ArtificialCraft.InfiniteBattles.Entities.Battles.Battle;
import net.ArtificialCraft.InfiniteBattles.Entities.Battles.BattleType;
import net.ArtificialCraft.InfiniteBattles.Entities.QueueHandler;
import net.ArtificialCraft.InfiniteBattles.IBattle;
import net.ArtificialCraft.InfiniteBattles.Misc.Util;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Enclosed in project InfiniteBattles for Aurora Enterprise.
 * Author: Josh Aurora
 * Date: 2013-04-27
 */
public class BattleCommand implements ICommand{

	@Override
	public String execute(CommandSender sender, String[] args){
		if(args.length >= 1){
			if(args[0].equalsIgnoreCase("create")){
				for(Battle b : QueueHandler.getQueue()){
					if(b.getCreator().equalsIgnoreCase(sender.getName()))
						return IError.noMoreThanOne;
				}
				BattleType bt;
				try{
					bt = BattleType.valueOf(args[1]);
				}catch(IllegalArgumentException x){
					return IError.invalidBattleType;
				}
				Arena a = null;
				if(args.length == 3)
					a = ArenaHandler.getUnusedArena(args[2]);

				if(a == null)
					a = ArenaHandler.getArenaFor(bt);

				if(a != null && args.length == 3){
					Util.error(sender, "This arena is not available so we are giving you another arena to fight at!");
				}else{
					return IError.outOfArenas;
				}
				createBattle(sender, bt, a);
			}else if(args[0].equalsIgnoreCase("join")){
				if(!(sender instanceof Player)){
					return IError.playerOnly;
				}
				Player p = (Player)sender;
				if(IBattle.isPlayerPlaying(p.getName()) != null){
					return IError.alreadyInBattle;
				}
				Battle b = IBattle.getBattle(args[1]);
				if(b != null){
					if(b.isJoinable()){
						b.addContestant(IBattle.getContestant(p.getName()));
						Util.msg(p, "You have been added to " + b.getName() + "!");
					}else{
						return "This battle has already started! Please type \"/spectate\" to watch!";
					}
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
		}else{
			IBattle.help(this);
		}
		return null;
	}

	private void createBattle(CommandSender sender, BattleType bt, Arena a){
		boolean toQueue = false;
		if(IBattle.getCurrentBattles().size() > 2)
			toQueue = true;
		final String name = IBattle.getCurrentBattles().containsKey("Battle1") ? "Battle2" : "Battle1";
		Battle b = new Battle(name, System.currentTimeMillis(), sender.getName(), bt, a);
		IBattle.addBattle(b);
		if(toQueue){
			if(!QueueHandler.addToQueue(b)){
				Util.error(sender, "The queue is filled with too many battles! Please wait!");
				Util.debug("The queue is filled with too many battles! Please wait!");
				return;
			}
			Util.error(sender, "Your battle has been added to the queue!");
		}else{

			b.startAcceptingContestants();
			Bukkit.getScheduler().runTaskLaterAsynchronously(IBattle.getPlugin(), new Runnable(){
				public void run(){
					Battle b = IBattle.getBattle(name);
					if(b.getContestants().size() > 1){
						b.setUp();
					}else{
						b.end("there were not enough players");
					}
				}
			}, 400);
		}
	}
}
