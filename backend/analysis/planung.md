# Konzept: Tütenbestellung & Zahlungsabwicklung
Stand: 2026-09-02
 
## Kontext
St. Martin Verein Viersen braucht eine Website zum Vorbestellen von St.Martins-Tüten.
Budget = Absolutes minimum (nur server hosting kosten)

## Ablauf

1. Nutzer registriert sich.
2. Login 
3. Wählt Tütenanzahl, bestellt (nur innerhalb der Bestellfrist möglich).
4. Bestellung bekommt eine **eindeutige Bestellnummer** als Verwendungszweck, Status `ausstehend`.
   Bestätigungsseite/-mail zeigt den GiroCode (IBAN + Betrag + Bestellnummer vorausgefüllt).
5. Nutzer scannt GiroCode mit Banking-App und bestätigt die Überweisung per TAN | oder. → sieht Vereins-IBAN und tätigt 
   Überweisung Manuell.
6. Einmal täglich: Kontoabgleich — per FinTS automatisiert (geplant, hängt von Sparkassen-
   Freischaltung ab) oder manuell durch den Verein (Fallback) — ordnet eingegangene Zahlungen
   per Bestellnummer den Bestellungen zu.
7. Bei Treffer: Bestellung wird als `bezahlt` markiert (automatisch per FinTS-Abgleich, oder
   manuell im Admin-Panel per Klick beim Fallback).
8. Automatisch: Bestätigungsmail mit **signiertem Abhol-QR-Code** z.B. Bestellnummer + HMAC-Signatur mit
   Server-Secret).
9. Am Ausgabetag: Verein scannt Abhol-QR-Code → Backend prüft Signatur, zeigt Bestelldaten an, markiert bei erstem Scan 
   als `abgeholt`. Erneutes Scannen desselben Codes zeigt eine Warnung ("bereits abgeholt am ...") statt erneuter
   Bestätigung — verhindert Mehrfacheinlösung z.B. via Screenshot. 
10. Tüten werden ausgegeben.
    Status-Modell pro Bestellung: `ausstehend` → `bezahlt` → `abgeholt`.

## Entscheidungen
 
**Zahlungsart (v1):** Vorkasse per Überweisung. Verein ist bei einer Sparkasse.
 
**GiroCode (EPC-QR-Code):** Auf der "Bestellung eingegangen, bitte
überweisen"-Seite und in der Bestellbestätigungsmail wird pro Bestellung ein GiroCode angezeigt,
der IBAN, Betrag und Verwendungszweck (Bestellnummer) vorausgefüllt enthält. Nutzer scannen ihn
mit der Banking-App und müssen nur noch mit TAN bestätigen — vermeidet Zahlendreher und
fehlenden/falschen Verwendungszweck.
 
**Kontoabgleich — geplant per FinTS, Fallback manuell:** 
**einmal täglich** wird das Konto automatisiert abgefragt, eingegangene Überweisungen
werden per Verwendungszweck (Bestellnummer) automatisch den Bestellungen zugeordnet, und bei
Treffer gehen automatisch Bestätigungsmail + Zahlungs-QR-Code-Nachfolger (der Abhol-QR-Code) raus.
Falls die Sparkasse das für das Vereinskonto nicht anbietet oder es zu aufwändig ist der Verein schaut manuell in den 
Kontoauszug und bestätigt die Treffer im Admin-Panel
 
**Bestellfrist:** Ja, vor dem 11.11.
**Mengenbegrenzung/Kontingent:** Nein, keine Obergrenze geplant.
 
## Admin-Panel (Anforderungen)
- Eigene Admin-Accounts, getrennte Rolle, nicht öffentlich registrierbar.
- Liste offener (unbezahlter) und bezahlter Bestellungen.
- Suche nach Bestellnummer/Name.
- Button "als bezahlt markieren" bleibt in jedem Fall vorhanden (manueller Fallback/Korrektur,
  auch wenn der FinTS-Abgleich klappt — z.B. für Sonderfälle wie falscher Verwendungszweck).
- QR-Scan-Ansicht für die Ausgabe vor Ort (liest den Abhol-QR-Code).
- Falls FinTS klappt: täglicher automatischer Abgleichs-Job braucht eine Stelle, an der man
  Fehlschläge/nicht zugeordnete Zahlungen einsehen kann (z.B. Betrag stimmt nicht, Verwendungs-
  zweck nicht erkannt) — diese landen zur manuellen Prüfung im Admin-Panel.
## Rechtliches (nicht Payment, aber Pflicht)
Da personenbezogene Daten inkl. Passwort-Hashes gespeichert werden: Impressum und
Datenschutzerklärung
 
## Mögliche Extension points
- PayPal-Checkout als zusätzliche Zahlart (Business-Konto + Vereinsverifizierung nötig, ca.
  1,5% + 0,35€ Gebühr pro Transaktion national, Stand prüfen).
- Kombianbieter wie Mollie (mehrere Zahlarten über eine API/Webhook) — für dieses Volumen als
  unnötiger Overhead eingeschätzt.
- 
## Offene technische Detailfragen für die nächste Session
- Ergebnis der Sparkassen-Nachfrage zu FinTS/HBCI-Zugang fürs Vereinskonto (Leseumfang, Kosten,
  Freischaltungsprozess) — entscheidet, ob Schritt 6/7 automatisch oder manuell läuft.
- QR-Signaturmechanismus konkret für den Abhol-QR-Code (z.B. HMAC-Secret, welches Format im QR).
- GiroCode-Implementierung: welche Library/Sprache, BIC-Ermittlung aus IBAN (z.B. per
  Bankleitzahlen-Datei oder Bank-API), Encoding-Details (UTF-8 vs. Latin-1, 331-Byte-Limit).
- Mailversand für Registrierung/Passwort-Reset/Bestellbestätigung (SMTP-Provider?).
- Konkretes Datenmodell (Tabellen: User, Bestellung, Admin, ggf. Bestellfrist-Konfiguration).

