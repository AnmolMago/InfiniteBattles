package net.ArtificialCraft.InfiniteBattles.Listeners;

import net.ArtificialCraft.InfiniteBattles.Entities.Battles.Battle;
import net.ArtificialCraft.InfiniteBattles.IBattle;
import net.ArtificialCraft.InfiniteBattles.Misc.Formatter;
import net.ArtificialCraft.InfiniteBattles.Misc.Util;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;


public class SignListener implements Listener{

	@EventHandler
	public void onClick(PlayerInteractEvent e){
		if(e.getAction().equals(Action.LEFT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_AIR) || IBattle.isPlayerPlaying(e.getPlayer().getName()) == null){
			return;
		}
		if(e.getClickedBlock().getType().equals(Material.WALL_SIGN) || e.getClickedBlock().getType().equals(Material.SIGN) || e.getClickedBlock().getType().equals(Material.SIGN_POST)){
			Sign s = (Sign)e.getClickedBlock().getState();
			Player p = e.getPlayer();
			Battle b = IBattle.isPlayerPlaying(p.getName());
			if(b == null){
				return;
			}
			if(s.getLine(0).equalsIgnoreCase("{FORCE START}")){
				if(b.getTime() + 120000 < System.currentTimeMillis()){
					b.getHandler().start();
				}else{
					Util.error(p, "You still have to wait " + Formatter.formatTimeSpan((b.getTime() + 120000) - System.currentTimeMillis()) + " before you can force start!");
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlace(SignChangeEvent e){
		if(e.getBlock().getType().equals(Material.WALL_SIGN) || e.getBlock().getType().equals(Material.SIGN_POST) || e.getBlock().getType().equals(Material.SIGN)){
			if(e.getLine(0).equalsIgnoreCase("{Role}") || e.getLine(0).equalsIgnoreCase("{Inv Pick}")){
				if(!e.getPlayer().isOp()){
					e.getBlock().breakNaturally();
					e.setCancelled(true);
					Util.error(e.getPlayer(), ChatColor.RED + "You do not have permissions to make this type of sign!");
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBreak(BlockBreakEvent e){
		if(e.getBlock().getType().equals(Material.WALL_SIGN) || e.getBlock().getType().equals(Material.SIGN_POST) || e.getBlock().getType().equals(Material.SIGN)){
			if(((Sign)e.getBlock().getState()).getLine(0).equalsIgnoreCase("{Role}") || ((Sign)e.getBlock().getState()).getLine(0).equalsIgnoreCase("{InvPick}")){
				if(!e.getPlayer().isOp()){
					e.setCancelled(true);
					Util.error(e.getPlayer(), ChatColor.RED + "You do not have permissions to break this type of sign!");
				}
			}
		}
	}

}
