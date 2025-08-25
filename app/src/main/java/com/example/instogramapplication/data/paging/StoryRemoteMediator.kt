package com.example.instogramapplication.data.paging

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.instogramapplication.data.local.database.StoryDatabase
import com.example.instogramapplication.data.local.entity.RemoteKeys
import com.example.instogramapplication.data.local.entity.StoryEntity
import com.example.instogramapplication.data.remote.network.ApiService
import kotlinx.coroutines.delay

@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator(
    private val database: StoryDatabase,
    private val apiService: ApiService,
    private val location: Int,
) : RemoteMediator<Int, StoryEntity>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, StoryEntity>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
            }

            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKeys = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKeys
            }

            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        try {
            val responseData = apiService.getStories(page, state.config.pageSize, location).body()

            val endOfPaginationReached = responseData?.listStory?.isEmpty() ?: false

            val entities = responseData?.listStory?.map {
                Log.d(TAG, "load: data story = $it")
                it.toEntity()
            }

            delay(1000)

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.remoteKeysDao().deleteRemoteKeys()
                    database.storyDao().deleteAll()
                }
                val prevKeys = if (page == 1) null else page - 1
                val nextKeys = if (endOfPaginationReached) null else page + 1
                val keys = entities?.map {
                    RemoteKeys(id = it.id, prevKey = prevKeys, nextKey = nextKeys)
                }
                keys?.let {
                    database.remoteKeysDao().insertAll(keys)
                }
                entities?.let {
                    database.storyDao().insertStory(entities)
                }
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: Exception) {
            return MediatorResult.Error(exception)
        }
    }

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, StoryEntity>): RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
            database.remoteKeysDao().getRemoteKeysId(data.id)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, StoryEntity>): RemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
            database.remoteKeysDao().getRemoteKeysId(data.id)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, StoryEntity>): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                database.remoteKeysDao().getRemoteKeysId(id)
            }
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
        private val TAG = StoryRemoteMediator::class.java.simpleName
    }
}