# ultiMinePlugin


> This is the Minecraft plugin developed for the ultimine.net Minecraft Server.

## Server

> ultimine.net was a German Minecraft Server and was online from 24.05.2011 until 22.12.2012.  
> Temporary it was very popular with up to 50 players online the same time.

> The community has more than 2700 registered members.

## Plugin

> This plugin managed the main registration workflow and handels the connection to the Woltlab Burning Board.

### Features

   - Registration check (in wbb3), if a joined user is registered
   - Giving build permissions to (in wbb3) registered users
   - Ingame announcement of News in wbb3
   - Ingame announcement of specific messages
   - ultiBot, which greet joining users and can chat public or private messages
   - Logic to handle financial provision for written posts in wbb3
   - Collecting statistics of online player records
   - Welcome packet for new registered players
   - Warning messages per private message in wbb3, if the free disc space on the server reaches a critical limit
   - Collecting of lag reports from users
   - Penalty point system to improve ban behavior
   - Database loggin of written brawn words
   - Anti-Capslock-Warnings
   - Displaying of team members hierarchy
   - Peaceful Day. No mobs are joining this day
   - Writing of currently online players to a text file including hyperlinks to the player profiles. File is included in the forum

### Technical Features
   - Connection to a MySQL-Database
   - Connection to Woltlab Burning Board 3
   - Developed for a Linux Server
   - Connection to permission plugin Permissions EX
   - Developed for Minecraft 1.3, but still works

### Woltlab Features
   - Check if a given user is registered and activated
   - Read post headlines of specific forums and posting ingame
   - Write PMs

### Release Notes (German)

#### Version 0.4.3 [22.05.2012]
- Anpassung an die aktuellen Bukkit- und PermissionsEX-Bibliotheken.
- Optimierte Fehlerbehandlung: Ein Fehler bei der Initialisierung des UltiBots lässt die restlichen Kernfunktionen des Plugins nicht mehr abschmieren.

#### Version 0.4.2 [24.04.2012]
- Die Personen oder Gruppen, welche von ultiBot eine Warnung als Foren-PM bei geringem Serverspeicherplatz erhalten, können jetzt einfach per Permission-Node definiert werden.

#### Permission-Nodes für die Warnstufen:
- ultimineplugin.ultibot.warning.level1 (greift, wenn weniger als 60 GB frei)
- ultimineplugin.ultibot.warning.level2 (greift, wenn weniger als 40 GB frei)
- ultimineplugin.ultibot.warning.level3 (greift, wenn weniger als 10 GB frei)
- ultimineplugin.ultibot.warning.level4 (greift, wenn weniger als 6 GB frei)

#### Version 0.4.1 [11.03.2012]
- Korrektur eines Fehlers, wodurch nicht im Forum registrierte Spieler nicht in der Onlineliste angezeigt wurden

