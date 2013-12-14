/*
 * TimeCommand.java
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
import com.wolvencraft.prison.mines.settings.Language;
import com.wolvencraft.prison.mines.util.Message;
import com.wolvencraft.prison.mines.util.Util;

public class TimeCommand implements BaseCommand {

    @Override
    public boolean run(String[] args) {
        Mine curMine = null;
        Language language = PrisonMine.getLanguage();
        
        if(args.length == 1) {
            curMine = PrisonMine.getCurMine();
            if(curMine == null) { getHelp(); return true; }
        }
        else {
            curMine = Mine.get(args[1]);
            if(curMine == null) { Message.sendFormattedError(language.ERROR_MINENAME.replaceAll("<ID>", args[1]), false); return false; }
        }
        
        if(args.length > 2) { Message.sendFormattedError(language.ERROR_ARGUMENTS); return false; }
        
        Mine parentMine = curMine.getSuperParent();
        
        String displayString = "---==[ " + ChatColor.GREEN + ChatColor.BOLD + curMine.getName() + ChatColor.WHITE + " ]==---";
        for(int i = 0; i < 25 - (curMine.getName().length() / 2); i++) displayString = " " + displayString;
        Message.send(displayString);
        Message.send("");
        
        if(parentMine.getAutomaticReset()) Message.send("    Resets every ->  " + ChatColor.GREEN + Util.secondsToTime(parentMine.getResetPeriodSafe()) + "    " + ChatColor.GOLD + Util.secondsToTime(parentMine.getResetsInSafe()) + ChatColor.WHITE + "  <- Next Reset");
        else Message.send("   Mine has to be reset manually");
        
        return true;
    }

    @Override
    public void getHelp() { getHelpLine(); }

    @Override
    public void getHelpLine() { Message.formatHelp("time", "<name>", "Shows the time until the timed reset", "prison.mine.info.time"); }

}
