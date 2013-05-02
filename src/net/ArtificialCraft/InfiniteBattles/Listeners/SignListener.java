package net.ArtificialCraft.InfiniteBattles.Listeners;

import net.ArtificialCraft.InfiniteBattles.Entities.Battles.Battle;
import net.ArtificialCraft.InfiniteBattles.Entities.Battles.BattleType;
import net.ArtificialCraft.InfiniteBattles.IBattle;
import net.ArtificialCraft.InfiniteBattles.Items.ItemID;
import net.ArtificialCraft.InfiniteBattles.Items.Items;
import net.ArtificialCraft.InfiniteBattles.Misc.Util;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;


public class SignListener implements Listener{

	public static HashMap<String, Integer> points = new HashMap<String, Integer>();
	List<String> picked = new ArrayList<String>();

	private static Items iH = new Items(IBattle.getPlugin());

	@EventHandler
	public void onClick(PlayerInteractEvent e){
		if(e.getAction().equals(Action.LEFT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_AIR) || !PlayerHandler.isPlaying(e.getPlayer())){
			return;
		}
		if(e.getClickedBlock().getType().equals(Material.WALL_SIGN) || e.getClickedBlock().getType().equals(Material.SIGN) || e.getClickedBlock().getType().equals(Material.SIGN_POST)){
			Sign s = (Sign)e.getClickedBlock().getState();
			Player p = e.getPlayer();
			if(s.getLine(0).equalsIgnoreCase("{Role}")){
				String c = s.getLine(1);
				if(c.equalsIgnoreCase("Finish")){
					Battle b = PlayerHandler.players.get(p.getName());
					p.teleport(b.getArena().getPitStop());
					picked.add(p.getName());
					if(picked.size() >= b.getPlayers().size()){
						IBattle.pickingBattles.remove(b.getName().toLowerCase());
						if(b.getBattleType().equals(BattleType.TEAMRP)){
							Create.startTeamBattle(b);
						}else{
							for(String bps : b.getPlayers()){
								Player bp = Util.getPlayer(bps);
								if(bp != null){
									int rand = new Random().nextInt(b.getArena().getSpawns().size());
									Util.broadcastDebug(rand + "");
									bp.teleport(b.getArena().getSpawns().get(rand));
									Util.msg(bp, ChatColor.DARK_RED + "The battle has begun! Good luck!");
								}else{
									PlayerHandler.removePlayer(bps);
								}
							}
						}
					}else{
						for(String pl : picked){
							Util.msg(pl, ChatColor.RED + "We are waiting for " + (b.getPlayers().size() - picked.size()) + " more players!");
						}
					}
				}else if(addClassInv(c, p)){
					Util.msg(e.getPlayer(), "§6You are now a part of the §3" + c + "§6 class!");
				}
			}else if(s.getLine(0).equalsIgnoreCase("{Inv Pick}")){
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
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlace(SignChangeEvent e){
		if(e.getBlock().getType().equals(Material.WALL_SIGN) || e.getBlock().getType().equals(Material.SIGN_POST) || e.getBlock().getType().equals(Material.SIGN)){
			if(e.getLine(0).equalsIgnoreCase("{Class}") || e.getLine(0).equalsIgnoreCase("{Inv Pick}")){
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
			if(((Sign)e.getBlock().getState()).getLine(0).equalsIgnoreCase("{Class}") || ((Sign)e.getBlock().getState()).getLine(0).equalsIgnoreCase("{InvPick}")){
				if(!e.getPlayer().isOp()){
					e.setCancelled(true);
					Util.error(e.getPlayer(), ChatColor.RED + "You do not have permissions to break this type of sign!");
				}
			}
		}
	}

	/*
	 * ranger: bow(power/punch 1), chainmail armour, arrow*128
	 * berserker: diamond sword(sharpness 3), diamond helmet(protection 4)
	 * mob: spawn eggs - already argo dogs
	 * demolition guy: tnt * 256, fint and steel * 2, lava * 5, lether helmet and boots (fire), iron other (blast)
	 * ninja: chainmain helmet, 3 health potions, ironsword, swiftness potion.
	 * warrior: iron armour, diamondsword
	 * mage: ghast tear(explodes target), wand(strikes lightning), gold armor
	 */

	public static boolean addClassInv(Player p){
		if(!p.getWorld().getName().equalsIgnoreCase("iBattle")){
			Util.error(p, ChatColor.RED + "Not in this world.");
			return false;
		}
		String c = PlayerHandler.classes.get(p.getName());
		if(c == null){
			Util.error(p, ChatColor.RED + "This is weird ... you dont seem to have a class. Please choose again and tell an admin what happend.");
			p.teleport(IBattle.getPlugin().rolepicker);
			return false;
		}
		p.getInventory().clear();
		if(c.equalsIgnoreCase("ranger")){
			addRangerInv(p);
		}else if(c.equalsIgnoreCase("berserker")){
			addBerserkerInv(p);
		}else if(c.equalsIgnoreCase("mob")){
			addMobInv(p);
		}else if(c.equalsIgnoreCase("pyro")){
			addPyroInv(p);
		}else if(c.equalsIgnoreCase("ninja")){
			addNinjaInv(p);
		}else if(c.equalsIgnoreCase("warrior")){
			addWarriorInv(p);
		}else if(c.equalsIgnoreCase("mage")){
			addMageInv(p);
		}else if(c.equalsIgnoreCase("clear")){
			Util.msg(p, ChatColor.RED + "Inv and Role Cleared.");
			if(PlayerHandler.classes.containsKey(p.getName())){
				PlayerHandler.classes.remove(p.getName());
			}
			return false;
		}else{
			Util.error(p, ChatColor.RED + "This is an invalid role, please notify an admin and choose another class!");
			return false;
		}
		p.getInventory().addItem(new ItemStack(Material.BREAD, 128));
		p.getInventory().addItem(new ItemStack(Material.COBBLESTONE, 128));
		if(p.hasPermission("class.donator")){
			p.getInventory().addItem(new ItemStack(Material.COMPASS, 1));
			Util.msg(p, ChatColor.RED + "For having donated to the server you have been given a compass to help track other players.");
		}
		return true;
	}

	public static boolean addClassInv(String c, Player p){
		if(!p.getWorld().getName().equalsIgnoreCase("iBattle")){
			Util.error(p, ChatColor.RED + "Not in this world.");
			return false;
		}
		p.getInventory().clear();
		if(c.equalsIgnoreCase("ranger")){
			addRangerInv(p);
		}else if(c.equalsIgnoreCase("berserker")){
			addBerserkerInv(p);
		}else if(c.equalsIgnoreCase("mob boss")){
			addMobInv(p);
		}else if(c.equalsIgnoreCase("pyro")){
			addPyroInv(p);
		}else if(c.equalsIgnoreCase("ninja")){
			addNinjaInv(p);
		}else if(c.equalsIgnoreCase("warrior")){
			addWarriorInv(p);
		}else if(c.equalsIgnoreCase("mage")){
			addMageInv(p);
		}else if(c.equalsIgnoreCase("clear")){
			Util.msg(p, ChatColor.RED + "Inv and Role Cleared.");
			if(PlayerHandler.classes.containsKey(p.getName())){
				PlayerHandler.classes.remove(p.getName());
			}
			return false;
		}else{
			Util.error(p, ChatColor.RED + "This is an invalid role, please notify an admin and choose another class!");
			p.teleport(IBattle.getPlugin().rolepicker);
			return false;
		}
		PlayerHandler.classes.put(p.getName(), c.toLowerCase());
		p.getInventory().addItem(new ItemStack(Material.BREAD, 128));
		p.getInventory().addItem(new ItemStack(Material.COBBLESTONE, 128));
		if(p.hasPermission("class.donator")){
			p.getInventory().addItem(new ItemStack(Material.COMPASS, 1));
			Util.msg(p, ChatColor.RED + "For having donated to the server you have been given a compass to help track other players.");
		}
		return true;
	}

	public static void addRangerInv(Player p){
		p.getInventory().setHelmet(new ItemStack(Material.CHAINMAIL_HELMET, 1));
		p.getInventory().setChestplate(new ItemStack(Material.CHAINMAIL_CHESTPLATE, 1));
		p.getInventory().setLeggings(new ItemStack(Material.CHAINMAIL_LEGGINGS, 1));
		p.getInventory().setBoots(new ItemStack(Material.CHAINMAIL_BOOTS, 1));
		p.getInventory().addItem(new ItemStack(Material.BOW, 1));
		p.getInventory().addItem(new ItemStack(Material.ARROW, 128));
		p.getInventory().getItem(p.getInventory().first(Material.ARROW)).addEnchantment(Enchantment.ARROW_DAMAGE, 1);
		p.getInventory().getItem(p.getInventory().first(Material.ARROW)).addEnchantment(Enchantment.ARROW_KNOCKBACK, 1);
	}

	public static void addBerserkerInv(Player p){
		p.getInventory().addItem(new ItemStack(Material.DIAMOND_SWORD, 1));
		p.getInventory().setHelmet(new ItemStack(Material.DIAMOND_HELMET, 1));
		p.getInventory().getItem(p.getInventory().first(Material.DIAMOND_SWORD)).addEnchantment(Enchantment.DAMAGE_ALL, 3);
		p.getInventory().getHelmet().addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
	}

	public static void addMobInv(Player p){
		p.getInventory().setHelmet(new ItemStack(Material.LEATHER_HELMET, 1));
		p.getInventory().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE, 1));
		p.getInventory().setLeggings(new ItemStack(Material.LEATHER_LEGGINGS, 1));
		p.getInventory().setBoots(new ItemStack(Material.LEATHER_BOOTS, 1));
		p.getInventory().addItem(new ItemStack(Material.MONSTER_EGG, 3, (short)50));//creeper
		p.getInventory().addItem(new ItemStack(Material.MONSTER_EGG, 3, (short)52));//skeletons
		p.getInventory().addItem(new ItemStack(Material.MONSTER_EGG, 3, (short)95));//wolves
	}

	public static void addPyroInv(Player p){
		p.getInventory().setHelmet(new ItemStack(Material.LEATHER_HELMET, 1));
		p.getInventory().setChestplate(new ItemStack(Material.IRON_CHESTPLATE, 1));
		p.getInventory().setLeggings(new ItemStack(Material.IRON_LEGGINGS, 1));
		p.getInventory().setBoots(new ItemStack(Material.LEATHER_BOOTS, 1));
		p.getInventory().addItem(new ItemStack(Material.TNT, 256));
		p.getInventory().addItem(new ItemStack(Material.FLINT_AND_STEEL, 2));
		p.getInventory().addItem(new ItemStack(Material.LAVA_BUCKET, 5));
		p.getInventory().getHelmet().addEnchantment(Enchantment.PROTECTION_FIRE, 3);
		p.getInventory().getBoots().addEnchantment(Enchantment.PROTECTION_FIRE, 3);
		p.getInventory().getChestplate().addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 2);
		p.getInventory().getLeggings().addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 2);
	}

