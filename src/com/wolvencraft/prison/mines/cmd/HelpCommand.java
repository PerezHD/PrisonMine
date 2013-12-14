/*
 * HelpCommand.java
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

import com.wolvencraft.prison.mines.CommandManager;
import com.wolvencraft.prison.mines.PrisonMine;
import com.wolvencraft.prison.mines.util.Message;

public class HelpCommand implements BaseCommand {
    
    @Override
    public boolean run(String[] args) {
        Message.formatHeader(20, PrisonMine.getLanguage().GENERAL_TITLE);
        for(CommandManager cmd : CommandManager.values()) { cmd.getHelpLine(); }
        return true;
    }
    
    @Override
    public void getHelp() {}
    
    @Override
    public void getHelpLine() {};
}
