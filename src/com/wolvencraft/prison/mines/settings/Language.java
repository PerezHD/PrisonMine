/*
 * Language.java
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

package com.wolvencraft.prison.mines.settings;

import com.wolvencraft.prison.mines.PrisonMine;

public class Language extends com.wolvencraft.prison.settings.Language {
    
    public final String GENERAL_TITLE;
    public final String GENERAL_SUCCESS;
    public final String GENERAL_ERROR;
    
    public final String ERROR_MINENAME;
    public final String ERROR_MINENOTSELECTED;
    public final String ERROR_FUCKIGNNOOB;
    
    public final String MINE_SELECTED;
    public final String MINE_DESELECTED;
    
    public final String RESET_MANUAL;
    public final String RESET_WARNING;
    public final String RESET_AUTOMATIC;
    public final String RESET_TIMED;
    public final String RESET_COMPOSITION;
    public final String RESET_COOLDOWN;
    
    public final String SIGN_TITLE;
    public final String SIGN_WITHDRAW;
    public final String SIGN_FUNDS;
    
    public final String MISC_TELEPORT;
    
    public Language(PrisonMine plugin) {
        super(PrisonMine.getPrisonSuite());
        GENERAL_TITLE = plugin.getLanguageData().getString("general.title");
        GENERAL_SUCCESS = plugin.getLanguageData().getString("general.title-success");
        GENERAL_ERROR = plugin.getLanguageData().getString("general.title-error");
        
        ERROR_MINENAME = plugin.getLanguageData().getString("error.mine-name");
        ERROR_MINENOTSELECTED = plugin.getLanguageData().getString("error.mine-not-selected");
        ERROR_FUCKIGNNOOB = plugin.getLanguageData().getString("error.removing-air");

        MINE_SELECTED = plugin.getLanguageData().getString("editing.mine-selected-successfully");
        MINE_DESELECTED = plugin.getLanguageData().getString("editing.mine-deselected-successfully");
        
        RESET_MANUAL = plugin.getLanguageData().getString("reset.manual-reset-successful");
        RESET_WARNING = plugin.getLanguageData().getString("reset.automatic-reset-warning");
        RESET_AUTOMATIC = plugin.getLanguageData().getString("reset.automatic-reset-successful");
        RESET_TIMED = plugin.getLanguageData().getString("reset.timed-reset-successful");
        RESET_COMPOSITION = plugin.getLanguageData().getString("reset.composition-reset-successful");
        RESET_COOLDOWN = plugin.getLanguageData().getString("reset.mine-cooldown");
        
        SIGN_TITLE = plugin.getLanguageData().getString("sign.title");
        SIGN_WITHDRAW = plugin.getLanguageData().getString("sign.withdraw");
        SIGN_FUNDS = plugin.getLanguageData().getString("sign.funds");
        
        MISC_TELEPORT = plugin.getLanguageData().getString("misc.mine-teleport");
    }
}