	public static void addNinjaInv(Player p){
		p.getInventory().setHelmet(new ItemStack(Material.CHAINMAIL_BOOTS, 1));
		p.getInventory().addItem(new ItemStack(Material.IRON_SWORD, 5));
		p.getInventory().addItem(new ItemStack(373, 3, (short)8197));
		p.getInventory().addItem(new ItemStack(373, 1, (short)8194));
	}

	public static void addWarriorInv(Player p){
		p.getInventory().setHelmet(new ItemStack(Material.IRON_HELMET, 1));
		p.getInventory().setChestplate(new ItemStack(Material.IRON_CHESTPLATE, 1));
		p.getInventory().setLeggings(new ItemStack(Material.IRON_LEGGINGS, 1));
		p.getInventory().setBoots(new ItemStack(Material.IRON_BOOTS, 1));
		p.getInventory().addItem(new ItemStack(Material.DIAMOND_SWORD, 1));
	}

	public static void addMageInv(Player p){
		p.getInventory().setHelmet(new ItemStack(Material.GOLD_HELMET, 1));
		p.getInventory().setChestplate(new ItemStack(Material.GOLD_CHESTPLATE, 1));
		p.getInventory().setLeggings(new ItemStack(Material.GOLD_LEGGINGS, 1));
		p.getInventory().setBoots(new ItemStack(Material.GOLD_BOOTS, 1));
		p.getInventory().addItem(new ItemStack(Material.GHAST_TEAR, 1));
		p.getInventory().addItem(new ItemStack(Material.BLAZE_ROD, 1));
	}

}
