package com.example.instogramapplication.data.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.instogramapplication.data.remote.model.StoryItem
import com.example.instogramapplication.data.remote.network.ApiService

class StoryPagingSource(private val apiService: ApiService) : PagingSource<Int, StoryItem>() {
    init {
        Log.d(TAG, "StoryPagingSource dibuat")
    }

    override fun getRefreshKey(state: PagingState<Int, StoryItem>): Int? {
        return  state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StoryItem> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val responseData = apiService.getStories(page = position, size = params.loadSize).body()
            val story = responseData?.listStory ?: emptyList()
            Log.d(TAG, "load: position = $position")
            Log.d(TAG, "load: respons data = $story")
            LoadResult.Page(
                data = story ,
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position -1,
                nextKey = if (story.isEmpty()) null else position +1
            )
        }catch (exception: Exception) {
            Log.e(TAG, "load: gagal load", exception)
            return LoadResult.Error(exception)
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
        private val TAG = StoryPagingSource::class.java.simpleName
    }
}