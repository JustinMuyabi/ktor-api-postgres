package com.example.plugins.user.routes

import com.example.data.repositories.UserRepository
import com.example.data.models.User
import io.github.smiley4.ktorswaggerui.dsl.get
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.userRoutes() {
    route("/users") {
        get({
                tags = listOf("User API")
                summary = "Get all users"
                description = "Retrieve a list of all users."
                response {
                    HttpStatusCode.OK to {
                        body<List<User>> { description = "List of users" }
                    }
                }
        }) {
            val users = UserRepository.getAllUsers()
            call.respond(users)
        }

        get("/{id}",{
            tags = listOf("User API")
            summary = "Get a user by ID"
            description = "Retrieve a user by their ID."
            response {
                HttpStatusCode.OK to {
                    body<User> { description = "Retrieved user" }
                }
                HttpStatusCode.NotFound to { description = "User not found" }
                HttpStatusCode.BadRequest to { description = "Invalid user ID" }
            }
        }){
            val userId = call.parameters["id"]?.toIntOrNull()
            if (userId == null) {
                call.respond(HttpStatusCode.BadRequest,"Invalid user ID")
                return@get
            }

            val user = UserRepository.getUserById(userId)
            if (user == null) {
                call.respond(HttpStatusCode.NotFound, "User not found")
            } else {
                call.respond(user)
            }
        }

        post {
            val user = call.receive<User>()
            val createdUser = UserRepository.createUser(user.name)
            call.respond(
                status = HttpStatusCode.Created,
                message = createdUser
            )
        }


        patch("/{id}"){
            val userId = call.parameters["id"]?.toIntOrNull()
            if (userId == null) {
                call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Invalid user ID"))
                return@patch
            }

            val user = call.receive<User>()
            val updatedUser = UserRepository.updateUser(userId, user.name)
            if (updatedUser == null) {
                call.respond(HttpStatusCode.NotFound, mapOf("message" to "User not found"))
            } else {
                call.respond(updatedUser)
            }
        }
    }
}