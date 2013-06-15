package net.ArtificialCraft.InfiniteBattles.ScoreBoard;

import net.ArtificialCraft.InfiniteBattles.Entities.Contestant.Contestant;
import net.ArtificialCraft.InfiniteBattles.Entities.Battles.Battle;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.Set;

/**
 * Enclosed in project InfiniteBattles for Aurora Enterprise.
 * Author: Josh Aurora
 * Date: 2013-04-29
 */
public class ScoreboardHandler{

	static ScoreboardManager manager = Bukkit.getScoreboardManager();

	public static Scoreboard getNewScoreBoard(Battle b){
		Scoreboard sb = manager.getNewScoreboard();
		Objective health = sb.registerNewObjective("Lives", "dummy");
		health.setDisplaySlot(DisplaySlot.BELOW_NAME);
		health.setDisplayName("lives");
		for(Contestant c : b.getContestants()){
			health.getScore(c.getPlayer()).setScore(b.getLivesLeft(c));
		}
		Objective side = sb.registerNewObjective("IBattle", "dummy");
		side.setDisplaySlot(DisplaySlot.SIDEBAR);
		side.setDisplayName(ChatColor.GOLD + b.getType().getName());
		return sb;
	}

	public static Team createTeam(Scoreboard sb, String name){
		return createTeam(sb, name, null);
	}

	public static Team createTeam(Scoreboard sb, String name, Set<Player> players){
		Team team = sb.registerNewTeam(name);
		if(players != null){
			for(Player p : players)
				team.addPlayer(p);
		}
		team.setAllowFriendlyFire(false);
		team.setCanSeeFriendlyInvisibles(true);
		return team;
	}

}
