package me.lorenzop.webauctionplus.tasks;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.lorenzop.webauctionplus.WebAuctionPlus;
import me.lorenzop.webauctionplus.dao.AuctionPlayer;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class PlayerConvertTask implements Runnable {

	public PlayerConvertTask() {

	}

	public synchronized void run() {         
            Connection conn = WebAuctionPlus.dataQueries.getConnection();
            PreparedStatement st = null;
            ResultSet rs = null;
            String name;
            String uuid;
            try {
                st = conn.prepareStatement("SELECT `playerName` FROM "+WebAuctionPlus.dataQueries.dbPrefix()+"Players WHERE uuid IS NULL");
                rs = st.executeQuery();
                
                ResultSetMetaData meta = rs.getMetaData();
                int colCount = meta.getColumnCount();
                while (rs.next())
                {
                    for (int col=1; col <= colCount; col++) 
                    {
                        Object value = rs.getObject(col);
                        if (value != null) 
                        {
                            name = value.toString();
                            OfflinePlayer Player = Bukkit.getOfflinePlayer(name);
                            if(Player.hasPlayedBefore()) {
                                uuid = Player.getUniqueId().toString();
                                st = conn.prepareStatement("UPDATE "+WebAuctionPlus.dataQueries.dbPrefix()+"Players SET uuid = '"+uuid+"' WHERE playerName = '"+name+"'");
                                st.execute();
                                WebAuctionPlus.log.info(WebAuctionPlus.logPrefix + "Converting Player Name: " + name + " to uuid: "+ uuid);
                            } else {
                                WebAuctionPlus.log.info(WebAuctionPlus.logPrefix + "Player " + name + " not found.");
                            }
                        }
                    }
                }  
            } catch (SQLException ex) {
                Logger.getLogger(PlayerConvertTask.class.getName()).log(Level.SEVERE, null, ex);
            }
	}


}
