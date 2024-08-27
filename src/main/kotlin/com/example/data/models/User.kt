package com.example.data.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.Column

@Serializable
data class User(
    val id: Int = 0,
    val name: String,
    val email: String,
    val password: String? = null,
)

object Users: Table() {
    val id: Column<Int> = integer("id").autoIncrement()
    val name = varchar("name", 50)
    val email = varchar("email", 50)
    val password = varchar("password", 255)
    override val primaryKey = PrimaryKey(id)
}
