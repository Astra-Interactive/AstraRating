<p align="center">
  <img src="assets/logo.png" style="border-radius: 6px" alt="AstraRating Logo" width="200"/>
</p>

<h1 align="center">AstraRating</h1>
<p align="center"><strong>A lightning-fast, Kotlin-powered rating plugin for Paper servers</strong></p>
<p align="center">⚡ Minimal. Modular. Modern. ⚡</p>

---

## 🚀 What is AstraRating?

**AstraRating** is a lightweight, flexible **player rating system** plugin for Minecraft servers running **PaperMC**.
Track, display, and manage player scores to add competition and prestige to your server!

Originally built for the EmpireProjekt community, AspeKt is open to all.

More plugins from [AstraInteractive](https://github.com/Astra-Interactive)


---

## ✨ Features

- **Player Ratings:** Track and update player ratings.
- **Persistent Data:** Player data stored in a MySQL/SQLite database.
- **Leaderboards:** Show off top players in-game or on your website.
- **Realtime Reloadable DB**: Reload Database in real-time without restarting server!

---

## 🚀 Getting Started

### Install

1. Download the latest release from [Releases](https://github.com/Astra-Interactive/AstraRating/releases).
2. Drop `AstraRating.jar` into your server’s `/plugins` folder.
3. Restart your server.
4. Edit generated `config.yml` and other config files

---

## 🗄️ Database Configuration

<details>

<summary>CLICK TO EXPAND</summary>

AstraRating supports multiple database types through YAML configuration. The plugin uses a flexible
`DatabaseConfiguration` system that supports **H2**, **MySQL**, **MariaDB**, and **SQLite** databases.

### Configuration File Location

Database configuration is stored in: `./plugins/AstraRating/database.yml`

### Supported Database Types

#### H2 Database

```yaml
rating_database:
  type: "H2"
  path: "./plugins/AstraRating/ASTRA_RATING_RATINGS"
```

#### SQLite Database (Lightweight file-based)

```yaml
rating_database:
  type: "SQLite"
  path: "./plugins/AstraRating/ratings.db"
  arguments:
    - "journal_mode=WAL"
    - "synchronous=NORMAL"
```

#### MySQL Database (Recommended for production)

```yaml
rating_database:
  type: "MySql"
  host: "localhost"
  port: 3306
  user: "astra_rating"
  password: "your_secure_password"
  name: "astra_rating_db"
  arguments:
    - "useSSL=false"
    - "allowPublicKeyRetrieval=true"
    - "serverTimezone=UTC"
```

#### MariaDB Database (MySQL-compatible alternative)

```yaml
rating_database:
  type: "MariaDB"
  host: "localhost"
  port: 3306
  user: "astra_rating"
  password: "your_secure_password"
  name: "astra_rating_db"
  arguments:
    - "useSSL=false"
    - "serverTimezone=UTC"
```

</details>

## 📜 PlaceholderAPI

| Placeholders        | Description        |
|:--------------------|:-------------------|
| `/%erating_color%`  | Show rating color  |
| `/%erating_rating%` | Show player rating |

### PlaceholderAPI Coloring setup

<details>

<summary>CLICK TO EXPAND</summary>

```yaml
# Color are sorted by value: [-10, -5, 0, 5, 10]
# Be sure to fill ALL GAPS/INTERVALS - if not you'll have errors in console
# If you don't want this feature - remove coloring section or comment it using '#' symbol
colorings:
  # [-inf,-10)
  - type: LESS
    value: -10
    color: "#9c0303"
  # [-10,-0)
  - type: LESS
    value: 0
    color: "#eb3131"
  # [0,0]
  - type: EQUALS
    value: 0
    color: "#FFFFFF"
  # [0,10)
  - type: MORE
    value: 0
    color: "#51a8f5"
  # [10,+inf]
  - type: MORE
    value: 10
    color: "#0872cf"    
```

</details>

### Commands

| Command                                             | Description                                               | Permission                                |
|:----------------------------------------------------|:----------------------------------------------------------|:------------------------------------------|
| `/aratingreload`                                    | Reload plugin                                             | astra_rating.reload                       |
| `/arating reload`                                   | Reload plugin                                             | astra_rating.reload                       |
| `/arating rating`                                   | Open rating GUI                                           | -                                         |
| `/arating rating <like/dislike> <player> <message>` | Raise/Downgrade player rating                             | astra_rating.vote                         |
| `/arating player <player_name>`                     | Open rating GUI of player                                 | -                                         |
| `-`                                                 | Delete player vote in GUI                                 | delete_report.vote                        |
| `-`                                                 | Allows player to vote M times per day for the same player | astra_rating.single_player_rate_per_day.M |
| `-`                                                 | Allows player to vote N times per day                     | astra_rating.max_rate_per_day.N           |

---
