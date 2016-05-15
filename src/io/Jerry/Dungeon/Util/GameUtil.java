package io.Jerry.Dungeon.Util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.Jerry.Dungeon.Main;
import io.Jerry.Dungeon.Party;
import io.Jerry.Dungeon.Games.Game;

public class GameUtil {
	public static List<Game> Game = new ArrayList<Game>();
	public static List<Party> list = new ArrayList<Party>();
	
	@SuppressWarnings("deprecation")
	public static void sendMessage(List<String> list, String... msg) {
		OfflinePlayer T;
		Player p;
		for(String str : list){
			T = Bukkit.getOfflinePlayer(str);
			p = T.getPlayer();
			for(String msg2 : msg){
				p.sendMessage("¡±3Party> ¡±f" + msg2);
			}
			p.playSound(p.getLocation(), "note.pling", 1, 1);
		}
	}
	
	public static Party getTeam(Player p){
		for(Party py : list){
			if(py.list.contains(p)){
				return py;
			}
		}
		return null;
	}
	
	public static boolean isInLocation(Location L, String Name){
		if(Main.c.get("Game." + Name + ".In") != null){
			if(!(L.getWorld().getName().equals(Main.c.getString("Game." + Name + ".In.W")) && 
				L.getBlockX() == Main.c.getInt("Game." + Name + ".In.X") &&
				L.getBlockY() == Main.c.getInt("Game." + Name + ".In.Y") && 
				L.getBlockZ() == Main.c.getInt("Game." + Name + ".In.Z"))){

				return false;
			}
		}
		return true;
	}
	
	public static boolean hasGame(String Name){
		return Main.c.get("Game." +  Name) != null;
	}
	
	public static Location getSpawnLoc(World w, String Name){
		if(Main.c.get("Game." + Name + ".X") == null){
			return w.getSpawnLocation();
		}else{
		    return new Location(w,Main.c.getInt("Game." + Name + ".X"),Main.c.getInt("Game." + Name + ".Y"),Main.c.getInt("Game." + Name + ".Z"));
        }
	}
	
	public static Location getExitLoc(String Name){
		return new Location(
				Bukkit.getWorld(Main.c.getString("Game." + Name + ".Exit.W")),
				Main.c.getInt("Game." + Name + ".Exit.X"),
				Main.c.getInt("Game." + Name + ".Exit.Y"),
				Main.c.getInt("Game." + Name + ".Exit.Z"));
	}
	
	public static ItemStack getItem(String name){
		return Main.c.getItemStack("Game." + name + ".Item");
	}
	
	public static int getMix(String Name){
		return Main.c.getInt("Game." + Name + ".Mix",0);
	}
	
	public static int getMax(String Name){
		return Main.c.getInt("Game." + Name + ".Max",0);
	}
	
	public static int getTime(String Name){
		return Main.c.getInt("Game." + Name + ".Time",20);
	}
	
	public static List<String> getEntity(String Name){
		return Main.c.getStringList("Game." + Name + ".Entity");
	}


	
}
