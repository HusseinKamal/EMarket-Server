package com.example.plugins

import com.example.data.models.story.StoryDataSource
import com.example.data.models.user.UserDataSource
import com.example.data.models.userdata.UserInfoDataSource
import com.example.routes.InsertStory
import com.example.routes.InsertUser
import com.example.routes.UpdateUser
import com.example.routes.authenticate
import com.example.routes.deleteStory
import com.example.routes.getAllStories
import com.example.routes.getSecretInfo
import com.example.routes.getStoryById
import com.example.routes.getUserByPhone
import com.example.routes.signUp
import com.example.routes.singIn
import com.example.security.salt.HashingService
import com.example.security.token.TokenConfig
import com.example.security.token.TokenService
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.configureRouting(
    userDataSource: UserDataSource,
    storyDataSource: StoryDataSource,
    userInfoDataSource : UserInfoDataSource,
    hashingService: HashingService,
    tokenService: TokenService,
    tokenConfig: TokenConfig
) {
    //val roomController by inject<RoomController>()
   /* install(Routing){
        chatSocket(roomController)
        getAllMessages(roomController)
    }*/
    routing {
        singIn(userDataSource, hashingService, tokenService, tokenConfig)
        signUp(hashingService,userDataSource)
        authenticate()
        getSecretInfo()
        InsertStory(storyDataSource)
        getStoryById(storyDataSource)
        getAllStories(storyDataSource)
        deleteStory(storyDataSource)
        InsertUser(userInfoDataSource)
        UpdateUser(userInfoDataSource)
        getUserByPhone(userInfoDataSource)
        /* get("/") {
             call.respondText("Hello World!")
         }*/
    }

}