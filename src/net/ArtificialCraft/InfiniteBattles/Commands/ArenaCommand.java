package net.ArtificialCraft.InfiniteBattles.Commands;

import net.ArtificialCraft.InfiniteBattles.Entities.Arena.Arena;
import net.ArtificialCraft.InfiniteBattles.Entities.Arena.ArenaHandler;
import net.ArtificialCraft.InfiniteBattles.Entities.Arena.LocationType;
import net.ArtificialCraft.InfiniteBattles.Entities.Battles.BattleType;
import net.ArtificialCraft.InfiniteBattles.Misc.Config;
import net.ArtificialCraft.InfiniteBattles.Misc.Util;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Enclosed in project InfiniteBattles for Aurora Enterprise.
 * Author: Josh Aurora
 * Date: 2013-06-26
 */
public class ArenaCommand implements ICommand{

	HashMap<List<String>, HashMap<LocationType, Location>> cache = new HashMap<List<String>, HashMap<LocationType, Location>>();

	public String execute(CommandSender sender, String[] args){
		if(args.length > 0 && sender instanceof Player){
			Player p = (Player) sender;
			if(args[0].equalsIgnoreCase("create")){
				if(args.length > 1 && ArenaHandler.getUnusedArena(args[1]) != null){
					return "There is already an arena by this name";
				}else if(args.length > 2){//iarena create first ctf
					BattleType type = BattleType.getByName(args[2]);
					if(type == null)
						return "This is an invalid type";
					List<String> list = Arrays.asList(p.getName(), args[1], type.name());
					cache.put(list, new HashMap<LocationType, Location>());
					Util.msg(sender, "Please set the " + getNext(sender) + " location!");
				}else if(args.length > 1){
					List<String> list = Arrays.asList(p.getName(), args[1], "null");
					cache.put(list, new HashMap<LocationType, Location>());
					Util.msg(sender, "Please set the " + getNext(sender) + " location!");
				}else{
					return "Please type the command as such: \"/iarena create {name} (type)";
				}
			}else if(args[0].equalsIgnoreCase("set")){
				if(args.length > 1){
					LocationType lt;
					try{
						lt = LocationType.valueOf(args[1].toLowerCase());
					}catch(Exception e){
						return "This is an invalid LocationType!";
					}
					HashMap<LocationType, Location> e = cache.get(getKey(p));
					if(e != null){
						e.put(lt, p.getLocation());
					}
					Util.msg(sender, "Please set the " + getNext(sender) + " location!");
				}else{
					return "Please type the command as such: \"/iarena set {LocationType}";
				}
			}else if(args[0].equalsIgnoreCase("edit")){
				if(args.length > 2){
					Arena a = ArenaHandler.getUnusedArena(args[1]);
					if(a == null)
						return "This is an invalid arena or it is currently being used!";
					LocationType lt;
					try{
						lt = LocationType.valueOf(args[2].toLowerCase());
					}catch(Exception e){
						return "This is an invalid LocationType!";
					}
					a.setLocation(lt, p.getLocation());
					Config.saveArena(a);
					p.sendMessage(ChatColor.GOLD + "The location " + lt + " for the arena " + a.getName() + " has now been set to your location!");
				}else{
					return "Please type the command as such: \"/iarena edit {ArenaName} {LocationType}";
				}
			}else if(args[0].equalsIgnoreCase("complete")){
				List<String> list = getKey(p);
				Util.broadcastDebug(list.get(1) + cache.get(list).keySet().toString() + list.get(2));
				Arena a = new Arena(list.get(1), cache.get(list), BattleType.getByName(list.get(2)));
				Config.addArena(a);
				cache.remove(list);
			}else{
				return "Please choose from create, set, edit or complete for the first argument.";
			}
		}else{
			return "Please supply the correct arguments!";
		}
		return null;
	}

	public List<String> getKey(CommandSender sender){
		for(List<String> list : cache.keySet()){
			if(list.get(0).equalsIgnoreCase(sender.getName()))
				return list;
		}
		return null;
	}

	public String getNext(CommandSender sender){
		List<String> list = getKey(sender);
		BattleType bt = BattleType.getByName(list.get(2));
		if(bt == null)
			bt = BattleType.Role_Play;
		List<LocationType> l = new ArrayList<LocationType>(bt.locations);
		String s;
		if(l.size() > cache.get(list).size()){
			s = l.get(cache.get(list).size()).name();
		}else{
			s = "COMPLETE IT";
		}
		return ChatColor.AQUA + s + ChatColor.GOLD;
	}

}
