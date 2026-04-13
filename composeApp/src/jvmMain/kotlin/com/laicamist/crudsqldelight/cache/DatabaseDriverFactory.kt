package com.laicamist.crudsqldelight.cache

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import java.util.Properties

class JvmDatabaseDriverFactory() : DatabaseDriverFactory {
    override fun createDriver(): SqlDriver {
        return JdbcSqliteDriver("jdbc:sqlite:dbfile.db", Properties(), AppDatabase.Schema)
    }
}