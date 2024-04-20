package com.music.stream.neptune.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.music.stream.neptune.data.api.Response
import com.music.stream.neptune.data.entity.ArtistsModel
import com.music.stream.neptune.data.entity.SongsModel
import com.music.stream.neptune.ui.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtistViewModel @Inject constructor(private val repository: AppRepository) : ViewModel() {

    private val _songs : MutableStateFlow<Response<List<SongsModel>>> = MutableStateFlow(Response.Loading())
    val songs : StateFlow<Response<List<SongsModel>>> = _songs

    private val _artists : MutableStateFlow<Response<List<ArtistsModel>>> = MutableStateFlow(Response.Loading())
    val artists : StateFlow<Response<List<ArtistsModel>>> = _artists

    init {
        fetchArtists()
        fetchSongs()
    }


    private fun fetchSongs() = viewModelScope.launch(Dispatchers.IO) {
        repository.provideSongs().collect { songs ->
            _songs.value = songs as Response<List<SongsModel>>

        }
    }

    private fun fetchArtists() = viewModelScope.launch(Dispatchers.IO) {
        repository.provideArtists().collect{ artists ->
            _artists.value = artists as Response<List<ArtistsModel>>
        }
    }
}