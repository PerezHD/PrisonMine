/*
 * AutomaticResetRoutine.java
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

package com.wolvencraft.prison.mines.routines;

import com.wolvencraft.prison.mines.PrisonMine;
import com.wolvencraft.prison.mines.mine.Mine;
import com.wolvencraft.prison.mines.util.Message;
import com.wolvencraft.prison.mines.util.Util;
import com.wolvencraft.prison.mines.util.constants.MineFlag;

public class AutomaticResetRoutine {
    public static void run(Mine mine) {
            if(mine.getAutomaticReset() && (mine.getResetsIn() <= 0 || PrisonMine.getSettings().RESET_FORCE_TIMER_UPDATE)) {
            Message.debug("| Resetting the timer (config)");
            mine.resetTimer();
        }
        
        if(!(mine.reset())) {
            Message.debug("| Error while executing the generator! Aborting.");
            Message.debug("+---------------------------------------------");
            return;
        }
        
        if(mine.isCooldownEnabled()) mine.resetCooldown();
        
        String broadcastMessage = PrisonMine.getLanguage().RESET_AUTOMATIC;
        
        if(mine.getAutomaticReset() && mine.getResetsIn() <= 0)
            broadcastMessage = PrisonMine.getLanguage().RESET_TIMED;
        
        if(mine.getCompositionReset() && mine.getCurrentPercent() <= mine.getRequiredPercent())
            broadcastMessage = PrisonMine.getLanguage().RESET_COMPOSITION;
        
        if(mine.getParent() == null && !mine.hasFlag(MineFlag.Silent)) {
            broadcastMessage = Util.parseVars(broadcastMessage, mine);
            Message.broadcast(broadcastMessage);
        }
        
        for(Mine childMine : mine.getChildren()) {
            Message.debug("+---------------------------------------------");
            Message.debug("| Mine " + childMine.getId() + " is resetting. Reset report:");
            Message.debug("| Reset cause: parent mine is resetting (" + mine.getId() + ")");
            run(childMine);
            Message.debug("| Reached the end of the report for " + childMine.getId());
            Message.debug("+---------------------------------------------");
        }
    }
}
