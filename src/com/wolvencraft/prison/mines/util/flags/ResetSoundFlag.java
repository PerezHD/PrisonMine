/*
 * ResetSoundFlag.java
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

package com.wolvencraft.prison.mines.util.flags;

import org.bukkit.Sound;

public class ResetSoundFlag implements BaseFlag {
    
    String option;
    
    @Override
    public String getName() { return "ResetSound"; }

    @Override
    public String getOption() { return option; }

    @Override
    public void setOption(String option) { this.option = option; }

    @Override
    public boolean isOptionValid(String option) { 
        try { Sound.valueOf(option); }
        catch (IllegalArgumentException iae) { return false; }
        catch (NullPointerException npe) { return false; }
        catch (Exception ex) { return false; }
        return true;
    }

}
