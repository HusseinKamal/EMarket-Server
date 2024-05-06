package com.example.di

import com.example.DATABASE
import org.koin.dsl.module
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

//KION Dependency injection
val mainModule = module {
    single {

        KMongo.createClient()
            .coroutine
            .getDatabase(DATABASE.dbName)
    }
   /* single<MessageDataSource> {
        MessageDataSourceImpl(get())
    }
    single {
        RoomController(get())
    }*/
}