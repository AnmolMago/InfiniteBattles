package net.ArtificialCraft.InfiniteBattles.Entities.Battles.BattleHandler;

import net.ArtificialCraft.InfiniteBattles.Entities.Battles.Battle;
import net.ArtificialCraft.InfiniteBattles.Entities.Battles.Status;
import net.ArtificialCraft.InfiniteBattles.Entities.Contestant.Contestant;
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
		for(Contestant c : getBattle().getContestants())
			c.teleport(IBattle.getInvpicker());
	}

	@Override
	public void start(){
		getBattle().setStatus(Status.Started);
		for(Contestant c : getBattle().getContestants())
			c.teleport(getBattle().getArena().getRandomSpawn());
	}

	@EventHandler
	public void onClick(PlayerInteractEvent e){
		if(!isBattleEvent(e) || e.getAction().equals(Action.LEFT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_AIR)){
			return;
		}
		if(getBattle().isStarted()){
			Util.error(e.getPlayer(), "You cannot use this sign when fighting!");
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
					p.teleport(b.getArena().getPitstop());
					points.remove(p.getName());
					if(points.size() == 0){
						start();
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
		}
	}
}
