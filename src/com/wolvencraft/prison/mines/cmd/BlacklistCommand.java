/*
 * BlacklistCommand.java
 * 
 * PrisonMine
 * Copyright (C) 2013 bitWolfy <http://www.wolvencraft.com> and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
*/

package com.wolvencraft.prison.mines.cmd;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.material.MaterialData;

import com.wolvencraft.prison.mines.PrisonMine;
import com.wolvencraft.prison.mines.mine.Mine;
import com.wolvencraft.prison.mines.settings.Language;
import com.wolvencraft.prison.mines.util.Message;
import com.wolvencraft.prison.mines.util.Util;
import com.wolvencraft.prison.mines.util.constants.BlacklistState;

public class BlacklistCommand implements BaseCommand {
    
    @Override
    public boolean run(String[] args) {

        Language language = PrisonMine.getLanguage();
        
        Mine curMine = PrisonMine.getCurMine();
        if(curMine == null && !(args.length == 2 && args[1].equalsIgnoreCase("help"))) { Message.sendFormattedError(PrisonMine.getLanguage().ERROR_MINENOTSELECTED); return false; }
        
        if(args.length == 1) {
            if(args[0].equalsIgnoreCase("blacklist")) {                
                if(curMine.getBlacklist().getState().equals(BlacklistState.BLACKLIST)) {
                    curMine.getBlacklist().setState(BlacklistState.DISABLED);
                    Message.sendFormattedSuccess("The replacement rules are now disabled");
                } else {
                    curMine.getBlacklist().setState(BlacklistState.BLACKLIST);
                    Message.sendFormattedSuccess("The replacement rules are now in blacklist mode");
                }
            } else if(args[0].equalsIgnoreCase("whitelist")) {                
                if(curMine.getBlacklist().getState().equals(BlacklistState.WHITELIST)) {
                    curMine.getBlacklist().setState(BlacklistState.DISABLED);
                    Message.sendFormattedSuccess("The replacement rules are now disabled");
                } else {
                    curMine.getBlacklist().setState(BlacklistState.WHITELIST);
                    Message.sendFormattedSuccess("The replacement rules are now in whitelist mode");
                }
            } else { Message.sendFormattedError(language.ERROR_COMMAND); return false; }
        } else if(args.length == 2) {
            if(args[1].equalsIgnoreCase("help")) {
                getHelp();
                return true;
            } else { Message.sendFormattedError(language.ERROR_COMMAND); return false; }
        } else if(args.length == 3) {
            if(args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("+")) {
                if(args.length != 3) { Message.sendFormattedError(language.ERROR_ARGUMENTS); return false; }
                
                MaterialData block = Util.getBlock(args[2]);
                if(block == null) { Message.sendFormattedError(language.ERROR_NOSUCHBLOCK.replaceAll("<BLOCK>", args[2])); return false; }
                
                List<MaterialData> blocks = curMine.getBlacklist().getBlocks();
                blocks.add(block);
                curMine.getBlacklist().setBlocks(blocks);
                Message.sendFormattedMine(ChatColor.GREEN + Util.getMaterialName(block) + ChatColor.WHITE + " has been added to the blacklistlist");
            }
            else if(args[1].equalsIgnoreCase("remove") || args[1].equalsIgnoreCase("-")) {
                if(args.length != 3) { Message.sendFormattedError(language.ERROR_ARGUMENTS); return false; }
                
                MaterialData block = Util.getBlock(args[2]);
                if(block == null) { Message.sendFormattedError(language.ERROR_NOSUCHBLOCK.replaceAll("<BLOCK>", args[2])); return false; }
                
                List<MaterialData> blocks = curMine.getBlacklist().getBlocks();
                blocks.remove(block);
                curMine.getBlacklist().setBlocks(blocks);
                Message.sendFormattedMine(ChatColor.GREEN + Util.getMaterialName(block) + ChatColor.WHITE + " has been removed from the list");
            }
            else { Message.sendFormattedError(language.ERROR_COMMAND); return false; }
        } else { Message.sendFormattedError(language.ERROR_ARGUMENTS); return false; }
        
        return curMine.saveFile();
    }
    
    @Override
    public void getHelp() {
        Message.formatHeader(20, "Blacklist");
        Message.formatHelp("blacklist", "", "Toggles the use of the blacklist for the mine");
        Message.formatHelp("whitelist", "", "Toggles the use of the whitelist for the mine");
        Message.formatHelp("blacklist", "+ <block>", "Add <block> to the list");
        Message.formatHelp("blacklist", "- <block>", "Remove <block> from the list");
        return;
    }
    
    @Override
    public void getHelpLine() { Message.formatHelp("blacklist help", "", "More information on the reset blacklist", "prison.mine.edit"); }
}
