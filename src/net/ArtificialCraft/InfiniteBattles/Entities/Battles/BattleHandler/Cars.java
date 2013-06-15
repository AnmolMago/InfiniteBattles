package net.ArtificialCraft.InfiniteBattles.Entities.Battles.BattleHandler;

import net.ArtificialCraft.InfiniteBattles.Entities.Battles.Battle;
import org.bukkit.ChatColor;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;

import java.util.HashMap;

/**
 * Enclosed in project InfiniteBattles for Aurora Enterprise.
 * Author: Josh Aurora
 * Date: 2013-05-08
 */
public class Cars extends IBattleHandler{

	HashMap<String, Integer> damage = new HashMap<String, Integer>();

	public Cars(Battle b){
		super(b);
	}

	@Override
	public void load(){

	}

	@Override
	public void start(){

	}

	@EventHandler
	public void collide(VehicleEntityCollisionEvent e){
		if(!isBattleEvent(e) || !(e.getEntity() instanceof Minecart && e.getVehicle() instanceof Minecart)){
			return;
		}
		Vehicle other = (Vehicle) e.getEntity();
		if(!(other.getPassenger() instanceof Player)){
			return;
		}
		Player victim = (Player) other.getPassenger();
		String name = victim.getName();
		other.setVelocity(other.getVelocity().multiply(1.5));
		if(!damage.containsKey(name)){
			damage.put(name, 1);
		}else if(damage.get(name) > 10){
			damage.remove(name);
			victim.setHealth(0);
			getBattle().onContestantDeath(victim);
			if(e.getVehicle().getPassenger() instanceof Player)
				((Player)e.getVehicle().getPassenger()).sendMessage(ChatColor.RED + "you have just killed " + victim.getName());
		}else{
			damage.put(name, damage.get(name) + 1);
		}
	}

}
