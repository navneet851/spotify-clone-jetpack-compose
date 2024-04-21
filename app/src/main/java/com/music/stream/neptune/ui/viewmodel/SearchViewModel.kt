package com.music.stream.neptune.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.music.stream.neptune.data.api.Response
import com.music.stream.neptune.data.entity.SongsModel
import com.music.stream.neptune.di.CurrentSongState
import com.music.stream.neptune.ui.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(private val repository: AppRepository, private val currentSongState: CurrentSongState) : ViewModel() {

    private val _songs : MutableStateFlow<Response<List<SongsModel>>> = MutableStateFlow(Response.Loading())
    val songs : StateFlow<Response<List<SongsModel>>> = _songs



    init {
        fetchSongs()
    }

    fun updateSongState(coverUri: String, name: String, singer: String, playingState: Boolean) {
        currentSongState.updateSongState(coverUri, name, singer, playingState)
    }

    private fun fetchSongs() = viewModelScope.launch(Dispatchers.IO) {
        repository.provideSongs().collect { songs ->
            _songs.value = songs as Response<List<SongsModel>>

        }
    }
}