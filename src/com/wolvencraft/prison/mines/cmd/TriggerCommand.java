/*
 * TriggerCommand.java
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

public class TriggerCommand implements BaseCommand {
    
    @Override
    public boolean run(String[] args) {
        
        if(args.length == 1 || (args.length == 2 && args[1].equalsIgnoreCase("help"))) { getHelp(); return true; }
        
        if(args.length > 3) { Message.sendFormattedError(PrisonMine.getLanguage().ERROR_ARGUMENTS); return false; }
        
        Mine curMine = PrisonMine.getCurMine();
        if(curMine == null) { Message.sendFormattedError(PrisonMine.getLanguage().ERROR_MINENOTSELECTED); return false; }
        
        if(args[1].equalsIgnoreCase("time")) {
            if(args.length == 2) {
                if(curMine.getAutomaticReset()) {
                    curMine.setAutomaticReset(false);
                    Message.sendFormattedMine("Time trigger is " + ChatColor.RED + "off");
                }
                else {
                    curMine.setAutomaticReset(true);
                    Message.sendFormattedMine("Time trigger is " + ChatColor.GREEN + "on");
                }
            } else {
                
                if(!curMine.getAutomaticReset()) {
                    curMine.setAutomaticReset(true);
                    Message.sendFormattedMine("Time trigger is " + ChatColor.GREEN + "on");
                }
                
                int time = Util.timeToSeconds(args[2]);
                if(time <= 0) { Message.sendFormattedError("Invalid time provided"); return false; }
                curMine.setResetPeriod(time);
                String parsedTime = Util.secondsToTime(time);
                Message.sendFormattedMine("Mine will now reset every " + ChatColor.GOLD + parsedTime + ChatColor.WHITE + " minute(s)");
            }
        } else if(args[1].equalsIgnoreCase("composition")) {
            if(args.length == 2) {
                if(curMine.getCompositionReset()) {
                    curMine.setCompositionReset(false);
                    Message.sendFormattedMine("Composition trigger is " + ChatColor.RED + "off");
                }
                else {
                    curMine.setCompositionReset(true);
                    Message.sendFormattedMine("Composition trigger is " + ChatColor.GREEN + "on");
                }
            } else {
                
                if(!curMine.getCompositionReset()) {
                    curMine.setCompositionReset(true);
                    Message.sendFormattedMine("Composition trigger is " + ChatColor.GREEN + "on");
                }
                
                String percentString = args[2];
                if(percentString.endsWith("%")) percentString.substring(0, percentString.length() - 1);
                double percent = 0;
                try {percent = Double.parseDouble(percentString) / 100; }
                catch (NumberFormatException nfe) { Message.sendFormattedError("Invalid percent value provided"); return false; }
                if(percent <= 0 || percent > 100) { Message.sendFormattedError("Invalid percent value provided"); return false; }
                curMine.setCompositionPercent(percent);
                Message.sendFormattedMine("Mine will reset once it is " + ChatColor.GOLD + percentString + "%" + ChatColor.WHITE + " empty");
            }
        } else { Message.sendFormattedError(PrisonMine.getLanguage().ERROR_COMMAND); return false; }
        
        return curMine.saveFile();
    }
    
    @Override
    public void getHelp() {
        Message.formatHeader(20, "Trigger");
        Message.formatHelp("trigger", "time", "Toggles the timer on and off");
        Message.formatHelp("trigger", "time <time>", "Sets the timer to the specified value");
        Message.formatHelp("trigger", "composition", "Toggles the composition trigger");
        Message.formatHelp("trigger", "composition <percent>", "Sets the composition percent");
    }
    
    @Override
    public void getHelpLine() { Message.formatHelp("trigger help", "", "Shows the reset trigger help page", "prison.mine.edit"); }

}
