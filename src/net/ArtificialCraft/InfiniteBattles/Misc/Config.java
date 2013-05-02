package net.ArtificialCraft.InfiniteBattles.Misc;

import net.ArtificialCraft.InfiniteBattles.Entities.Arena.Arena;
import net.ArtificialCraft.InfiniteBattles.Entities.Arena.ArenaHandler;
import net.ArtificialCraft.InfiniteBattles.IBattle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.util.HashMap;

/**
 * Enclosed in project ArtificialIntelligence for Aurora Enterprise.
 * Author: Josh Aurora
 * Date: 2013-04-25
 */
public class Config{

	private static File configFile, dataFolder;
	private static FileConfiguration config;

	public static void init(File folder, InputStream is){
		dataFolder = folder;
		configFile = new File(folder, "config.yml");
		try{
			firstRun(is);
		}catch(Exception e){
			e.printStackTrace();
		}
		config = new YamlConfiguration();
		loadYamls();
		loadContestants();
	}

	public static FileConfiguration getConfig(){
		return config;
	}

	private static void firstRun(InputStream is) throws Exception{
		if(configFile.exists()){
			Util.debug("Misc file found!");
		}else{
			Util.debug("Misc file NOT found, creating now!");
			configFile.getParentFile().mkdirs();
			copy(is, configFile);
		}
	}

	public static void copy(InputStream in, File file){
		try{
			OutputStream out = new FileOutputStream(file);
			byte[] buf = new byte[1024];
			int len;
			while((len = in.read(buf)) > 0){
				out.write(buf, 0, len);
			}
			out.close();
			in.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public static void loadYamls(){
		try{
			config.load(configFile);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public static void saveYamls(){
		try{
			config.save(configFile);
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	public static void loadArenas(){
		HashMap<String, Arena> arenas = new HashMap<String, Arena>();
		if(config.getConfigurationSection("Arenas") == null){
			Util.debug("null");
			return;
		}
		for(String key : config.getConfigurationSection("Arenas").getKeys(false)){
			Arena a = Formatter.parseArena(config.getString("Arenas." + key));
			arenas.put(key, a);
		}
		IBattle.getArenas().putAll(arenas);
		ArenaHandler.getUnusedArenas().putAll(arenas);
	}

	public static void addArena(Arena a){
		String name = a.getName();
		config.set("Arenas." + name + ".", a.toString());
		Config.saveYamls();
	}

	public static void loadContestants(){
		File f = getContestantList();
		try{
			BufferedReader reader = new BufferedReader(new FileReader(f));
			String line;
			while((line = reader.readLine()) != null){
				if(line.contains("#"))
					line = line.split("#")[0];   //con|test|ant #this one is a whore

				IBattle.addContestant(Formatter.parseContestant(line));
			}
			reader.close();
		}catch(FileNotFoundException x){
			f.mkdirs();
		}catch(IOException x){
			x.printStackTrace();
		}
	}

	public static File getContestantList(){
		return new File(dataFolder.getAbsolutePath() + "/contestants.txt");
	}

	public static void saveContestants(){

	}

}
