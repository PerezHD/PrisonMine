/*
 * ProtectionCommand.java
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
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

import com.wolvencraft.prison.PrisonSuite;
import com.wolvencraft.prison.hooks.WorldEditHook;
import com.wolvencraft.prison.region.PrisonSelection;
import com.wolvencraft.prison.mines.CommandManager;
import com.wolvencraft.prison.mines.PrisonMine;
import com.wolvencraft.prison.mines.mine.Mine;
import com.wolvencraft.prison.mines.settings.Language;
import com.wolvencraft.prison.mines.util.Message;
import com.wolvencraft.prison.mines.util.Util;
import com.wolvencraft.prison.mines.util.constants.BlacklistState;
import com.wolvencraft.prison.mines.util.constants.ProtectionType;

public class ProtectionCommand  implements BaseCommand {
    
    @Override
    public boolean run(String[] args) {
        
        if(args.length == 1 || (args.length == 2 && args[1].equalsIgnoreCase("help"))) { getHelp(); return true; }

        Language language = PrisonMine.getLanguage();
        Mine curMine = PrisonMine.getCurMine();
        if(curMine == null) { Message.sendFormattedError(PrisonMine.getLanguage().ERROR_MINENOTSELECTED); return false; }
        
        if(args[1].equalsIgnoreCase("save")) {
            Player player;
            if(CommandManager.getSender() instanceof Player) player = (Player) CommandManager.getSender();
            else { Message.sendFormattedError(language.ERROR_SENDERISNOTPLAYER); return false; }
            
            if(args.length != 2) { Message.sendFormattedError(language.ERROR_ARGUMENTS); return false; }
            
            PrisonSelection sel = PrisonSuite.getSelection(player);
            if(!sel.locationsSet()) {
                if(!PrisonSuite.usingWorldEdit()) { Message.sendFormattedError("Make a selection first"); return false; }
                else {
                    Location[] loc = WorldEditHook.getPoints(player);
                    if(loc == null) { Message.sendFormattedError("Make a selection first"); return false; }
                    sel.setCoordinates(loc);
                }
            }
            
            if(!sel.getPos1().getWorld().equals(sel.getPos2().getWorld())) { Message.sendFormattedError("Your selection points are in different worlds"); return false; }
            if(!sel.getPos1().getWorld().equals(curMine.getWorld())) { Message.sendFormattedError("Mine and protection regions are in different worlds"); return false; }
            
            curMine.getProtectionRegion().setCoordinates(sel);
            Message.sendFormattedMine("Protection region has been set!");
        }
        else if(args[1].equalsIgnoreCase("pvp")) {
            if(args.length != 2) { Message.sendFormattedError(language.ERROR_ARGUMENTS); return false; }
            
            if(curMine.getProtection().contains(ProtectionType.PVP)) {
                curMine.getProtection().remove(ProtectionType.PVP);
                Message.sendFormattedMine("PVP protection has been turned " + ChatColor.RED + "off");
            } else {
                curMine.getProtection().add(ProtectionType.PVP);
                Message.sendFormattedMine("PVP protection has been turned " + ChatColor.GREEN + "on");
            }
        }
        else if(args[1].equalsIgnoreCase("breaking") || args[1].equalsIgnoreCase("break")) {
            if(args.length < 3) {
                Message.sendFormattedError("Invalid parameters. Check your argument count!");
                return false;
            }
            
            if(args[2].equalsIgnoreCase("blacklist")) {
                if(args.length != 3) {
                    Message.sendFormattedError(language.ERROR_ARGUMENTS);
                    return false;
                }
                
                if(curMine.getProtection().contains(ProtectionType.BLOCK_BREAK) && curMine.getBreakBlacklist().getState().equals(BlacklistState.BLACKLIST)) {
                    curMine.getBreakBlacklist().setState(BlacklistState.DISABLED);
                    curMine.getProtection().remove(ProtectionType.BLOCK_BREAK);
                    Message.sendFormattedSuccess("The block breaking protection is now disabled");
                } else {
                    curMine.getBreakBlacklist().setState(BlacklistState.BLACKLIST);
                    curMine.getProtection().add(ProtectionType.BLOCK_BREAK);
                    Message.sendFormattedSuccess("The block breaking protection is now in blacklist mode");
                }
            } else if(args[2].equalsIgnoreCase("whitelist")) {
                if(args.length != 3) { Message.sendFormattedError(language.ERROR_ARGUMENTS); return false; }
                
                if(curMine.getProtection().contains(ProtectionType.BLOCK_BREAK) && curMine.getBreakBlacklist().getState().equals(BlacklistState.WHITELIST)) {
                    curMine.getBreakBlacklist().setState(BlacklistState.DISABLED);
                    curMine.getProtection().remove(ProtectionType.BLOCK_BREAK);
                    Message.sendFormattedSuccess("The block breaking protection is now disabled");
                } else {
                    curMine.getBreakBlacklist().setState(BlacklistState.WHITELIST);
                    curMine.getProtection().add(ProtectionType.BLOCK_BREAK);
                    Message.sendFormattedSuccess("The block breaking protection is now in whitelist mode");
                }
            } else if(args[2].equalsIgnoreCase("add") || args[2].equalsIgnoreCase("+")) {
                if(args.length != 4) { Message.sendFormattedError(language.ERROR_ARGUMENTS); return false; }
                MaterialData block = Util.getBlock(args[3]);
                if(block == null) { Message.sendFormattedError(language.ERROR_NOSUCHBLOCK); return false; }
                
                List<MaterialData> blockList = curMine.getBreakBlacklist().getBlocks();
                blockList.add(block);
                curMine.getBreakBlacklist().setBlocks(blockList);
                
                Message.sendFormattedMine(ChatColor.GREEN + block.getItemType().toString().toLowerCase().replace("_", " ") + ChatColor.WHITE + " was added to the block breaking protection blacklist");
            }
            else if(args[2].equalsIgnoreCase("remove") || args[2].equalsIgnoreCase("-")) {
                if(args.length != 4) { Message.sendFormattedError(language.ERROR_ARGUMENTS); return false; }
                MaterialData block = Util.getBlock(args[3]);
                if(block == null) { Message.sendFormattedError(language.ERROR_NOSUCHBLOCK); return false; }
                
                List<MaterialData> blockList = curMine.getBreakBlacklist().getBlocks();
                
                if(blockList.indexOf(block) == -1) { Message.sendFormattedError("There is no '" + args[3] + "' in break protection blacklist of mine '" + curMine.getId() + "'"); return false; }
                blockList.remove(block);
                curMine.getBreakBlacklist().setBlocks(blockList);

                Message.sendFormattedMine(ChatColor.RED + block.getItemType().toString().toLowerCase().replace("_", " ") + ChatColor.WHITE + " was removed from the block breaking protection blacklist");
            } else { Message.sendFormattedError(language.ERROR_ARGUMENTS); return false; }
        }
        else if(args[1].equalsIgnoreCase("placement") || args[1].equalsIgnoreCase("place")) {
            if(args.length < 3) { Message.sendFormattedError("Invalid parameters. Check your argument count!"); return false; }
            
            if(args[2].equalsIgnoreCase("blacklist")) {
                if(args.length != 3) { Message.sendFormattedError(language.ERROR_ARGUMENTS); return false; }
                
                if(curMine.getProtection().contains(ProtectionType.BLOCK_PLACE) && curMine.getPlaceBlacklist().getState().equals(BlacklistState.BLACKLIST)) {
                    curMine.getPlaceBlacklist().setState(BlacklistState.DISABLED);
                    curMine.getProtection().remove(ProtectionType.BLOCK_PLACE);
                    Message.sendFormattedSuccess("The place protection is now disabled");
                } else {
                    curMine.getPlaceBlacklist().setState(BlacklistState.BLACKLIST);
                    curMine.getProtection().add(ProtectionType.BLOCK_PLACE);
                    Message.sendFormattedSuccess("The block placement protection is now in blacklist mode");
                }
            } else if(args[2].equalsIgnoreCase("whitelist")) {
                if(args.length != 3) { Message.sendFormattedError(language.ERROR_ARGUMENTS); return false; }
                
                if(curMine.getProtection().contains(ProtectionType.BLOCK_PLACE) && curMine.getPlaceBlacklist().getState().equals(BlacklistState.WHITELIST)) {
                    curMine.getPlaceBlacklist().setState(BlacklistState.DISABLED);
                    curMine.getProtection().remove(ProtectionType.BLOCK_PLACE);
                    Message.sendFormattedSuccess("The block placement protection is now disabled");
                } else {
                    curMine.getPlaceBlacklist().setState(BlacklistState.WHITELIST);
                    curMine.getProtection().add(ProtectionType.BLOCK_PLACE);
                    Message.sendFormattedSuccess("The block placement protection is now in whitelist mode");
                }
            } else if(args[2].equalsIgnoreCase("add") || args[2].equalsIgnoreCase("+")) {
                if(args.length != 4) { Message.sendFormattedError(language.ERROR_ARGUMENTS); return false; }
                MaterialData block = Util.getBlock(args[3]);
                
                if(block == null) { Message.sendFormattedError(language.ERROR_NOSUCHBLOCK); return false; }
                
                List<MaterialData> blockList = curMine.getPlaceBlacklist().getBlocks();
                blockList.add(block);
                curMine.getPlaceBlacklist().setBlocks(blockList);
                
                Message.sendFormattedMine(ChatColor.GREEN + block.getItemType().toString().toLowerCase().replace("_", " ") + ChatColor.WHITE + " was added to the block placement protection blacklist");
            } else if(args[2].equalsIgnoreCase("remove") || args[2].equalsIgnoreCase("-")) {
                if(args.length != 4) { Message.sendFormattedError(language.ERROR_ARGUMENTS); return false; }
                MaterialData block = Util.getBlock(args[3]);
                
                if(block == null) { Message.sendFormattedError(language.ERROR_NOSUCHBLOCK); return false; }
                
                List<MaterialData> blockList = curMine.getPlaceBlacklist().getBlocks();
                
                if(blockList.indexOf(block) == -1) { Message.sendFormattedError("There is no '" + args[3] + "' in place protection blacklist of mine '" + curMine.getId() + "'"); return false; }
                blockList.remove(block);
                curMine.getPlaceBlacklist().setBlocks(blockList);

                Message.sendFormattedMine(ChatColor.RED + block.getItemType().toString().toLowerCase().replace("_", " ") + ChatColor.WHITE + " was removed from the block placement protection blacklist");
            } else { Message.sendFormattedError(language.ERROR_ARGUMENTS); return false; }
        } else { Message.sendFormattedError(language.ERROR_COMMAND); return false; }
        
        return curMine.saveFile();
    }
    
    @Override
    public void getHelp() {
        Message.formatHeader(20, "Protection");
        Message.formatHelp("prot", "pvp", "Toggles the PVP for the current mine");
        Message.send(ChatColor.RED + " Block Breaking Protection:");
        Message.formatHelp("prot", "break blacklist", "Toggles blacklist mode");
        Message.formatHelp("prot", "break whitelist", "Toggles whitelist mode");
        Message.formatHelp("prot", "break + <block>", "Add <block> to the list");
        Message.formatHelp("prot", "break - <block>", "Remove <block> from the list");
        Message.send(ChatColor.RED + " Block Placement Protection:");
        Message.formatHelp("prot", "place blacklist", "Toggles blacklist mode");
        Message.formatHelp("prot", "place whitelist", "Toggles whitelist mode");
        Message.formatHelp("prot", "place + <block>", "Add <block> to the blacklist");
        Message.formatHelp("prot", "place - <block>", "Remove <block> from the blacklist");
        return;
    }
    
    @Override
    public void getHelpLine() { Message.formatHelp("prot help", "", "Shows the help page for mine protection options", "prison.mine.edit"); }
}
