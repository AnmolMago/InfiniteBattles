package net.ArtificialCraft.InfiniteBattles.Entities.Battles.BattleHandler;

import net.ArtificialCraft.InfiniteBattles.Entities.Battles.Battle;
import net.ArtificialCraft.InfiniteBattles.Entities.Battles.BattleType;
import net.ArtificialCraft.InfiniteBattles.Entities.Contestant.Contestant;
import net.ArtificialCraft.InfiniteBattles.IBattle;
import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Tameable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.vehicle.VehicleEvent;
import org.bukkit.potion.PotionEffect;

/**
 * Enclosed in project InfiniteBattles for Aurora Enterprise.
 * Author: Josh Aurora
 * Date: 2013-04-27
 */
public abstract class IBattleHandler implements Listener{

	//protected ConfigurationSection config;
	protected String battle;

	public IBattleHandler(Battle b){
		this.battle = b.getName();
		Bukkit.getPluginManager().registerEvents(this, IBattle.getPlugin());
		/*String path = "Handlers." + this.getClass().getSimpleName();
		if(!Config.getConfig().isConfigurationSection(path)){
			Config.getConfig().createSection(path);
		}
		config = Config.getConfig().getConfigurationSection(path);  */
	}

	public void unregisterHandler(){
		HandlerList.unregisterAll(this);
	}

	protected Battle getBattle(){
		return IBattle.getBattle(battle);
	}

	protected boolean isBattleEvent(Event e){
        if(getBattle() == null)
            return false;
		if(!getBattle().isStarted() && !(e instanceof PlayerInteractEvent) && !(e instanceof EntityDamageEvent)&& !(e instanceof PlayerDropItemEvent)&& !(e instanceof PlayerPickupItemEvent))
			return false;
		if(e instanceof EntityEvent){
			EntityEvent ee = (EntityEvent) e;
			Player p = null;
			if(ee instanceof EntityDamageByEntityEvent){
				EntityDamageByEntityEvent ebe = (EntityDamageByEntityEvent)ee;
				if(ebe.getEntity() instanceof Player){
					p = (Player)ebe.getEntity();
				}else if(ebe.getDamager() instanceof Player){
					p = (Player)ebe.getDamager();
				}else if(ebe.getDamager() instanceof Projectile && ((Projectile)ebe.getDamager()).getShooter() instanceof Player){
					p = (Player) ((Projectile)ebe.getDamager()).getShooter();
				}else if(ebe.getDamager() instanceof Tameable){
					if(((Tameable)ebe.getDamager()).getOwner() instanceof Player){
						p = (Player) ((Tameable)ebe.getDamager()).getOwner();
					}
				}else if(ebe.getEntity() instanceof Player){
					p = (Player)ebe.getEntity();
				}
			}else if(ee instanceof EntityDamageEvent){
				if(ee.getEntity() instanceof Player)
					p = (Player) ee.getEntity();
			}else if(e instanceof PlayerDeathEvent){
				p = ((PlayerDeathEvent)e).getEntity();
			}
			return p != null && p.getWorld().getName().equalsIgnoreCase("Warfare") && getBattle().hasContestant(IBattle.getContestant(p.getName()));
		}else if(e instanceof VehicleEvent){
			VehicleEvent ve = (VehicleEvent) e;
			Player p = (Player) ve.getVehicle().getPassenger();
			return p != null && p.getWorld().getName().equalsIgnoreCase("Warfare") && getBattle().hasContestant(IBattle.getContestant(p.getName()));
		}else if(e instanceof PlayerEvent){
			PlayerEvent pe = (PlayerEvent)e;
			return pe.getPlayer().getWorld().getName().equalsIgnoreCase("Warfare") && getBattle().hasContestant(IBattle.getContestant(pe.getPlayer().getName()));
		}else if(e instanceof BlockEvent){
			Player p;
			if(e instanceof BlockPlaceEvent){
				p = ((BlockPlaceEvent)e).getPlayer();
			}else if(e instanceof BlockBreakEvent){
				p = ((BlockBreakEvent)e).getPlayer();
			}else{
				return false;
			}
			return p.getWorld().getName().equalsIgnoreCase("Warfare") && getBattle().hasContestant(IBattle.getContestant(p.getName()));
		}
		return false;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onArrow(EntityDamageByEntityEvent e){
		if(!(e.getEntity() instanceof Player))
			return;
		Player p = (Player) e.getEntity();
		if(p.getHealth() - e.getDamage() > 1)
			return;
		if(e.getDamager() instanceof Arrow || e.getDamager().getType().equals(EntityType.ARROW)){
			e.setCancelled(true);
			Arrow a = (Arrow) e.getDamager();
			a.remove();
			LivingEntity shooter = a.getShooter();
			if(shooter != null){
				p.damage(e.getDamage(), shooter);
			}else{
				p.damage(e.getDamage());
			}
		}
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent e){
		if(getBattle() == null || !isBattleEvent(e))
			return;
		getBattle().onContestantDeath(e.getEntity());
		e.getDrops().clear();
		if(!e.getEntity().getActivePotionEffects().isEmpty()){
			for(PotionEffect pe : e.getEntity().getActivePotionEffects())
				e.getEntity().removePotionEffect(pe.getType());
		}
		if(getBattle().getType().equals(BattleType.Capture_The_Flag) || getBattle().getType().equals(BattleType.Infection) || getBattle().getType().equals(BattleType.PaintBall)){
			return;
		}
		Contestant c = IBattle.getContestant(e.getEntity().getName());
		if(getBattle().getType().getLives() <= getBattle().lives.get(c.getName())){
			getBattle().addSpectator(c);
			c.onBattlePlayed(getBattle().getType(), false);
		}
		if(getBattle().getContestants().size() == 1){
			getBattle().end(getBattle().getContestants().get(0));
		}
	}

	@EventHandler
	public void SBReset(PlayerRespawnEvent e){
		if(getBattle() == null || !isBattleEvent(e))
			return;
		e.getPlayer().setScoreboard(getBattle().getScoreboard());
	}

	public abstract void load();

	public abstract void start();

}
