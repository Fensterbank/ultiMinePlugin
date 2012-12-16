/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Fensterbank.ultiMinePlugin.Objects;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 *
 * @author vncuser
 */
public class News {    
    private String title;
    private Date dateTime;
    
    
    public News(long timestamp, String title) {        
        this.dateTime = new Date(timestamp * 1000L); 
        this.title = title;
    }
    
    @Override
    public String toString() {
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy, HH:mm");        
        return df.format(this.dateTime) + " Uhr - " + this.title;      
    }
    
    public Date getDate() {
        return this.dateTime;
    }
}
