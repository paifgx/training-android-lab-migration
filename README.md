# DepotFlow Legacy Java Migration Lab

Dieses Projekt ist ein bewusst realistisches Legacy-Android-Projekt für die AMCON-Android-Schulung. Es ist **kein** Best-Practice-Template. Es enthält absichtlich typische Migrationsprobleme, die in einem echten Java-Android-Projekt vor einer Kotlin-/Room-/Coroutines-Migration auftauchen.

## Ziel des Labs

Die Teilnehmer sollen nicht an einem Toy-Beispiel üben, sondern an einem gewachsenen Projekt mit:

- Java-only Codebasis
- XML-Layouts und klassischen Activities
- globalem `ServiceLocator`
- Retrofit mit Callback-API
- GreenDAO-nahem Entity-/DAO-Stil
- lokaler SQLite-Persistenz mit altem Schema
- verschachtelter Repository-Logik
- Offline-Outbox für ausstehende Änderungen
- bewusst gemischten Verantwortlichkeiten

Die gemeinsame Migration kann Schritt für Schritt erfolgen:

1. Projekt verstehen und Risiken erfassen
2. ausgewählte Java-Klassen nach Kotlin migrieren
3. Retrofit-Callbacks zu `suspend`/Coroutines umbauen
4. GreenDAO-/SQLite-Schema sauber nach Room übertragen
5. Repository-API auf `Flow`/`StateFlow` vorbereiten
6. globalen `ServiceLocator` durch Hilt ersetzen
7. Migrationstests und Datenkompatibilität absichern

## Domäne

**DepotFlow** simuliert eine kleine Depot-/Außendienst-App:

- Work Orders werden aus einem Backend geladen.
- Stopps und Checklisten werden lokal gespeichert.
- Offline abgeschlossene Aufträge landen in einer Outbox.
- Ein späterer Sync schiebt lokale Änderungen zum Backend.

Die Domäne ist bewusst neutral und nicht AMCON-spezifisch.

## Start

Importiere den Ordner in Android Studio:

```text
trainings/amcon-android-2026/projects/legacy-java-migration-lab
```

Empfohlene Umgebung:

- JDK 17
- Android Studio Hedgehog oder neuer
- Android Gradle Plugin 8.5.2
- compileSdk 35
- installierte Android Build Tools 36.0.0 oder Android Studio mit passender SDK-Verwaltung

Das Projekt enthält einen Gradle Wrapper und ist als **vorher lauffähige Legacy-App** gedacht:

```bash
./gradlew :app:assembleDebug
./gradlew :app:installDebug
```

Für lokale Unit Tests:

```bash
./gradlew :app:testDebugUnitTest
```

Die App verwendet standardmäßig `FakeDepotApiClient`, damit Listen-Refresh, Detailansicht, Offline-Abschluss und Outbox-Sync ohne echten Backend-Server demonstrierbar sind. Die Retrofit-Callback-Implementierung `RetrofitDepotApi` bleibt trotzdem im Code und ist bewusst Teil der späteren Migrationsübung. Umschalten kann man in `core/ServiceLocator.java` über `USE_FAKE_API_FOR_TRAINING`.

## Wichtiger Trainingshinweis

Das Projekt benutzt die GreenDAO-Runtime und GreenDAO-Annotationen, aber keinen alten Generator-Workflow. Die DAO-Klassen sind bewusst eingecheckter Legacy-Code. Das macht das Lab stabiler für moderne Android-Gradle-Versionen und trotzdem realistisch genug für eine GreenDAO→Room-Migration.

## Wo anfangen?

- `MIGRATION_ROADMAP.md` erklärt die geplante Schrittfolge.
- `KNOWN_LEGACY_DEBT.md` beschreibt die absichtlich eingebauten Schwachstellen.
- `tasks/` enthält Aufgabenblätter für die Teilnehmer.
- `docs/schema.md` beschreibt das bestehende Datenbankschema.

## Nicht sofort modernisieren

Für das Training ist wichtig: Zuerst verstehen, dann ändern. Bitte nicht direkt alles nach Kotlin, Room und Hilt umbauen. Die Altstruktur ist der Lerngegenstand.
