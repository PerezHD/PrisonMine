/*
 * ImportData.java
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

package com.wolvencraft.prison.mines.upgrade;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.wolvencraft.prison.mines.PrisonMine;
import com.wolvencraft.prison.mines.mine.Mine;
import com.wolvencraft.prison.mines.util.Message;


/**
 * Loads data from MineReset and MineResetLite
 * @author bitWolfy
 *
 */
public class ImportData {
    
    /**
     * Loads the mine data from disc
     * @param mines List of mines to write the data to
     * @return Loaded list of mines
     */
    public static List<Mine> loadAll() {
        List<Mine> mines = new ArrayList<Mine>();
        
        File importFolder = new File(PrisonMine.getInstance().getDataFolder(), "import");
        if (!importFolder.exists() || !importFolder.isDirectory()) { return null; }
        File[] mrFiles = importFolder.listFiles(new FileFilter() {
            public boolean accept(File file) {
                return file.getName().contains(".yml");
            }
        });

        for (File mineFile : mrFiles) {
            try {
                FileConfiguration mineConf = YamlConfiguration.loadConfiguration(mineFile);
                Object mine = mineConf.get("mine");
                if (mine instanceof MRMine) { mines.add(((MRMine) mine).importMine()); }
                else if(mine instanceof MRLMine) { mines.add(((MRLMine) mine).importMine()); }
                else Message.log(Level.WARNING, "Unknown file in the import directory: " + mineFile.getName());
            } catch (IllegalArgumentException ex) { Message.log(Level.SEVERE, "You failed to rename the class properly: " + mineFile.getName()); }
            
        }
        return mines;
    }
}
