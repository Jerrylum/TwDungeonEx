package io.Jerry.Dungeon.Command;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Lists;

import io.Jerry.Dungeon.Aerry;
import io.Jerry.Dungeon.Main;
import io.Jerry.Dungeon.Games.Game;
import io.Jerry.Dungeon.Games.GameError;
import io.Jerry.Dungeon.Util.GameUtil;
import io.Jerry.Dungeon.Util.ItemsUtil;
import io.Jerry.Dungeon.Util.TitleApi;

public class CMD implements CommandExecutor {
	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if((sender.isOp() && sender instanceof Player) == false){
			return false;
		}

		Player p = (Player)sender;
		//----------------------------------------------------------------------------------------------
		//DONE
		if(args.length == 2 && args[0].equalsIgnoreCase("Goto")){
			for(Game G : GameUtil.Game){
				if(G.getWorld().getPlayers().contains(p)){
					p.sendMessage("§3Dun> §f你正在使用其他副本");
					return true;
				}
			}
			
			double startTime = System.currentTimeMillis();
			
			try {
				Game G = new Game(args[1],null);
				G.onGameStart(true, Arrays.asList(new String[]{p.getName()}));
				GameUtil.Game.add(G);
			} catch (GameError e) {
				p.sendMessage("§3Dun> §f" + e.getMessage());
			}
			
			double endTime   = System.currentTimeMillis();
			double totalTime = endTime - startTime;
			totalTime = totalTime/1000;
			p.sendMessage("§3Dun> §f使用" + totalTime + "秒嘗試創建");
			return true;
		}
		//----------------------------------------------------------------------------------------------

		if(args.length == 1 && args[0].equalsIgnoreCase("End")){
			boolean use = false;
			for(Game G : GameUtil.Game){
				if(G.getWorld().getPlayers().contains(p)){
					double startTime = System.currentTimeMillis();
					
					G.onGameEnd();
					GameUtil.Game.remove(G);
					
					double endTime   = System.currentTimeMillis();
					double totalTime = endTime - startTime;
					totalTime = totalTime/1000;
					p.sendMessage("§3Dun> §f使用" + totalTime + "秒刪除");
					
					use = true;
					break;
				}
			}
			if(use == false){
				p.sendMessage("§3Dun> §f你不在任何副本內");
			}
			return true;
		}
		//----------------------------------------------------------------------------------------------

		if(args.length == 2 && args[0].equalsIgnoreCase("addMob")){
			for(Game G : GameUtil.Game){
				if(G.getWorld().getPlayers().contains(p)){
					Location L = p.getLocation();
					String str = L.getBlockX() + "," + L.getBlockY() + "," + L.getBlockZ() + "," + args[1];
					List<String> list = GameUtil.getEntity(G.getName());
					list = list == null ? Lists.newArrayList() : list;
					list.add(str);
					Main.c.set("Game." + G.getName() + ".Entity", list);//TODO
					Main.PL.saveConfig();
					
					p.sendMessage("§3Dun> §f已新增怪物");
					return true;
				}
			}
			p.sendMessage("§3Dun> §f你不在任何副本內");
			return true;
		}
		//----------------------------------------------------------------------------------------------

		if(args.length == 2 && args[0].equalsIgnoreCase("delMob")){
			for(Game G : GameUtil.Game){
				if(G.getWorld().getPlayers().contains(p)){
					Location L = p.getLocation();
					String str = L.getBlockX() + "," + L.getBlockY() + "," + L.getBlockZ() + "," + args[1];
					List<String> list = GameUtil.getEntity(G.getName());
					list = list == null ? Lists.newArrayList() : list;
					list.remove(str);
					Main.c.set("Game." + G.getName() + ".Entity", list);//TODO
					Main.PL.saveConfig();
					
					p.sendMessage("§3Dun> §f已移除怪物");
					return true;
				}
			}
			p.sendMessage("§3Dun> §f你不在任何副本內");
			return true;
		}
		//----------------------------------------------------------------------------------------------

