# API-Kontrakt für das Legacy-Lab

Die API ist absichtlich klein gehalten. Sie dient als Retrofit-Migrationsfläche von `Call<T>`/Callbacks zu `suspend`-Funktionen.

## Base URL

Im Legacy-Projekt steht aktuell:

```java
https://example.invalid/depot-api/
```

Das ist bewusst kein echter Server. Die lauffähige Trainings-App nutzt standardmäßig `FakeDepotApiClient`, damit die Demo ohne Backend funktioniert. `RetrofitDepotApi` bleibt als Legacy-Migrationsfläche im Code und kann über `ServiceLocator.USE_FAKE_API_FOR_TRAINING = false` aktiviert werden.

## Endpunkte

### `GET depots/{depotId}/work-orders`

Lädt Work Orders inklusive Stopps und Checkliste.

Query:

- `changedSince`: optionaler ISO-Zeitpunkt

Response: `List<WorkOrderDto>`

### `GET work-orders/{serverId}`

Lädt Details zu einer Work Order.

Response: `WorkOrderDto`

### `POST work-orders/{serverId}/complete`

Markiert eine Work Order serverseitig als erledigt.

Body: `OutboxDto`

Response: `WorkOrderDto`

### `POST sync/outbox`

Schiebt lokale Outbox-Einträge zum Backend.

Body: `List<OutboxDto>`

Response: `SyncResponseDto`

## Migrationshinweise

Beim Umbau auf Coroutines sollten HTTP-Fehler, Netzwerkfehler und fachliche Ablehnungen nicht mehr alle als generisches `Throwable` in der UI landen. Eine mögliche Zielstruktur:

```kotlin
sealed interface RemoteResult<out T> {
    data class Success<T>(val value: T) : RemoteResult<T>
    data class HttpError(val code: Int, val body: String?) : RemoteResult<Nothing>
    data class NetworkError(val cause: IOException) : RemoteResult<Nothing>
    data class MappingError(val cause: Throwable) : RemoteResult<Nothing>
}
```

Im ersten Migrationsschritt reicht aber eine kleine Adapter-Schicht, damit nicht das gesamte Repository gleichzeitig umgebaut werden muss.
