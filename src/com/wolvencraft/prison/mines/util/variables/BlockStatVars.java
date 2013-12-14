/*
 * BlockStatsVars.java
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

package com.wolvencraft.prison.mines.util.variables;

import org.bukkit.ChatColor;

import com.wolvencraft.prison.mines.mine.Mine;
import com.wolvencraft.prison.mines.util.Message;

public class BlockStatVars implements BaseVar {

    @Override
    public String parse(Mine mine, String option) {
        if(option.equalsIgnoreCase("tblocks")) {
            String tblocks = mine.getTotalBlocks() + "";
            if(tblocks.length() > 5) tblocks = tblocks.substring(0, 5);
            return tblocks;
        }
        else if(option.equalsIgnoreCase("rblocks")) {
            String rblocks = mine.getBlocksLeft() + "";
            if(rblocks.length() > 5) rblocks = rblocks.substring(0, 5);
            return rblocks;
        }
        else if(option.equalsIgnoreCase("pblocks")) {
            String pblocks = mine.getCurrentPercent() + "";
            if(pblocks.length() > 5) pblocks = pblocks.substring(0, 5);
            return pblocks;
        }
        return "";
    }

    @Override
    public void getHelp() {
        Message.send("+ Mine Statistics variables");
        Message.send("|- " + ChatColor.GOLD + "<TBLOCKS> " + ChatColor.WHITE + "Total number of blocks in the mine", false);
        Message.send("|- " + ChatColor.GOLD + "<RBLOCKS> " + ChatColor.WHITE + "Remaining blocks in the mine", false);
        Message.send("|- " + ChatColor.GOLD + "<PBLOCKS> " + ChatColor.WHITE + "Percentage of the mine that is not air", false);
        Message.send("");
    }

}
