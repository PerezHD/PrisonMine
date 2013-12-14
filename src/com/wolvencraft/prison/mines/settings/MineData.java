/*
 * MineData.java
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

public class MineData {
    /**
     * Saves all the mine data to disc
     */
    public static void saveAll() {
        for (Mine mine : PrisonMine.getStaticMines()) mine.saveFile();
    }
    
    /**
     * Loads the mine data from disc
     * @return Loaded list of mines
     */
    public static List<Mine> loadAll() {
        List<Mine> mines = new ArrayList<Mine>();
        File mineFolder = new File(PrisonMine.getInstance().getDataFolder(), "mines");
        if (!mineFolder.exists() || !mineFolder.isDirectory()) {
            mineFolder.mkdir();
            return mines;
        }
        File[] mineFiles = mineFolder.listFiles(new FileFilter() {
            public boolean accept(File file) { return file.getName().contains(".pmine.yml"); }
        });

        for (File mineFile : mineFiles) {
            try {
                FileConfiguration mineConf = YamlConfiguration.loadConfiguration(mineFile);
                Object mine = mineConf.get("mine");
                if (mine instanceof Mine) mines.add((Mine) mine);
            }
            catch (IllegalArgumentException ex) {
                Message.log(Level.SEVERE, ex.getMessage());
                continue;
            }
        }
        return mines;
    }
}