		if(args.length == 1 && args[0].equalsIgnoreCase("listMob")){
			for(Game G : GameUtil.Game){
				if(G.getWorld().getPlayers().contains(p)){
					List<String> list = GameUtil.getEntity(G.getName());
					if(list == null || list.isEmpty()){
						p.sendMessage("§3Dun> §f沒有生成怪物");
						return true;
					}
					
					Aerry A;
					Location L;
					for(String str : list){
						A = new Aerry(str,p.getWorld());
						L = A.getLocation();
						p.sendMessage(" - " + L.getBlockX() + "," + L.getBlockY() + "," + L.getBlockZ() + " | " + A.getMobName());
					}
					return true;
				}
			}
			p.sendMessage("§3Dun> §f你不在任何副本內");
			return true;
		}
		//----------------------------------------------------------------------------------------------

		if(args.length == 1 && args[0].equalsIgnoreCase("Spawn")){
			for(Game G : GameUtil.Game){
				if(G.getWorld().getPlayers().contains(p)){
					if(Main.c.get("Game." + G.getName() + ".X") == null){//TODO
						p.sendMessage("§3Dun> §f副本出生點沒有設定");
					}else{
						p.teleport(GameUtil.getSpawnLoc(G.getWorld(), G.getName()));
					}
					return true;
				}
			}
			p.sendMessage("§3Dun> §f你不在任何副本內");
			return true;
		}
		//----------------------------------------------------------------------------------------------

		if(args.length == 1 && args[0].equalsIgnoreCase("SetSpawn")){
			boolean use = false;
			for(Game G : GameUtil.Game){
				if(G.getWorld().getPlayers().contains(p)){
					Location L = p.getLocation();//TODO
					Main.c.set("Game." + G.getName() + ".X",L.getBlockX());
					Main.c.set("Game." + G.getName() + ".Y",L.getBlockY());
					Main.c.set("Game." + G.getName() + ".Z",L.getBlockZ());
					Main.PL.saveConfig();
					p.sendMessage("§3Dun> §f已設定出生點");
					use = true;
					break;
				}
			}
			if(use == false){
				p.sendMessage("§3Dun> §f你不在任何副本內");
			}
			return true;
		}
		//----------------------------------------------------------------------------------------------
		//DONE
		if(args.length >= 2 && args[0].equalsIgnoreCase("SetExit")){
			if(GameUtil.hasGame(args[1]) ==  false){
				p.sendMessage("§3Dun> §f沒有此副本");
				return true;
			}
			
			if(args.length == 2){
				Location L = p.getLocation();//TODO
				Main.c.set("Game." + args[1] + ".Exit.W",L.getWorld().getName());
				Main.c.set("Game." + args[1] + ".Exit.X",L.getBlockX());
				Main.c.set("Game." + args[1] + ".Exit.Y",L.getBlockY());
				Main.c.set("Game." + args[1] + ".Exit.Z",L.getBlockZ());
				Main.PL.saveConfig();
				p.sendMessage("§3Dun> §f已設定離開位置");
				return true;
			}else if(args.length == 3 && args[2].equalsIgnoreCase("null")){
				Main.c.set("Game." + args[1] + ".Exit",null);
				p.sendMessage("§3Dun> §f已設定離開位置為Spawn點");
				
				Main.PL.saveConfig();
				return true;
			}
			
		}
		//----------------------------------------------------------------------------------------------
		//DONE
		if(args.length >= 2 && args[0].equalsIgnoreCase("SetIn")){
			if(GameUtil.hasGame(args[1]) ==  false){
				p.sendMessage("§3Dun> §f沒有此副本");
				return true;
			}
			
			if(args.length == 2){
				Location L = p.getLocation();//TODO
				Main.c.set("Game." + args[1] + ".In.W",L.getWorld().getName());
				Main.c.set("Game." + args[1] + ".In.X",L.getBlockX());
				Main.c.set("Game." + args[1] + ".In.Y",L.getBlockY());
				Main.c.set("Game." + args[1] + ".In.Z",L.getBlockZ());
				p.sendMessage("§3Dun> §f已設定進入位置");
				
				Main.PL.saveConfig();
				return true;
			}else if(args.length == 3 && args[2].equalsIgnoreCase("null")){
				Main.c.set("Game." + args[1] + ".In",null);
				p.sendMessage("§3Dun> §f已設定任何位置都能進入");
				
				Main.PL.saveConfig();
				return true;
			}
			
			
		}
		//----------------------------------------------------------------------------------------------
		//DONE
		if(args.length == 2 && args[0].equalsIgnoreCase("SetItem")){
			if(p.getItemInHand() == null || p.getItemInHand().getType() == Material.AIR){
				p.sendMessage("§3Dun> §f手上應拿著物品");
				return true;
			}
			
			if(GameUtil.hasGame(args[1]) ==  false){
				p.sendMessage("§3Dun> §f沒有此副本");
				return true;
			}
			
			Main.c.set("Game." + args[1] + ".Item",p.getItemInHand());//TODO
			Main.PL.saveConfig();
			p.sendMessage("§3Dun> §f已設定副本傳送書為" + ItemsUtil.GetName(p.getItemInHand()));
			return true;
		}
		//----------------------------------------------------------------------------------------------
		//DONE
		if(args.length == 2 && args[0].equalsIgnoreCase("getItem")){
			if(GameUtil.hasGame(args[1]) ==  false){
				p.sendMessage("§3Dun> §f沒有此副本");
				return true;
			}
			
			ItemStack i = GameUtil.getItem(args[1]);
			if(i == null){
				p.sendMessage("§3Dun> §f沒有設定副本傳送書");
			}else{
				p.getInventory().addItem(i);
			}
			return true;
		}
		
