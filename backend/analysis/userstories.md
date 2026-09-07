# User Stories

Stand: 2026-09-02

## Rollen

| Rolle | Beschreibung |
|---|---|
| **Besucher** | Nicht eingeloggter Nutzer, der öffentliche Infoseiten (z.B. Impressum) einsehen kann |
| **Käufer** | Nutzer, der eine Tüte kaufen will |
| **VereinsmitgliedAdmin** | Nutzer, der Übersichten über Käufe sehen und Änderungen vornehmen kann |
| **Vereinsmitglied** | Nutzer, der QR-Codes scannen kann, um Abholungen zu überprüfen und zu bestätigen |
| **Admin** | Nutzer (z.B. Entwickler) mit allen möglichen Rechten |

## Allgemein (alle registrierten Rollen)

**US-01** – Registrierung
Als Interessent möchte ich mich registrieren (E-Mail + Passwort), damit ich als Käufer Bestellungen aufgeben kann.

**US-02** – Login
Als registrierter Nutzer möchte ich mich einloggen können, damit ich auf die für meine Rolle vorgesehenen Funktionen zugreifen kann.

**US-03** – Passwort vergessen
Als registrierter Nutzer möchte ich mein Passwort zurücksetzen können, wenn ich es vergessen habe, damit ich wieder Zugriff auf meinen Account bekomme, ohne einen neuen Account anzulegen.

**US-04** – E-Mail-Verifizierung
Als Interessent möchte ich meine E-Mail-Adresse bei der Registrierung verifizieren, damit sichergestellt ist, dass mich Bestell- und Abhol-Benachrichtigungen auch erreichen.

## Käufer

**US-05** – Tüten bestellen
Als Käufer möchte ich eine oder mehrere Tüten bestellen, damit ich für das Martinssingen mit Tüten versorgt bin.

**US-06** – Kontodaten einsehen
Als Käufer möchte ich nach der getätigten Bestellung die Kontodaten des Vereins sehen, damit ich die Überweisung tätigen kann.

**US-07** – Überweisung per GiroCode
Als Käufer möchte ich die Überweisung vereinfacht via GiroCode tätigen können, damit ich keine Tippfehler bei IBAN/Verwendungszweck mache und die Überweisung schneller geht.

**US-08** – Bestellbestätigung per E-Mail
Als Käufer möchte ich eine Bestellbestätigung via E-Mail erhalten, damit ich einen Nachweis über meine Bestellung habe.

**US-09** – Abhol-QR-Code per E-Mail
Als Käufer möchte ich bei Zahlungsbestätigung eine E-Mail mit QR-Code zum Abholen erhalten, damit ich die Tüten am Ausgabetag abholen kann.

**US-10** – Eigene Bestellungen einsehen
Als Käufer möchte ich meine eigenen Bestellungen und deren Status einsehen, damit ich weiß, ob meine Zahlung bereits bestätigt wurde und ich die Tüten abholen kann.

**US-11** – Eigene unbezahlte Bestellung stornieren
Als Käufer möchte ich eine eigene, noch nicht bezahlte Bestellung selbst stornieren können, damit ich eine versehentliche oder nicht mehr gewollte Bestellung nicht erst überweisen und dann vom Verein stornieren lassen muss.

## VereinsmitgliedAdmin

**US-12** – Alle Bestellungen einsehen
Als VereinsmitgliedAdmin möchte ich alle Bestellungen einsehen, damit ich einen Überblick über Bestellstatus und -volumen habe.

**US-13** – Bestellung auf bezahlt setzen
Als VereinsmitgliedAdmin möchte ich eine Bestellung auf bezahlt setzen, damit der Käufer die Bestätigung inkl. Abhol-QR-Code erhält.

**US-14** – Bestellung stornieren
Als VereinsmitgliedAdmin möchte ich eine Bestellung stornieren, damit nicht bezahlte oder ungültige Bestellungen aus dem aktiven Bestand entfernt werden.

**US-15** – Bestellung auf abgeschlossen setzen
Als VereinsmitgliedAdmin möchte ich eine Bestellung auf abgeschlossen setzen, damit dokumentiert ist, dass die Tüten abgeholt wurden.

**US-16** – Bestellungen durchsuchen
Als VereinsmitgliedAdmin möchte ich nach einer Bestellung via Bestellnummer, Name oder Datum suchen können, damit ich eine bestimmte Bestellung schnell finde.

**US-17** – Konfiguration von Bestellfrist, Preis und Kontodaten
Als VereinsmitgliedAdmin möchte ich die Bestellfrist, den Preis pro Tüte und die Vereins-Kontodaten (IBAN/BIC) konfigurieren können, damit diese Angaben ohne Programmieraufwand jede Saison aktuell gehalten werden können.

**US-18** – Nicht zugeordnete Zahlungen prüfen
Als VereinsmitgliedAdmin möchte ich nicht automatisch zugeordnete oder mehrdeutige Zahlungseingänge einsehen und manuell einer Bestellung zuordnen können, damit auch Zahlungen mit falschem oder fehlendem Verwendungszweck korrekt verbucht werden.

## Vereinsmitglied

**US-19** – Alle Bestellungen einsehen
Als Vereinsmitglied möchte ich alle Bestellungen einsehen, damit ich weiß, welche Bestellungen zur Abholung anstehen.

**US-20** – Bestellung auf abgeschlossen setzen
Als Vereinsmitglied möchte ich eine Bestellung auf abgeschlossen setzen, damit die Abholung (z.B. nach Scan des Abhol-QR-Codes) dokumentiert ist.

## Admin

**US-21** – Alle Rechte von VereinsmitgliedAdmin und Vereinsmitglied
Als Admin möchte ich alle Funktionen nutzen können, die VereinsmitgliedAdmin und Vereinsmitglied zur Verfügung stehen, damit ich als Entwickler/Betreiber uneingeschränkten Zugriff für Verwaltung und Fehlerbehebung habe.

**US-22** – Nutzerübersicht
Als Admin möchte ich eine Übersicht über alle Nutzer und deren Daten sehen, damit ich Support leisten und Probleme diagnostizieren kann.

**US-23** – Rollenverwaltung
Als Admin möchte ich Nutzerrollen zuweisen, entziehen oder ändern können, damit ich steuern kann, wer welche Rechte im System hat.

## Besucher (nicht eingeloggt)

**US-24** – Impressum einsehen
Als Besucher möchte ich das Impressum der Seite einsehen können, damit ich weiß, wer für die Seite und den Tütenverkauf verantwortlich ist.

**US-25** – Datenschutzerklärung einsehen
Als Besucher möchte ich die Datenschutzerklärung der Seite einsehen können, damit ich weiß, wie meine personenbezogenen Daten (z.B. bei Registrierung und Bestellung) verarbeitet werden.
