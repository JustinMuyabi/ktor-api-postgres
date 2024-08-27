package com.example.data.repositories

import com.example.data.models.User
import com.example.data.models.Users
import com.example.database.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import com.example.data.repositories.UserRepository.getUserById as getUserById1

object UserRepository {
    suspend fun getAllUsers(limit: Int, offset: Int): List<User> = dbQuery {
        Users.selectAll().limit(limit, offset.toLong()).map { toUser(it) }
    }

    suspend fun getUserById(id: Int): User? = dbQuery {
        Users.select { Users.id eq id }.mapNotNull { toUser(it) }.singleOrNull()
    }

    suspend fun createUser(name: String, email: String, password: String) = dbQuery {
        Users.insert {
            it[Users.name] = name
            it[Users.email] = email
            it[Users.password] = password
        }.resultedValues?.singleOrNull()?.let { toUser(it) }
    }

    suspend fun updateUser(id: Int, name: String): User? = dbQuery {
        val update = Users.update({ Users.id eq id }) {
            it[Users.name] = name
        }
        if (update == 1) Users.select { Users.id eq id }.mapNotNull { toUser(it) }.singleOrNull() else null
    }

    private fun toUser(row: ResultRow): User =
        User(id = row[Users.id], name = row[Users.name], email = row[Users.email])
}