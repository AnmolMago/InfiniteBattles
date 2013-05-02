package net.ArtificialCraft.InfiniteBattles.ScoreBoard;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
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

	public static Scoreboard getNewScoreBoard(){
		return manager.getNewScoreboard();
	}

	public static Team createTeam(Scoreboard sb, String name, Set<Player> players){
		Team team = sb.registerNewTeam(name);
		for(Player p : players)
			team.addPlayer(p);

		team.setAllowFriendlyFire(false);
		team.setCanSeeFriendlyInvisibles(true);
		return team;
	}

}
