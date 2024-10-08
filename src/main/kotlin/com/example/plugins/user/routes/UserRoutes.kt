package com.example.plugins.user.routes

import com.example.data.repositories.UserRepository
import com.example.data.models.User
import io.github.smiley4.ktorswaggerui.dsl.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.mindrot.jbcrypt.BCrypt

fun Route.userRoutes() {
    route("/users") {
        get({
            tags = listOf("User API")
            summary = "Get all users"
            description = "Retrieve a list of all users."
            request {
                queryParameter<Int> ("limit") {
                    description = "Maximum number of users to return"
                    example = 10
                    required
                }
                queryParameter<Int>("offset") {
                    description = "Number of users to skip"
                    example = 0
                    required
                }
            }
            response {
                HttpStatusCode.OK to {
                    body<List<User>> { description = "List of users" }
                }
            }
        }) {
            val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 10
            val offset = call.request.queryParameters["offset"]?.toIntOrNull() ?: 0
            val users = UserRepository.getAllUsers(limit, offset)
            call.respond(users)
        }

        post({
            tags = listOf("User API")
            summary = "Create a user"
            description = "Create a new user."
            request {
                body<User> { description = "User data to be created" }
            }
            response {
                HttpStatusCode.Created to { body<User> { description = "Created user" } }
            }
        }) {
            val user = call.receive<User>()
            val encryptedPassword = BCrypt.hashpw(user.password, BCrypt.gensalt())
            val createdUser = UserRepository.createUser(user.name, user.email, encryptedPassword) ?: return@post call.respond(
                mapOf("message" to "User already exists")
            )
            call.respond(status = HttpStatusCode.Created, message = createdUser)
        }

        get("/{id}",{
            tags = listOf("User API")
            summary = "Get a user by ID"
            description = "Retrieve a user by their ID."
            request {
                pathParameter<Int>("id") {
                    description = "User ID"
                    required
                }
            }
            response {
                HttpStatusCode.OK to { body<User> { description = "Retrieved user" } }
                HttpStatusCode.NotFound to { description = "User not found" }
                HttpStatusCode.BadRequest to { description = "Invalid user ID" }
            }
        }){
            val userId = call.parameters["id"]?.toIntOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest,"Invalid user ID")

            val user = UserRepository.getUserById(userId)
                ?: return@get call.respond(HttpStatusCode.NotFound, "User not found")

            call.respond(user)
        }

        patch("/{id}", {
            tags = listOf("User API")
            summary = "Update a user"
            description = "Update a user's information by their ID."
            request {
                pathParameter<Int>("id") {
                    description = "User ID"
                    required = true
                }
                body<User> {
                    description = "User data to be updated"
                }
            }
            response {
                HttpStatusCode.OK to {
                    body<User> { description = "Updated user" }
                }
                HttpStatusCode.NotFound to { description = "User not found" }
                HttpStatusCode.BadRequest to { description = "Invalid user ID" }
            }
        }) {
            val userId = call.parameters["id"]?.toIntOrNull()
            if (userId == null) {
                call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Invalid user ID"))
                return@patch
            }

            val user = call.receive<User>()
            val updatedUser = UserRepository.updateUser(userId, user.name) ?: return@patch call.respond(mapOf("message" to "User not found"))
                call.respond(updatedUser)
        }
    }
}