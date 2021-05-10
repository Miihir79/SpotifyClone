package com.mihir.spotifyclone.ui.viewmodels

import android.media.browse.MediaBrowser
import android.os.Build
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_MEDIA_ID
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mihir.spotifyclone.entities.Song
import com.mihir.spotifyclone.exoplayer.MusicServiceConnection
import com.mihir.spotifyclone.exoplayer.isPlayEnabled
import com.mihir.spotifyclone.exoplayer.isPlaying
import com.mihir.spotifyclone.exoplayer.isPrepared
import com.mihir.spotifyclone.other.Constants.MEDIA_ROOT_ID
import com.mihir.spotifyclone.other.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
@HiltViewModel
class MainViewModel @Inject constructor(
    private val musicServiceConnection:MusicServiceConnection
): ViewModel() {

    private val _mediaItems = MutableLiveData<Resource<List<Song>>>()
    val mediaItems:LiveData<Resource<List<Song>>> = _mediaItems

    val isConnected  =  musicServiceConnection.isConnected
    val networkError = musicServiceConnection.networkError
    val currentlyPlayingSong = musicServiceConnection.currentlyPlayingSong
    val playbackState = musicServiceConnection.playbackState

    init {
        _mediaItems.postValue(Resource.loading(null))
        musicServiceConnection.subscribe(MEDIA_ROOT_ID, object: MediaBrowserCompat.SubscriptionCallback(){
            override fun onChildrenLoaded(
                parentId: String,
                children: MutableList<MediaBrowserCompat.MediaItem>
            ) {
                super.onChildrenLoaded(parentId, children)
                val items = children.map {
                    Song(it.mediaId!!,it.description.title.toString(),
                        it.description.subtitle.toString(),
                        it.description.mediaUri.toString(),
                        it.description.iconUri.toString())
                }

                _mediaItems.postValue(Resource.Success(items))
            }
        })
    }

    fun skipToNextSong(){
        musicServiceConnection.transportControls?.skipToNext()
    }

    fun skipToPreviousSong(){
        musicServiceConnection.transportControls?.skipToPrevious()
    }

    fun seekTo(position:Long){
        musicServiceConnection.transportControls?.seekTo(position)
    }

    fun playOrToggleSong(mediaItem: Song, toggle:Boolean = false){
        val isPrepared = playbackState.value?.isPrepared?: false
        if(isPrepared && mediaItem.Id == currentlyPlayingSong.value?.getString(METADATA_KEY_MEDIA_ID)) {
            playbackState.value?.let {playbackState->
                when{
                    playbackState.isPlaying -> if (toggle) {musicServiceConnection.transportControls?.pause()} else Unit
                    playbackState.isPlayEnabled -> musicServiceConnection.transportControls?.play()
                    else -> Unit
                }
            }
        }
        else{
            musicServiceConnection.transportControls?.playFromMediaId(mediaItem.Id,null)
        }
    }

    override fun onCleared() {
        super.onCleared()
        musicServiceConnection.unSubscribe(MEDIA_ROOT_ID, object :MediaBrowserCompat.SubscriptionCallback(){})
    }
}