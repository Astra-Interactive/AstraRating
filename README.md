<p align="center">
  <img src="assets/logo.png" style="border-radius: 6px" alt="AstraRating logo" width="200"/>
</p>

<h1 align="center">AstraRating</h1>
<p align="center"><strong>Player-driven reputation system for Minecraft servers.</strong></p>
<p align="center">Let players like and dislike each other, browse reputations in a GUI, and rank the whole server.</p>

---

## Supported version

- **Loader:** Paper (Bukkit/Spigot-based)
- **Minecraft:** 1.13+ (`api-version: 1.13`)
- **Java:** 21

> AstraRating registers its commands through Paper's modern Brigadier command API, so a recent Paper
> build is recommended.

---

## Features

- [x] **Player ratings** — players give each other a `+1` (like) or `-1` (dislike) together with a short message explaining why.
- [x] **Ratings leaderboard GUI** — an in-game menu listing every rated player, sortable and paginated.
- [x] **Per-player reviews GUI** — open any player's page to read every rating and message they received.
- [x] **Daily vote limits** — cap how many votes a player may cast per day, and how many times they may vote for the same player. Limits are configurable and can be raised per group via permissions.
- [x] **Moderation** — staff can delete individual ratings/reports straight from the GUI.
- [x] **Message validation** — enforce a minimum and maximum message length and trim long messages in the GUI.
- [x] **Kill penalty (optional)** — automatically change a killer's rating when they kill a positively-rated player.
- [x] **PlaceholderAPI support** — expose a player's rating value and a configurable rating color.
- [x] **Multiple databases** — H2 (default), SQLite, MySQL, or MariaDB.
- [x] **Live reload** — reload configuration and even switch databases at runtime, no restart required.
- [x] bStats metrics.

---

## Installation

