/*
 * ListCommand.java
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
import com.wolvencraft.prison.mines.cmd.BaseCommand;
import com.wolvencraft.prison.mines.mine.Mine;
import com.wolvencraft.prison.mines.util.Message;

public class ListCommand implements BaseCommand {
    
    @Override
    public boolean run(String[] args) {
        
        if(args.length != 1) { Message.sendFormattedError(PrisonMine.getLanguage().ERROR_ARGUMENTS); return false; }
        Message.send(ChatColor.DARK_RED + "                    -=[ " + ChatColor.GREEN + ChatColor.BOLD + "Public Mines" + ChatColor.DARK_RED + " ]=-");
        
        for(Mine mine : PrisonMine.getStaticMines()) {
            String displayName = mine.getName();
            if(displayName.equals(mine.getId())) Message.send(" - " + ChatColor.GREEN + mine.getId() + "");
            else Message.send(" - " + ChatColor.GREEN + displayName + ChatColor.WHITE + " (" + mine.getId() + ")");
        }
        
        return true;
    }
    
    @Override
    public void getHelp() {}
    
    @Override
    public void getHelpLine() { Message.formatHelp("list", "", "Lists all the available mines", "prison.mine.info.list"); }
}
