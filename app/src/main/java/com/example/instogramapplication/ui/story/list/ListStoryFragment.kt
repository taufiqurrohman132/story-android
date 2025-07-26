package com.example.instogramapplication.ui.story.list

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.instogramapplication.R
import com.example.instogramapplication.data.remote.model.ListStoryItem
import com.example.instogramapplication.databinding.FragmentListStoryBinding
import com.example.instogramapplication.ui.story.detail.DetailStoryActivity
import com.example.instogramapplication.ui.story.detail.DetailStoryActivity.Companion.DETAIL_ID
import com.example.instogramapplication.ui.story.post.PostActivity
import com.example.instogramapplication.utils.DialogUtils
import com.example.instogramapplication.utils.Resource
import com.example.instogramapplication.viewmodel.UserViewModelFactory
import kotlinx.coroutines.launch
import java.util.Locale.filter
import kotlin.math.abs

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
        setupCollapsStoryX()
        setupListener()


    }

    private fun initView(){

    }

    private fun setupListener(){
        binding.homeSwipRefresh.setOnRefreshListener {
            viewModel.refresh()
            binding.homeSwipRefresh.isRefreshing = false
        }
    }

    private fun handler(){

    }

    private fun observer(){
        lifecycleScope.launch {
            viewModel.storiesState.collect { result ->
                Log.d(TAG, "observer: stories $result")
                when (result) {
                    is Resource.Loading -> showLoading()
                    is Resource.Success -> showStories(result.data)
                    is Resource.Error -> showError(result.message)
                    is Resource.ErrorConnection -> showErrorConnect(result.message)
                    is Resource.Empty -> showEmpty()
                }
            }
        }

        // username on
        viewModel.userName.observe(viewLifecycleOwner){ username ->
            adapterX.updateUserName(username)
        }
    }

    private fun showEmpty() {
        binding.apply {
            storySimmer.visibility = View.INVISIBLE
            homeLottieError.visibility = View.INVISIBLE
            homeLottieLayoutErrorConnect.visibility = View.INVISIBLE
        }
    }

    private fun showErrorConnect(message: String?) {
        binding.apply {
            rvStory.visibility = View.VISIBLE
            rvPost.visibility = View.VISIBLE
            storySimmer.visibility = View.INVISIBLE
            homeLottieError.visibility = View.INVISIBLE

//            homeLottieConnect.apply {
//                visibility = View.VISIBLE
//                playAnimation()
//                addAnimatorListener(object : AnimatorListenerAdapter() {
//                    override fun onAnimationEnd(animation: Animator) {
//                        homeLottieConnect.visibility = View.INVISIBLE
//                        if (adapterX.currentList.isEmpty()){
//                            homeLottieLayoutErrorConnect.visibility = View.VISIBLE
//                        }
//                    }
//                })
//            }
        }

        Log.d(TAG, "showErrorConnect: is running")
        message?.let {
            DialogUtils.showToast(message, requireActivity())
        }
    }

    private fun setupRecyclerView(){
        val horiLayout = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)

        adapterX = ListStoryXAdapter(
            context = requireActivity(),
            onItemClick = {img, desc, story ->
                showDetailStory(desc, img, story)
            },
            onAddStory = {
                val intent = Intent(requireActivity(), PostActivity::class.java)
                startActivity(intent)
            }
        )

        binding.rvStory.apply {
            layoutManager = horiLayout
            adapter = adapterX
        }

        val linearLayout = LinearLayoutManager(requireActivity())
        adapterY = ListStoryYAdapter(requireActivity()){ img, desc, story ->
            showDetailStory(desc, img, story)
        }

        binding.apply {
            rvPost.apply {
                layoutManager = linearLayout
                adapter = adapterY
            }
        }

    }

    private fun showLoading(){
        binding.apply {
            rvStory.visibility = View.GONE
            rvPost.visibility = View.INVISIBLE

            storySimmer.apply {
                startShimmer()
                visibility = View.VISIBLE
            }

            // error invisible
            homeLottieError.visibility = View.INVISIBLE
            homeLottieLayoutErrorConnect.visibility = View.INVISIBLE
        }
    }

    private fun showError(message: String?){
        binding.apply {
            storySimmer.visibility = View.INVISIBLE
            homeLottieError.visibility = View.INVISIBLE
            homeLottieLayoutErrorConnect.visibility = View.INVISIBLE
        }

        message?.let {
            DialogUtils.showToast(message, requireActivity())
        }
    }

    private fun showDetailStory(desc: TextView, img: ImageView, story: ListStoryItem){
        val optionsCompat: ActivityOptionsCompat =
            ActivityOptionsCompat.makeSceneTransitionAnimation(
                requireActivity(),
                Pair(img, "img_story"),
                Pair(desc, "desc")
            )
        val intent = Intent(requireContext(), DetailStoryActivity::class.java)
        intent.putExtra(DetailStoryActivity.EXTRA_DETAIL, story)
        requireActivity().startActivity(intent, optionsCompat.toBundle())

        Log.d(TAG, "onStoryXClick: navigate to post activity")
    }

    private fun showStories(data: List<ListStoryItem>?) {
        binding.apply {
            rvStory.visibility = View.VISIBLE
            rvPost.visibility = View.VISIBLE

            storySimmer.visibility = View.INVISIBLE
            homeLottieError.visibility = View.INVISIBLE
            homeLottieLayoutErrorConnect.visibility = View.INVISIBLE
        }

        adapterX.submitList(data)
        adapterY.submitList(data)
    }

    private fun setupCollapsStoryX(){
        // listener
        binding.appBarLayout2.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            val scrollRange = appBarLayout.totalScrollRange
            viewModel.isCollaps = verticalOffset == -scrollRange
            Log.d(TAG, "setupCollapsStoryX: scroll range ${-scrollRange} vertical offset $verticalOffset")

            val alphaValue = 1f - (abs(verticalOffset) / scrollRange)
            binding.rvStory.animate().alpha(alphaValue).setDuration(200).start()
        }
    }


    companion object{
        private val TAG = ListStoryFragment::class.java.simpleName
    }
}