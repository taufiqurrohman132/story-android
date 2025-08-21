package com.example.instogramapplication.ui.story.list

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.instogramapplication.R
import com.example.instogramapplication.data.local.entity.StoryEntity
import com.example.instogramapplication.databinding.FragmentListStoryBinding
import com.example.instogramapplication.ui.story.detail.DetailStoryActivity
import com.example.instogramapplication.ui.story.list.adapter.HeaderListStoryYAdapter
import com.example.instogramapplication.ui.story.list.adapter.ListStoryXAdapter
import com.example.instogramapplication.ui.story.list.adapter.ListStoryYAdapter
import com.example.instogramapplication.ui.story.list.adapter.LoadingStateAdapter
import com.example.instogramapplication.ui.story.list.adapter.MyStoryXAdapter
import com.example.instogramapplication.ui.story.post.PostActivity
import com.example.instogramapplication.viewmodel.UserViewModelFactory
import kotlinx.coroutines.launch
import uz.jamshid.library.progress_bar.CircleProgressBar

class ListStoryFragment : Fragment() {

    private var _binding: FragmentListStoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapterX: ListStoryXAdapter
    private lateinit var adapterY: ListStoryYAdapter
    private lateinit var myAdapterX: MyStoryXAdapter

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

        binding.storySimmer.isVisible = false
        binding.rvPost.isVisible = true

        init()
        setupRecyclerView()
        observer()
        setupListener()
    }

    private fun init() {
        // progress bar
        val sizeCircularPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            50f,
            requireContext().resources.displayMetrics
        )
        val circle = CircleProgressBar(requireContext())
        circle.apply {
            setColors(Color.WHITE, requireContext().getColor(R.color.color_variant))
            setBorderWidth(2)
            setSize(sizeCircularPx.toInt())
        }
        binding.homeSwipRefresh.setCustomBar(circle)
    }

    private fun setupListener() {
        binding.homeSwipRefresh.apply {
            setRefreshListener {
                adapterX.refresh()
                adapterY.refresh()

            }
        }
    }

    private fun observer() {

//        lifecycleScope.launch {
//            viewModel.storiesState.collect { result ->
//                when (result) {
//                    is Resource.Loading -> showLoading()
//                    is Resource.Success -> showStories(result.data, result.message)
//                    is Resource.Error -> showError()
//                    is Resource.ErrorConnection -> showErrorConnect(result.message)
//                    is Resource.Empty -> showEmpty()
//                }
//            }
//        }

//        viewModel.myStory.observe(viewLifecycleOwner) { story ->
//            Log.d(TAG, "observer: story = $story")
//            adapterX.setMyStory(story)
//        }

        viewModel.myStory.observe(viewLifecycleOwner) { story ->
            showMyStories(story)
        }

        viewModel.storiesY.observe(viewLifecycleOwner) { story ->
            showStories(story, "")
        }

        // notif error
        lifecycleScope.launch {
//            viewModel.eventFlow.collect { message ->
//                DialogUtils.showToast(message, requireActivity())
//            }
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
            rvPost.visibility = View.VISIBLE
            storySimmer.visibility = View.INVISIBLE
            homeLottieError.visibility = View.INVISIBLE

//            if (adapterY.currentList.isEmpty())
//                homeLottieLayoutErrorConnect.visibility = View.VISIBLE
        }
    }

    private fun setupRecyclerView() {
        val horiLayout =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)

        adapterX = ListStoryXAdapter(
            context = requireActivity(),
            onItemClick = { img, desc, story ->
                showDetailStory(desc, img, story)
            },
            onAddStory = {
                val intent = Intent(requireActivity(), PostActivity::class.java)
                startActivity(intent)
            }
        )

        myAdapterX = MyStoryXAdapter(
            context = requireActivity(),
            onItemClick = { img, desc, story ->
                showDetailStory(desc, img, story)
            },
            onAddStory = {
                val intent = Intent(requireActivity(), PostActivity::class.java)
                startActivity(intent)
            }
        )

        val concatAdapterX = ConcatAdapter(myAdapterX, adapterX)

        val linearLayout = LinearLayoutManager(requireActivity())
        adapterY = ListStoryYAdapter(requireActivity()) { img, desc, story ->
            showDetailStory(desc, img, story)
        }

        val headerAdapterStoryY = HeaderListStoryYAdapter(concatAdapterX)
        val concatAdapterY = ConcatAdapter(headerAdapterStoryY,
            adapterY.withLoadStateFooter(
                footer = LoadingStateAdapter {
                    adapterY.retry()
                }
            )
        )

        binding.apply {
            rvPost.apply {
                layoutManager = linearLayout
                adapter = concatAdapterY
            }
        }
        adapterX.addLoadStateListener { loadState ->
            val isRefreshDone = loadState.source.refresh is LoadState.NotLoading &&
                    loadState.mediator?.refresh !is LoadState.Loading
            if (isRefreshDone) {
                Log.d(TAG, "setupRecyclerView: is refresh down")
                binding.homeSwipRefresh.setRefreshing(false)
            }

            val isListEmpty =
                loadState.refresh is LoadState.NotLoading
                        && adapterX.itemCount == 0
            if (isListEmpty ) {
                Log.d(TAG, "Tidak ada data")
            } else {
                Log.d(TAG, "Jumlah item = ${adapterY.itemCount}")
            }
        }
    }

    private fun showLoading() {
        binding.apply {
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

    private fun showError() {
        binding.apply {
            storySimmer.visibility = View.INVISIBLE
            homeLottieError.visibility = View.INVISIBLE
            homeLottieLayoutErrorConnect.visibility = View.INVISIBLE
        }
    }

    private fun showDetailStory(desc: TextView, img: ImageView, story: StoryEntity) {
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

    private fun showStories(data: PagingData<StoryEntity>, username: String?) {
        binding.apply {
            rvPost.visibility = View.VISIBLE

            storySimmer.visibility = View.INVISIBLE
            homeLottieError.visibility = View.INVISIBLE
            homeLottieLayoutErrorConnect.visibility = View.INVISIBLE
        }

        adapterX.submitData(lifecycle, data)
        adapterY.submitData(lifecycle, data)
    }

    private fun showMyStories(data: StoryEntity?) {
        myAdapterX.submitList(listOf(data))
    }

    companion object {
        private val TAG = ListStoryFragment::class.java.simpleName
    }
}