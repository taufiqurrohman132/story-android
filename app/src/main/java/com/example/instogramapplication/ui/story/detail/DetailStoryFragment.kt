package com.example.instogramapplication.ui.story.detail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import com.bumptech.glide.Glide
import com.example.instogramapplication.R
import com.example.instogramapplication.data.remote.model.Story
import com.example.instogramapplication.databinding.FragmentDetailStoryBinding
import com.example.instogramapplication.databinding.FragmentEditBinding
import com.example.instogramapplication.ui.story.post.EditFragment
import com.example.instogramapplication.utils.Resource
import com.example.instogramapplication.viewmodel.UserViewModelFactory
import kotlinx.coroutines.launch

class DetailStoryFragment : Fragment() {

    private var _binding: FragmentDetailStoryBinding? = null
    private val binding get() = _binding!!

    private val factory: UserViewModelFactory by lazy {
        UserViewModelFactory.getInstance(requireActivity())
    }
    private val viewModel: DetailStoryViewModel by viewModels {
        factory
    }

    private val args: DetailStoryFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()

        sharedElementEnterTransition = TransitionInflater.from(requireContext())
            .inflateTransition(android.R.transition.move)

        sharedElementReturnTransition = TransitionInflater.from(requireContext())
            .inflateTransition(android.R.transition.move)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailStoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: fragment detail")

        val storyId = args.idStory
        binding.detailImgStory.transitionName = "img_story_$storyId"
        binding.detailUser.transitionName = "username_$storyId"

        // transsisi
        postponeEnterTransition()
        view .doOnPreDraw {
            startPostponedEnterTransition()
        }

        setupListener()
        observer()
    }

    private fun initView(){
        val detailId = args.idStory
        Log.d(TAG, "initView: detail id $detailId")
        if (detailId != null) {
            viewModel.loadDetailStory(detailId)
        } else {
            Toast.makeText(requireActivity(), "Story ID tidak ditemukan", Toast.LENGTH_SHORT).show()
            requireActivity().supportFragmentManager.popBackStack()
        }

//        // transisi
//        postponeEnterTransition()
//        view?.doOnPreDraw { startPostponedEnterTransition() }
    }

    private fun setupListener(){
        binding.apply {
            detailBtnBack.setOnClickListener { findNavController().popBackStack() }
        }
    }

    private fun observer(){
        lifecycleScope.launch {
            viewModel.detailStory.collect{ result ->
                when (result) {
                    is Resource.Loading -> showLoading(true)
                    is Resource.Success -> showStories(result.data)
                    is Resource.Error -> showError(result.message)
                    else -> {}
                }
            }
        }
    }

    private fun handler(){

    }

    private fun showLoading(isLoading: Boolean){

    }

    private fun showError(message: String?){

    }

    private fun showStories(story: Story?) {
        if (story != null){
            binding.apply {
                detailUser.text = story.name
                detailDesc.text = story.description
                Glide.with(this@DetailStoryFragment)
                    .load(story.photoUrl)
                    .centerCrop()
                    .override(800)
                    .into(detailImgStory)
            }
        }
    }

//    private fun detailFragment(detailId: String){
//        val args = Bundle().apply {
//            putString(DETAIL_ID, detailId)
//        }
//        findNavController().navigate(R.id.detail_story_fragment, args)
//    }

    companion object{
        const val DETAIL_ID = "detail_id"
        private val TAG = DetailStoryViewModel::class.java.simpleName

////        private const val ARG_ID = "id_detail"
//
//        fun newInstance(detailId: String?): DetailStoryFragment{
//
//            return fragment
//        }

    }
}