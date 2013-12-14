/*
 * MetaCommand.java
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

import com.wolvencraft.prison.mines.PrisonMine;
import com.wolvencraft.prison.mines.util.Message;

import org.bukkit.ChatColor;

public class MetaCommand  implements BaseCommand {
    
    @Override
    public boolean run(String[] args) {
        Message.formatHeader(20, PrisonMine.getLanguage().GENERAL_TITLE);
        Message.send(ChatColor.GREEN + "PrisonMine v. " + ChatColor.BLUE + PrisonMine.getSettings().PLUGIN_VERSION + ChatColor.GREEN + " (build " + PrisonMine.getSettings().PLUGIN_BUILD + ")");
        Message.send(ChatColor.YELLOW + "http://dev.bukkit.org/server-mods/prisonmine/");
        Message.send("Author: " + ChatColor.AQUA + "bitWolfy");
        Message.send("Testers: " + ChatColor.AQUA + "theangrytomato" + ChatColor.WHITE + ", " + ChatColor.AQUA + "Dhs92" + ChatColor.WHITE + ", " + ChatColor.AQUA + "Speedrookie");
        return true;
    }
    
    @Override
    public void getHelp() {}
    
    @Override
    public void getHelpLine() { Message.formatHelp("about", "", "Shows the basic information about the plugin"); }
}
