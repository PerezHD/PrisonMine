/*
 * ResetCommand.java
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

import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.wolvencraft.prison.mines.CommandManager;
import com.wolvencraft.prison.mines.PrisonMine;
import com.wolvencraft.prison.mines.mine.Mine;
import com.wolvencraft.prison.mines.util.Message;
import com.wolvencraft.prison.mines.util.Util;
import com.wolvencraft.prison.mines.util.constants.MineFlag;

public class ResetCommand implements BaseCommand {
    
    @Override
    public boolean run(String[] args) {
        
        Mine curMine = null;
        
        if(args.length == 1) {
            curMine = PrisonMine.getCurMine();
            if(curMine == null) { getHelp(); return true; }
        } else if(args.length == 2) {
            if(args[1].equalsIgnoreCase("all")) {
                boolean success = true;
                for(Mine mine : PrisonMine.getStaticMines()) {
                    if(!run(mine.getId())) success = false;
                }
                return success;
            } else if(args[1].equalsIgnoreCase("help")) { getHelp(); return true; }
            curMine = Mine.get(args[1]);
            if(curMine == null) { Message.sendFormattedError(PrisonMine.getLanguage().ERROR_ARGUMENTS); return false; }
        } else { Message.sendFormattedError(PrisonMine.getLanguage().ERROR_ARGUMENTS); return false; }
        
        String broadcastMessage = "";

        Message.debug("+---------------------------------------------");
        Message.debug("| Mine " + curMine.getId() + " is resetting. Reset report:");
        Message.debug("| Reset cause: MANUAL (command/sign)");
        
        if(!Util.hasPermission("prison.mine.reset.manual." + curMine.getId()) && !Util.hasPermission("prison.mine.reset.manual")) {
            Message.sendFormattedError(PrisonMine.getLanguage().ERROR_ACCESS);
            Message.debug("| Insufficient permissions. Cancelling...");
            Message.debug("| Reached the end of the report for " + curMine.getId());
            Message.debug("+---------------------------------------------");
            return false;
        }
        
        if(curMine.isCooldownEnabled() && curMine.getCooldownEndsIn() > 0 && !Util.hasPermission("prison.mine.bypass.cooldown")) {
            Message.sendFormattedError(Util.parseVars(PrisonMine.getLanguage().RESET_COOLDOWN, curMine));
            Message.debug("| Cooldown is in effect. Checking for bypass...");
            Message.debug("| Failed. Cancelling...");
            Message.debug("| Reached the end of the report for " + curMine.getId());
            Message.debug("+---------------------------------------------");
            return false;
        }
        
        if(curMine.getAutomaticReset() && PrisonMine.getSettings().RESET_FORCE_TIMER_UPDATE) {
            Message.debug("| Resetting the timer (config)");
            curMine.resetTimer();
        }
        
        broadcastMessage = PrisonMine.getLanguage().RESET_MANUAL;
        
        if(curMine.isCooldownEnabled()) curMine.resetCooldown();
        
        if(!(curMine.reset())) {
            Message.debug("| Error while executing the generator! Aborting.");
            Message.debug("+---------------------------------------------");
            return false;
        }
        
        broadcastMessage = Util.parseVars(broadcastMessage, curMine);
        
        if(!curMine.hasFlag(MineFlag.Silent)) Message.broadcast(broadcastMessage);
        else Message.sendFormattedSuccess(broadcastMessage);
        
        if(PrisonMine.getSettings().RESET_TRIGGERS_CHILDREN_RESETS) {
            for(Mine childMine : curMine.getChildren()) {
                Message.debug("+---------------------------------------------");
                Message.debug("| Mine " + childMine.getId() + " is resetting. Reset report:");
                Message.debug("| Reset cause: parent mine is resetting (" + curMine.getId() + ")");
                run(childMine.getId());
                Message.debug("| Reached the end of the report for " + childMine.getId());
                Message.debug("+---------------------------------------------");
            }
        }
        
        Message.debug("| Reached the end of the report for " + curMine.getId());
        Message.debug("+---------------------------------------------");
        
        if(CommandManager.getSender() instanceof ConsoleCommandSender) curMine.setLastResetBy("CONSOLE");
        else curMine.setLastResetBy(((Player) CommandManager.getSender()).getPlayerListName());
        
        return true;
    }
    
    public boolean run(String arg) {
        String[] args = {"", arg};
        return run(args);
    }
    
    @Override
    public void getHelp() { Message.formatHeader(20, "Manual Reset"); getHelpLine(); }
    
    @Override
    public void getHelpLine() { Message.formatHelp("reset", "<name>", "Resets the mine manually", "prison.mine.reset.manual"); }
}
