/*
 * UtilCommand.java
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
import com.wolvencraft.prison.mines.exceptions.DisplaySignNotFoundException;
import com.wolvencraft.prison.mines.settings.MineData;
import com.wolvencraft.prison.mines.util.Message;

public class UtilCommand implements BaseCommand {
    
    @Override
    public boolean run(String[] args) throws DisplaySignNotFoundException {
        
        if(args[0].equalsIgnoreCase("reload")) {
            PrisonMine.getInstance().reloadConfig();
            PrisonMine.getInstance().reloadSettings();
            PrisonMine.getInstance().reloadLanguageData();
            PrisonMine.getInstance().reloadLanguage();
            PrisonMine.setMines(MineData.loadAll());
            Message.sendFormattedSuccess("Data loaded from disk successfully", false);
            return true;
        } else if(args[0].equalsIgnoreCase("saveall")) {
            MineData.saveAll();
            Message.sendFormattedSuccess("Data saved to disk successfully", false);
            return true;
        } else {
            Message.sendFormattedError(PrisonMine.getLanguage().ERROR_COMMAND);
            return false;
        }
    }
    
    @Override
    public void getHelp() {}
    
    @Override
    public void getHelpLine() { Message.formatHelp("reload", "", "Reloads all data from file", "prison.mine.admin"); }
}
