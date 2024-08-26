package com.example.data.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable

@Serializable
data class User(val id: Int = 0, val name: String)

object Users: IntIdTable() {
    val name = varchar("name", 50)
}
