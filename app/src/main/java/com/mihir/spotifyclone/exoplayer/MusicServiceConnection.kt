package com.mihir.spotifyclone.exoplayer

import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mihir.spotifyclone.other.Constants.NETWORK_ERROR
import com.mihir.spotifyclone.other.Event
import com.mihir.spotifyclone.other.Resource

class MusicServiceConnection(context: Context) {

    private val is_connected = MutableLiveData<Event<Resource<Boolean>>>()
    val isConnected : LiveData<Event<Resource<Boolean>>> = is_connected

    private val _networkError = MutableLiveData<Event<Resource<Boolean>>>()
    val networkError : LiveData<Event<Resource<Boolean>>> = _networkError

    private val _playbackState = MutableLiveData<PlaybackStateCompat?>()
    val playbackState : LiveData<PlaybackStateCompat?> = _playbackState

    private val _currentlyPlayingSong = MutableLiveData<MediaMetadataCompat?>()
    val currentlyPlayingSong : LiveData<MediaMetadataCompat?> = _currentlyPlayingSong

    lateinit var mediaController: MediaControllerCompat

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private val mediaBrowserConnectionCallback_ = mediaBrowserConnectionCallback(context)

    val transportControls: MediaControllerCompat.TransportControls?
    get()= mediaController.transportControls

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private val mediaBrowser = MediaBrowserCompat(context, ComponentName(
        context, MusicService::class.java
    ),
    mediaBrowserConnectionCallback_,null).apply {
        connect()
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun subscribe(parentId:String, callback: MediaBrowserCompat.SubscriptionCallback){
         mediaBrowser.subscribe(parentId,callback)
    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun unSubscribe(parentId:String, callback: MediaBrowserCompat.SubscriptionCallback){
         mediaBrowser.unsubscribe(parentId,callback)
    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private inner class mediaBrowserConnectionCallback(private val context: Context): MediaBrowserCompat.ConnectionCallback(){

        override fun onConnected() {
            mediaController = MediaControllerCompat(context,mediaBrowser.sessionToken).apply {
                registerCallback(MediaControllerCallback())
            }
            is_connected.postValue(Event(Resource.Success(true)))

        }

        override fun onConnectionSuspended() {
            is_connected.postValue(Event(Resource.error("The connection was suspended",false)))
        }

        override fun onConnectionFailed() {
            is_connected.postValue(Event(Resource.error("Couldnt connect to meida browser",false)))
        }
    }

    private inner class MediaControllerCallback: MediaControllerCompat.Callback(){

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            _playbackState.postValue(state)
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            _currentlyPlayingSong.postValue(metadata)
        }

        override fun onSessionEvent(event: String?, extras: Bundle?) {
            super.onSessionEvent(event, extras)
            when(event){
            NETWORK_ERROR -> _networkError.postValue(
                Event(Resource.error("couldnt connect to server. check internet", null))
            )
        }
        }

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        override fun onSessionDestroyed() {
            mediaBrowserConnectionCallback_.onConnectionSuspended()
        }
    }

}