/*
 * MineVariable.java
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

package com.wolvencraft.prison.mines.util.constants;

import java.util.logging.Level;

import lombok.AccessLevel;
import lombok.Getter;

import com.wolvencraft.prison.mines.mine.Mine;
import com.wolvencraft.prison.mines.util.Message;
import com.wolvencraft.prison.mines.util.variables.BaseVar;
import com.wolvencraft.prison.mines.util.variables.BlockStatVars;
import com.wolvencraft.prison.mines.util.variables.CompositionTriggerVars;
import com.wolvencraft.prison.mines.util.variables.IDVars;
import com.wolvencraft.prison.mines.util.variables.NameVars;
import com.wolvencraft.prison.mines.util.variables.PlayerVars;
import com.wolvencraft.prison.mines.util.variables.TimeTriggerVars;

public enum MineVariable {
    
    ID          (IDVars.class, "ID", null, true),
    IDS         (IDVars.class, "IDS", "", false),
    NAME        (NameVars.class, "NAME", null, true),
    NAMES       (NameVars.class, "NAMES", "", false),
    PLAYER      (PlayerVars.class, "PLAYER", null, true),
    TBLOCKS     (BlockStatVars.class, "TBLOCKS", "tblocks", true),
    RBLOCKS     (BlockStatVars.class, "RBLOCKS", "rblocks", false),
    PBLOCKS     (BlockStatVars.class, "PBLOCKS", "pblocks", false),
    PPER        (CompositionTriggerVars.class, "PPER", "pper", true),
    NPER        (CompositionTriggerVars.class, "NPER", "nper", false),
    PHOUR       (TimeTriggerVars.class, "PHOUR", "phour", true),
    PMIN        (TimeTriggerVars.class, "PMIN", "pmin", false),
    PSEC        (TimeTriggerVars.class, "PSEC", "psec", false),
    PTIME       (TimeTriggerVars.class, "PTIME", "ptime", false),
    NHOUR       (TimeTriggerVars.class, "NHOUR", "nhour", true),
    NMIN        (TimeTriggerVars.class, "NMIN", "nmin", false),
    NSEC        (TimeTriggerVars.class, "NSEC", "nsec", false),
    NTIME       (TimeTriggerVars.class, "NTIME", "ntime", false);
    
    MineVariable(Class<?> clazz, String name, String option, boolean showHelp) {
        try {
            this.object = (BaseVar) clazz.newInstance();
            this.name = name;
            this.option = option;
            this.showHelp = showHelp;
        } catch (InstantiationException e) {
            Message.log(Level.SEVERE, "Error while instantiating a command! InstantiationException");
            return;
        } catch (IllegalAccessException e) {
            Message.log(Level.SEVERE, "Error while instantiating a command! IllegalAccessException");
            return;
        }
    }
    
    private BaseVar object;
    
    @Getter(AccessLevel.PUBLIC)
    private String name;
    private String option;
    private boolean showHelp;
    
    public String parse(Mine mine) { return object.parse(mine, option); }
    public void getHelp() { if(showHelp) object.getHelp(); }
}
