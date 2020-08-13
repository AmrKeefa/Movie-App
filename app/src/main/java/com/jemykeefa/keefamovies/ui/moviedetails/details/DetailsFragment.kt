package com.jemykeefa.keefamovies.ui.moviedetails.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.jemykeefa.keefamovies.R
import com.jemykeefa.keefamovies.data.model.common.ResourceState
import com.jemykeefa.keefamovies.data.model.model.Movie
import com.jemykeefa.keefamovies.di.component.AppComponent
import com.jemykeefa.keefamovies.di.component.DaggerAppComponent
import com.jemykeefa.keefamovies.ui.home.adapter.HomeRecyclerAdapter
import com.jemykeefa.keefamovies.ui.home.adapter.HomeViewPagerAdapter
import com.jemykeefa.keefamovies.utils.Constants
import com.jemykeefa.keefamovies.utils.extensions.load
import com.jemykeefa.keefamovies.utils.extensions.toastLong
import kotlinx.android.synthetic.main.fragment_details.*
import kotlinx.android.synthetic.main.fragment_home.*
import javax.inject.Inject

class DetailsFragment :Fragment() {

    @Inject
    lateinit var detailsViewModelFactory: DetailsViewModelFactory
    private val viewModel: DetailsViewModel by lazy {
        ViewModelProvider(this, detailsViewModelFactory)
            .get(DetailsViewModel::class.java)
    }
    //dagger code
    lateinit var component: AppComponent


    //this is just for assign xml layout to the fragment
    @Override
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_details, container, false)

    //this is similar to onCreate in Activity
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupInjection()
        getMovies()
        observeMovies(view)
    }

    //dagger code
    private fun setupInjection() {
        component = DaggerAppComponent.builder()
            .build()
        component.inject(this)
    }

    private fun getMovies() {
        viewModel.getMovieDetils()
    }

    private fun observeMovies(view: View) {
        viewModel.movies.observe(viewLifecycleOwner, Observer { resource ->
            when (resource.state) {
                ResourceState.LOADING -> {
                }
                ResourceState.SUCCESS -> {
                    resource.data?.let { detailsResponse ->
                        val movie =   detailsResponse.detailsData.movieDetails
                      movie.large_cover_image?.let { detailsBackgroundImage.load(it) }
                      movie.medium_cover_image?.let { detailsSmallCoverImage.load(it) }
                        detailTitleTextView.text = movie.title_long
                        summaryTextView.text = movie.description_intro
                        descriptionTextView.text = movie.description_full
                        ratingTextDetail.text = movie.rating.toString()
                        sizeTextView.text = movie.torrents?.get(0)?.size
                        qualityTextView.text = movie.torrents?.get(0)?.quality


                    }
                }
                ResourceState.ERROR -> {
                    resource.message?.let { msg ->
                        when (msg) {
                            Constants.Error.GENERAL -> requireActivity().toastLong(getString(R.string.general_error))
                        }
                    } ?: requireActivity().toastLong(getString(R.string.general_error))
                }
            }
        })
    }
}