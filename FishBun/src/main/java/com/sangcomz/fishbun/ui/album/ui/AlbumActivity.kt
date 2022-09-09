package com.sangcomz.fishbun.ui.album.ui

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.Group
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.sangcomz.fishbun.BaseActivity
import com.sangcomz.fishbun.FishBun
import com.sangcomz.fishbun.Fishton
import com.sangcomz.fishbun.R
import com.sangcomz.fishbun.adapter.image.ImageAdapter
import com.sangcomz.fishbun.datasource.FishBunDataSourceImpl
import com.sangcomz.fishbun.datasource.ImageDataSourceImpl
import com.sangcomz.fishbun.ui.album.AlbumContract
import com.sangcomz.fishbun.ui.album.adapter.AlbumListAdapter
import com.sangcomz.fishbun.ui.album.listener.AlbumClickListener
import com.sangcomz.fishbun.ui.album.model.Album
import com.sangcomz.fishbun.ui.album.model.AlbumViewData
import com.sangcomz.fishbun.ui.album.model.repository.AlbumRepositoryImpl
import com.sangcomz.fishbun.ui.album.mvp.AlbumPresenter
import com.sangcomz.fishbun.ui.picker.PickerActivity
import com.sangcomz.fishbun.util.MainUiHandler
import com.sangcomz.fishbun.util.SingleMediaScanner
import com.sangcomz.fishbun.util.isLandscape
import com.sangcomz.fishbun.util.setStatusBarColor
import java.io.File

class AlbumActivity : BaseActivity(),
    AlbumContract.View, AlbumClickListener {
    private val albumPresenter: AlbumContract.Presenter by lazy {
        AlbumPresenter(
            this,
            AlbumRepositoryImpl(
                ImageDataSourceImpl(contentResolver),
                FishBunDataSourceImpl(Fishton)
            ),
            MainUiHandler()
        )
    }
    private var groupEmptyView: Group? = null
    private var recyclerAlbumList: RecyclerView? = null
    private var adapter: AlbumListAdapter? = null
    private var txtAlbumMessage: TextView? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_album)
        initView()
        albumPresenter.getDesignViewData()
        if (checkPermission()) albumPresenter.loadAlbumList()
    }

    override fun onResume() {
        super.onResume()
        albumPresenter.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        albumPresenter.release()
    }

    private fun initView() {
        groupEmptyView = findViewById(R.id.group_album_empty)
        recyclerAlbumList = findViewById(R.id.recycler_album_list)
        txtAlbumMessage = findViewById(R.id.txt_album_msg)
    }

    override fun setRecyclerView(albumViewData: AlbumViewData) {
        val layoutManager =
            if (this.isLandscape()) GridLayoutManager(this, albumViewData.albumLandscapeSpanCount)
            else GridLayoutManager(this, albumViewData.albumPortraitSpanCount)

        recyclerAlbumList?.layoutManager = layoutManager
    }

    override fun setToolBar(albumViewData: AlbumViewData) {
        val toolbar = findViewById<Toolbar>(R.id.toolbar_album_bar)
        val cancel = findViewById<TextView>(R.id.button_cancel)

        txtAlbumMessage?.setText(R.string.msg_loading_image)
        setSupportActionBar(toolbar)
        setStatusBarColor(resources.getColor(R.color.status_bar))
        cancel.setOnClickListener {
            onBackPressed()
        }
        cancel.setTextColor(albumViewData.themeColor)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            ENTER_ALBUM_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    albumPresenter.finish()
                } else if (resultCode == TAKE_A_NEW_PICTURE_RESULT_CODE) {
                    albumPresenter.loadAlbumList()
                }
            }
        }
    }

    override fun scanAndRefresh() {
        val savedPath = cameraUtil.savedPath ?: return

        SingleMediaScanner(this, File(savedPath)) {
            albumPresenter.loadAlbumList()
        }
    }

    override fun showNothingSelectedMessage(nothingSelectedMessage: String) {
        recyclerAlbumList?.let {
            it.post {
                Snackbar.make(it, nothingSelectedMessage, Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    override fun showMinimumImageMessage(currentSelectedCount: Int) {
        recyclerAlbumList?.let {
            it.post {
                Snackbar
                    .make(
                        it,
                        getString(R.string.msg_minimum_image, currentSelectedCount),
                        Snackbar.LENGTH_SHORT
                    )
                    .show()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_STORAGE -> {
                if (grantResults.isNotEmpty()) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        // permission was granted, yay!
                        albumPresenter.loadAlbumList()
                    } else {
                        permissionCheck.showPermissionDialog()
                        finish()
                    }
                }
            }
        }
    }

    override fun showAlbumList(
        albumList: List<Album>,
        imageAdapter: ImageAdapter,
        albumViewData: AlbumViewData
    ) {
        recyclerAlbumList?.visibility = View.VISIBLE
        groupEmptyView?.visibility = View.GONE
        setAlbumListAdapter(albumList, imageAdapter, albumViewData)
    }


    override fun onAlbumClick(position: Int, album: Album) {
        PickerActivity.getPickerActivityIntent(this, album.id, album.displayName, position)
            .also { startActivityForResult(it, ENTER_ALBUM_REQUEST_CODE) }
    }

    override fun changeToolbarTitle(selectedImageCount: Int, albumViewData: AlbumViewData) {
        supportActionBar?.apply {
            title =
                if (albumViewData.maxCount == 1 || !albumViewData.isShowCount) albumViewData.titleActionBar
                else getString(
                    R.string.title_toolbar,
                    albumViewData.titleActionBar,
                    selectedImageCount,
                    albumViewData.maxCount
                )
        }
    }

    override fun finishActivityWithResult(selectedImages: List<Uri>) {
        val i = Intent()
        i.putParcelableArrayListExtra(FishBun.INTENT_PATH, ArrayList(selectedImages))
        setResult(Activity.RESULT_OK, i)
        finish()
    }

    private fun checkPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissionCheck.checkStoragePermission(PERMISSION_STORAGE)
        } else true
    }

    override fun setRecyclerViewSpanCount(albumViewData: AlbumViewData) {
        val recyclerView = recyclerAlbumList ?: return
        val gridLayoutManager = recyclerView.layoutManager as? GridLayoutManager ?: return

        gridLayoutManager.spanCount =
            if (isLandscape()) albumViewData.albumLandscapeSpanCount
            else albumViewData.albumPortraitSpanCount
    }

    override fun refreshAlbumItem(position: Int, imagePath: ArrayList<Uri>) {
        val thumbnailPath = imagePath[imagePath.size - 1].toString()
        val addedCount = imagePath.size
        adapter?.updateAlbumMeta(0, addedCount, thumbnailPath)
        adapter?.updateAlbumMeta(position, addedCount, thumbnailPath)
    }


    private fun setAlbumListAdapter(
        albumList: List<Album>,
        imageAdapter: ImageAdapter,
        albumViewData: AlbumViewData
    ) {
        if (adapter == null) {
            adapter = AlbumListAdapter(this, albumViewData.albumThumbnailSize, imageAdapter).also {
                recyclerAlbumList?.adapter = it
            }
        }

        adapter?.let {
            it.setAlbumList(albumList)
            it.notifyDataSetChanged()
        }
    }

    override fun showEmptyView() {
        groupEmptyView?.visibility = View.VISIBLE
        recyclerAlbumList?.visibility = View.INVISIBLE
        txtAlbumMessage?.setText(R.string.msg_no_image)
    }
}