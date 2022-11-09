package com.sangcomz.fishbun.ui.picker

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.graphics.drawable.DrawableCompat
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
import com.sangcomz.fishbun.datasource.PickerIntentDataSourceImpl
import com.sangcomz.fishbun.permission.PermissionCheck
import com.sangcomz.fishbun.ui.picker.listener.OnPickerActionListener
import com.sangcomz.fishbun.ui.picker.model.PickerListItem
import com.sangcomz.fishbun.ui.picker.model.PickerRepositoryImpl
import com.sangcomz.fishbun.ui.picker.model.PickerViewData
import com.sangcomz.fishbun.util.MainUiHandler


class PickerActivity : BaseActivity(),
    PickerContract.View, OnPickerActionListener {
    private val pickerPresenter: PickerContract.Presenter by lazy {
        PickerPresenter(
            this, PickerRepositoryImpl(
                ImageDataSourceImpl(this.contentResolver),
                FishBunDataSourceImpl(Fishton),
                PickerIntentDataSourceImpl(intent)
            ),
            MainUiHandler()
        )
    }

    private var recyclerView: RecyclerView? = null
    private var adapter: PickerAdapter? = null
    private var layoutManager: GridLayoutManager? = null

    override fun onSaveInstanceState(outState: Bundle) {
        try {
            outState.putString(SAVE_INSTANCE_SAVED_IMAGE, cameraUtil.savedPath)
            outState.putParcelableArrayList(
                SAVE_INSTANCE_NEW_IMAGES,
                ArrayList(pickerPresenter.getAddedImagePathList())
            )
        } catch (e: Exception) {
            Log.d(TAG, e.toString())
        }

        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(outState: Bundle) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(outState)
        // Restore state members from saved instance
        try {
            val addedImagePathList = outState.getParcelableArrayList<Uri>(SAVE_INSTANCE_NEW_IMAGES)
            val savedImage = outState.getString(SAVE_INSTANCE_SAVED_IMAGE)

            if (addedImagePathList != null) {
                pickerPresenter.addAllAddedPath(addedImagePathList)
            }

            if (savedImage != null) {
                cameraUtil.savedPath = savedImage
            }

            pickerPresenter.getPickerListItem()
        } catch (e: Exception) {
            Log.d(TAG, e.toString())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_picker)
        initView()
        if (checkPermission()) pickerPresenter.getPickerListItem()

        val album = findViewById<TextView>(R.id.button_back)
        val batal = findViewById<TextView>(R.id.button_batal)
        val kirim = findViewById<TextView>(R.id.button_kirim)

        kirim.setOnClickListener {
            pickerPresenter.onClickMenuDone()
        }

        album.setOnClickListener {
            onBackPressed()
        }

        batal.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        pickerPresenter.transImageFinish()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (Build.VERSION.SDK_INT > 32){
            pickerPresenter.getPickerListItem()
        } else {
            when (requestCode) {
                PERMISSION_STORAGE -> {
                    if (grantResults.isNotEmpty()) {
                        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                            pickerPresenter.getPickerListItem()
                            // permission was granted, yay! do the
                            // calendar task you need to do.
                        } else {
                            PermissionCheck(this).showPermissionDialog()
                            finish()
                        }
                    }
                }
            }
        }
    }

    override fun setToolbarTitle(
        pickerViewData: PickerViewData,
        selectedPhotoCount: Int,
        albumName: String
    ) {
        val title = findViewById<TextView>(R.id.toolbar_title)
        title.text = albumName
    }

    private fun initView() {
        pickerPresenter.getDesignViewData()
    }

    override fun initToolBar(pickerViewData: PickerViewData) {
        val toolbar = findViewById<Toolbar>(R.id.toolbar_picker_bar)
        setSupportActionBar(toolbar)

        val batal = findViewById<TextView>(R.id.button_batal)
        val kirim = findViewById<TextView>(R.id.button_kirim)
        val search = findViewById<ImageView>(R.id.icon_search)
        val back = findViewById<TextView>(R.id.button_back)
        batal.setTextColor(pickerViewData.themeColor)
        kirim.setTextColor(pickerViewData.themeColor)
        search.setColorFilter(pickerViewData.themeColor)
        back.setTextColor(pickerViewData.themeColor)
        val drawables = back.compoundDrawables
        drawables.forEach {
            if (it != null) {
                DrawableCompat.setTint(DrawableCompat.wrap(it), pickerViewData.themeColor)
            }
        }
    }

    override fun initRecyclerView(pickerViewData: PickerViewData) {
        recyclerView = findViewById(R.id.recycler_picker_list)
        layoutManager =
            GridLayoutManager(this, pickerViewData.photoSpanCount, RecyclerView.VERTICAL, false)
        recyclerView?.layoutManager = layoutManager
    }

    override fun takeANewPictureWithFinish(position: Int, addedImageList: List<Uri>) {
        setResult(TAKE_A_NEW_PICTURE_RESULT_CODE)
        finish()
    }

    override fun addImage(pickerListImage: PickerListItem.Image) {
        adapter?.addImage(pickerListImage)
    }

    override fun setBottomBar(imageCount: Int, videoCount: Int) {
        val bottombar = findViewById<TextView>(R.id.tv_count)
        bottombar.text = "$imageCount Photos, $videoCount Videos"
    }

    override fun getContext(): Context = this


    override fun showImageList(
        imageList: List<PickerListItem>,
        adapter: ImageAdapter,
        hasCameraInPickerPage: Boolean
    ) {
        setImageList(imageList, adapter, hasCameraInPickerPage)
    }

    override fun onDeselect() {
        pickerPresenter.getPickerListItem()
    }

    override fun onClickImage(position: Int) {}

    override fun onClickThumbCount(position: Int) {
        pickerPresenter.onClickThumbCount(position)
    }

    override fun onCheckStateChange(position: Int, image: PickerListItem.Image) {
        adapter?.updatePickerListItem(position, image)
    }

    override fun showLimitReachedMessage(messageLimitReached: String) {
        recyclerView?.let {
            it.post {
                Snackbar.make(it, messageLimitReached, Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    override fun showMinimumImageMessage(currentSelectedCount: Int) {
        recyclerView?.let {
            it.post {
                Snackbar.make(
                    it,
                    getString(R.string.msg_minimum_image, currentSelectedCount),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun showNothingSelectedMessage(messageNotingSelected: String) {
        recyclerView?.let {
            it.post {
                Snackbar.make(it, messageNotingSelected, Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkPermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionCheck.checkStoragePermission(PERMISSION_STORAGE)) return true
        } else return true
        return false
    }

    override fun finishActivity() {
        val i = Intent()
        setResult(Activity.RESULT_OK, i)
        finish()
    }

    /**
     * when use startInAllView
     */
    override fun finishActivityWithResult(selectedImages: List<Uri>) {
        val i = Intent()
        setResult(Activity.RESULT_OK, i)
        i.putParcelableArrayListExtra(FishBun.INTENT_PATH, ArrayList(selectedImages))
        finish()
    }

    private fun setImageList(
        pickerList: List<PickerListItem>,
        imageAdapter: ImageAdapter,
        hasCameraInPickerPage: Boolean
    ) {
        if (adapter == null) {
            adapter = PickerAdapter(this, imageAdapter, this, hasCameraInPickerPage)
            recyclerView?.adapter = adapter
        }

        adapter?.setPickerList(pickerList)
    }

    companion object {
        private const val TAG = "PickerActivity"
        fun getPickerActivityIntent(
            context: Context?,
            albumId: Long?,
            albumName: String?,
            albumPosition: Int
        ): Intent {
            val intent = Intent(context, PickerActivity::class.java)
            intent.putExtra(PickerIntentDataSourceImpl.ARG_ALBUM_ID, albumId)
            intent.putExtra(PickerIntentDataSourceImpl.ARG_ALBUM_NAME, albumName)
            intent.putExtra(PickerIntentDataSourceImpl.ARG_ALBUM_POSITION, albumPosition)
            return intent
        }
    }
}