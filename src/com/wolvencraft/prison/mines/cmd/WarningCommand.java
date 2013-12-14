/*
 * WarningCommand.java
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

import org.bukkit.ChatColor;

import com.wolvencraft.prison.mines.PrisonMine;
import com.wolvencraft.prison.mines.mine.Mine;
import com.wolvencraft.prison.mines.util.Message;
import com.wolvencraft.prison.mines.util.Util;

public class WarningCommand  implements BaseCommand {
    
    @Override
    public boolean run(String[] args) {
        
        if(args.length == 1 || (args.length == 2 && args[1].equalsIgnoreCase("help"))) { getHelp(); return true; }

        Mine curMine = PrisonMine.getCurMine();
        if(curMine == null) { Message.sendFormattedError(PrisonMine.getLanguage().ERROR_MINENOTSELECTED); return false; }
        
        if(args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("+")) {
            if(args.length != 3) { Message.sendFormattedError(PrisonMine.getLanguage().ERROR_ARGUMENTS); return false; }
            
            int time = Util.timeToSeconds(args[2]);
            if(time <= 0) { Message.sendFormattedError("Invalid time provided"); return false; }
            if(time > curMine.getResetPeriod()) { Message.sendFormattedError("Time cannot be set to a value greater then the reset time"); return false; }

            String parsedTime = Util.secondsToTime(time);
            if(curMine.hasWarningTime(time)) { Message.sendFormattedError("Mine already sends a warning at " + ChatColor.GOLD + parsedTime, false); return false; }
            curMine.addWarningTime(time);
            Message.sendFormattedMine("Mine will now send warnings at " + ChatColor.GOLD + parsedTime);
        } else if(args[1].equalsIgnoreCase("remove") || args[1].equalsIgnoreCase("-")) {
            if(args.length != 3) { Message.sendFormattedError(PrisonMine.getLanguage().ERROR_ARGUMENTS); return false; }
            
            int time = Util.timeToSeconds(args[2]);
            if(time <= 0) { Message.sendFormattedError("Invalid time provided"); return false; }
            
            String parsedTime = Util.secondsToTime(time);
            if(!curMine.hasWarningTime(time)) { Message.sendFormattedError("Mine does not send a warning at " + ChatColor.GOLD + parsedTime, false); return false; }
            curMine.removeWarningTime(time);
            
            Message.sendFormattedMine("Mine will no longer send a warning at " + ChatColor.GOLD + parsedTime);
        }
        else { Message.sendFormattedError(PrisonMine.getLanguage().ERROR_COMMAND); return false; }
        
        return curMine.saveFile();
    }
    
    @Override
    public void getHelp() {
        Message.formatHeader(20, "Timer");
        Message.formatHelp("warning", "+ <time>", "Adds a warning at time specified");
        Message.formatHelp("warning", "- <time>", "Remove a warning at time specified");
        return;
    }
    
    @Override
    public void getHelpLine() { Message.formatHelp("warning help", "", "Shows reset warning options", "prison.mine.edit"); }
}
