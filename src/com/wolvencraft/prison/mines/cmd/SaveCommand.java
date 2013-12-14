/*
 * SaveCommand.java
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

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.wolvencraft.prison.PrisonSuite;
import com.wolvencraft.prison.hooks.WorldEditHook;
import com.wolvencraft.prison.region.PrisonRegion;
import com.wolvencraft.prison.region.PrisonSelection;
import com.wolvencraft.prison.mines.CommandManager;
import com.wolvencraft.prison.mines.PrisonMine;
import com.wolvencraft.prison.mines.mine.Mine;
import com.wolvencraft.prison.mines.util.Message;

public class SaveCommand implements BaseCommand {
    
    @Override
    public boolean run(String[] args) {
        
        if(args.length == 1 || (args.length == 2 && args[1].equalsIgnoreCase("help"))) { getHelp(); return true; }
        if(args.length > 3) { Message.sendFormattedError(PrisonMine.getLanguage().ERROR_ARGUMENTS); return false; }
        
        Player player = (Player) CommandManager.getSender();
        
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

        if(Mine.get(args[1]) != null) { Message.sendFormattedError("Mine '" + args[1] + "' already exists!"); return false; }
        
        for(String bannedName : PrisonMine.getSettings().BANNEDNAMES) {
            if(args[1].equalsIgnoreCase(bannedName)) { Message.sendFormattedError("This name is not valid"); return false; }
        }
        
        Mine newMine = new Mine(args[1], new PrisonRegion(sel), sel.getPos1().getWorld(), player.getLocation());
        
        PrisonMine.addMine(newMine);
        PrisonMine.setCurMine(newMine);
        
        Message.sendFormattedMine("Mine created successfully!");
        return newMine.saveFile();
    }
    
    @Override
    public void getHelp() { Message.formatHeader(20, "Mine creation"); getHelpLine(); }
    
    @Override
    public void getHelpLine() { Message.formatHelp("create", "<id>", "Saves the mine region to file", "prison.mine.edit"); }
}
