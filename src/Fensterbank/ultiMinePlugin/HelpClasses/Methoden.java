/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Fensterbank.ultiMinePlugin.HelpClasses;

import Fensterbank.ultiMinePlugin.Objects.DataRow;
import org.bukkit.entity.Player;
import java.io.*;
import org.bukkit.ChatColor;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.sql.*;
import java.util.Map;
import org.yaml.snakeyaml.*;
import org.bukkit.command.CommandSender;
import java.util.Calendar;
import java.util.Date;

// System-Monitoring
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

/**
 *
 * @author vncuser
 */
public class Methoden {

    
    public static ArrayList<DataRow> sqlQuery(String database, String statement, ArrayList<String> columnsToGet) throws Exception {
        Connection connect = null;
        String treiber = null, DbUrl = null;

        //*** "Name des Datenbanktreibers eingeben
        treiber = "com.mysql.jdbc.Driver";
        //*** "Url der Databank eingeben *********
        //*** Server : linux
        //*** Service-Nummer : 3306
        //*** Bezeichnung der Datenbank : test1
        DbUrl = "jdbc:mysql://localhost:3306/" + database;

        //*** Treiber laden ***********************************
        try {
            Class.forName(treiber).newInstance();
            //*** Verbindung aufnehmen:    ************************
            //*** Der User peter mit Kennwort mysql möcht was wissen
            Connection cn = DriverManager.getConnection(DbUrl, "minecraftserver", "***");

            Statement st = cn.createStatement();
            ArrayList<DataRow> rows = new ArrayList<DataRow>();
            if (columnsToGet != null) {
                ResultSet rs = st.executeQuery(statement);

                DataRow row;
                while (rs.next()) {
                    row = new DataRow();
                    for (String columnName : columnsToGet) {
                        row.addItem(columnName, rs.getObject(columnName));
                    }
                    rows.add(row);
                }

                rs.close();
            } else {
                String[] commands = statement.split(";");
                if (commands.length>1) {
                    for (String s : commands) {
                        st.addBatch(s);
                    }
                    st.executeBatch();
                } else {
                    st.executeUpdate(statement);
                    
                }
            }
            st.close();
            cn.close();

            return rows;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public static void saveLagFeedback(Player player, int onlineCount) {
        String playerName = player.getName();
        String location = String.valueOf(Math.round(player.getLocation().getX() * 100. ) / 100.) + "," + String.valueOf(Math.round(player.getLocation().getY() * 100. ) / 100.) + "," + String.valueOf(Math.round(player.getLocation().getZ() * 100. ) / 100.);
        OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean(); 
        String processLines = "";
                
        try {
            String line;
            Process p = Runtime.getRuntime().exec("ps -e");
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((line = input.readLine()) != null) {
                processLines += line + "\n";
            }
            sqlQuery("ultiMinePlugin", "INSERT INTO lagFeedback ( username , onlineCount, location, serverload, pslist ) VALUES  ( '" + playerName + "', '" + onlineCount + "', '" + location + "', '" + operatingSystemMXBean.getSystemLoadAverage() + "', '" + processLines + "')", null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * <p>Checks if two dates are on the same day ignoring time.</p>
     * @param date1  the first date, not altered, not null
     * @param date2  the second date, not altered, not null
     * @return true if they represent the same day
     * @throws IllegalArgumentException if either date is <code>null</code>
     */
    public static boolean isSameDay(java.util.Date date1, java.util.Date date2) {
        if (date1 == null || date2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return isSameDay(cal1, cal2);
    }
    
    /**
     * <p>Checks if two calendars represent the same day ignoring time.</p>
     * @param cal1  the first calendar, not altered, not null
     * @param cal2  the second calendar, not altered, not null
     * @return true if they represent the same day
     * @throws IllegalArgumentException if either calendar is <code>null</code>
     */
    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
    }
    
    /**
     * <p>Checks if a date is today.</p>
     * @param date the date, not altered, not null.
     * @return true if the date is today.
     * @throws IllegalArgumentException if the date is <code>null</code>
     */
    public static boolean isToday(java.util.Date date) {
        return isSameDay(date, Calendar.getInstance().getTime());
    }
    
    public static long getTimeDayDifferenceBetween(Date date1, Date date2) {        
        return ((date1.getTime() - date2.getTime()) / (1000 * 60 * 60 * 24));        
    }
    
    /**
     * <p>Checks if a calendar date is today.</p>
     * @param cal  the calendar, not altered, not null
     * @return true if cal date is today
     * @throws IllegalArgumentException if the calendar is <code>null</code>
     */
    public static boolean isToday(Calendar cal) {
        return isSameDay(cal, Calendar.getInstance());
    }
    
    public static String removeDangerChars(String text) {
        text.replace('\'', '°');
        return text;
    }
    
    public static void printHelp(CommandSender sender, int pageNr) {
        try {
            InputStream input;
            Yaml yaml = new Yaml();


            input = new FileInputStream(new File("plugins/ultiMine/HelpList.yml"));
            LinkedHashMap<String, String> document = (LinkedHashMap<String, String>) yaml.load(input);

            int pageAmount = (int) java.lang.Math.ceil((double) document.size() / 8);
            if (pageNr > pageAmount) {
                pageNr = pageAmount;
            }

            int startKey = ((pageNr - 1) * 8) + 1;
            int i = 0;
            int u = 0;
            sender.sendMessage((ChatColor.GOLD + "Liste der Commands - Seite " + pageNr + " / " + pageAmount));
            for (Map.Entry<String, String> e : document.entrySet()) {
                i++;
                if (i >= startKey && u < 8) {
                    u++;
                    sender.sendMessage(commandName(e.getKey()) + e.getValue());
                }
            }
        } catch (Exception ex) {
            sender.sendMessage(ChatColor.RED + "Sorry, da ging etwas schief...");
            ex.printStackTrace();
        }
    }

    private static String commandName(String name) {
        return ChatColor.DARK_GREEN + name + ": " + ChatColor.WHITE;
    }

    public static Boolean registerBetaMailAddress(Player player, String email) throws Exception {
        InputStream input;
        Yaml yaml = new Yaml();
        Boolean returnState;

        input = new FileInputStream(new File("plugins/ultiMine/BetaEmailNotifications.yml"));
        LinkedHashMap document = (LinkedHashMap) yaml.load(input);

        if (document == null) {
            document = new LinkedHashMap();
        }

        if (!document.containsKey(player.getName())) {
            document.put(player.getName(), email);
            returnState = true;
        } else {
            document.remove(player.getName());
            document.put(player.getName(), email);
            returnState = false;
        }

        yaml.dump(document, new FileWriter(new File("plugins/ultiMine/BetaEmailNotifications.yml")));
        return returnState;
    }
}
