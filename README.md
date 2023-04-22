# Simple Rating plugin for EmpireProjekt.ru

## No-Lag and free!

Advantages:

- [x] Advanced sorting
- [x] Highly customizable
- [x] Fast and open source
- [x] Advanced permissions
- [x] Convenient GUI
- [x] Fully translatable

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
databaseConnection:
  sqlite: true
  mysql:
    database: "my_database_name"
    host: "127.0.0.1"
    port: 3306
    username: "my_username"
    password: "XXX_SUPER_PASSWORD_SECURITY"
```

### Adding Colors placeholders

Section will be created by default if you're installing plugin first time

```yaml
# Color are sorted by value: [-10, -5, 0, 5, 10]
# Be sure to fill ALL GAPS/INTERVALS - if not you'll have errors in console
# If you don't want this feature - remove coloring section or comment it using '#' symbol
coloring:
  # If <-5 -> #eb3131
  - less: -5
    color: #eb3131
  # If <-10 -> #9c0303
  - less: -10
    color: #9c0303
  # If <0 -> #FFFFFF
  - less: 0
    color: #FFFFFF
  # If ==0 -> #FFFFFF
  - equal: 0
    color: #FFFFFF
  # If >0 -> #FFFFFF
  - more: 0
    color: #FFFFFF
  # If >5 -> #51a8f5
  - more: 5
    color: #51a8f5
  # If >10 -> #0872cf
  - more: 10
    color: #0872cf
```

### Adding events

Section will be created by default if you installing plugin first time

```yaml
events:
  kill_player:
    change_by: -2
    enabled: false
```

<img src="https://bstats.org/signatures/bukkit/AstraRating.svg"/>
