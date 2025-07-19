package com.example.instogramapplication

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import android.widget.RemoteViewsService.RemoteViewsFactory
import androidx.core.os.bundleOf
import com.example.instogramapplication.data.di.Injection
import com.example.instogramapplication.data.remote.model.ListStoryItem
import com.example.instogramapplication.data.repository.UserRepository
import com.example.instogramapplication.utils.ConvertionUtils
import java.io.File

internal class StackRemoteViewsFactory(private val mContext: Context) : RemoteViewsFactory {
    private val items = mutableListOf<ListStoryItem>()
    private val repository: UserRepository by lazy {
        Injection.provideRepository(mContext)
    }

    override fun onCreate() {}

    override fun onDataSetChanged() {
        Log.d("StackWidget", "Fetching data...")

        val itemsFromApi = repository.getItemWidget()
        Log.d("StackWidget", "Fetched items: ${itemsFromApi.size}") // harusnya > 0

        items.apply {
            clear()
            addAll(repository.getItemWidget())

            // pre cache img
            forEachIndexed { index, item ->
                val file = File(mContext.cacheDir, "img_$index.jpg")
                if (file.exists()) file.delete()

                item.photoUrl?.let {
                    ConvertionUtils.downloadImageToCache(mContext, item.photoUrl, "img_$index.jpg")
                }
            }
        }
    }

    override fun getViewAt(position: Int): RemoteViews {
        val views = RemoteViews(mContext.packageName, R.layout.story_widget_item)

        val bitmap = ConvertionUtils.getBitmapFromCache(mContext, "img_$position.jpg")
        if (bitmap != null) {
            views.setImageViewBitmap(R.id.img_widget, bitmap)
        } else {
            views.setImageViewResource(R.id.img_widget, R.drawable.img)
        }

        val extras = bundleOf(
            StoryAppWidget.EXTRA_ITEM to position
        )
        val fillInIntent = Intent()
        fillInIntent.putExtras(extras)

        views.setOnClickFillInIntent(R.id.img_widget, fillInIntent)

        return views
    }


    override fun onDestroy() {}

    override fun getCount(): Int = items.size


    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(position: Int): Long = position.toLong()

    override fun hasStableIds(): Boolean = true
}