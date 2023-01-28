# Simple Rating plugin for EmpireProjekt.ru
## No-Lag and free!

Advantages:
- [x] Advanced sorting
- [x] Highly customizable
- [x] Fast and open source
- [x] Advanced permissions
- [x] Convenient GUI
- [x] Fully translatable

More plugins from AstraInteractive [AstraInteractive](https://github.com/Astra-Interactive)
### Placeholders: 
- %erating_color% - display color of player depending on players rating value
- %erating_rating% - display rating of player
### Commands
- /aratingreload - reload plugin
- /arating reload - reload plugin
- /arating like <player> <message> - raise player rating
- /arating dislike <player> <message> - downgrade player rating
- /arating rating - open menu with ratings
### Permissions
- astra_rating.reload - reload plugin
- astra_rating.max_rate_per_day.N - allows player to vote N times per day
- astra_rating.single_player_rate_per_day.M - allows player to vote M times per day for the same player
- astra_rating.vote - allows player to vote
- delete_report.vote - allows player to delete votes (Admin permission)
### Adding MySql - EARLY SUPPORT!!!
In order to add MySql support you nee to create section in your config.yml

Section will be created by default if you installing plugin first time
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
Section will be created by default if you installing plugin first time

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
