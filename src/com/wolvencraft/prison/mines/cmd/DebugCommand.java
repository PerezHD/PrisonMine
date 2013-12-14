/*
 * DebugCommand.java
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
import java.util.Locale;
import java.util.ResourceBundle;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.wolvencraft.prison.PrisonSuite;
import com.wolvencraft.prison.mines.CommandManager;
import com.wolvencraft.prison.mines.PrisonMine;
import com.wolvencraft.prison.mines.mine.Mine;
import com.wolvencraft.prison.mines.upgrade.ImportData;
import com.wolvencraft.prison.mines.util.Message;
import com.wolvencraft.prison.region.PrisonSelection;

public class DebugCommand implements BaseCommand {

    @Override
    public boolean run(String[] args) {
        if(args.length == 1 && !args[0].equalsIgnoreCase("debug") && !args[0].equalsIgnoreCase("import")) {
            Message.sendFormattedError(PrisonMine.getLanguage().ERROR_COMMAND);
            return false;
        }

        CommandSender player = CommandManager.getSender();
        
        Message.debug("Checks passed, parsing the command");
        
        if(args[0].equalsIgnoreCase("debug")) {
            getHelp();
            return true;
        } else if(args[0].equalsIgnoreCase("import")) {
            List<Mine> newMines = ImportData.loadAll();
            if(newMines == null) {
                Message.sendFormatted("DEBUG", "Import folder not found", false);
                return false;
            }
            for(Mine mine : newMines) { PrisonMine.addMine(mine); }
            Message.sendFormatted("DEBUG", "Mines imported into the system. Check the server log for more info", false);
            return true;
        } else if(args[0].equalsIgnoreCase("setregion")) {
            Mine curMine = Mine.get(args[1]);
            PrisonSelection sel = PrisonSuite.getSelection((Player) player);
            curMine.setRegion(sel);
            Message.sendFormatted("DEBUG", "Region set", false);
            return curMine.saveFile();
        } else if(args[0].equalsIgnoreCase("tp")) {
            Mine curMine = Mine.get(args[1]);
            ((Player) player).teleport(curMine.getRegion().getMaximum());
            Message.sendFormatted("DEBUG", "Teleported to: " + curMine.getId(), false);
            return true;
        } else if(args[0].equalsIgnoreCase("unload")) {
            PrisonMine.removeMine(Mine.get(args[1]));
            Message.sendFormatted("DEBUG", "Unloaded " + args[1] + " from memory", false);
            return true;
        } else if(args[0].equalsIgnoreCase("locale")) {
            if(args[1].equalsIgnoreCase("fr")) Locale.setDefault(Locale.FRENCH);
            else Locale.setDefault(Locale.ENGLISH);
            ResourceBundle.clearCache();
            Message.sendFormatted("DEBUG", "Locale set to " + Locale.getDefault().toString(), false);
            return true;
        } else {
            Message.sendFormattedError(PrisonMine.getLanguage().ERROR_COMMAND);
            return false;
        }
    }

    @Override
    public void getHelp() { 
        Message.formatHeader(20, "Debug");
        Message.formatHelp("import", "", "Imports MR and MRL files into the system");
        Message.formatHelp("setregion", "<id>", "Sets the reset region of a mine to the one specified");
        Message.formatHelp("tp", "<id>", "Teleports the sender to the specified mine");
        Message.formatHelp("unload", "<id>", "Unloads the mine from the memory. The mine will be loaded back on server restart");
        Message.formatHelp("saveall", "", "Forces the plugin to save all data to the file");
    }

    @Override
    public void getHelpLine() { }

}
