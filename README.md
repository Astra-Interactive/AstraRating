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