		//----------------------------------------------------------------------------------------------
		if(args.length == 2 && args[0].equalsIgnoreCase("Create")){
			if(GameUtil.hasGame(args[1])){
				p.sendMessage("§3Dun> §f已經有此副本");
				return true;
			}
			
			Main.c.set("Game." + args[1] + ".UUID",UUID.randomUUID().toString());
			Main.PL.saveConfig();
			p.sendMessage("§3Dun> §f成功增設");
			return true;
		}
		
		
		if(args.length == 2 && args[0].equalsIgnoreCase("Delete")){
			if(GameUtil.hasGame(args[1]) ==  false){
				p.sendMessage("§3Dun> §f沒有此副本");
				return true;
			}
			
			Main.c.set("Game." + args[1] ,null);//TODO
			Main.PL.saveConfig();
			p.sendMessage("§3Dun> §f已刪除副本設定檔");
			return true;
		}
		
		if(args.length == 4 && args[0].equalsIgnoreCase("Limit")){
			if(GameUtil.hasGame(args[1]) ==  false){
				p.sendMessage("§3Dun> §f沒有此副本");
				return true;
			}
			
			try{
				Main.c.set("Game." + args[1] + ".Mix",Integer.parseInt(args[2]));//TODO
				Main.c.set("Game." + args[1] + ".Max",Integer.parseInt(args[3]));
				Main.PL.saveConfig();
				p.sendMessage("§3Dun> §f已更改副本設定檔");
			}catch(Exception ex){
				p.sendMessage("§3Dun> §f設定錯誤");
			}
			return true;
		}
		
		if(args.length == 3 && args[0].equalsIgnoreCase("Time")){
			if(GameUtil.hasGame(args[1]) ==  false){
				p.sendMessage("§3Dun> §f沒有此副本");
				return true;
			}
			
			try{
				Main.c.set("Game." + args[1] + ".Time",Integer.parseInt(args[2]));//TODO
				Main.PL.saveConfig();
				p.sendMessage("§3Dun> §f已更改副本設定檔");
			}catch(Exception ex){
				p.sendMessage("§3Dun> §f設定錯誤");
			}
			return true;
		}
		
