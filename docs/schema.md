# Legacy-Datenbankschema

Dieses Schema ist die Grundlage für die spätere Room-Migration. Die Spaltennamen sind absichtlich im alten Datenbankstil gehalten und dürfen bei der ersten Migration nicht versehentlich geändert werden.

## Datenbank

- Name: `depotflow-legacy.db`
- Version: `4`
- Helper: `data/db/LegacyDatabase.java`

## Tabelle `WORK_ORDER`

| Spalte | Typ | Bedeutung | Room-Migrationshinweis |
|---|---:|---|---|
| `_id` | INTEGER PK AUTOINCREMENT | lokale ID | als `@PrimaryKey(autoGenerate = true)` abbilden |
| `SERVER_ID` | TEXT UNIQUE NOT NULL | Backend-ID | stabiler fachlicher Schlüssel |
| `EXTERNAL_NUMBER` | TEXT NOT NULL | sichtbare Auftragsnummer | nicht umbenennen |
| `TITLE` | TEXT NOT NULL | Titel | Pflichtfeld |
| `CUSTOMER_NAME` | TEXT NOT NULL | Kunde/Standort | Pflichtfeld |
| `STATUS` | TEXT NOT NULL | `new`, `accepted`, `in_progress`, ... | stabiler Converter, nicht `enum.name()` |
| `PRIORITY` | INTEGER NOT NULL | Sortierung | Default prüfen |
| `DUE_AT` | TEXT | ISO-Zeitpunkt | später evtl. `Instant` + Converter |
| `UPDATED_AT` | TEXT | Serverstand | Konfliktlogik beachten |
| `ASSIGNED_USER` | TEXT | Disponent/Techniker | kam in DB-Version 2 dazu |
| `SYNC_STATUS` | TEXT NOT NULL | `clean`, `dirty`, `syncing`, `failed` | kam in DB-Version 3 dazu |
| `LAST_ERROR` | TEXT | letzter Sync-Fehler | nullable |
| `DIRTY` | INTEGER NOT NULL | lokale Änderung | Boolean-Konvention 0/1 |

## Tabelle `STOP`

| Spalte | Typ | Bedeutung |
|---|---:|---|
| `_id` | INTEGER PK AUTOINCREMENT | lokale ID |
| `WORK_ORDER_ID` | INTEGER NOT NULL | Referenz auf `WORK_ORDER._id` |
| `REMOTE_ID` | TEXT | Backend-ID |
| `SEQUENCE_NO` | INTEGER NOT NULL | Reihenfolge |
| `TYPE` | TEXT NOT NULL | `pickup`, `delivery`, `service`, `return` |
| `NAME` | TEXT NOT NULL | Anzeigename |
| `ADDRESS` | TEXT | Adresse |
| `LATITUDE` | REAL | Koordinate |
| `LONGITUDE` | REAL | Koordinate |
| `STATUS` | TEXT NOT NULL | Legacy-String |
| `ARRIVAL_WINDOW_FROM` | TEXT | Zeitfenster Start |
| `ARRIVAL_WINDOW_TO` | TEXT | Zeitfenster Ende |

## Tabelle `CHECKLIST_ITEM`

| Spalte | Typ | Bedeutung |
|---|---:|---|
| `_id` | INTEGER PK AUTOINCREMENT | lokale ID |
| `WORK_ORDER_ID` | INTEGER NOT NULL | Referenz auf `WORK_ORDER._id` |
| `REMOTE_ID` | TEXT | Backend-ID |
| `LABEL` | TEXT NOT NULL | Aufgabe |
| `IS_CHECKED` | INTEGER NOT NULL | Boolean 0/1 |
| `IS_MANDATORY` | INTEGER NOT NULL | Boolean 0/1 |
| `NOTE` | TEXT | optionale Notiz |

## Tabelle `SYNC_OUTBOX`

| Spalte | Typ | Bedeutung |
|---|---:|---|
| `_id` | INTEGER PK AUTOINCREMENT | lokale ID |
| `AGGREGATE_TYPE` | TEXT NOT NULL | z. B. `WORK_ORDER` |
| `AGGREGATE_ID` | TEXT NOT NULL | Server-ID des Aggregats |
| `OPERATION` | TEXT NOT NULL | z. B. `COMPLETE` |
| `PAYLOAD` | TEXT NOT NULL | JSON-Payload |
| `CREATED_AT` | TEXT NOT NULL | lokaler Zeitpunkt |
| `ATTEMPTS` | INTEGER NOT NULL | Retry-Zähler |
| `LAST_ERROR` | TEXT | letzter Fehler |

## Migrationstest-Fragen

- Bleiben alle Spaltennamen exakt erhalten?
- Sind Boolean-Spalten weiterhin `0/1`?
- Werden Statuswerte stabil konvertiert?
- Bleiben Outbox-Einträge nach Migration erhalten?
- Werden Child-Tabellen korrekt über `WORK_ORDER_ID` verbunden?
- Muss `ON DELETE CASCADE` neu eingeführt werden oder wäre das ein Verhaltenswechsel?
