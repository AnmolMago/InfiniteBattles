package net.ArtificialCraft.InfiniteBattles.Entities.Battles.BattleHandler;

import net.ArtificialCraft.InfiniteBattles.Contestant.Contestant;
import net.ArtificialCraft.InfiniteBattles.Entities.Battles.Battle;
import net.ArtificialCraft.InfiniteBattles.IBattle;
import net.ArtificialCraft.InfiniteBattles.Items.ItemID;
import net.ArtificialCraft.InfiniteBattles.Items.Items;
import net.ArtificialCraft.InfiniteBattles.Misc.Util;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Random;

/**
 * Enclosed in project InfiniteBattles for Aurora Enterprise.
 * Author: Josh Aurora
 * Date: 2013-05-02
 */
public class InvPick extends IBattleHandler{

	private static HashMap<String, Integer> points = new HashMap<String, Integer>();
	private static Items iH = new Items();

	public InvPick(Battle b){
		super(b);
	}

	@Override
	public void load(){

	}

	@EventHandler
	public void onClick(PlayerInteractEvent e){
		if(!isBattleEvent(e)){return;}
		if(e.getAction().equals(Action.LEFT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_AIR) || IBattle.isPlayerPlaying(e.getPlayer().getName()) == null){
			return;
		}
		if(e.getClickedBlock().getType().equals(Material.WALL_SIGN) || e.getClickedBlock().getType().equals(Material.SIGN) || e.getClickedBlock().getType().equals(Material.SIGN_POST)){
			Sign s = (Sign)e.getClickedBlock().getState();
			if(!s.getLine(0).equalsIgnoreCase("{Role}") && !s.getLine(0).equalsIgnoreCase("{FORCE START}")){
				return;
			}
			Player p = e.getPlayer();
			Battle b = getBattle();
			if(s.getLine(0).equalsIgnoreCase("{Inv Pick}")){
				if(s.getLine(1).equalsIgnoreCase("Finish")){
					p.teleport(b.getArena().getPitStop());
					points.remove(p.getName());
					if(points.size() == 0){
						for(Contestant c : b.getContestants()){
							Player bp = c.getPlayer();
							if(bp != null){
								bp.teleport(b.getArena().getSpawns().get(new Random().nextInt(b.getArena().getSpawns().size())));
							}
						}
					}
					return;
				}
				int cost, amt, type = 999;
				String name;
				ItemID id;
				if(s.getLine(3).contains(":")){
					String[] split = s.getLine(3).split("\\:");
					name = split[0];
					type = Integer.parseInt(split[1]);
				}else{
					name = s.getLine(3);
				}
				try{
					amt = Integer.parseInt(s.getLine(1));
				}catch(Exception ex){
					Util.error(p, ChatColor.RED + "Woah, invalid amount! Contact the admins right away!");
					return;
				}
				try{
					cost = Integer.parseInt(s.getLine(2).replace(" points", ""));
				}catch(Exception ex){
					Util.error(p, ChatColor.RED + "Woah, invalid price! Contact the admins right away!");
					return;
				}
				id = iH.getItemIDByName(name);
				if(id == null){
					Util.error(p, ChatColor.RED + "Cannot get id! Contact the admins right away!");
					return;
				}
				if(points.get(p.getName()) >= cost){
					points.put(p.getName(), points.get(p.getName()) - cost);
					if(type == 999){
						p.getInventory().addItem(new ItemStack(id.getId(), amt));
					}else{
						p.getInventory().addItem(new ItemStack(id.getId(), amt, (short)type));
					}
					Util.msg(p, ChatColor.RED + "Congratulations, you have purchased some " + s.getLine(3) + " with " + cost + " points!");
				}else{
					Util.error(p, ChatColor.RED + "You do not have enough points available!");
				}
			}
		/*else if(s.getLine(0).equalsIgnoreCase("{Inv Pick}")){
				if(s.getLine(1).equalsIgnoreCase("Finish")){
					Battle b = PlayerHandler.players.get(p.getName());
					PlayerHandler.inv.put(p.getName(), p.getInventory().getContents());
					p.teleport(b.getArena().getPitStop());
					points.remove(p.getName());
					if(points.size() == 0){
						for(String bps : b.getPlayers()){
							Player bp = Util.getPlayer(bps);
							if(bp != null){
								bp.teleport(b.getArena().getSpawns().get(new Random().nextInt(b.getArena().getSpawns().size())));
							}else{
								PlayerHandler.removePlayer(bps);
							}
						}
					}
					return;
				}
				int cost, amt, type = 999;
				String name;
				ItemID id;
				if(s.getLine(3).contains(":")){
					String[] split = s.getLine(3).split("\\:");
					name = split[0];
					type = Integer.parseInt(split[1]);
				}else{
					name = s.getLine(3);
				}
				try{
					amt = Integer.parseInt(s.getLine(1));
				}catch(Exception ex){
					Util.error(p, ChatColor.RED + "Woah, invalid amount! Contact the admins right away!");
					return;
				}
				try{
					cost = Integer.parseInt(s.getLine(2).replace(" points", ""));
				}catch(Exception ex){
					Util.error(p, ChatColor.RED + "Woah, invalid price! Contact the admins right away!");
					return;
				}
				id = iH.getItemIDByName(name);
				if(id == null){
					Util.error(p, ChatColor.RED + "Cannot get id! Contact the admins right away!");
					return;
				}
				if(points.get(p.getName()) >= cost){
					points.put(p.getName(), points.get(p.getName()) - cost);
					if(type == 999){
						p.getInventory().addItem(new ItemStack(id.getId(), amt));
						Util.msg(p, ChatColor.RED + "Congratulations, you have purchased some " + s.getLine(3) + " with " + cost + " points!");
					}else{
						p.getInventory().addItem(new ItemStack(id.getId(), amt, (short)type));
						Util.msg(p, ChatColor.RED + "Congratulations, you have purchased some " + s.getLine(3) + " with " + cost + " points!");
					}
				}else{
					Util.error(p, ChatColor.RED + "You do not have enough points available!");
				}
			}
		}*/
		}
	}
}
