package net.ArtificialCraft.InfiniteBattles.Entities.Battles.BattleHandler;

import net.ArtificialCraft.InfiniteBattles.Entities.Contestant.Contestant;
import net.ArtificialCraft.InfiniteBattles.Entities.Battles.Battle;
import net.ArtificialCraft.InfiniteBattles.IBattle;
import net.ArtificialCraft.InfiniteBattles.Misc.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Enclosed in project InfiniteBattles for Aurora Enterprise.
 * Author: Josh Aurora
 * Date: 2013-04-29
 */
public class RolePlay extends IBattleHandler{

	Set<String> chosen = new HashSet<String>();
	private static HashMap<String, String> roles = new HashMap<String, String>();
	private static HashMap<String, Long> lightning = new HashMap<String, Long>();
	private boolean started;

	public RolePlay(Battle b){
		super(b);
	}

	@Override
	public void load(){
		for(Contestant c : getBattle().getContestants()){
			c.teleport(IBattle.getRolepicker());
			c.getPlayer().getInventory().clear();
			c.getPlayer().getInventory().setArmorContents(null);
		}
	}

	@Override
	public void start(){
		started = true;
		for(Contestant c : getBattle().getContestants()){
			Player p = c.getPlayer();
			if(p != null)
				p.teleport(getBattle().getArena().getSpawns().get(new Random().nextInt(3)));
		}
	}

	@EventHandler
	public void onDamage(EntityDamageEvent e){
		if(!isBattleEvent(e)){return;}
		if(!started){
			e.setCancelled(true);
		}
		Player p = (Player)e.getEntity();
		if(e.getCause().equals(DamageCause.BLOCK_EXPLOSION)){
			e.setDamage(e.getDamage()/2);
		}
		if(roles.containsKey(p.getName()) && roles.get(p.getName()).equals("mage") && e.getCause().equals(DamageCause.LIGHTNING)){
			e.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlace(BlockPlaceEvent e){
		if(!isBattleEvent(e)){return;}
		if(e.getBlock().getType().equals(Material.TNT) && roles.containsKey(e.getPlayer().getName()) && roles.get(e.getPlayer().getName()).equals("pyro")){
			e.getBlock().setType(Material.AIR);
			e.getPlayer().getWorld().spawnEntity(e.getBlock().getLocation().add(0,1,0), EntityType.PRIMED_TNT);
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onClick(PlayerInteractEvent e){
		if(!isBattleEvent(e)){return;}
		if(e.getAction().equals(Action.LEFT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_AIR) || IBattle.isPlayerPlaying(e.getPlayer().getName()) == null){
			return;
		}
		Player p = e.getPlayer();
		if(e.getClickedBlock().getType().equals(Material.WALL_SIGN) || e.getClickedBlock().getType().equals(Material.SIGN) || e.getClickedBlock().getType().equals(Material.SIGN_POST)){
			Sign s = (Sign)e.getClickedBlock().getState();
			Battle b = getBattle();
			if(s.getLine(0).equalsIgnoreCase("{Role}") && started){
				Util.error(e.getPlayer(), "You cannot use this sign when fighting!");
				return;
			}
			if(s.getLine(0).equalsIgnoreCase("{Role}") && addClassInv(s.getLine(1), p, b)){
				chosen.add(p.getName());
				p.teleport(getBattle().getArena().getPitstop());
				Util.msg(p, "You will start this battle as a " + ChatColor.DARK_AQUA + roles.get(p.getName()) + "§6!");
				if(chosen.size() == b.getContestants().size()){
					start();
				}else{
					for(String ch : chosen)
						Util.msg(Bukkit.getPlayerExact(ch), ChatColor.RED + "We are waiting for " + (b.getContestants().size() - chosen.size()) + " more players!");
				}
				e.setCancelled(true);
			}
		}else if(e.getItem().getType().equals(Material.BLAZE_ROD) && roles.containsKey(e.getPlayer().getName()) && roles.get(e.getPlayer().getName()).equals("mage")){
			if(!lightning.containsKey(p.getName()) || lightning.get(p.getName()) + 15000 < System.currentTimeMillis()){
				lightning.put(p.getName(), System.currentTimeMillis());
				p.getWorld().strikeLightning(e.getClickedBlock().getLocation());
			}else{
				Util.error(p, "You have " + (lightning.get(p.getName()) + 15000 - System.currentTimeMillis())/1000 + " seconds left before you can use lightning again!");
			}
		}else if(e.getItem().getType().equals(Material.EGG) && e.getItem().getData().getData() == 50 && roles.containsKey(e.getPlayer().getName()) && roles.get(e.getPlayer().getName()).equals("mage")){
			e.setCancelled(true);
			p.getWorld().spawnEntity(e.getClickedBlock().getLocation(), EntityType.CREEPER);
		}
	}

	@EventHandler
	public void onTarget(EntityTargetLivingEntityEvent e){
		if(e.getTarget() instanceof Player && !(e.getEntity() instanceof Player)){
			Player p = (Player)e.getTarget();
			if(roles.containsKey(p.getName()) && roles.get(p.getName()).equals("mage")){
				e.setCancelled(true);
			}
		}
	}

	@SuppressWarnings( "deprecation" )
	public static boolean addClassInv(String c, Player p, Battle b){
		if(!p.getWorld().getName().equalsIgnoreCase("Warfare")){
			Util.error(p, ChatColor.RED + "Not in this world.");
			return false;
		}
		c = c.toLowerCase();
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
		}else{
			c = "ranger";
			addRangerInv(p);
		}
		roles.put(p.getName(), c);
		p.getInventory().addItem(new ItemStack(Material.BREAD, 128));
		p.getInventory().addItem(new ItemStack(Material.COBBLESTONE, 128));
		if(p.hasPermission("class.donator")){
			p.getInventory().addItem(new ItemStack(Material.COMPASS, 1));
			Util.msg(p, ChatColor.RED + "For having donated to the server you have been given a compass to help track other players.");
		}
		p.updateInventory();
		return true;
	}

	public static void addRangerInv(Player p){
		p.getInventory().setHelmet(new ItemStack(Material.CHAINMAIL_HELMET, 1));
		p.getInventory().setChestplate(new ItemStack(Material.CHAINMAIL_CHESTPLATE, 1));
		p.getInventory().setLeggings(new ItemStack(Material.CHAINMAIL_LEGGINGS, 1));
		p.getInventory().setBoots(new ItemStack(Material.CHAINMAIL_BOOTS, 1));
		p.getInventory().addItem(new ItemStack(Material.BOW, 1));
		p.getInventory().addItem(new ItemStack(Material.ARROW, 128));
		p.getInventory().getItem(p.getInventory().first(Material.BOW)).addEnchantment(Enchantment.ARROW_DAMAGE, 1);
		p.getInventory().getItem(p.getInventory().first(Material.BOW)).addEnchantment(Enchantment.ARROW_KNOCKBACK, 1);
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
