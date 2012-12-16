/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Fensterbank.ultiMinePlugin.Objects;
import java.io.Serializable;


/**
 *
 * @author fred
 */
public class UltiBotMemory implements Serializable {
    private int diskSpaceWarningLevel;
    
    public UltiBotMemory() {
        this.diskSpaceWarningLevel = 0;
    }
    
    public void setDiskSpaceWarningLevel(int value) {
        this.diskSpaceWarningLevel = value;
    }
    public int getDiskSpaceWarningLevel() {
        return this.diskSpaceWarningLevel;
    }
    
   
}
