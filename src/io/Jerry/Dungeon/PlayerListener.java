package io.Jerry.Dungeon;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Lists;

import io.Jerry.Dungeon.Games.Game;
import io.Jerry.Dungeon.Games.GameError;
import io.Jerry.Dungeon.Util.GameUtil;
import io.Jerry.Dungeon.Util.TitleApi;

public class PlayerListener implements Listener {	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void OpenBook(PlayerInteractEvent e) {
		if(e.getPlayer().getItemInHand() == null){
			return;
		}
		
		Player p = e.getPlayer();
		if(Main.c.isConfigurationSection("Game")){
			ItemStack i2 = p.getItemInHand();
			ItemStack i;
			
			for(String Name : Main.c.getConfigurationSection("Game").getKeys(false)){
				i = GameUtil.getItem(Name);
				if(i2.equals(i)){
					double startTime = System.currentTimeMillis();
						
					List<String> list2 = Lists.newArrayList();
					Party py = GameUtil.getTeam(p);
					if(py != null){
						list2.addAll(py.list);
					}
					list2.add(p.getName());
					
					Game LastG = null;
					for(Game G : GameUtil.Game){
						for(String T : G.hasPlayed()){
							if(list2.contains(T)){
								LastG = G;
							}
						}

					}
					
					if(LastG != null){
						TitleApi.sendAction("{\"text\":\"§3Dun> §f本隊伍正在進行副本,按\","
								+ "\"extra\":["
								+ "{\"text\":\"§a我\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/party play\"}},"
								+ "{\"text\":\"§f加入\"}"
								+ "]}",p);
						return;
					}
					
					p.setItemInHand(null);
					
					try {
						Game G = new Game(Name,p);
						G.onGameStart(false, list2);
									
						GameUtil.Game.add(G);
						double endTime = System.currentTimeMillis();
						double totalTime = endTime - startTime;
						totalTime = totalTime/1000;
						p.sendMessage("§3Dun> §f使用" + totalTime + "秒創建副本地圖");
					} catch (GameError ex) {
						p.sendMessage("§3Dun> §c" + ex.getMessage());
						p.setItemInHand(i2);
					}
					break;
				}
			}
			
		}
	}
	
	@EventHandler
	public void Tp(PlayerTeleportEvent e){

		Player p = e.getPlayer();
		for(Game G : GameUtil.Game){
				if(G.getWorld().equals(e.getTo().getWorld()) && G.getWorld().equals(e.getFrom().getWorld()) == false){
					for(Player T : G.getWorld().getPlayers()){
						T.sendMessage("§3Party> §f" + p.getName() + "加入副本");
					}
					if(G.hasPlayed().contains(p.getName()) == false){
						G.hasPlayed().add(p.getName());	
					}
				}else if(G.getWorld().equals(e.getFrom().getWorld()) && G.getWorld().equals(e.getTo().getWorld()) == false){
					for(Player T : G.getWorld().getPlayers()){
						T.sendMessage("§3Party> §f" + p.getName() + "退出副本");
					}
				}			
		}
	}
	
	@EventHandler
	public void Spawn(CreatureSpawnEvent e){
		if(e.getSpawnReason() == SpawnReason.NATURAL){
			World W = e.getLocation().getWorld();
			for(Game G : GameUtil.Game){
				if(G.getWorld().equals(W)){
					e.setCancelled(true);
					return;
				}
			}
		}
	}
	
//	@EventHandler
//	public void UseCommand(PlayerCommandPreprocessEvent e){
//		Player p = e.getPlayer();
//		if(p.isOp() == false){
//			for(Game G : GameUtil.Game){
//				if(G.getPlayers().contains(p.getName())){
//					if((e.getMessage().startsWith("/game") || e.getMessage().startsWith("/party")) == false){
//						e.setCancelled(true);
//						p.sendMessage("§c玩家進行副本時指令一律禁止,若要離開副本請輸入/party leave");
//					}
//					return;
//				}
//			}
//		}
//	}
	
	@EventHandler
	public void Quit(PlayerQuitEvent e){
		Player p = e.getPlayer();
		for(Game G : GameUtil.Game){
			if(G.getWorld().equals(p.getWorld())){
				if(Main.c.get("Game." + G.getName() + ".Exit") == null){//TODO
					p.performCommand("spawn");
				}else{
				   	p.teleport(GameUtil.getExitLoc(G.getName()));
				}
			}
			
		}
	}
	
	@EventHandler
	public void Join(PlayerJoinEvent e){
		Player p = e.getPlayer();
	
		List<String> list2 = Lists.newArrayList();
		Party py = GameUtil.getTeam(p);
		if(py != null){
			list2.addAll(py.list);
		}
		list2.add(p.getName());
		
		Game LastG = null;
		for(Game G : GameUtil.Game){
			for(String T : G.hasPlayed()){
				if(list2.contains(T)){
					LastG = G;
				}
			}

		}
		
		if(LastG != null){
			TitleApi.sendAction("{\"text\":\"§3Dun> §f本隊伍正在進行副本,按\","
					+ "\"extra\":["
					+ "{\"text\":\"§a我\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/party play\"}},"
					+ "{\"text\":\"§f加入\"}"
					+ "]}",p);
			return;
		}
	}
	
	@EventHandler
	public void Respawn(PlayerDeathEvent e){
		Player p = e.getEntity();
		for(Game G : GameUtil.Game){
			if(G.getWorld().equals(p.getWorld()) ){
				p.setHealth(p.getMaxHealth());
				if(Main.c.getBoolean("Game." + G.getName() + ".Respawn", true) ){//TODO
					ItemStack[] Items = p.getInventory().getContents();
					Location L = GameUtil.getSpawnLoc(G.getWorld(), G.getName());
					p.teleport(L);
					p.getInventory().setContents(Items);
					p.teleport(L);
				}else{
					p.sendMessage("§3你退出了副本");
					
					ItemStack[] Items = p.getInventory().getContents();
					if(Main.c.get("Game." + G.getName() + ".Exit") == null){//TODO
						p.performCommand("/spawn");
						p.getInventory().setContents(Items);
						p.performCommand("/spawn");					
					}else{
						Location L = GameUtil.getExitLoc(G.getName());
						p.teleport(L);
						p.getInventory().setContents(Items);
						p.teleport(L);
					}
					
					G.getDiedPlayers().add(p.getName());
				}
			}
			
		}
	}
	
}
