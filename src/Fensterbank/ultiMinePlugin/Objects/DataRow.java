/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Fensterbank.ultiMinePlugin.Objects;

import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Logger;

/**
 *
 * @author vncuser
 */
public class DataRow {

    ArrayList<Object> items;
    ArrayList<String> itemTitles;
    private Logger log = Logger.getLogger("Minecraft");

    public DataRow() {
        this.items = new ArrayList<Object>();
        this.itemTitles = new ArrayList<String>();
    }

    public void addItem(Object i) {
        items.add(i);
        itemTitles.add("");
    }

    public void addItem(String ItemTitle, Object i) {
        items.add(i);
        itemTitles.add(ItemTitle);
    }

    public Object get(int i) {
        return items.get(i);
    }

    public Object get(String ItemTitle) {
        return items.get(itemTitles.indexOf(ItemTitle));
    }

    public String getString(int i) {
        return (String) items.get(i);
    }

    public String getString(String ItemTitle) {
        try {
            return (String) items.get(itemTitles.indexOf(ItemTitle));
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public Date getDate(int i) {
        return (Date) items.get(i);
    }

    public Date getDate(String ItemTitle) {
        return (Date) items.get(itemTitles.indexOf(ItemTitle));
    }


    public Long getLong(String ItemTitle) {
        try {                   
            Long var = new Long(getInt(ItemTitle));
            return var;
        } catch (Exception ex) {
            try {
                Long var2 = (Long)items.get(itemTitles.indexOf(ItemTitle));
                return var2;
            } catch (Exception e) {
                e.printStackTrace();
                return -1l;
            }
        }
    }

    public int getInt(String ItemTitle) throws Exception {
        try {
            int zahl = (Integer)items.get(itemTitles.indexOf(ItemTitle));
            return zahl;
        } catch (Exception ex) {
            throw ex;            
        }
    }
    
    public double getDouble(String ItemTitle) {
        try {
            return (Double)items.get(itemTitles.indexOf(ItemTitle));            
        } catch (Exception ex) {
            ex.printStackTrace();
            return -1;
        }
    }
    
}
