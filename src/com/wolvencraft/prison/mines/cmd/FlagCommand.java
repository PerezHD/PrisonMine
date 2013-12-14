/*
 * FlagCommand.java
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
import com.wolvencraft.prison.mines.mine.Mine;
import com.wolvencraft.prison.mines.settings.Language;
import com.wolvencraft.prison.mines.util.Message;
import com.wolvencraft.prison.mines.util.constants.MineFlag;

public class FlagCommand implements BaseCommand {

    @Override
    public boolean run(String[] args) {
        if(args.length == 1 || (args.length == 2 && args[1].equalsIgnoreCase("help"))) { getHelp(); return true; }
        
        Language language = PrisonMine.getLanguage();
//        if(args.length > 3) { Message.sendFormattedError(language.ERROR_ARGUMENTS); return false; }
        
        Mine curMine = PrisonMine.getCurMine();
        if(curMine == null) { Message.sendFormattedError(PrisonMine.getLanguage().ERROR_MINENOTSELECTED); return false; }
        
        MineFlag flag = MineFlag.get(args[1]);
        if(flag == null) { Message.sendFormattedError("The specified flag does not exist"); return false; }
        
        if(flag.isParameterized()) {
            if(!flag.isMultiWord() && args.length != 3) { Message.sendFormattedError(language.ERROR_ARGUMENTS); return false; }
            if(!flag.isOptionValid(args[2])) { Message.sendFormattedError("This option is not valid"); return false; }
            
            String argString = "";
            if(!flag.isMultiWord()) argString = args[2];
            else {
                for(int i = 2; i < args.length; i++) {
                    argString += args[i] + " ";
                }
            }
            
            if(curMine.hasFlag(flag)) {
                if(flag.isDuplicateAware()) {
                    if(curMine.hasFlag(flag, argString)) {
                        curMine.removeFlag(flag, argString);
                        Message.sendFormattedMine("Flag " + flag + " has been removed");
                    } else {
                        curMine.addFlag(flag, argString);
                        Message.sendFormattedMine("Flag " + flag + " has been added");
                    }
                } else {
                    curMine.removeFlag(flag);
                    Message.sendFormattedMine("Flag " + flag + " has been removed");
                }
            } else {
                curMine.addFlag(flag, argString);
                Message.sendFormattedMine("Flag " + flag + " has been added");
            }
        } else {
            if(curMine.hasFlag(flag)) {
                curMine.removeFlag(flag);
                Message.sendFormattedMine("Flag " + flag + " has been removed");
            } else {
                curMine.addFlag(flag);
                Message.sendFormattedMine("Flag " + flag + " has been added");
            }
        }
        
        return curMine.saveFile();
    }

    @Override
    public void getHelp() {
        Message.formatHeader(20, "Flags");
        Message.formatHelp("flag", "<flag> [option]", "Adds a flag value to the mine");
        MineFlag[] validFlags = MineFlag.values();
        String flagString = validFlags[0].getAlias();
        for(int i = 1; i < validFlags.length; i++) {
            flagString += ", " + validFlags[i].getAlias();
        }
        Message.send("Available flags: "+ flagString);
        Message.send("Not all flags have options available");
    }

    @Override
    public void getHelpLine() {
        Message.formatHelp("flag help", "", "Shows the help page on mine flags", "prison.mine.edit");
    }
}
