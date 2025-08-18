package com.example.instogramapplication.ui.story.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertHeaderItem
import androidx.paging.map
import com.example.instogramapplication.data.local.entity.StoryEntity
import com.example.instogramapplication.data.local.entity.UIModel
import com.example.instogramapplication.data.remote.model.StoryItem
import com.example.instogramapplication.data.repository.UserRepository
import kotlinx.coroutines.launch
class ListStoryViewModel(
    private val repository: UserRepository
) : ViewModel() {

//    private val refreshTrigger =
//        MutableSharedFlow<Unit>()
//
//    private val _eventFlow = Channel<String>()
//    val eventFlow = _eventFlow.receiveAsFlow()

//    private lateinit var currentUserName: String
//    val myStory = MutableLiveData<StoryItem>() // untuk story khusus user

    val storiesX: LiveData<PagingData<UIModel>> = liveData {
        // ambil username dari repository (suspend)
        val userName = repository.getUserName()

        // ambil latestMyStory dari DB/API
        val latestMyStory = repository.getLatestMyStory(userName)

        // ambil PagingData stories + insert header
        val pagingData = repository.getStories()
            .map { pagingData ->
                pagingData
                    .map <StoryEntity, UIModel> { UIModel.StoriesItem(it) }
                    .insertHeaderItem(item = UIModel.MyStoriesItem(latestMyStory))
            }
            .cachedIn(viewModelScope)

        //sebagai LiveData
        emitSource(pagingData)
}

//    init {
//
//        fun getStoriesWithHeader(username: String): LiveData<PagingData<UIModel>> = liveData {
//        val latestMyStory = repository.getLatestMyStory(username)
//        latestMyStory?.let { myStory ->
//            emitSource(
//                repository.getStories()
//                    .map { pagingData ->
//                        pagingData
//                            .map<StoryEntity, UIModel> { UIModel.StoriesItem(it) }
//                            .insertHeaderItem(item = UIModel.MyStoriesItem(myStory))
//                    }
//            )
//        }
//    }
//            .map { pagingData: PagingData<StoryEntity> ->
//                pagingData
//                    // lagi-lagi paksa R = UIModel
//                    .map<StoryEntity, UIModel> { entity -> UIModel.StoriesItem(entity) }
//                    // sekarang aman nyisipin subclass lain sebagai header
//                    .insertHeaderItem(item = UIModel.MyStoriesItem(latestMyStory))
//            }
//            .cachedIn(viewModelScope)
//    val story: LiveData<PagingData<UIModel>> =
//        repository.getStories()
//            .map {pagingData ->
//                pagingData.map {story ->
//                    UIModel.StoryEntities(story)
//                }
//            }.cachedIn(viewModelScope)
//        _currentUserName.switchMap { userName ->
//            repository.getStories()
//                .map { pagingData ->
//                    // cari story user
//                    val tempList = mutableListOf<StoryItem>()
//
//                    pagingData.filter { story ->
//                        Log.d(TAG, "paging data story = : $story")
//                        tempList.add(story)
//                        true // tetep masukan semua story
//                    }
//
//                    // ambil story terbaru user
//                    val latestMyStory = tempList
//                        .filter {
//                            Log.d(TAG, "name = ${it.name} - username = $userName ")
//                            it.name == userName
//                        }
//                        .maxByOrNull { it.createdAt ?: "" }
//                    Log.d(TAG, "latest my story = : $latestMyStory")
//
//                    myStory.postValue(latestMyStory)
//                    pagingData
//
//                }.cachedIn(viewModelScope)
//        }


//    @OptIn(ExperimentalCoroutinesApi::class)
//    val storiesState = refreshTrigger
//        .onStart { emit(Unit) } // auto-fetch saat pertama kali di-observe
//        .flatMapLatest {
//            flow {
//                emit(Resource.Loading())
//                delay(1000)
//                val result = repository.getStories()// hanya bisa di konsum sekali
//                result.collect { res ->
//                    emit(res)
//                    if (
//                        res is Resource.Error ||
//                        res is Resource.ErrorConnection ||
//                        res is Resource.Empty
//                    ) {
//                        res.message?.let {
//                            _eventFlow.send(res.message)
//                        }
//                    }
//                }
//            }
//        }
//        .distinctUntilChanged()
//        .stateIn(
//            viewModelScope,
//            SharingStarted.WhileSubscribed(0),
//            Resource.Loading()
//        )

    var isCollaps = false

//    fun refresh() {
//        viewModelScope.launch {
//            refreshTrigger.emit(Unit)
//        }
//    }

    companion object{
        private val TAG = ListStoryViewModel::class.java.simpleName
    }
}

