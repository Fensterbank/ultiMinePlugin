/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Fensterbank.ultiMinePlugin.Manager;

import Fensterbank.ultiMinePlugin.HelpClasses.Convert;
import Fensterbank.ultiMinePlugin.HelpClasses.Methoden;
import Fensterbank.ultiMinePlugin.Objects.TeamRank;
import Fensterbank.ultiMinePlugin.Objects.DataRow;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 *
 * @author vncuser
 */
public class TeamRanksManager {

    private static final Logger log = Logger.getLogger("Minecraft");
    private ArrayList<TeamRank> cachedRanks;
    private Boolean active;

    public TeamRanksManager() {
        cachedRanks = new ArrayList<TeamRank>();
        if (!reloadRanks()) {
            loadDummyRanks();
            this.active = false;
            log.info("[ultiMine] Fehler beim initialisieren des TeamRanksManager. Dummyranks geladen, Modul deaktiviert.");
        } else {
            this.active = true;
            log.info("[ultiMine] TeamRanksManager initialisiert.");
        }

        
    }

    public final Boolean reloadRanks() {
        cachedRanks.clear();
        ArrayList<DataRow> rows = new ArrayList<DataRow>();
        ArrayList<String> columnsToGet = new ArrayList<String>();


        try {
            columnsToGet.add("Rangcode");
            columnsToGet.add("Bezeichnung");
            columnsToGet.add("Farbcode");
            columnsToGet.add("Level");
            rows = Methoden.sqlQuery("ultiMinePlugin", "SELECT Rangcode, Bezeichnung, Farbcode, Level FROM TeamRanks ORDER BY Level DESC, Bezeichnung ASC", columnsToGet);

            for (DataRow row : rows) {
                cachedRanks.add(Convert.toTeamRank(row));
            }
            log.info("[ultiMine] " + cachedRanks.size() + " Teamranks erfolgreich geladen.");
            return true;
        } catch (Exception ex) {
            log.info("[ultiMine] Fehler beim Laden der Teamranks.");
            return false;
        }
    }

    public final void loadDummyRanks() {
        cachedRanks.clear();
        cachedRanks.add(new TeamRank("A", "Administrator", "&4", 10));
        cachedRanks.add(new TeamRank("M", "Moderator", "&e", 5));
        cachedRanks.add(new TeamRank("S", "Supporter", "&b", 3));        
    }

    public ArrayList<TeamRank> getCachedTeamRanks() {
        return this.cachedRanks;
    }
    
    public Boolean isActive() {
        return this.active;
    }
}
