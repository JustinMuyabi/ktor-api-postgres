package com.example.database

import com.example.data.models.Users.password
import com.typesafe.config.ConfigFactory
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    private val config = ConfigFactory.load()
    private val dbUrl = config.getString("database.url")
    private val dbDriver = config.getString("database.driver")
    private val dbUser = config.getString("database.user")
    private val dbPassword = config.getString("database.password")

    fun init() {
        Database.connect(hikari())
        val flyway = org.flywaydb.core.Flyway.configure()
            .dataSource(dbUrl, dbUser, dbPassword)
            .baselineOnMigrate(true)
            .load()
        flyway.migrate()
    }

    private fun hikari(): HikariDataSource {
        val config = HikariConfig()
        config.driverClassName = dbDriver
        config.jdbcUrl = dbUrl
        config.username = dbUser
        config.password = dbPassword
        config.maximumPoolSize = 3
        config.isAutoCommit = false
        config.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        config.validate()
        return HikariDataSource(config)
    }

    suspend fun <T> dbQuery(block: () -> T): T =
        withContext(Dispatchers.IO) {
            transaction { block() }
        }
}