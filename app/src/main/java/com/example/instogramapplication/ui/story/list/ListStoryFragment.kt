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
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
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
import com.google.android.material.snackbar.Snackbar
import retrofit2.HttpException
import uz.jamshid.library.progress_bar.CircleProgressBar
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class ListStoryFragment : Fragment() {

    private var _binding: FragmentListStoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapterX: ListStoryXAdapter
    private lateinit var adapterY: ListStoryYAdapter
    private lateinit var myAdapterX: MyStoryXAdapter

    private lateinit var currentSnackbar: Snackbar
    private var lastError: Throwable? = null

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

        //snakbar
        this.currentSnackbar = Snackbar.make(
            binding.root,
            "",
            Snackbar.LENGTH_SHORT
        ).setAction(requireContext().getString(R.string.retry)) {
            adapterY.retry()
            Log.d(TAG, "init: coba lagi is running")
        }

        // matikan swip refresh ketika bukan di top
        binding.rvPost.addOnScrollListener(object : OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val canScrollUp = recyclerView.canScrollVertically(-1)
                binding.homeSwipRefresh.isEnabled = !canScrollUp
            }
        })
    }

    private fun setupListener() {
        binding.homeSwipRefresh.apply {
            setRefreshListener {
                adapterY.refresh()
                adapterX.refresh()
            }
        }
    }

    private fun observer() {
        viewModel.myStory.observe(viewLifecycleOwner) { story ->
            showMyStories(story)
        }

        viewModel.storiesY.observe(viewLifecycleOwner) { story ->
            showStories(story)
        }
    }

    private fun showEmpty(isEmpty: Boolean) {
        Log.d(TAG, "showEmpty: isempty $isEmpty")
        binding.apply {
            rvPost.isVisible = !isEmpty
            homeLottieError.isVisible = isEmpty
            storyScrollFrame.isScrollContainer = isEmpty
        }
    }

    private fun setupRecyclerView() {
        adapterX = ListStoryXAdapter(
            context = requireActivity(),
            onItemClick = { img, desc, story ->
                showDetailStory(desc, img, story)
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

        val concatAdapterX = ConcatAdapter(myAdapterX,
            adapterX.withLoadStateFooter(
                footer = LoadingStateAdapter(LoadingStateAdapter.ITEM_HOLDER_X) {
                    adapterX.retry()
                }
            )
        )

        val linearLayout = LinearLayoutManager(requireActivity())
        adapterY = ListStoryYAdapter(requireActivity()) { img, desc, story ->
            showDetailStory(desc, img, story)
        }

        val headerAdapterStoryY = HeaderListStoryYAdapter(concatAdapterX)
        val concatAdapterY = ConcatAdapter(headerAdapterStoryY,
            adapterY.withLoadStateFooter(
                footer = LoadingStateAdapter(LoadingStateAdapter.ITEM_HOLDER_Y) {
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
        adapterY.addLoadStateListener { loadState ->
            val isRefreshDone = loadState.source.refresh is LoadState.NotLoading &&
                    loadState.mediator?.refresh !is LoadState.Loading
            if (isRefreshDone) {
                Log.d(TAG, "setupRecyclerView: is refresh down")
                binding.homeSwipRefresh.setRefreshing(false)
                showLoading(false)
                val hasData = adapterX.itemCount > 0
                Log.d(TAG, "setupRecyclerView: is data success = $hasData")
                if (hasData)
                    showEmpty(false)
            }

            // kosong karena error + belum ada data sama sekali
            val isErrorEmpty = loadState.refresh is LoadState.Error &&
                    adapterX.itemCount == 0
            if (isErrorEmpty && isRefreshDone) {
                Log.d(TAG, "Tidak ada data")
                showEmpty(true)
            } else {
                Log.d(TAG, "Jumlah item = ${adapterY.itemCount}")
            }

            val isListEmpty = loadState.refresh is LoadState.NotLoading && adapterX.itemCount == 0
            val isLoading = loadState.source.refresh is LoadState.Loading
                    || loadState.mediator?.refresh is LoadState.Loading
            if (isLoading && isListEmpty) {
                binding.apply {
                    showLoading(true)
                }
            }

            // error handling
            // Awal load gagal (refresh error)
            val refreshError = (loadState.source.refresh as? LoadState.Error)
                ?: (loadState.mediator?.refresh as? LoadState.Error)
            if (refreshError != null) {
                val throwable = refreshError.error
                // tampilkan hanya kalau error berbeda
                if (lastError?.javaClass != throwable.javaClass ||
                    lastError?.message != throwable.message
                ) {
                    showError(throwable)
                    lastError = throwable
                }
            } else {
                // reset kalau sukses
                lastError = null
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            rvPost.isVisible = !isLoading

            storySimmer.apply {
                startShimmer()
                isVisible = isLoading
            }

            // error invisible
            homeLottieError.visibility = View.INVISIBLE
            homeLottieLayoutErrorConnect.visibility = View.INVISIBLE
        }
    }

    private fun showError(throwable: Throwable?) {
        val errorName = when (throwable) {
            is UnknownHostException -> getString(R.string.error_koneksi)
            is SocketTimeoutException -> getString(R.string.error_408)
            is HttpException -> {
                when (throwable.code()) {
                    400 -> getString(R.string.error_400)
                    401 -> getString(R.string.error_401)
                    403 -> getString(R.string.error_403)
                    404 -> getString(R.string.error_404)
                    408 -> getString(R.string.error_408)
                    422 -> getString(R.string.error_422)
                    500 -> getString(R.string.error_500)
                    503 -> getString(R.string.error_503)
                    else -> getString(R.string.error_else)
                }
            }

            is IOException -> getString(R.string.error_koneksi)
            else -> getString(R.string.error_takterduga)
        }

        this.currentSnackbar.setText(errorName)
        if (currentSnackbar.isShownOrQueued) {
            // sudah tampil, jangan bikin baru
            return
        }
        currentSnackbar.show()
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

    private fun showStories(data: PagingData<StoryEntity>) {
        binding.apply {
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