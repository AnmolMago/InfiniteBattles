package net.ArtificialCraft.InfiniteBattles.Entities.Arena;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.data.DataException;
import net.ArtificialCraft.InfiniteBattles.IBattle;
import net.ArtificialCraft.InfiniteBattles.Misc.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public class Rebuild implements Listener{

	private WorldEditPlugin WorldEdit;

	public static void rebuild(final Arena a){
		Bukkit.getScheduler().runTaskAsynchronously(IBattle.getPlugin(), new Runnable(){
			@Override
			public void run(){
				try{
					pasteArena(a);
				}catch(Exception ex){
					Util.broadcastDebug(ChatColor.RED + "ERROR: " + a.getName() + " has not pasted properly and will remain fucked up until corrected! Please inform Alien right away that what happend was: " + ex.getMessage());
					ex.printStackTrace();
				}
			}

		});
	}

	private static void pasteArena(Arena a) throws DataException, IOException, MaxChangedBlocksException{
		EditSession es = new EditSession(new BukkitWorld(a.getFirstSpawn().getWorld()), Integer.MAX_VALUE);
		@SuppressWarnings("deprecation") CuboidClipboard cc = CuboidClipboard.loadSchematic(new File(IBattle.getPlugin().getDataFolder(), a.getName().toLowerCase() + ".schematic"));
		//Location l = a.getPastepoint();
		//cc.paste(es, new Vector(l.getBlockX(), l.getBlockY(), l.getBlockZ()), false);
	}

	@EventHandler
	public void onPluginEnable(PluginEnableEvent e){
		Object localObject;
		if((e.getPlugin().getDescription().getName().equals("WorldEdit"))){
			localObject = (WorldEditPlugin)e.getPlugin();
			if((localObject != null) && (((Plugin)localObject).isEnabled())){
				this.setWorldEdit((WorldEditPlugin)localObject);
				Util.debug("attached to WorldEdit.");
			}
		}
	}

	@EventHandler
	public void onPluginDisable(PluginDisableEvent e){
		if(e.getPlugin().getDescription().getName().equals("WorldEdit")){
			this.setWorldEdit(null);
			Util.debug("lost connection to WorldEdit.");
		}
	}

	public void setWorldEdit(WorldEditPlugin paramWorldEdit){
		this.WorldEdit = paramWorldEdit;
	}

	public WorldEditPlugin getWorldEdit(){
		return this.WorldEdit;
	}
}
