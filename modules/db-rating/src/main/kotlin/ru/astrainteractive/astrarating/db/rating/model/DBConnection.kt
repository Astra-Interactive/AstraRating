package ru.astrainteractive.astrarating.db.rating.model

sealed class DBConnection(val driver: String) {
    class SQLite(val name: String) : DBConnection("org.sqlite.JDBC") {
        val url = "jdbc:sqlite:$name"
    }

    class MySql(
        val url: String,
        val user: String,
        val password: String
    ) : DBConnection("com.mysql.cj.jdbc.Driver")
}
