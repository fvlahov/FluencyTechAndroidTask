package com.vlahov.data.di

import com.vlahov.data.usecase.ChatUseCaseImpl
import com.vlahov.data.usecase.chat_connection.ChatConnectionUseCase
import com.vlahov.data.usecase.chat_connection.ChatConnectionUseCaseImpl
import com.vlahov.domain.usecase.ChatUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ChatModule {

    @Provides
    @Singleton
    fun provideChatConnectionUseCase(impl: ChatConnectionUseCaseImpl): ChatConnectionUseCase = impl

    @Provides
    fun provideChatUseCase(impl: ChatUseCaseImpl): ChatUseCase = impl
}