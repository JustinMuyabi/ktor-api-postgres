package com.example.data.repositories

import com.example.data.models.User
import com.example.data.models.Users
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object UserRepository {

    init {
        transaction { SchemaUtils.create(Users) }
    }

    fun getAllUsers(limit: Int, offset: Int): List<User> {
        return transaction {
            Users.selectAll()
                .orderBy(Users.id, SortOrder.DESC)
                .limit(limit, offset.toLong())
                .map { toUser(it) }
        }
    }

    fun getUserById(id: Int): User? {
        return transaction {
            Users.select { Users.id eq id }.mapNotNull { toUser(it) }.singleOrNull()
        }
    }

    fun createUser(name: String): User {
        return transaction {
            Users.insert {
                it[Users.name] = name
            }.resultedValues?.singleOrNull()?.let { toUser(it) } ?: throw Exception("User not created")
        }
    }

    fun updateUser(id: Int, name: String): User? {
        return transaction {
            Users.update({ Users.id eq id }) {
                it[Users.name] = name
            }
            getUserById(id) }
    }

    private fun toUser(row: ResultRow): User =
        User(id = row[Users.id].value, name = row[Users.name])
}