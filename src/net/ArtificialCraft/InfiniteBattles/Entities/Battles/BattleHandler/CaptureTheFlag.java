package net.ArtificialCraft.InfiniteBattles.Entities.Battles.BattleHandler;

import net.ArtificialCraft.InfiniteBattles.Contestant.Contestant;
import net.ArtificialCraft.InfiniteBattles.Entities.Battles.Battle;
import net.ArtificialCraft.InfiniteBattles.Misc.Formatter;
import net.ArtificialCraft.InfiniteBattles.Misc.Util;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.List;

/**
 * Enclosed in project InfiniteBattles for Aurora Enterprise.
 * Author: Josh Aurora
 * Date: 2013-04-27
 */
public class CaptureTheFlag extends IBattleHandler{

	Block redBlock, blueBlock;
	Location redLoc, blueLoc;
	String blueHolder, redHolder;
	Team redTeam, blueTeam;
	Scoreboard board = getBattle().getScoreboard();

	public CaptureTheFlag(Battle b){
		super(b);
		redLoc = Formatter.parseLoc(config.getString("redBlock"));
		blueLoc = Formatter.parseLoc(config.getString("blueBlock"));
		redBlock = Bukkit.getWorld(redLoc.getWorld().getName()).getBlockAt(redLoc);
		blueBlock = Bukkit.getWorld(blueLoc.getWorld().getName()).getBlockAt(blueLoc);
		redTeam = getBattle().getScoreboard().registerNewTeam("redTeam");
		List<Contestant> teams = getBattle().getContestants();
		int half = teams.size() / 2;
		for(int i = 0; i < half; i++)
			redTeam.addPlayer(teams.remove(i).getPlayer());

		for(Contestant c : teams)
			blueTeam.addPlayer(c.getPlayer());

	}

	@Override
	public void load(){

	}

	@EventHandler
	public void onInteract(PlayerInteractEvent e){
		if(!isBattleEvent(e)){return;}
		Player p = e.getPlayer();
		if(e.getClickedBlock().equals(redBlock)){
			if(board.getPlayerTeam(p).equals(redTeam)){
				if(redBlock.getLocation().equals(redLoc)){
					Util.error(p, "Your flag is safe!");
				}else{
					e.getClickedBlock().setType(Material.AIR);
					redLoc.getWorld().getBlockAt(redLoc).setTypeIdAndData(35, (byte)14, false);
				}
			}else if(board.getPlayerTeam(p).equals(blueTeam)){
				redHolder = p.getName();

			}
		}else if(e.getClickedBlock().equals(blueBlock)){
			if(board.getPlayerTeam(p).equals(blueTeam)){
				if(blueBlock.getLocation().equals(blueLoc)){
					Util.error(p, "Your flag is safe!");
				}else{
					e.getClickedBlock().setType(Material.AIR);
					blueLoc.getWorld().getBlockAt(blueLoc).setTypeIdAndData(35, (byte)11, false);
				}
			}
		}
	}

	@EventHandler
	public void onMove(PlayerMoveEvent e){
		if(!isBattleEvent(e)){return;}
		if(e.getPlayer().getName().equalsIgnoreCase(redHolder) || e.getPlayer().getName().equalsIgnoreCase(blueHolder)){
			e.getPlayer().getWorld().playEffect(e.getPlayer().getLocation(), Effect.MOBSPAWNER_FLAMES, 0, 10);
		}
	}

}
