package com.example.routes

import com.example.data.models.story.Story
import com.example.data.models.story.StoryDataSource
import com.example.data.models.user.User
import com.example.data.models.user.UserDataSource
import com.example.data.models.userdata.UserData
import com.example.data.models.userdata.UserInfoDataSource
import com.example.data.requests.AuthRequest
import com.example.data.requests.StoryRequest
import com.example.data.requests.UserRequest
import com.example.data.responses.AuthResponse
import com.example.data.responses.StoryResponse
import com.example.data.responses.UserResponse
import com.example.security.salt.HashingService
import com.example.security.salt.SaltedHash
import com.example.security.token.TokenClaim
import com.example.security.token.TokenConfig
import com.example.security.token.TokenService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receiveNullable
import io.ktor.server.request.receiveOrNull
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import org.apache.commons.codec.digest.DigestUtils
import org.bson.types.ObjectId

fun Route.signUp(
    hashingService: HashingService,
    userDataSource: UserDataSource
){
    post("signup") {
        val request = call.receiveNullable<AuthRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }
        val areFieldBlank = request.username.isBlank() || request.password.isBlank()
        val isPasswordTooShort = request.password.length < 8
        if(areFieldBlank || isPasswordTooShort){
            call.respond(HttpStatusCode.Conflict)
            return@post
        }
        val saltedHash = hashingService.generateSaltedHash(request.password)
        val user = User(
            username = request.username,
            password = saltedHash.hash,
            salt = saltedHash.salt,
        )
        val wasAcknowledge = userDataSource.insertUser(user)
        if(!wasAcknowledge){
            call.respond(HttpStatusCode.Conflict)
            return@post
        }

        //Success
        call.respond(HttpStatusCode.OK)
    }
}

fun Route.singIn(
    userDataSource: UserDataSource,
    hashingService: HashingService,
    tokenService: TokenService,
    tokenConfig: TokenConfig
){
    post("signin") {
        val request = call.receiveOrNull<AuthRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val user = userDataSource.getUserByName(request.username)
        if(user == null){
            call.respond(HttpStatusCode.Conflict,"Incorrect username or password1")
            return@post
        }

        val isValidPassword = hashingService.verify(
            value = request.password,
            saltedHash = SaltedHash(
                hash = user.password,
                salt = user.salt
            )
        )
        if(!isValidPassword) {
            println("Entered hash: ${DigestUtils.sha256Hex("${user.salt}${request.password}")}, Hashed PW: ${user.password}")
            call.respond(HttpStatusCode.Conflict, "Incorrect username or password2")
            return@post
        }

        val token = tokenService.generate(
            config = tokenConfig,
            TokenClaim(
                name = "userId",
                value = user.id.toString()
            )
        )

        call.respond(HttpStatusCode.OK,
            message = AuthResponse(
                token = token
            )
        )

    }

}

fun Route.authenticate(){
    authenticate {
        get("authenticate") {
            call.respond(HttpStatusCode.OK)
        }
    }
}

fun Route.getSecretInfo(){
    authenticate {
        get("secret") {
            val principle = call.principal<JWTPrincipal>()
            val userId = principle?.getClaim("userId", String::class)
            call.respond(HttpStatusCode.OK , "Your userId is $userId")
            /*same as :TokenClaim(
            name = "userId",
            value = user.id.toString()
            )*/
        }
    }
}

fun Route.InsertStory(storyDataSource: StoryDataSource){
    post("story/add") {
        val request = call.receiveNullable<StoryRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }
        val story = Story(
            text = request.text,
            time = request.time,
            color = request.color,
            userId = request.userId
        )
        val wasAcknowledge = storyDataSource.insertStory(story)
        if(!wasAcknowledge){
            call.respond(HttpStatusCode.Conflict)
            return@post
        }

        //Success
        call.respond(HttpStatusCode.OK)
    }
}

