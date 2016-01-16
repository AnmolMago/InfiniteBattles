package net.ArtificialCraft.InfiniteBattles.Items;

import net.ArtificialCraft.InfiniteBattles.IBattle;
import net.ArtificialCraft.InfiniteBattles.Misc.Config;
import net.ArtificialCraft.InfiniteBattles.Misc.Util;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Pattern;

public class Items{

	private IBattle plugin = IBattle.getPlugin();
	private YamlConfiguration items;
	private double itemVersion = 0.7;
	private HashMap<ItemID, String> itemsByID = new HashMap<ItemID, String>();
	private HashMap<String, ItemID> itemsByName = new HashMap<String, ItemID>();
	private HashMap<ItemID, List<String>> itemTypes = new HashMap<ItemID, List<String>>();
	private HashMap<String, ItemID> itemsAliases = new HashMap<String, ItemID>();

	private List<Integer> getTypeList(int id) {
		List<Integer> types = new ArrayList<Integer>();
		for(Map.Entry<ItemID, List<String>> entry : itemTypes.entrySet()) {
			ItemID key = entry.getKey();
			if(key.getId() == id)
				types.add(key.getType());
		}

		return types;
	}

	public Items() {
		File itemsFile = new File(plugin.getDataFolder(), "items.yml");
		if(!itemsFile.exists()) {
			Util.debug(Level.INFO, "[" + plugin.getName() + "] Extracting new items.yml file...");
			Config.copy(plugin.getResource("items.yml"), itemsFile);
		}

		items = YamlConfiguration.loadConfiguration(itemsFile);
		double v = items.getDouble("version");
		if(v < this.itemVersion) {
			Util.debug(Level.INFO, "[" + plugin.getName() + "] Your items.yml has ran out of date. Updating now!");
			File oItemsFile = new File(plugin.getDataFolder(), "items.yml." + v + ".bak");
			itemsFile.renameTo(oItemsFile);
			Config.copy(plugin.getResource("items.yml"), itemsFile);
			items = YamlConfiguration.loadConfiguration(itemsFile);
		}

		loadNames();
		loadTypes();
	}

	public final void loadNames() {
		Set<String> list = items.getConfigurationSection("names").getKeys(false);
		if(list == null) {
			Util.debug(Level.INFO, "[" + plugin.getName() + "] There are no item names specified in the items.yml file?!");
			return;
		}

		for(String item : list) {
			int id;
			try {
				id = Integer.valueOf(item.substring(4));
			} catch(NumberFormatException x) {
				Util.debug(Level.WARNING, "[" + plugin.getName() + "] Invalid key detected in items.yml (names." + item + ")");
				continue;
			}

			List<String> names = items.getStringList("names." + item);
			if(names.size() > 0) {
				String name = names.get(0);
				String namePrep = name.toLowerCase().replaceAll(" ", "_");
				ItemID iID = new ItemID(id, null);
				iID.setName(name);

				itemsByID.put(iID, name);
				itemsByName.put(namePrep, iID);

				for(int i = 1; i < names.size(); i++) {
					itemsAliases.put(names.get(i).toLowerCase().replaceAll(" ", "_"), iID);
				}
			}else{
				String name = items.getString("names." + item);
				String namePrep = name.toLowerCase().replaceAll(" ", "_");
				ItemID iID = new ItemID(id, null);
				iID.setName(name);

				itemsByID.put(iID, name);
				itemsByName.put(namePrep, iID);
			}
		}
	}

	public final void loadTypes() {
		Set<String> list = items.getConfigurationSection("types").getKeys(false);
		if(list == null) {
			Util.debug(Level.INFO, "[" + plugin.getName() + "] There are no item types specified in the items.yml file?!");
			return;
		}

		for(String item : list) {
			int itemID;

			try {
				itemID = Integer.valueOf(item.substring(4));
			} catch(NumberFormatException x) {
				Util.debug(Level.WARNING, "[" + plugin.getName() + "] Invalid key detected in items.yml (types." + item + ")");
				continue;
			}

			Set<String> types = items.getConfigurationSection("types." + item).getKeys(false);
			for(String type : types) {
				int typeID;
				try {
					typeID = Integer.valueOf(type.substring(4));
				} catch(NumberFormatException x) {
					Util.debug(Level.WARNING, "[" + plugin.getName() + "] Invalid key detected in items.yml (types." + item + "." + type + ")");
					continue;
				}

				List<String> typeNames = items.getStringList("types." + item + "." + type);
				if(typeNames.size() > 0) {
					ItemID iID = new ItemID(itemID, typeID);
					String name = typeNames.get(0).toLowerCase().replaceAll(" ", "_");
					iID.setName(typeNames.get(0));

					this.itemTypes.put(iID, typeNames);
					this.itemsByID.put(iID, typeNames.get(0));
					this.itemsByName.put(name, iID);

					for(int i = 1; i < typeNames.size(); i++) {
						itemsAliases.put(typeNames.get(i).toLowerCase().replaceAll(" ", "_"), iID);
					}
				}else{
					Util.debug(Level.WARNING, "[" + plugin.getName() + "] Given type does not have any names. (types." + item + "." + type + ")");
				}
			}
		}
	}

	public ArrayList<ItemID> getItemIDsByPart(String part) {
		ArrayList<ItemID> iList = new ArrayList<ItemID>();

		Pattern p = Pattern.compile("^(.*?)" + part + "(.*?)$", Pattern.CASE_INSENSITIVE);

		for(Map.Entry<String, ItemID> e : this.itemsByName.entrySet()) {
			if(p.matcher(e.getKey()).matches()) {
				// Item name matches search pattern
				iList.add(e.getValue());
			}
		}

		return iList;
	}

	public ItemID getItemIDByName(String item) {
		String name = item.toLowerCase().replaceAll(" ", "_");
		if(this.itemsByName.containsKey(name))
			return this.itemsByName.get(name);

		if(this.itemsAliases.containsKey(name))
			return this.itemsAliases.get(name);

		return null;
	}

	public String getItemNameByID(int itemID) {
		return this.getItemNameByID(itemID, null);
	}

	public String getItemNameByID(int itemID, Integer itemType) {
		ItemID key = new ItemID(itemID, itemType);

		for(Map.Entry<ItemID, String> entry : itemsByID.entrySet()) {
			ItemID item = entry.getKey();
			if(item.equals(key))
				return entry.getValue();
		}

		return null;
	}

	public boolean isValidItem(int itemID) {
		return isValidItem(itemID, null);
	}

	public boolean isValidItem(int itemID, Integer itemType) {
		if(itemID < 0 || (itemType != null && itemType < 0))
			return false;

		Material mat = Material.getMaterial(itemID);
		if(mat != null) {
			if(itemType == null)
				return true;

			List<Integer> types = this.getTypeList(itemID);
			if(types.contains(itemType))
				return true;
		}

		return false;
	}

	public Integer getMaxStackSize(int id) {
		return Material.getMaterial(id).getMaxStackSize();
	}
}