#### Version 0.4.0 [07.03.2012]
- Anpassungen für Minecraft 1.2, da nicht mehr lauffähig
- Entfernung von altem, nicht mehr benötigtem Code.
#### Version 0.3.5 [21.01.2012]
- News, die älter als 6 Tage sind, werden nicht mehr ingame nicht mehr als neueste News gepostet. 
#### Version 0.3.4 [30.12.2011]
- Beim Verwenden von Befehlen des ultiMinePlugins funktioniert die Rechteüberprüfung jetzt wieder korrekt.
- Breitere Skalierung der Strafpunktfarben.
- Strafpunkte-Toplist zeigt jetzt die Strafpunkteanzahl in unterschiedlich skalierten Farben an.
- Jeden Monat verfällt ein Strafpunkt (-> wird ein Minus-Strafpunkt hinzugefügt)
- ultiBot kann jetzt PMs versenden.
- Joined ein Spieler mit vorhandenen, nicht verfallen Strafpunkten den Server, sendet ultiBot eine PM diesbezüglich an die Teammitglieder.
- Am Peaceful-Day wird jetzt auch das Spawnen von Magma Cubes (Lavaslime) und Blazes (Lohe) verhindert
#### Version 0.3.3 [08.12.2011]
- Da seit dem Update auf 1.0 das Plugin, welches für die Online-Playerliste verantwortlich war, nicht mehr funktionierte, hat diese Funktion jetzt das ultiMinePlugin übernommen.
- Anstatt den reinen Spielernamen werden jetzt Rangkürzel und ein Link zum Forenprofil des Spielers angezeigt.
#### Version 0.3.2 [04.11.2011]
- Anzeige der aktiven oder inaktiven Module des ultiMine-Plugins kann mit dem Befehl /modules abgerufen werden.
- Anpassung des Plugins an das neue Rechtesystem von PermissionsEX
#### Version 0.3.1 [23.10.2011]
- Diverse Codeverbesserungen.
- Erste Implementierung der utliMine-Lotterie.
- ultiBot schreibt PM im Forum an Teammitglieder, wenn Speicherplatz zur Neige geht.
#### Version 0.3.0 [09.10.2011]
- Aufbau einiger Teile modularer.
- Fehlerhandling verbessert.
- Implementierung des ultiBots, der bisher im Auftrag chatten kann und Spieler einmal am Tag begrüßt.
- Impelementierung erster Methoden und Funktionen für eine bis jetzt noch geheime Funktion. 
#### Version 0.2.12 [03.10.2011]
- Alle Funktionen, die das Nachwachsen der Wolle steuern, wurden in ein neues Plugin "RegrowingSheepcoat" ausgelagert.
- Fehler behoben: Bei /sp top wird jetzt die korrekte Anzahl an Strafpunkten angezeigt.
#### Version 0.2.11 [24.09.2011]
- Plugin angepasst an Minecraft 1.8: Das Spawnen von Endermen wird am Peaceful Day jetzt auch verindert.
- Aufgrund von massivem Griefing in LIBERA wird das Wegnehmen und Setzen von Blöcken durch Endermen verhindert.
- Geschorene Schafe bekommen nach 30 Minuten ihr Fell wieder, was Schaffarmen ermöglicht.
- Aus Interessensgründen können mit /shearedsheeps Infos der aktuell geschorenen Schafe abgerufen werden.
#### Version 0.2.10 [18.09.2011]
- Strafpunktsystem vorläufig fertiggestellt. (Vergeben, detailliert anzeigen, Rangliste anzeigen)
- Peacefulday wird zur richtigen Uhrzeit aktiviert.
- Permission-Nodes für Strafpunktsystem implementiert.
- Behebung des Bugs, dass das Datum bei den News falsch angezeigt wurde.
#### Version 0.2.9 [14.09.2011]
- Automatische Aktivierung und Deaktivierung der Peaceful Days.
- Wenn Peaceful Day ist, wird der Spieler beim Einloggen darauf hingewiesen.
- Wird ein CAPS LOCK-Spammer nach der Warnung automatisch gekickt, wird sein deshalb geschriebener Text nicht mehr gesendet.
- Starke Optimierung der Klassen und des Codes
- Einbau einer Managerklasse, die Spielermessagepakete unterschiedlicher Priorität annimmt und das Senden von Infonachrichten an Spieler verwaltet. Sinnvoll für diverse Meldungen, die beim Einloggen eines Spielers gezeigt werden
- Einbau einer Converter-Klasse zum Konvertieren diverser Datentypen
#### Version 0.2.8 [10.09.2011]
- Spieler mit Strafpunkten werden beim Einloggen auf die Anzahl ihrer Strafpunkte hingewiesen.
- Diverse Bugfixes und Codeoptimierungen
- Implementierung der Möglichkeit, mit /teamranks die Bedeutungen der Teamkürzel-Prefixe abzurufen
- Werden die Hinweise, Großschreibung zu unterlassen, nicht ernst genommen, werden nach einer letzten Warnung alle weiteren Spammer gekickt.
#### Version 0.2.7 [20.08.2011]
- Mehr Kreativität bei den Hinweisen, Großschreibung zu unterlassen
- Implementierung einer ersten Strafpunktlogik
#### Version 0.2.6 [14.08.2011]
- Neues Event wird abgefangen: onPlayerChat
- Werden bestimmte Beleidigungen im Chat erwähnt, wird dies zur Identifizierung von Regelverstößen geloggt
- Werden innerhalb von 40 Sekunden mindestens drei Nachrichten in reiner Großschreibung in den Chat geschrieben, wird ein  Hinweis global gesendet, mit der Bitte, das doch zu unterlassen.
#### Version 0.2.5 [13.08.2011]
- ultiMinePlugin ist jetzt an Permissions gebunden.
- Neuladen der Permissions nach Editieren der globalUsers.yml erfolgt ab jetzt über die Permissions-API, nicht über einen Befehl.
- /residencetool ist an den Permissions-Node ultiMinePlugin.residencetool gebunden.
- Lag-Meldefunktion eingebaut.
- /lag ist an den Permissions-Node ultiMinePlugin.lag gebunden.
#### Version 0.2.4 [24.07.2011]
- Auszahlungen des PostProvisionManagers werden in eine KontenTransaktionstabelle geloggt.
- Update-Statements für Kontoänderungen werden nur noch getätigt, wenn sich der neue Kontostand geändert hat.
#### Version 0.2.3 [23.07.2011]
- Mehr Meldungen, wenn sich ein Spieler nach der Registrierung das erste mal einloggt.
- Beim ersten Einloggen nach der Registrierung erhält der Spieler ein Willkommenspaket, bestehend aus einer Steinhacke, Holzaxt, 6 Fackeln und 6 Fischen.
#### Version 0.2.2 [19.07.2011]
- Aufräumen und optimieren des Quellcodes
- Der BetaMessage-Scheduler wurde komplett entfernt, zu broadcastende ultiMine-Messages werden jetzt ebenfalls vom ultiMine-Scheduler getriggert, jede 15. und 45. Minute nach der vollen Stunde.
- Die BroadcastMessage-Funktion wurde in dem Zug aus der Hauptklasse entfernt und in eine eigene Klasse gebaut, welche vom ultiMine-Scheduler deklariert wurde.
- Hinzufügen der Message mit dem Hinweis, für den Server zu voten.
#### Version 0.2.1 [14.07.2011]
- Intervall der News-Meldungen verlängert. Meldung kommt nur noch zu vollen Stunde.
- Es wird nur noch die neueste News angezeigt, nicht mehr die drei neuesten.
#### Version 0.2.0 [09.07.2011]
- Versionssprung auf 0.2.0, weil das ultiMine-Plugin ab jetzt das erste mal aktiv in das Wirtschaftssystem des Spiels eingreift.
- Implementieren von SQL-Funktionalität inkl. einer C#-ähnlichen DataRow-Klasse.
- Umbau und abstrahierung des Codes.
- Implementieren eines ultiMine-Schedulers, der jede halbe Minute getriggert wird und je nach Uhrzeit bestimmte Methoden weiterer Klassen aufruft.
- Implementieren der NewsPoster-Klasse, welche jede halbe Stunde die drei neuesten ultiMine-News broadcastet.
- Implementieren der PostProvisionManager-Klasse, welche jede Nacht um 3:00 Uhr die Belohnung für Forenbeiträge in Spielgeld berechnet und durchführt.
#### Version 0.1.4 [03.07.2011]
- Deaktivieren der Beta-Mail-Funktion
- Ändern oder Herausnehmen der Hinweise à la "Dies ist ein Beta-Server"
#### Version 0.1.3 [28.06.2011]
- Deaktivieren des /help-Befehls, da der Befehl von Essentials auf Permissions achtet und demnach sinnvoller ist.
- Implementieren des Befehls /residencetool, mit dem, wenn noch nicht vorhanden, dem Spieler ein Stück Zucker gegeben wird.
#### Version 0.1.2 [26.06.2011]
- Verkürzen der Zeit, bis der Hinweis zur Mailhinterlegung an Gäste erscheint.
- Ersetzen des /help-Befehls von Essentials. Anzeigen eigens definierter Befehle und deren Beschreibung aus der HelpList.yml
#### Version 0.1.1 [25.06.2011]
- Implementieren der Möglichkeit für nicht an der Closed Beta teilnehmende Gäste, ihre E-Mail-Adresse zu hinterlassen.
#### Version 0.1.0 [24.06.2011]
- Implementieren der Forenmitgliedsüberprüfung.
- Automatische Gewährung von Baurechten bei Forenmitgliedschaft.
#### Version 0.0.2 [30.05.2011]
- Diverse Verbesserungen.
#### Version 0.0.1 [28.05.2011]
- Implementieren des Messagerollings.
 