1. Download the latest `AstraRating.jar` from [Releases](https://github.com/Astra-Interactive/AstraRating/releases).
2. Drop it into your server's `plugins/` folder.
3. Start the server once to generate the default configuration.
4. Edit the files in `plugins/AstraRating/` and run `/aratingreload`.

The JDBC drivers (H2, SQLite, MySQL, MariaDB) are downloaded automatically as plugin libraries on first start.

---

## Commands & permissions

| Command                                                    | Description                                          | Permission            |
|:-----------------------------------------------------------|:-----------------------------------------------------|:----------------------|
| `/arating`                                                 | Open the ratings leaderboard GUI                     | —                     |
| `/arating rating`                                          | Open the ratings leaderboard GUI                     | —                     |
| `/arating rating <like\|dislike\|+\|-> <player> <message>` | Give a `+1` / `-1` rating to a player with a message | `astra_rating.vote`   |
| `/arating player <player>`                                 | Open the reviews GUI for a specific player           | —                     |
| `/aratingreload`                                           | Reload configuration and translations                | `astra_rating.reload` |

### Capability permissions

These permissions are not tied to a single command — they grant abilities or raise limits:

| Permission                                    | Effect                                                                               |
|:----------------------------------------------|:-------------------------------------------------------------------------------------|
| `delete_report.vote`                          | Show and allow the "delete report" button inside the reviews GUI                     |
| `astra_rating.max_rate_per_day.<N>`           | Allow up to `N` total votes per day (overrides `max_rating_per_day`)                 |
| `astra_rating.single_player_rate_per_day.<N>` | Allow up to `N` votes per day on the same player (overrides `max_rating_per_player`) |

> The `.<N>` suffix is read as a weighted permission: assign the highest number you want a group to
> have, e.g. `astra_rating.max_rate_per_day.20`.

---

## Configuration

All files live in `plugins/AstraRating/`.

### `config.yml`

```yaml
# Maximum number of ratings a player may give per day (overridable via astra_rating.max_rate_per_day.<N>)
max_rating_per_day: 5
# Maximum number of ratings a player may give to the same player per day
# (overridable via astra_rating.single_player_rate_per_day.<N>)
max_rating_per_player: 1
# Require the voter to have a linked DiscordSRV account
need_discord_linked: false
# Minimum time played on the server required before a player may rate others
min_time_on_server: 0
# Minimum time on Discord required before a player may rate others
min_time_on_discord: 0
# Allowed rating message length
min_message_length: 5
max_message_length: 30
# Trim displayed messages after this many characters in the GUI
trim_message_after: 10
cut_words: false
gui:
  show_first_connection: true
  show_last_connection: true
  show_delete_report: true
  # Date format used in the GUI
  format: "yyyy-MM-dd"
  buttons:
    back:
      material: "PAPER"
      custom_model_data: 0
    prev:
      material: "PAPER"
      custom_model_data: 0
    next:
      material: "PAPER"
      custom_model_data: 0
    sort:
      material: "SUNFLOWER"
      custom_model_data: 0
events:
  # Lower the killer's rating when they kill a player who has a positive rating
  kill_player:
    change_by: -1
    enabled: false
debug: false
```

### `database.yml`

AstraRating stores ratings through a flexible database configuration. The default is a local **H2**
file — no setup required. Switch the `type` to use another backend.

<details>
<summary>Database examples</summary>

**H2 (default, file-based):**

```yaml
rating_database:
  type: "H2"
  path: "./plugins/AstraRating/ASTRA_RATING_RATINGS"
```

**SQLite (file-based):**

```yaml
rating_database:
  type: "SQLite"
  path: "./plugins/AstraRating/ratings.db"
  arguments: [ ]
```

**MySQL (recommended for production):**

```yaml
rating_database:
  type: "MySql"
  host: "localhost"
  port: 3306
  user: "astra_rating"
  password: "your_secure_password"
  name: "astra_rating"
  arguments:
    - "useSSL=false"
    - "allowPublicKeyRetrieval=true"
    - "serverTimezone=UTC"
```

**MariaDB:**

```yaml
rating_database:
  type: "MariaDB"
  host: "localhost"
  port: 3306
  user: "astra_rating"
  password: "your_secure_password"
  name: "astra_rating"
  arguments:
    - "useSSL=false"
```

</details>

Changing this file and running `/aratingreload` reconnects to the new database without a restart.

---

## Integrations

### PlaceholderAPI

When [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) is installed,
AstraRating registers the `erating` expansion:

| Placeholder        | Description                                                                |
|:-------------------|:---------------------------------------------------------------------------|
| `%erating_rating%` | The player's total rating                                                  |
| `%erating_color%`  | A hex color for the player's rating, based on the thresholds in `papi.yml` |

#### `papi.yml` — rating colors

Define color thresholds for `%erating_color%`. Entries are evaluated by `value`; cover every
interval so there are no gaps.

```yaml
# Sorted by value: e.g. [-10, -5, 0, 5, 10]
# Fill ALL gaps/intervals — uncovered ratings produce an error in console.
# Remove the colorings section to disable this feature.
colorings:
  # (-inf, -10)
  - type: "LESS"
    value: -10
    color: "#9c0303"
  # [-10, 0)
  - type: "LESS"
    value: 0
    color: "#eb3131"
  # [0]
  - type: "EQUALS"
    value: 0
    color: "#FFFFFF"
  # (0, 10]
  - type: "MORE"
    value: 0
    color: "#51a8f5"
  # (10, +inf)
  - type: "MORE"
    value: 10
    color: "#0872cf"
```

### DiscordSRV

Listed as an optional soft-dependency for the `need_discord_linked` voting requirement.

---

## 💜 Support Us

If our projects help you, consider supporting their development.

<table>
<tr>
<td align="center" width="130">
<img src="https://cdn.simpleicons.org/bitcoin/F7931A" width="25" alt="BTC"/><br/>
<sub><b>Bitcoin</b></sub>
</td>
<td>

```text
bc1q9a8dr55jgfae0mhevw3vvczegjv0khfp0ngrnv
```

</td>
</tr>
<tr>
<td align="center" width="130">
<img src="https://cdn.simpleicons.org/ethereum/627EEA" width="25" alt="ETH"/><br/>
<sub><b>Ethereum</b></sub>
</td>
<td>

```text
0x0BaAeEA44Ce08c8DC139224ff57563695B30d423
```

</td>
</tr>
<tr>
<td align="center" width="130">
<img src="https://cdn.simpleicons.org/boosty/F15F2C" width="25" alt="Boosty"/><br/>
<sub><b>Boosty</b></sub>
</td>
<td align="center">
<a href="https://boosty.to/empireprojekt/donate">
<img width="70%" src="https://img.shields.io/badge/Donate-Boosty-F15F2C?style=for-the-badge&logo=boosty&logoColor=white" alt="Donate on Boosty"/>
</a>
</td>
</tr>
</table>

---

## Links

- Author: **Makeev Roman** · makeevrserg@gmail.com
- Organization: [AstraInteractive](https://github.com/Astra-Interactive) · [empireprojekt.ru](https://empireprojekt.ru)
- More plugins: [github.com/Astra-Interactive](https://github.com/Astra-Interactive)

[![bStats](https://bstats.org/signatures/bukkit/AstraRating.svg)](https://bstats.org/plugin/bukkit/AstraRating/15801)