		if(args.length == 3 && args[0].equalsIgnoreCase("Respawn")){
			if(GameUtil.hasGame(args[1]) ==  false){
				p.sendMessage("§3Dun> §f沒有此副本");
				return true;
			}
			
			try{
				Main.c.set("Game." + args[1] + ".Respawn",Boolean.valueOf(args[2]));//TODO
				Main.PL.saveConfig();
				p.sendMessage("§3Dun> §f已更改副本設定檔");
			}catch(Exception ex){
				p.sendMessage("§3Dun> §f設定錯誤");
			}
			return true;
		}
		
		if(args.length == 1 && args[0].equalsIgnoreCase("2")){
			p.sendMessage("§3Dun> §f副本指令(對你正預覽的副本有效)");
			help(p,helplist2);
			p.sendMessage("§6請打/Dun <頁數> 查詢");
			return true;
		}
		
		if(args.length == 1 && args[0].equalsIgnoreCase("3")){
			p.sendMessage("§3Dun> §f設定檔指令");
			help(p,helplist3);
			p.sendMessage("§6請打/Dun <頁數> 查詢");

			return true;
		}
		p.sendMessage("§3Dun> §f基本指令");
		help(p,helplist1);
		p.sendMessage("請打/Dun <頁數> 查詢");
		return true;
	}
	public static String[] helplist1 = {
			"/Dun §7setExit <副本>","/Dun setExit <副本>",
				"設定副本結束後傳送到你腳下的地方\n"
				+ "使用/Dun setExit <副本> null\n"
				+ "則設定副本結束後傳送到Spawn點",
			"/Dun §7setIn <副本>","/Dun setIn <副本>",
				"設定需在哪裹按下傳送書啟動副本\n"
				+ "使用/Dun setIn <副本> null\n"
				+ "則設定任何位置都能啟動副本",
			"/Dun §7getItem <副本>","/Dun getItem <副本>","取得副本傳送書",
			"/Dun §7setItem <副本>","/Dun setItem <副本>","設定副本傳送書",
			"/Dun §7Goto <副本>","/Dun Goto <副本>","進入預覽副本"};

	public static String[] helplist2 = {
			"/Dun §7setspawn","/Dun setspawn","設定出生點",
			"/Dun §7spawn","/Dun spawn","進入出生點",
			"/Dun §7addMob <怪物名稱>","/Dun addMob <怪物名稱>","在你的位置新增怪物",
			"/Dun §7delMob <怪物名稱>","/Dun delMob <怪物名稱>","移除在你的位置生成的怪物",
			"/Dun §7listMob","/Dun listMob","查看生成的怪物",
			"/Dun §7End","/Dun End","結束副本"};
	
	public static String[] helplist3 = {
			"/Dun §7Create <副本>","/Dun Create <副本>","創建副本設定檔",
			"/Dun §7Delete <副本>","/Dun Delete <副本>","刪除副本設定檔",
			"/Dun §7limit <副本> <最小> <最多>","/Dun limit <副本> <最小> <最多>","設定副本人數限制",
			"/Dun §7time <副本> <秒>","/Dun time <副本> <秒>","設定副本時限",
			"/Dun §7Respawn <副本> <True|False>","/Dun Respawn <副本> <True|False>","是否重生在副本內"};
	
		
	public void help(Player p, String[] list){
		for(int i = 0; i + 2 < list.length; i = i +3){
			TitleApi.sendAction("{\"text\":\"" + list[i] + "\","
					+ "\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"" + list[i+1] + "\"},"
					+ "\"hoverEvent\":{\"action\":\"show_text\",\"value\":\""
						+ list[i+2]
						+ "\n點擊以查看指令"
					+ "\"}}", p);
		}
	}
}
