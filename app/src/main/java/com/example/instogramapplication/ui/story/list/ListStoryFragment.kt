package com.example.instogramapplication.ui.story.list

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.instogramapplication.R
import com.example.instogramapplication.data.remote.model.ListStoryItem
import com.example.instogramapplication.databinding.FragmentListStoryBinding
import com.example.instogramapplication.ui.story.detail.DetailStoryFragment
import com.example.instogramapplication.ui.story.detail.DetailStoryFragment.Companion.DETAIL_ID
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
            val imageTransitionName = "img_story_${story.id}"
            val nameTransitionName = "username_${story.id}"

            imgStory.transitionName = imageTransitionName
            userName.transitionName = nameTransitionName

            val extras = FragmentNavigatorExtras(
                imgStory to imageTransitionName,
                userName to nameTransitionName
            )
            // navigate to detail
            val action = ListStoryFragmentDirections.actionListStoryFragmentToDetailStoryFragment2(story.id)
            findNavController().navigate(action, extras)
        }

        val linearLayout = LinearLayoutManager(requireActivity())
        adapterY = ListStoryYAdapter(requireActivity()){ story ->
            navigateToDetail(story.id)
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

    private fun onStoryXClick(id: String?){
        Log.d(TAG, "onStoryXClick: navigate to post activity")
        val intent = Intent(requireActivity(), PostActivity::class.java)
        startActivity(intent)
        val args = Bundle().apply {
            putString(DETAIL_ID, id)
        }

        val extras = FragmentNavigatorExtras(

        )
        findNavController().navigate(R.id.action_listStoryFragment_to_detailStoryFragment2, args)
    }

    private fun navigateToDetail(id: String?){
        val action = ListStoryFragmentDirections.actionListStoryFragmentToDetailStoryFragment2(id)
        findNavController().navigate(action)
    }

    private fun showStories(data: List<ListStoryItem>?) {
        adapterX.submitList(data)
        adapterY.submitList(data)
    }

    companion object{
        private val TAG = this::class.java.simpleName
    }
}