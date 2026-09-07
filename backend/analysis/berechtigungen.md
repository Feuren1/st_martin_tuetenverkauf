# Berechtigungsmatrix

Stand: 2026-09-02
Abgeleitet aus `analysis/userstories.md`. Legende: ✓ = Zugriff erlaubt, – = kein Zugriff.

| US | Aktion | Besucher | Käufer | Vereinsmitglied | VereinsmitgliedAdmin | Admin |
|---|---|:---:|:---:|:---:|:---:|:---:|
| US-01 | Registrieren | ✓ | – | – | – | – |
| US-02 | Login | – | ✓ | ✓ | ✓ | ✓ |
| US-03 | Passwort zurücksetzen | – | ✓ | ✓ | ✓ | ✓ |
| US-04 | E-Mail-Adresse verifizieren | – | ✓ | – | – | – |
| US-05 | Tüten bestellen | – | ✓ | – | – | – |
| US-06 | Kontodaten des Vereins einsehen | – | ✓ | – | – | – |
| US-07 | Überweisung per GiroCode tätigen | – | ✓ | – | – | – |
| US-08 | Bestellbestätigung per E-Mail erhalten | – | ✓ | – | – | – |
| US-09 | Abhol-QR-Code per E-Mail erhalten | – | ✓ | – | – | – |
| US-10 | Eigene Bestellung(en) einsehen | – | ✓ | – | – | – |
| US-11 | Eigene unbezahlte Bestellung stornieren | – | ✓ | – | – | – |
| US-12 | Alle Bestellungen einsehen | – | – | – | ✓ | ✓ |
| US-13 | Bestellung auf bezahlt setzen | – | – | – | ✓ | ✓ |
| US-14 | Bestellung stornieren | – | – | – | ✓ | ✓ |
| US-15 | Bestellung auf abgeschlossen setzen | – | – | – | ✓ | ✓ |
| US-16 | Bestellungen durchsuchen (Nr./Name/Datum) | – | – | – | ✓ | ✓ |
| US-17 | Bestellfrist, Preis, Kontodaten konfigurieren | – | – | – | ✓ | ✓ |
| US-18 | Nicht zugeordnete Zahlungen prüfen | – | – | – | ✓ | ✓ |
| US-19 | Alle Bestellungen einsehen (Ausgabe) | – | – | ✓ | ✓ (via US-12) | ✓ |
| US-20 | Bestellung auf abgeschlossen setzen (Scan) | – | – | ✓ | ✓ (via US-15) | ✓ |
| US-21 | Alle Rechte von VereinsmitgliedAdmin & Vereinsmitglied | – | – | – | – | ✓ |
| US-22 | Nutzerübersicht (alle Nutzer & Daten) einsehen | – | – | – | – | ✓ |
| US-23 | Nutzerrollen zuweisen/entziehen/ändern | – | – | – | – | ✓ |
| US-24 | Impressum einsehen | ✓ | ✓ | ✓ | ✓ | ✓ |
| US-25 | Datenschutzerklärung einsehen | ✓ | ✓ | ✓ | ✓ | ✓ |

## Rollenhierarchie

- **Admin** hat alle Rechte
- **VereinsmitgliedAdmin** ⊇ **Vereinsmitglied**: VereinsmitgliedAdmin kann alles, was Vereinsmitglied kann plus bezahlt setzen, stornieren, durchsuchen, konfigurieren, Zahlungsabgleich prüfen.
- **Vereinsmitglied** hat ausschließlich Lese- und Abschließen-Rechte für Bestellungen
- **Käufer**-Rechte sind ausschließlich auf die eigenen Bestellungen beschränkt
- **Besucher** (nicht eingeloggt) hat nur Zugriff auf öffentliche Seiten
