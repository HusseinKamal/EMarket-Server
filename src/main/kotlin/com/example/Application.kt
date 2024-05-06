package com.example

import com.example.data.models.story.MongoStoryDataSource
import com.example.data.models.user.MongoUserDataSource
import com.example.data.models.userdata.MongoUserInfoDataSource
import com.example.di.mainModule
import com.example.plugins.*
import com.example.security.salt.SHA256HashingService
import com.example.security.token.JwtTokenService
import com.example.security.token.TokenConfig
import io.ktor.server.application.*
import org.koin.ktor.plugin.Koin
import org.litote.kmongo.coroutine.coroutine

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    install(Koin) {
        modules(mainModule)
    }
    //husseinkamal —> username mongoDB
    //ktor-server-app —> password mongoDB
    //Databae name : ktor-auth

    //Check Edit Configuration menu : MONGO_PW=ktor-server-app
    val mongoPassword = System.getenv("MONGO_PW") // add user password  in MongoDB

    //val dbName = "message-me" // Choose any name you need
    val db =org.litote.kmongo.reactivestreams.KMongo.createClient(
        connectionString = "mongodb+srv://Emarket:$mongoPassword@cluster0.e9whvcz.mongodb.net/$DATABASE.dbName?retryWrites=true&w=majority"
    ).coroutine.getDatabase(DATABASE.dbName)


    val userDataSource = MongoUserDataSource(db)
    val storyDataSource = MongoStoryDataSource(db)
    val userInfoDataSource = MongoUserInfoDataSource(db)
    //Create user for testing and go to MongoDB to check if added or not in DataBase->Collections
    /* GlobalScope.launch {
        val user = User(
            username = "test",
            password = "test-password",
            salt = "salt"
        )
        userDataSource.insertUser(user)
    }*/
    val tokenService  = JwtTokenService()
    val tokenConfig = TokenConfig(
        issuer = environment.config.property("jwt.issuer").getString(),
        audience = environment.config.property("jwt.audience").getString(),
        expiresIn = 356L  * 1000L * 60L * 60L *24L,//For one year token still alive
        secret = System.getenv("JWT_SECRET") //Check Edit Configuration menu : JWT_SECRET=jwt-secret
    )
    val hashingService = SHA256HashingService()
    configureSockets()
    configureSerialization()
    configureMonitoring()
    configureSecurity(tokenConfig)
    configureRouting(userDataSource,storyDataSource,userInfoDataSource,hashingService,tokenService, tokenConfig)
}
object DATABASE{
    val dbName = "emarket" // Choose any name you need
}
