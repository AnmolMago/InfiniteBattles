package net.ArtificialCraft.InfiniteBattles.Commands;

import net.ArtificialCraft.InfiniteBattles.Collections.IError;
import net.ArtificialCraft.InfiniteBattles.Entities.Arena.Arena;
import net.ArtificialCraft.InfiniteBattles.Entities.Arena.ArenaHandler;
import net.ArtificialCraft.InfiniteBattles.Entities.Battles.Battle;
import net.ArtificialCraft.InfiniteBattles.Entities.Battles.BattleType;
import net.ArtificialCraft.InfiniteBattles.Entities.QueueHandler;
import net.ArtificialCraft.InfiniteBattles.IBattle;
import net.ArtificialCraft.InfiniteBattles.Misc.Config;
import net.ArtificialCraft.InfiniteBattles.Misc.Formatter;
import net.ArtificialCraft.InfiniteBattles.Misc.Util;
import org.bukkit.ChatColor;
import org.bukkit.Location;
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
				String name = args.length > 1 ? args[1] : null;
				BattleType bt = null;
				if(name != null){
					for(BattleType can : BattleType.values()){
						if(can.isType(name))
							bt = can;
					}
				}
				if(bt == null){
					Util.error(sender, "This is an invalid battletype, please choose from the following:");
					for(BattleType type : BattleType.values())
						sender.sendMessage(ChatColor.BLUE + "     - " + ChatColor.RED + type.getTypableName());
					return null;
				}
				Arena a = null;
				if(args.length == 3)
					a = ArenaHandler.getUnusedArena(args[2]);

				if(a == null)
					a = ArenaHandler.getArenaFor(bt);

				if(a != null && args.length == 3){
					Util.error(sender, "This arena is not available so we are giving you another arena to fight at!");
				}else if(a == null){
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
				if(args.length > 1 && IBattle.getBattle(args[1]) != null){
					Battle b = IBattle.getBattle(args[1]);
					if(b.isJoinable()){
						if(b.addContestant(IBattle.getContestant(p.getName()))){
							Util.msg(p, "You have been added to " + b.getName() + "!");
						}else{
							return "Error: Could not add you to battle!";
						}
					}else{
						return "This battle has already started! Please type \"/spectate\" to watch!";
					}
				}else{
					String running;
					if(IBattle.getCurrentBattles().keySet().size() == 0){
						running = "There are currently no battles available, type \"/iBattle create\" if you wish to create a battle!";
					}else{
						StringBuilder sb = new StringBuilder();
						String b1 = "", b2 = "";
						int count = 0;
						if(IBattle.getCurrentBattles().containsKey("battle1") && IBattle.getBattle("battle1").isJoinable()){//You can join the battle "Battle1"
							count++;
							b1 = " \"Battle1\"";
						}
						if(IBattle.getCurrentBattles().containsKey("battle2") && IBattle.getBattle("battle2").isJoinable()){//You can join the battles "Battle1" and "Battle2"
							if(count == 1)
								b1 = "s" + b1 + " and ";

							b2 = "\"Battle2\"";
						}
						sb.append("To join a battle type \"/join {Battle Name}\"! You can join the battle" + b1 + b2 + "!");
						running = sb.toString();
					}
					return "This battle is not running! " + running;
				}
			}else if(args[0].equalsIgnoreCase("leave")){
				if(!(sender instanceof Player)){
					return IError.playerOnly;
				}
				Player p = (Player)sender;
				Battle b = IBattle.isPlayerPlaying(p.getName());
                if(b == null)
                    b = IBattle.isPlayerSpectating(p.getName());
				if(b != null && b.goHome(IBattle.getContestant(p.getName()))){
					Util.msg(p, "You have been removed from the battle");
				}else{
					Util.error(p, "You are not part of a battle!");
				}
			}else if(args[0].equalsIgnoreCase("config")){
				if(!sender.isOp())
					return "You do not have permission to use this command!";
				if(args.length > 1){
					if(args[1].equalsIgnoreCase("reload")){
						Config.init(IBattle.getPlugin().getDataFolder(), IBattle.getPlugin().getResource("config.yml"));

						Config.loadArenas();
						Config.loadContestants();
						Util.msg(sender, "k done");
					}else if(args[1].equalsIgnoreCase("set") && args.length > 2 && sender instanceof Player){
						Player p = (Player) sender;
						Location l = p.getLocation();
						Util.broadcastDebug(l + " | " + l.getX() + "|" + l.getY() + "|" + l.getZ());
						if(args[2].equalsIgnoreCase("rolepicker")){
							IBattle.setRolepicker(p.getLocation());
							Config.getConfig().set("rolepicker", Formatter.configLoc(IBattle.getRolepicker()));
							Config.saveYamls();
							Util.msg(sender, "k done");
						}else if(args[2].equalsIgnoreCase("invpicker")){
							IBattle.setInvpicker(p.getLocation());
							Config.getConfig().set("invpicker", Formatter.configLoc(IBattle.getRolepicker()));
							Config.saveYamls();
							Util.msg(sender, "k done");
						}else if(args[2].equalsIgnoreCase("blueflag") || args[2].equalsIgnoreCase("redflag")){
							byte color = args[2].equalsIgnoreCase("redflag") ? (byte)14 : (byte)11;
							p.getLocation().getBlock().setTypeIdAndData(35, color, false);
							Config.getConfig().getConfigurationSection("Handlers.CaptureTheFlag").set(args[2].toLowerCase(), Formatter.configLoc(p.getLocation()));
							Util.msg(sender, "k done");
						}else if(args[2].equalsIgnoreCase("boatspawn")){
							Config.getConfig().getConfigurationSection("Handlers.Boat").set(args[2].toLowerCase(), Formatter.configLoc(p.getLocation()));
							Util.msg(sender, "k done");
						}
					}
				}else{
					return "not enough args bro";
				}
			}
		}else{
			Util.msg(sender, "InfiniteBattles is a one of a kind plugin that allows you to face your enemies in many different gamemodes.");
			Util.msg(sender, "You can use the following commands:");
			Util.msg(sender, ChatColor.DARK_AQUA + "     - \"/ibattle create {BattleType}\"");
			Util.msg(sender, ChatColor.DARK_AQUA + "     - \"/join {Battle}\"");
			Util.msg(sender, ChatColor.DARK_AQUA + "     - \"/spectate {Battle}\"");
			Util.msg(sender, ChatColor.DARK_AQUA + "     - \"/status\"");
		}
		return null;
	}

	private void createBattle(CommandSender sender, BattleType bt, Arena a){
		boolean toQueue = IBattle.getCurrentBattles().size() >= 2;
		final String name = IBattle.getCurrentBattles().containsKey("battle1") ? "Battle2" : "Battle1";
		Battle b = new Battle(name, System.currentTimeMillis(), sender.getName(), bt, a);
		if(toQueue){
			if(!QueueHandler.addToQueue(b)){
				Util.error(sender, "The queue is filled with too many battles! Please wait!");
				Util.debug("The queue is filled with too many battles! Please wait!");
				return;
			}
			Util.error(sender, "There are too many battles going on at once so your battle has been added to the queue!");
		}else{
			IBattle.startBattle(b);
		}
	}
}
