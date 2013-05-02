package net.ArtificialCraft.InfiniteBattles.Misc;

import net.ArtificialCraft.InfiniteBattles.Entities.Battles.BattleType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.logging.Level;

public class Util{

	public static void msg(CommandSender p, String msg){
		if(msg == null)
			return;
		if(p instanceof ConsoleCommandSender){
			debug(msg);
		}else if(p != null){
			p.sendMessage(ChatColor.GOLD + "[IBattle] " + msg);
		}
	}

	public static void msg(String name, String msg){
		Player p = Bukkit.getServer().getPlayer(name);
		if(p == null){
			debug("ERROR: could not send to " + name + ": " + msg);
			return;
		}
		p.sendMessage(ChatColor.GOLD + "[IBattle] " + ChatColor.RED + msg);
	}

	public static void error(CommandSender p, String msg){
		if(msg == null)
			return;
		if(p instanceof ConsoleCommandSender){
			debug(msg);
		}else if(p != null){
			p.sendMessage(ChatColor.DARK_RED + "[IBattle] " + ChatColor.RED + msg);
		}
	}

	public static void debug(String s){
		Bukkit.getLogger().log(Level.SEVERE, "[IBattle] " + s);
	}

	public static void debug(Level l, String s){
		Bukkit.getLogger().log(l, "[IBattle] " + s);
	}

	public static Player getPlayer(String s){
		return Bukkit.getServer().getPlayer(s);
	}

	public static void broadcast(String msg){
		Bukkit.getServer().broadcastMessage(ChatColor.GOLD + "[IBattle] " + ChatColor.WHITE + msg);
	}

	public static void broadcastDebug(String msg){
		for(Player p : Bukkit.getServer().getOnlinePlayers()){
			if(p.hasPermission("class.mod")){
				p.sendMessage(ChatColor.GOLD + "[IBattle] " + ChatColor.WHITE + msg);
			}
		}
	}

	public static void broadcastError(String msg){
		Bukkit.getServer().broadcastMessage(ChatColor.DARK_RED + "[IBattle] " + ChatColor.WHITE + msg);
	}

	public static boolean teleportRP(Player p){
		return false; //p.teleport(IBattle.getPlugin().rolepicker);
	}

	public static String getBattleTypes(){
		StringBuilder sb = new StringBuilder();
		for(BattleType bt : BattleType.values())
			sb.append(", " + ChatColor.BLUE + bt.name() + ChatColor.RED);

		return sb.toString().replaceFirst(", ", "");
	}
}
