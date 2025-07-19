package com.example.instogramapplication.ui.story.list

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.instogramapplication.R
import com.example.instogramapplication.data.remote.model.ListStoryItem
import com.example.instogramapplication.databinding.FragmentListStoryBinding
import com.example.instogramapplication.ui.story.detail.DetailStoryActivity
import com.example.instogramapplication.ui.story.detail.DetailStoryActivity.Companion.DETAIL_ID
import com.example.instogramapplication.ui.story.post.PostActivity
import com.example.instogramapplication.utils.Resource
import com.example.instogramapplication.viewmodel.UserViewModelFactory
import kotlinx.coroutines.launch

class ListStoryFragment : Fragment() {

    private var _binding: FragmentListStoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapterX: ListStoryXAdapter
    private lateinit var adapterY: ListStoryYAdapter

    private val factory: UserViewModelFactory by lazy {
        UserViewModelFactory.getInstance(requireActivity())
    }
    private val viewModel: ListStoryViewModel by viewModels {
        factory
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListStoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observer()
    }

    private fun initView(){

    }

    private fun setupListener(){

    }

    private fun handler(){

    }

    private fun observer(){
        lifecycleScope.launch {
            viewModel.storiesState.collect { result ->
                when (result) {
                    is Resource.Loading -> showLoading()
                    is Resource.Success -> showStories(result.data)
                    is Resource.Error -> showError(result.message)
                    else -> {}
                }
            }
        }
    }

    private fun setupRecyclerView(){
        val horiLayout = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        adapterX = ListStoryXAdapter(requireActivity()) { imgStory, userName, story ->

        }

        val linearLayout = LinearLayoutManager(requireActivity())
        adapterY = ListStoryYAdapter(requireActivity()){ img, desc, story ->
            showDetailStory(desc, img, story)
        }

        binding.apply {
            rvStory.apply {
                layoutManager = horiLayout
                adapter = adapterX
            }
            rvPost.apply {
                layoutManager = linearLayout
                adapter = adapterY
            }
        }

    }

    private fun showLoading(){

    }

    private fun showError(message: String?){

    }

    private fun showDetailStory(desc: TextView, img: ImageView, story: ListStoryItem){
        Log.d(TAG, "onStoryXClick: navigate to post activity")
        Glide.with(requireContext())
            .load(story.photoUrl)
            .into(img)
        desc.text = story.description

        val optionsCompat: ActivityOptionsCompat =
            ActivityOptionsCompat.makeSceneTransitionAnimation(
                requireActivity(),
                Pair(img, "img_story"),
                Pair(desc, "desc")
            )
        val intent = Intent(requireContext(), DetailStoryActivity::class.java)
        intent.putExtra(DetailStoryActivity.EXTRA_DETAIL, story)
        requireActivity().startActivity(intent, optionsCompat.toBundle())
    }

    private fun showStories(data: List<ListStoryItem>?) {
        adapterX.submitList(data)
        adapterY.submitList(data)
    }

    companion object{
        private val TAG = ListStoryFragment::class.java.simpleName
    }
}