fun Route.getStoryById(storyDataSource: StoryDataSource){
    get("story/{id}"){
        val id = call.parameters["id"]
        if (id == null) {
            call.respond(HttpStatusCode.BadRequest, "Missing 'id' parameter")
            return@get
        }

        val story = storyDataSource.getStoryById(id)
        if (story == null) {
            call.respond(HttpStatusCode.NotFound, "Story not found")
            return@get
        }
        //Success
        call.respond(HttpStatusCode.OK, StoryResponse(
            id =story.id.toString(),
            time = story.time,
            text = story.text,
            userId = story.userId,
            color =story.color ,
        )
        )
    }
}
fun Route.getAllStories(storyDataSource: StoryDataSource){
    get("getAllstories/{id}"){
        val id = call.parameters["id"]
        if (id == null) {
            call.respond(HttpStatusCode.BadRequest, "Missing 'id' parameter")
            return@get
        }

        val story = storyDataSource.getAllStory(id)
        if (story == null) {
            call.respond(HttpStatusCode.NotFound, "Story not found")
            return@get
        }
       val stories = ArrayList<StoryResponse>()
       for (item in story){
           stories.add(StoryResponse(
               id =item.id.toString(),
               time = item.time,
               text = item.text,
               userId = item.userId,
               color =item.color ,
           ))
       }
        //Success
        call.respond(HttpStatusCode.OK,stories)
    }
}

fun Route.deleteStory(storyDataSource: StoryDataSource){
    delete("story/delete/{id}") {
        val id = call.parameters["id"]
        if (id == null) {
            call.respond(HttpStatusCode.BadRequest, "Missing 'id' parameter")
            return@delete
        }

        val story = storyDataSource.getStoryById(id)
        if (story == null) {
            call.respond(HttpStatusCode.NotFound, "Story not found")
            return@delete
        }
        val wasAcknowledge = storyDataSource.deleteStory(id)
        if(!wasAcknowledge){
            call.respond(HttpStatusCode.Conflict)
            return@delete
        }

        //Success
        call.respond(HttpStatusCode.OK)
    }
}

//User Data
fun Route.InsertUser(userInfoSource: UserInfoDataSource){
    post("user/add") {
        val request = call.receiveNullable<UserRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }
        val user = UserData(
            username = request.username,
            email = request.email,
            phone = request.phone,
        )
        if (userInfoSource.getUserByName(request.phone) != null) {
            call.respond(HttpStatusCode.Conflict, "User Already exists")
            return@post
        }

        val wasAcknowledge = userInfoSource.insertUser(user)
        if(!wasAcknowledge){
            call.respond(HttpStatusCode.Conflict)
            return@post
        }
        //Success
        val userData = userInfoSource.getUserByName(user.phone)
        userData?.let {
            call.respond(HttpStatusCode.OK, UserResponse(
                id =userData.id.toString(),
                username = userData.username,
                email = userData.email,
                phone = userData.phone,
            )
            )
            return@post
        }
        call.respond(HttpStatusCode.OK)
    }
}
fun Route.UpdateUser(userInfoSource: UserInfoDataSource){
    post("user/update") {
        val request = call.receiveNullable<UserRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }
        val user = UserData(
            id = ObjectId(request.id),
            username = request.username,
            email = request.email,
            phone = request.phone,
        )
        if(userInfoSource.getUserByName(request.phone) == null){
            call.respond(HttpStatusCode.Conflict,"User not exists")
            return@post
        }
        val wasAcknowledge =userInfoSource.updateUser(user)
        if(!wasAcknowledge){
            call.respond(HttpStatusCode.Conflict)
            return@post
        }
        //Success
        val userData = userInfoSource.getUserByName(user.phone)
        userData?.let {
            call.respond(HttpStatusCode.OK,UserResponse(
                id =userData.id.toString(),
                username = userData.username,
                email = userData.email,
                phone = userData.phone,
            ))
            return@post
        }
        call.respond(HttpStatusCode.OK)
    }
}

fun Route.getUserByPhone(userInfoSource: UserInfoDataSource){
    get("user/{phone}"){
        val phone = call.parameters["phone"]
        if (phone == null) {
            call.respond(HttpStatusCode.BadRequest, "Missing 'phone' parameter")
            return@get
        }

        val user = userInfoSource.getUserByName(phone)
        if (user == null) {
            call.respond(HttpStatusCode.NotFound, "User not found")
            return@get
        }
        //Success
        call.respond(HttpStatusCode.OK,UserResponse(
            id =user.id.toString(),
            username = user.username,
            email = user.email,
            phone = user.phone,
        ))
    }
}



