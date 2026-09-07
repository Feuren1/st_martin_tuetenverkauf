# Technische Dokumentation

Stand: 2026-09-02

## Projekt-Setup
- Sprache/Runtime: Java 21
- Framework: Spring Boot 4.1.1, Maven

- **Noch zu ergänzende Dependencies:**
  - QR-Code-Erzeugung: `com.google.zxing:core` + `zxing:javase` (Java-Äquivalent zu den Python/Node-
    QR-Bibliotheken, die vorher als Beispiel genannt wurden)
  - FinTS/HBCI-Anbindung: `HBCI4Java` (etablierte Java-Bibliothek für FinTS)

## Architektur
- **Getrenntes Frontend (React)** und **Backend (Spring Boot REST-API)**

## Authentifizierung & Autorisierung
**Entscheidung:** JWT-basiert

- **Access-Token:** JWT, kurzlebig (~15 Min), Claims: `sub` (userId), `roles`. Wird im Response-Body
  zurückgegeben, React hält es nur im Speicher (State), nicht in `localStorage` (XSS-Schutz).
- **Refresh-Token:** langlebig (~30 Tage), serverseitig gehasht in einer DB-Tabelle gespeichert **und**
  zusätzlich als HttpOnly-, Secure-, SameSite-Cookie an den Client übergeben. Löst das
  JWT-Widerrufsproblem: bei Logout/Passwortänderung/Rollenänderung wird einfach die DB-Zeile
  gelöscht, der Refresh-Token ist damit tot.
- **Passwort-Hashing:** `BCryptPasswordEncoder` (Spring Security), kein Custom-Hashing.
- **Request-Validierung:** `OncePerRequestFilter` liest `Authorization: Bearer <token>`, validiert
  Signatur/Ablauf, setzt Rollen in den `SecurityContext`.
- **Rollenprüfung:** `@PreAuthorize("hasRole('VEREINSMITGLIED_ADMIN')")` je Endpunkt, gemäß
  `analysis/berechtigungen.md`.
- **Endpunkte:** `POST /api/auth/register`, `/login`, `/refresh`, `/logout`.

## Zahlungsabwicklung
- **v1:** Vorkasse per Überweisung. PayPal, Mollie und Wero wurden besprochen, aber zurückgestellt
  (siehe `analysis/planung.md`, Abschnitt "Zurückgestellt").
- **GiroCode (EPC-QR-Code):** wird pro Bestellung serverseitig generiert (IBAN, Betrag,
  Bestellnummer als Verwendungszweck vorausgefüllt)
- **Abhol-QR-Code:** signiert Backend markiert die Bestellung beim ersten Scan als `ABGEHOLT`, ein
  zweiter Scan zeigt eine Warnung statt erneuter Bestätigung.
- **Kontoabgleich:** geplant automatisiert per FinTS (einmal täglich),
  Fallback: manuelle Prüfung durch den Verein im selben Rhythmus. Hängt von der Freischaltung durch
  die Vereins-Sparkasse ab (noch offen).

## Datenmodell
Siehe `design/datenmodell.puml` für das vollständige Diagramm.

## Rollen & Berechtigungen
Fünf Rollen: Besucher, Käufer, Vereinsmitglied, VereinsmitgliedAdmin, Admin.
Vollständige Rechte-Matrix je User Story: siehe `analysis/berechtigungen.md`.

## Rechtliches
Impressum und Datenschutzerklärung sind Pflicht (DSGVO, deutsche Website mit Registrierung/
personenbezogenen Daten) — siehe US-24/US-25 in `analysis/userstories.md`.

## Offene technische Punkte
- Ergebnis der Sparkassen-Nachfrage zu FinTS/HBCI-Zugang fürs Vereinskonto (Leseumfang, Kosten,
  Freischaltungsprozess).
- QR-Signaturmechanismus im Detail (Verwaltung des HMAC-Secrets, genaues Payload-Format).
- GiroCode: BIC-Ermittlung aus IBAN (z.B. über eine BLZ/BIC-Tabelle oder Bank-Verzeichnis-API),
  Encoding-Details (UTF-8 vs. Latin-1, 331-Byte-Limit).
- Mailversand-Provider (SMTP) noch nicht gewählt.
- Konkrete Spring-Security-Konfiguration (Filter, `SecurityConfig`, Login/Refresh-Controller) noch
  nicht implementiert, nur konzeptionell festgelegt.
- Hosting-Details
