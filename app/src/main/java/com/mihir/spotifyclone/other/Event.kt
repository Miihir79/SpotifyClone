package com.mihir.spotifyclone.other

open class Event<out T>(private val data:T) {
    var hasbeenhandled = false

    private set

    fun getcontentHandled():T?{
        return if (hasbeenhandled){
            null
        }else{
            hasbeenhandled = true
            data
        }

    }

    fun peekContent() = data
}