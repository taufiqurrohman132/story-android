package com.example.instogramapplication.ui.story.list

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.example.instogramapplication.DataDummy
import com.example.instogramapplication.MainDispatcherRule
import com.example.instogramapplication.data.local.entity.StoryEntity
import com.example.instogramapplication.data.repository.UserRepository
import com.example.instogramapplication.getOrAwaitValue
import com.example.instogramapplication.ui.story.list.adapter.ListStoryYAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ListStoryViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: UserRepository

    @Test
    fun `when Get Story Should Not Null and Return Data`() = runTest {
        val dummyStory = DataDummy.generateDummyStoryResponse()
        val data: PagingData<StoryEntity> = StoryPagingSource.snapshot(dummyStory)
        val expectedStory = MutableLiveData<PagingData<StoryEntity>>()
        expectedStory.value = data
        Mockito.`when`(storyRepository.getStories()).thenReturn(expectedStory)

        val viewModel = ListStoryViewModel(storyRepository)

        val actualStory: PagingData<StoryEntity> = viewModel.storiesY.getOrAwaitValue()
        val differ = AsyncPagingDataDiffer(
            diffCallback = ListStoryYAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStory)


        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummyStory.size, differ.snapshot().size)
        Assert.assertEquals(dummyStory[0], differ.snapshot()[0])
    }

    @Test
    fun `when Get Quote Empty Should Return No Data`() = runTest {
        val data: PagingData<StoryEntity> = PagingData.from(emptyList())
        val expectationStory = MutableLiveData<PagingData<StoryEntity>>()
        expectationStory.value = data

        Mockito.`when`(storyRepository.getStories()).thenReturn(expectationStory)
        val viewModel = ListStoryViewModel(storyRepository)
        val actualStory: PagingData<StoryEntity> = viewModel.storiesY.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = ListStoryYAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStory)


        Assert.assertEquals(0, differ.snapshot().size)
    }

    private val noopListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }

}

class StoryPagingSource : PagingSource<Int, LiveData<List<StoryEntity>>>() {
    companion object {
        fun snapshot(items: List<StoryEntity>): PagingData<StoryEntity> {
            return PagingData.from(items)
        }
    }

    @Suppress("Detekt:ReturnConstant")
    override fun getRefreshKey(state: PagingState<Int, LiveData<List<StoryEntity>>>): Int {
        return 0
    }


    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<StoryEntity>>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }
}