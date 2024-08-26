package com.example.database

import com.typesafe.config.ConfigFactory
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.TransactionManager

object DatabaseFactory {
    fun init() {
        val config = ConfigFactory.load()

        val dbUrl = config.getString("database.url")
        val dbDriver = config.getString("database.driver")
        val dbUser = config.getString("database.user")
        val dbPassword = config.getString("database.password")

        Database.connect(
            url = dbUrl,
            driver = dbDriver,
            user = dbUser,
            password = dbPassword
        )

        TransactionManager.defaultDatabase = Database.connect(
            url = dbUrl,
            driver = dbDriver,
            user = dbUser,
            password = dbPassword
        )
    }
}