package com.mihir.spotifyclone.ui.fragments

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.mihir.spotifyclone.R
import com.mihir.spotifyclone.adapters.songAdapter
import com.mihir.spotifyclone.other.Status
import com.mihir.spotifyclone.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_home.*
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment:Fragment(R.layout.fragment_home) {

    lateinit var mainViewModel: MainViewModel
    @Inject
    lateinit var songAdapter: songAdapter

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        setUpRecyclerView()
        subscribeToObserver()

        songAdapter.setOnItemClickedListener {
            mainViewModel.playOrToggleSong(it)
        }
    }

    private fun setUpRecyclerView() =rvAllSongs.apply{
        adapter = songAdapter
        layoutManager= LinearLayoutManager(requireContext())
    }

    private fun subscribeToObserver(){
        mainViewModel.mediaItems.observe(viewLifecycleOwner){result ->
            when(result.status){
                Status.SUCCESS ->{
                    allSongsProgressBar.isVisible = false
                    result.data?.let{
                        songAdapter.songs = it
                    }
                }

                Status.ERROR -> Unit

                Status.LOADING -> allSongsProgressBar.isVisible = true
            }
        }
    }
}