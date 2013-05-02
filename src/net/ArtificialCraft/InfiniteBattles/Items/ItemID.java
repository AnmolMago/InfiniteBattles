package net.ArtificialCraft.InfiniteBattles.Items;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

/**
 * @author Giant
 */
public class ItemID{

	private int id, type;
	private String name;
	private boolean hasType = false;

	public ItemID(){
		this(0, null);
	}

	public ItemID(int id){
		this(id, null);
	}

	public ItemID(int id, Integer type){
		this(id, type, null);
	}

	public ItemID(int id, Integer type, String name){
		this.id = id;
		if(type == null){
			this.type = 0;
			this.hasType = false;
		}else{
			this.type = type;
			this.hasType = true;
		}

		this.name = name;
	}

	public int getId(){
		return id;
	}

	public Integer getType(){
		if(hasType)
			return type;
		return null;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return this.name;
	}

	public Material getMaterial(){
		return Material.getMaterial(id);
	}

	public ItemStack getItemStack(){
		return this.getItemStack(1);
	}

	public ItemStack getItemStack(int amount){
		ItemStack iStack;

		if(type != 0){
			if(id != 373)
				iStack = new MaterialData(id, (byte)((int)type)).toItemStack(amount);
			else
				iStack = new ItemStack(id, amount, (short)((int)type));
		}else{
			iStack = new ItemStack(id, amount);
		}

		return iStack;
	}

	@Override
	public String toString(){
		return "Item ID: " + this.id + " Item type: " + this.type;
	}

	public boolean equals(ItemID key){
		Integer type = (key.getType() == null) ? 0 : key.getType();
		return (key.getId() == this.id && type == this.type);
	}
}
