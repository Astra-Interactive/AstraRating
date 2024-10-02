# Simple Rating plugin for EmpireProjekt.ru

## No-Lag and free!

> [!CAUTION]
> Java 21 and Paper 1.21 only supported! Use other versions on your own risk!

More plugins from [AstraInteractive](https://github.com/Astra-Interactive)

| Placeholders        | Description        |
|:--------------------|:-------------------|
| `/%erating_color%`  | Show rating color  |
| `/%erating_rating%` | Show player rating |

| Command                                      | Description                                               | Permission                                |
|:---------------------------------------------|:----------------------------------------------------------|:------------------------------------------|
| `/aratingreload`                             | Reload plugin                                             | astra_rating.reload                       |
| `/arating reload`                            | Reload plugin                                             | astra_rating.reload                       |
| `/arating rating`                            | Open rating GUI                                           | -                                         |
| `/arating rating <player_name>`              | Open rating GUI of player                                 | -                                         |
| `/arating <like/dislike> <player> <message>` | Raise/Downgrade player rating                             | astra_rating.vote                         |
| `-`                                          | Delete player vote in GUI                                 | delete_report.vote                        |
| `-`                                          | Allows player to vote M times per day for the same player | astra_rating.single_player_rate_per_day.M |
| `-`                                          | Allows player to vote N times per day                     | astra_rating.max_rate_per_day.N           |

### Adding MySql

In order to add MySql support you need to create section in your config.yml

Section will be created by default if you're installing plugin first time

```yaml
# Use only one sqlite or mysql
# If you define both mysql and sqlite - mysql will be used
# To reconnect from mysql to sqlite and vise versa full restart is required
rating_database:
  type: "MySql"
  host: "0.0.0.0"
  port: 3006
  user: "user_name"
  password: "password"
  name: "rating_database"
# Or  
rating_database:
  type: "H2"
  name: "file_name"
# Or  
rating_database:
  type: "SQLite"
  name: "file_name"
```

### Adding Colors placeholders

Section will be created by default if you're installing plugin first time

papi.yml
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

### Adding events [Experimental]

Section will be created by default if you're installing plugin first time.

When enabled, the plugin will decrease player rating when killed a player with positive rating

```yaml
events:
  kill_player:
    change_by: -1
    enabled: false
```

<img src="https://bstats.org/signatures/bukkit/AstraRating.svg"/>
