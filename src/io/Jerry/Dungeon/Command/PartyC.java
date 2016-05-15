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
			p.sendMessage("��3/party ��7<���a> ��f- �o�X�ն��n�D");
			p.sendMessage("��3/party ��7kick��3 <���a> ��f- ��X����");
			p.sendMessage("��3/party ��7leave��3 <���a> ��f- ���}����");
			p.sendMessage("��3/party ��7owner��3 <���a> ��f- �ഫ����");
			p.sendMessage("��3/party ��7list ��f- �d�ݶ���");
			p.sendMessage("��3/party ��7play ��f- �i�J����ƥ�");
			return true;
		}
		
		Party py = GameUtil.getTeam(p);
		if(args[0].equalsIgnoreCase("leave")){
			if(py == null){
				p.sendMessage("��3Party> ��f�A�S���[�J���󶤥�");
				return true;
			}
			
			if(py.isOwner(p)){
				GameUtil.sendMessage(py.list,"����Ѵ��F");
				GameUtil.list.remove(py);
			}else{
				py.list.remove(p.getName());
				GameUtil.sendMessage(py.list,"���}�F����");
				p.sendMessage("��3Party> ��f�A���}�F����");
			}
			return true;
		}
		
		if(args.length == 2 && args[0].equalsIgnoreCase("Kick")){
			if(py == null){
				p.sendMessage("��3Party> ��f�A�S���[�J���󶤥�");
				return true;
			}
			
			if(py.isOwner(p) == false){
				p.sendMessage("��3Party> ��f�A���O����");
			}
			
			OfflinePlayer T = Bukkit.getOfflinePlayer(args[1]);
			if(T == null){
				p.sendMessage("��3Party> ��f�䤣��" + args[1] + "���a");
				return true;
			}
			
			if(T.getName().equals(p.getName())){
				p.sendMessage("��3Party> ��f�A�����X�ۤv");
				return true;
			}
				
			if(py.isPartner(T.getName())){
				py.remove(T.getName());
				GameUtil.sendMessage(py.list,T.getName() + "�Q��X",T.getName() + "���}�F����");
				if(T.isOnline()){
					T.getPlayer().sendMessage("��6�A�Q��X�F����");
				}
			}else{
				p.sendMessage("��3Party> ��f�L���O�A������");
			}
			return true;
		}
		
		if(args.length == 2 && args[0].equalsIgnoreCase("Owner")){
			if(py == null){
				p.sendMessage("��3Party> ��f�A�S���[�J���󶤥�");
				return true;
			}
			
			if(py.isOwner(p) == false){
				p.sendMessage("��3Party> ��f�A���O����");
			}

			Player T = Bukkit.getPlayer(args[1]);
			if(T == null){
				p.sendMessage("��3Party> ��f�䤣��" + args[1] + "���a");
				p.playSound(p.getLocation(), "note.pling", 1, 1);
				return true;
			}
			
			if(T.getName().equals(p.getName())){
				p.sendMessage("��3Party> ��f�A�����אּ�ۤv");
				return true;
			}
				
			if(py.isPartner(T.getName()) == false){
				p.sendMessage("��3Party> ��f�L���O�A������");
				return true;
			}

			if(T.isOnline() == false){
				p.sendMessage("��3Party> ��f" + T.getName() + "�ä��b�u");
				return true;
			}
				
			py.Owner = T.getName();
			GameUtil.sendMessage(py.list,py.Owner + "��������");
			return true;
		}
		
		if(args[0].equalsIgnoreCase("List")){
			if(py == null){
				p.sendMessage("��3Party> ��f�A�S���[�J���󶤥�");
				return true;
			}
			
			p.sendMessage("��3" + py.Owner + "������");
			for(String TT : py.list){
				p.sendMessage("- ��7" + TT);
			}
			return true;
		}
		
		if(args[0].equalsIgnoreCase("Play")){

			List<String> list2 = Lists.newArrayList();
			list2.add(p.getName());
			
			Game LastG = null;
			for(Game G : GameUtil.Game){
				if(G.getWorld().equals(p.getWorld())){
					p.sendMessage("��3Party> ��f�A���b�i��ƥ�");
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
				p.sendMessage("��3Party> ��f�A������S�����b�i�檺�ƥ�");
				return true;
			}
			
			if(LastG.getDiedPlayers().contains(p.getName())){
				p.sendMessage("��3Party> ��f�A�v�g���`,����A�i�J�ƥ�");
				return true;
			}
			
			if(LastG.getWorld().getPlayers().size() +1 > GameUtil.getMax(LastG.getName())){
				p.sendMessage("��3Party> ��f�w�W�L�ƥ��H�ƤW��");
				return true;
			}

			p.teleport(GameUtil.getSpawnLoc(LastG.getWorld(), LastG.getName()));

			return true;
		}
		
		Player T = Bukkit.getPlayer(args[0]);
		if(T == null){
			p.sendMessage("��3Party> ��f�䤣��" + args[0] + "���a");
			return true;
		}
		
		if(T == p){
			p.sendMessage("��3Party> ��f�A�����ܽЦۤv");
			return true;
		}
		
		Party tpy = GameUtil.getTeam(T);
		if(py != null){//�ۤv�w�g������
			if(py.AddList.contains(T.getName())){//�}�l�ܽ�T
				p.sendMessage("��3Party> ��f�A�w�g�ܽйL�L�F");
			}else{
				if(tpy != null){//��H�S������
					p.sendMessage("��3Party> ��f�L�w�g������F");
					return true;
				}
				
				py.AddList.add(T.getName());
				T.playSound(T.getLocation(), "note.pling", 1, 1);
				p.sendMessage("��6�w�o�X�ܽ�");
				T.sendMessage("��6" + p.getName() + "�ܽЧA�i�J�L������(" + py.Size() + "�H)");
				TitleApi.sendAction("{\"text\":\"��6�ϥ�/party " + p.getName() + " �Ρ�a���ڱ�����6\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/party " + p.getName() + "\"}}",T);
			}
		}else{//�ۤv�S������
			if(tpy != null){//�T���ܽ�
				if(tpy.AddList.contains(p.getName())){//��H�w�g�o�X�L�ܽ�
					p.sendMessage("��6���\�[�J");
					tpy.AddList.remove(p.getName());
					tpy.list.add(p.getName());
					GameUtil.sendMessage(tpy.list,p.getName() + "�[�J�F����");
				}else{
					p.sendMessage("��3Party> ��f�L�w�g�[�J�F����");
				}
			}else{//��H�S������
				Party NewPy = new Party();
				NewPy.Owner = p.getName();
				NewPy.list.add(p.getName());
				NewPy.AddList.add(T.getName());
				GameUtil.list.add(NewPy);
				
				p.sendMessage("��6�w�o�X�ܽ�");
				T.playSound(T.getLocation(), "note.pling", 1, 1);
				T.sendMessage("��6" + p.getName() + "�ܽЧA�i�J�L���s����");
				TitleApi.sendAction("{\"text\":\"��6�ϥ�/party " + p.getName() + " �Ρ�a���ڱ�����6\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/party " + p.getName() + "\"}}",T);
			}
		}
		return true;
	}
}
