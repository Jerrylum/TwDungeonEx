package io.Jerry.Dungeon.Games;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import io.Jerry.Dungeon.Aerry;
import io.Jerry.Dungeon.Main;
import io.Jerry.Dungeon.Util.FileUtil;
import io.Jerry.Dungeon.Util.GameUtil;
import io.Jerry.Dungeon.Util.TitleApi;
import net.elseland.xikage.MythicMobs.API.Mobs;

@SuppressWarnings("deprecation")
public class Game{
	private String Name;
	private File Path;
	private List<String> Died;
	private List<String> Played;
	private World world;
	private int Task;
	
	public Game(String Name,Player Owner) throws GameError{
		if(Main.c.get("Game." + Name) == null){//TODO
			throw new GameError("§c沒有此副本");
		}
		
		this.Name = Name;
		this.Path = new File("plugins/" + Main.Name + "/" + Name);

		if(Owner != null){
			if(Main.c.get("Game." + Name + ".X") == null){//TODO
				throw new GameError("§c副本不能進入");
			}
			
			if(GameUtil.isInLocation(Owner.getLocation(), Name) == false){
				throw new GameError("§c你不在指定位置傳送");
			}
		}

	}
	
	public List<String> getDiedPlayers(){
		return Died;
	}
	
	public List<String> hasPlayed() {
		return Played;
	}
	
	public String getName(){
		return Name;
	}
	
	public World getWorld(){
		return world;
	}
	
	public void onGameStart(boolean Pass, List<String> list_player) throws GameError{
		Died = new ArrayList<String>();
		Played = new ArrayList<String>();
		OfflinePlayer T;
		List<Player> players = new ArrayList<Player>();
		for(String str : list_player){
			T = Bukkit.getOfflinePlayer(str);
			if(T.isOnline()){
				players .add(T.getPlayer());
			}
		}
		
		if(Pass == false){
			
			int mix = GameUtil.getMix(Name);
			int max = GameUtil.getMax(Name);
			
			if(mix > players.size()){
				throw new GameError("§c進入人數需大於" + (mix-1) );
			}
					
			if(max != 0 && max < players.size()){
				throw new GameError("§c進入人數需少於" + (max +1) );
			}
		}

		String worldname = "副本地圖 " + Name + " ID" + new Random().nextInt();
		File WorldFile = new File(worldname);
		try {
			if(WorldFile.exists() == false){
				FileUtil.copyFolder(this.Path,WorldFile);
			}
			WorldCreator creator = WorldCreator.name(worldname);
		    World world = creator.createWorld();
			if(world == null){
			   	throw new GameError("創建失畋 - null");
			}
		    this.world = world;
		} catch (IOException e) {
			throw new GameError("創建失畋");
		}

		List<String> list = GameUtil.getEntity(Name);
		if(list != null && list.isEmpty() == false){
			Aerry A;
			for(String str : list){
				A = new Aerry(str,this.world);
				Mobs.spawnMythicMob(A.getMobName(), A.getLocation());
			}
		}
		
		Location L = GameUtil.getSpawnLoc(world, Name);
		
		for(Player p : players){
			Played.add(p.getName());
		    ItemStack[] Items = p.getInventory().getContents();
		    p.getPlayer().teleport(L);
		    p.getPlayer().getInventory().setContents(Items);
		    p.getPlayer().teleport(L);
		}
		
		if(GameUtil.getTime(Name) != 0 && Pass == false){
			this.Task = new BukkitRunnable(){
				int Time = GameUtil.getTime(Name);
				
		    	public void run(){
		    		if(Time <= 0){
			    		for(Player T : world.getPlayers()){
			    			T.sendMessage("§3Dun> §f時限已到");
			    		}
			    		onGameEnd();
		    		}else{
		    			Time --;
		    			for(Player T : world.getPlayers()){
			    			TitleApi.sendBar( "副本時間剩下" + Time +"秒時間",T);
			    		}
		    		}
		    	}
		   }.runTaskTimer(Main.PL, 0, 20).getTaskId();
		}

	}
	
	public void onGameEnd(){
		if(this.world != null){
			Location L = GameUtil.getExitLoc(Name);
			
			if(L == null){
				for(Player T : this.world.getPlayers()){
					if(T.getWorld().equals(getWorld())){
						T.performCommand("spawn");
					}
				}
			}else{
				for(Player T : this.world.getPlayers()){
					if(T.getWorld().equals(getWorld())){
		    			T.teleport(L);
					}
				}
			}
			Main.PL.getServer().unloadWorld(world, true);
		}
		Main.PL.getServer().getScheduler().cancelTask(this.Task);
		GameUtil.Game.remove(this);

	}
	
}
