package io.Jerry.Dungeon.Command;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;

import io.Jerry.Dungeon.Party;
import io.Jerry.Dungeon.Games.Game;
import io.Jerry.Dungeon.Util.GameUtil;
import io.Jerry.Dungeon.Util.TitleApi;

public class PartyC implements CommandExecutor {
	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(!(sender instanceof Player)){
			return false;
		}
		
		Player p = (Player)sender;
		if(args.length == 0){
			p.sendMessage("§3/party §7<玩家> §f- 發出組隊要求");
			p.sendMessage("§3/party §7kick§3 <玩家> §f- 踢出隊員");
			p.sendMessage("§3/party §7leave§3 <玩家> §f- 離開隊伍");
			p.sendMessage("§3/party §7owner§3 <玩家> §f- 轉換隊長");
			p.sendMessage("§3/party §7list §f- 查看隊員");
			p.sendMessage("§3/party §7play §f- 進入隊伍副本");
			return true;
		}
		
		Party py = GameUtil.getTeam(p);
		if(args[0].equalsIgnoreCase("leave")){
			if(py == null){
				p.sendMessage("§3Party> §f你沒有加入任何隊伍");
				return true;
			}
			
			if(py.isOwner(p)){
				GameUtil.sendMessage(py.list,"隊伍解散了");
				GameUtil.list.remove(py);
			}else{
				py.list.remove(p.getName());
				GameUtil.sendMessage(py.list,"離開了隊伍");
				p.sendMessage("§3Party> §f你離開了隊伍");
			}
			return true;
		}
		
		if(args.length == 2 && args[0].equalsIgnoreCase("Kick")){
			if(py == null){
				p.sendMessage("§3Party> §f你沒有加入任何隊伍");
				return true;
			}
			
			if(py.isOwner(p) == false){
				p.sendMessage("§3Party> §f你不是隊長");
			}
			
			OfflinePlayer T = Bukkit.getOfflinePlayer(args[1]);
			if(T == null){
				p.sendMessage("§3Party> §f找不到" + args[1] + "玩家");
				return true;
			}
			
			if(T.getName().equals(p.getName())){
				p.sendMessage("§3Party> §f你不能踢出自己");
				return true;
			}
				
			if(py.isPartner(T.getName())){
				py.remove(T.getName());
				GameUtil.sendMessage(py.list,T.getName() + "被踢出",T.getName() + "離開了隊伍");
				if(T.isOnline()){
					T.getPlayer().sendMessage("§6你被踢出了隊伍");
				}
			}else{
				p.sendMessage("§3Party> §f他不是你的隊員");
			}
			return true;
		}
		
		if(args.length == 2 && args[0].equalsIgnoreCase("Owner")){
			if(py == null){
				p.sendMessage("§3Party> §f你沒有加入任何隊伍");
				return true;
			}
			
			if(py.isOwner(p) == false){
				p.sendMessage("§3Party> §f你不是隊長");
			}

			Player T = Bukkit.getPlayer(args[1]);
			if(T == null){
				p.sendMessage("§3Party> §f找不到" + args[1] + "玩家");
				p.playSound(p.getLocation(), "note.pling", 1, 1);
				return true;
			}
			
			if(T.getName().equals(p.getName())){
				p.sendMessage("§3Party> §f你不能更改為自己");
				return true;
			}
				
			if(py.isPartner(T.getName()) == false){
				p.sendMessage("§3Party> §f他不是你的隊員");
				return true;
			}

			if(T.isOnline() == false){
				p.sendMessage("§3Party> §f" + T.getName() + "並不在線");
				return true;
			}
				
			py.Owner = T.getName();
			GameUtil.sendMessage(py.list,py.Owner + "成為隊長");
			return true;
		}
		
		if(args[0].equalsIgnoreCase("List")){
			if(py == null){
				p.sendMessage("§3Party> §f你沒有加入任何隊伍");
				return true;
			}
			
			p.sendMessage("§3" + py.Owner + "的隊伍");
			for(String TT : py.list){
				p.sendMessage("- §7" + TT);
			}
			return true;
		}
		
		if(args[0].equalsIgnoreCase("Play")){

			List<String> list2 = Lists.newArrayList();
			list2.add(p.getName());
			
			Game LastG = null;
			for(Game G : GameUtil.Game){
				if(G.getWorld().equals(p.getWorld())){
					p.sendMessage("§3Party> §f你正在進行副本");
					return true;
				}
			}
			
			
			if(py != null){
				list2.addAll(py.list);
			}
			
			for(Game G : GameUtil.Game){
				for(String T : G.hasPlayed()){
					if(list2.contains(T)){
						LastG = G;
					}
				}

			}
			
			if(LastG == null){
				p.sendMessage("§3Party> §f你的隊伍沒有正在進行的副本");
				return true;
			}
			
			if(LastG.getDiedPlayers().contains(p.getName())){
				p.sendMessage("§3Party> §f你己經死亡,不能再進入副本");
				return true;
			}
			
			if(LastG.getWorld().getPlayers().size() +1 > GameUtil.getMax(LastG.getName())){
				p.sendMessage("§3Party> §f已超過副本人數上限");
				return true;
			}

			p.teleport(GameUtil.getSpawnLoc(LastG.getWorld(), LastG.getName()));

			return true;
		}
		
		Player T = Bukkit.getPlayer(args[0]);
		if(T == null){
			p.sendMessage("§3Party> §f找不到" + args[0] + "玩家");
			return true;
		}
		
		if(T == p){
			p.sendMessage("§3Party> §f你不能邀請自己");
			return true;
		}
		
		Party tpy = GameUtil.getTeam(T);
		if(py != null){//自己已經有隊伍
			if(py.AddList.contains(T.getName())){//開始邀請T
				p.sendMessage("§3Party> §f你已經邀請過他了");
			}else{
				if(tpy != null){//對象沒有隊伍
					p.sendMessage("§3Party> §f他已經有隊伍了");
					return true;
				}
				
				py.AddList.add(T.getName());
				T.playSound(T.getLocation(), "note.pling", 1, 1);
				p.sendMessage("§6已發出邀請");
				T.sendMessage("§6" + p.getName() + "邀請你進入他的隊伍(" + py.Size() + "人)");
				TitleApi.sendAction("{\"text\":\"§6使用/party " + p.getName() + " 或§a按我接受§6\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/party " + p.getName() + "\"}}",T);
			}
		}else{//自己沒有隊伍
			if(tpy != null){//確應邀請
				if(tpy.AddList.contains(p.getName())){//對象已經發出過邀請
					p.sendMessage("§6成功加入");
					tpy.AddList.remove(p.getName());
					tpy.list.add(p.getName());
					GameUtil.sendMessage(tpy.list,p.getName() + "加入了隊伍");
				}else{
					p.sendMessage("§3Party> §f他已經加入了隊伍");
				}
			}else{//對象沒有隊伍
				Party NewPy = new Party();
				NewPy.Owner = p.getName();
				NewPy.list.add(p.getName());
				NewPy.AddList.add(T.getName());
				GameUtil.list.add(NewPy);
				
				p.sendMessage("§6已發出邀請");
				T.playSound(T.getLocation(), "note.pling", 1, 1);
				T.sendMessage("§6" + p.getName() + "邀請你進入他的新隊伍");
				TitleApi.sendAction("{\"text\":\"§6使用/party " + p.getName() + " 或§a按我接受§6\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/party " + p.getName() + "\"}}",T);
			}
		}
		return true;
	}
}
