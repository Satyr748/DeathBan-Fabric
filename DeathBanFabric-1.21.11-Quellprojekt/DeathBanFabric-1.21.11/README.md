# DeathBan Fabric – Minecraft 1.21.11

Serverseitige Fabric-Mod (Java 21). Eine Admin-Ausnahme ist **nicht** enthalten.

## Funktionen
- DeathBan bei Spielertod
- Dauer in `s`, `m`, `h`, `d` oder `permanent`
- JSON-Konfiguration und gespeicherte UUID-Bans
- OP-Befehle: `/deathban on`, `/deathban off`, `/deathban settime 24h`, `/deathban status`, `/deathban unban <uuid>`

## Bauen
Mit Java 21 und installiertem Gradle:
`gradle build`

Die fertige JAR liegt danach in `build/libs/`.

## Installation
Fabric Loader und Fabric API für Minecraft 1.21.11 auf dem Server installieren. Danach die DeathBan-JAR nach `mods/` legen und den Server starten.

## Konfiguration
Beim ersten Start wird `config/deathban.json` erzeugt. Beispiel:

```json
{
  "enabled": true,
  "duration": "24h",
  "hardcoreHeartsEnabled": false,
  "resourcePackUrl": "",
  "bans": {}
}
```

`duration`: `30m`, `12h`, `3d` oder `permanent`.

## Wichtig zum Texturepack
Diese erste Projektfassung enthält noch **keine Herz-Texturen** und lädt kein Pack automatisch herunter. `hardcoreHeartsEnabled` und `resourcePackUrl` sind vorbereitete Konfigurationsfelder. Für ein echtes Server-Resourcepack muss eine ZIP-Datei auf einer öffentlich erreichbaren HTTPS-Adresse liegen und in der `server.properties` als `resource-pack` eingetragen werden. Ein beliebiges Texturepack kann nicht ohne dessen ZIP-Datei in die Mod eingebaut werden.

## Hinweis
Dies ist ein Quellcode-Projekt, keine bereits kompilierte und getestete JAR. Vor dem Einsatz auf einem öffentlichen Server bitte auf einem Testserver prüfen und Backups anlegen.
