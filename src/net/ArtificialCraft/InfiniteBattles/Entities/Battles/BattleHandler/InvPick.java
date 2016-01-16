package net.ArtificialCraft.InfiniteBattles.Entities.Battles.BattleHandler;

import net.ArtificialCraft.InfiniteBattles.Entities.Arena.LocationType;
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
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Enclosed in project InfiniteBattles for Aurora Enterprise.
 * Author: Josh Aurora
 * Date: 2013-05-02
 */
public class InvPick extends IBattleHandler{

	private HashMap<String, Integer> points = new HashMap<String, Integer>();
	private Items iH = new Items();
	private HashMap<String, PInv> invs = new HashMap<String, PInv>();

	public InvPick(Battle b){
		super(b);
	}

	@Override
	public void load(){
		for(Contestant c : getBattle().getContestants()){
			points.put(c.getName(), 1000);
			c.teleport(IBattle.getInvpicker());
			Util.msg(c.getPlayer(), "This area will allow you to choose your items by clicking the signs! You only have 1000 points so choose carefully!");
		}
	}

	@Override
	public void start(){
		for(Contestant c : getBattle().getContestants()){
			PlayerInventory pi = c.getPlayer().getInventory();
			invs.put(c.getName(), new PInv(pi.getContents(), pi.getArmorContents()));
			c.teleport(getBattle().getArena().getRandomLocation());
		}
		getBattle().setStatus(Status.Started);
	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent e){
		if(!isBattleEvent(e)){return;}
		String name = e.getPlayer().getName();
		if(invs.containsKey(name))
			restoreInv(e.getPlayer());
		e.setRespawnLocation(getBattle().getArena().getRandomLocation());
	}

	@EventHandler
	public void onPvP(EntityDamageByEntityEvent e){
		if(!isBattleEvent(e)){return;}
		if(!getBattle().isStarted())
			e.setCancelled(true);
	}

	@EventHandler
	public void onClick(PlayerInteractEvent e){
		if(!isBattleEvent(e) || e.getAction().equals(Action.LEFT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_AIR)){
			return;
		}
		if(e.getClickedBlock().getType().equals(Material.WALL_SIGN) || e.getClickedBlock().getType().equals(Material.SIGN) || e.getClickedBlock().getType().equals(Material.SIGN_POST)){
			Sign s = (Sign)e.getClickedBlock().getState();
			Player p = e.getPlayer();
			Battle b = getBattle();
			if(s.getLine(0).equalsIgnoreCase("{Inv Pick}")){
				e.setCancelled(true);
				if(s.getLine(1).equalsIgnoreCase("Finish")){
					p.teleport(b.getArena().getLocation(LocationType.pitstop));
					points.remove(p.getName());
					if(points.size() == 0){
						start();
					}else{
						String ss = "";
						List<String> names = new ArrayList<String>(points.keySet());
						for(String name : names)
							ss += ", " + name;
						getBattle().warnUsers("You are still waiting for: " + ss.replaceFirst(", ", ""));
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
					try{
						id = iH.getItemIDByName(iH.getItemNameByID(Integer.parseInt(name)));
					}catch(Exception ex){
						Util.error(p, ChatColor.RED + "Cannot get id! Contact the admins right away!");
						return;
					}
				}
				if(points.get(p.getName()) >= cost){
					points.put(p.getName(), points.get(p.getName()) - cost);
					if(type == 999){
						p.getInventory().addItem(new ItemStack(id.getId(), amt));
					}else{
						p.getInventory().addItem(new ItemStack(id.getId(), amt, (short)type));
					}
					Util.msg(p, ChatColor.RED + "Congratulations, you have purchased some " + s.getLine(3) + " with " + cost + " points!");
					alertPoints(p);
					p.updateInventory();
				}else{
					Util.error(p, ChatColor.RED + "You do not have enough points available!");
					alertPoints(p);
				}
			}
		}
	}

	@EventHandler
	public void onItemDrop(PlayerDropItemEvent e){
		if(!isBattleEvent(e)){return;}
		e.setCancelled(true);
	}

	@EventHandler
	public void onItemDrop(PlayerPickupItemEvent e){
		if(!isBattleEvent(e)){return;}
		e.setCancelled(true);
	}

	public void alertPoints(Player p){
		int val = points.get(p.getName());
		if(val > 0){
			Util.msg(p, ChatColor.GOLD + "You have " + ChatColor.DARK_AQUA + val + ChatColor.GOLD + " points remaining!");
		}else{
			Util.msg(p, ChatColor.GOLD + "You have used up all of your points. Please click the finished sign!");
		}
	}

	public void restoreInv(Player p){
		p.getInventory().setContents(invs.get(p.getName()).getContents());
		p.getInventory().setArmorContents(invs.get(p.getName()).getArmour());
		for(ItemStack i : p.getInventory().getContents()){
			if(i != null && i.getAmount() < 1){
				i.setAmount(1);
			}
		}
	}
}

class PInv{

	ItemStack[] contents, armour;

	public PInv(ItemStack[] contents, ItemStack[] armour){
		this.contents = contents;
		this.armour = armour;
	}

	public ItemStack[] getContents(){
		return contents;
	}
	public ItemStack[] getArmour(){
		return armour;
	}

}
