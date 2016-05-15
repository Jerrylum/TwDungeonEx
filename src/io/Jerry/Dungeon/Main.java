package io.Jerry.Dungeon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import com.google.common.collect.Lists;

import io.Jerry.Dungeon.Command.CMD;
import io.Jerry.Dungeon.Command.PartyC;
import io.Jerry.Dungeon.Games.Game;
import io.Jerry.Dungeon.Util.GameUtil;
import io.Jerry.Dungeon.Util.Metrics;

public class Main extends JavaPlugin{
	public static String Name;
	public static Plugin PL;
	public static FileConfiguration c;

	
	public void onEnable(){
		PL = this;
		Name = this.getName();
		c = getConfig();
		c.options().copyDefaults(true);
		saveConfig();
		getCommand("Dun").setExecutor(new CMD());
		getCommand("Party").setExecutor(new PartyC());
		getServer().getPluginManager().registerEvents(new PlayerListener(), this);
		if(c.getBoolean("ScoreBoard",true)){
			run();
		}
		
		try {
			new Metrics(this).start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		getLogger().info("初始化完成...");
	}
	
	public void onDisable(){
		Location L;
		
		for(Game G : GameUtil.Game){
			L = GameUtil.getExitLoc(G.getName());
			for(Player p : G.getWorld().getPlayers()){
				p.teleport(L);
			}
		}
	}
	
	public static List<Player> getOnlinePlayers() {
	    List<Player> list = Lists.newArrayList();
	    for (World world : Bukkit.getWorlds()) {
	        list.addAll(world.getPlayers());
	    }
	    return Collections.unmodifiableList(list);
	}
	
	public void run(){
		new BukkitRunnable(){
			
			@Override
			public void run() {
					
				for(Player send : getOnlinePlayers()){
					Party u = GameUtil.getTeam(send);
					if(u != null){
						List<String> list = new ArrayList<String>();
						String Title = "§3§l隊伍";
						list.add("§3隊長:" + u.Owner);
						list.add("§3隊員:");
						for(String Name : u.list){
							list.add("§7" + Name);
						}
					
						Scoreboard s = Bukkit.getScoreboardManager().getNewScoreboard();
						Objective objective = s.registerNewObjective(Title, "dummy");
		
						objective.setDisplaySlot(DisplaySlot.SIDEBAR);
						for(int i = 0; i < list.size(); i ++){
							objective.getScore(list.get(i)).setScore(list.size() - i);
						}
						send.setScoreboard(s);
					}else{
						Scoreboard s = Bukkit.getScoreboardManager().getNewScoreboard();
						send.setScoreboard(s);
					}
				}
			}
		}.runTaskTimer(this, 0,10);
	}
}
