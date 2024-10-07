package com.vlahov.fluencytask

import android.content.Context
import android.widget.Toast
import com.vlahov.data.usecase.chat_connection.ChatConnectionUseCase
import com.vlahov.fluencytask.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val chatConnectionUseCase: ChatConnectionUseCase,
) : BaseViewModel(), AppContract {

    private val mutableState = MutableStateFlow(AppContract.State)
    override val state: StateFlow<AppContract.State> = mutableState.asStateFlow()

    private val mutableEffect = Channel<AppContract.Effect>()
    override val effect = mutableEffect.receiveAsFlow()

    override fun onEvent(event: AppContract.Event) = Unit

    init {
        launchIn {
            chatConnectionUseCase.onConnected.collectLatest {
                Toast.makeText(context, context.getString(R.string.connection_successful), Toast.LENGTH_SHORT).show()
                mutableEffect.trySend(AppContract.Effect.NavigateToChatScreen)
            }
        }

        launchIn {
            chatConnectionUseCase.onLoseConnection.collectLatest {
                Toast.makeText(context, context.getString(R.string.connection_lost), Toast.LENGTH_SHORT).show()
                mutableEffect.trySend(AppContract.Effect.NavigateToMenu)
            }
        }
    }
}
