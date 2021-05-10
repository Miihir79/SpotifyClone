package com.mihir.spotifyclone.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.mihir.spotifyclone.R
import com.mihir.spotifyclone.entities.Song
import kotlinx.android.synthetic.main.list_item.view.*
import javax.inject.Inject

class songAdapter @Inject constructor(
    private val glide: RequestManager
): RecyclerView.Adapter<songAdapter.SongViewHolder>(){
    class SongViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)

    private val diffCallback = object : DiffUtil.ItemCallback<Song>(){
        override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem.Id == newItem.Id
        }

        override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        return SongViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.list_item,parent,false)
        )
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songs[position]
        holder.itemView.apply {
            tvPrimary.text = song.title
            tvSecondary.text = song.subtitle
            glide.load(song.imageUrl).into(ivItemImage)

            setOnItemClickedListener {
                onItemClickedListener?.let {  click ->
                    click(song)
                }
            }
        }
    }

    private var onItemClickedListener : ((Song) -> Unit)? = null

    fun setOnItemClickedListener(listner: (Song) -> Unit){
        onItemClickedListener = listner
    }

    override fun getItemCount(): Int {
        return songs.size
    }

    private val differ = AsyncListDiffer(this,diffCallback)
    var songs : List<Song>
        get() = differ.currentList
        set(value) = differ.submitList(value)





}