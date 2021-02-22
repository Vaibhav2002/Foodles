package com.example.zomato.di

import android.content.Context
import com.example.zomato.remote.Api
import com.example.zomato.util.BASE_URL
import com.example.zomato.util.InternetCheck
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Provides
    fun providesRetrofit(): Retrofit =
        Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    fun provideApi(retrofit: Retrofit): Api = retrofit.create(Api::class.java)

    @Provides
    @Singleton
    fun providesIsConnected(@ApplicationContext context: Context) = InternetCheck(context)
//
//    @Provides
//    @Singleton
//    fun providesDatabase(@ApplicationContext context: Context) = Room.databaseBuilder(context,dataBase::class.java,"my_db")
//        .fallbackToDestructiveMigration()
//        .build()
//
//    @Provides
//    fun getDao(dataBase: dataBase) = dataBase.dao()
}