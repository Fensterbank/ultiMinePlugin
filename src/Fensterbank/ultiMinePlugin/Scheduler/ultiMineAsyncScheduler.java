/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Fensterbank.ultiMinePlugin.Scheduler;
import java.util.Calendar;
//import java.util.logging.Logger;
import java.io.File;
import Fensterbank.ultiMinePlugin.Objects.UltiBot;
import Fensterbank.ultiMinePlugin.Manager.PostProvisionManager;

// Imports für PermissionsEx
import ru.tehkode.permissions.PermissionManager;
/**
 *
 * @author fred
 */
public class ultiMineAsyncScheduler implements Runnable {
    
  //  private Logger log = Logger.getLogger("Minecraft");
    private int lastMessageMinuteCheck;
    private UltiBot ultiBot;
    PostProvisionManager postProvisionManager;

    public ultiMineAsyncScheduler(UltiBot currBot, PermissionManager permissions) {        
        lastMessageMinuteCheck = -1;
        this.ultiBot = currBot;
        this.postProvisionManager = new PostProvisionManager(permissions);
    }
    
    private int getFreeSpaceInGB() {
        File fileclass = new File(".");        
        return (int) (fileclass.getUsableSpace() / 1024 / 1024 / 1024);        
    }

    @Override
    public void run() {
        if (ultiBot != null) {
            Calendar myCal = Calendar.getInstance();
            int minute = myCal.get(Calendar.MINUTE);
            int stunde = myCal.get(Calendar.HOUR_OF_DAY);

            if (lastMessageMinuteCheck != minute) {
                lastMessageMinuteCheck = minute;

                ultiBot.checkWarningForDiskSpace(this.getFreeSpaceInGB());            

                switch (minute) {                    
                    case 0:
                        if (stunde == 3) {
                            postProvisionManager.calculateProvision();
                        }                                   
                        break;
                }
            }
        }
    }
}
