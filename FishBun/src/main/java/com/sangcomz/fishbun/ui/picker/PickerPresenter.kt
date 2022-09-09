package com.sangcomz.fishbun.ui.picker

import android.net.Uri
import com.sangcomz.fishbun.ui.picker.model.PickerListItem
import com.sangcomz.fishbun.ui.picker.model.PickerRepository
import com.sangcomz.fishbun.util.UiHandler
import com.sangcomz.fishbun.util.future.CallableFutureTask
import com.sangcomz.fishbun.util.future.FutureCallback
import com.sangcomz.fishbun.util.getRealPathFromURI
import java.net.URLConnection

/**
 * Created by sangcomz on 2015-11-05.
 */
class PickerPresenter internal constructor(
    private val pickerView: PickerContract.View,
    private val pickerRepository: PickerRepository,
    private val uiHandler: UiHandler
) : PickerContract.Presenter {

    private var imageListFuture: CallableFutureTask<List<Uri>>? = null
    private var dirPathFuture: CallableFutureTask<String>? = null

    override fun addAddedPath(addedImagePath: Uri) {
        pickerRepository.addAddedPath(addedImagePath)
    }

    override fun addAllAddedPath(addedImagePathList: List<Uri>) {
        pickerRepository.addAllAddedPath(addedImagePathList)
    }

    override fun getAddedImagePathList() = pickerRepository.getAddedPathList()

    override fun getPickerListItem() {
        val albumData = pickerRepository.getPickerAlbumData() ?: return

        imageListFuture = getAllMediaThumbnailsPath(albumData.albumId)
            .also {
                it.execute(object : FutureCallback<List<Uri>> {
                    override fun onSuccess(result: List<Uri>) {
                        onSuccessAllMediaThumbnailsPath(result)
                        val imageCount = getImageCount(result)
                        val videoCount = result.size - imageCount
                        pickerView.setBottomBar(imageCount, videoCount)
                    }
                })
            }
    }

    override fun transImageFinish() {
        val albumData = pickerRepository.getPickerAlbumData() ?: return

        pickerView.takeANewPictureWithFinish(
            albumData.albumPosition,
            pickerRepository.getAddedPathList()
        )
    }

    override fun successTakePicture(addedImagePath: Uri) {
        addAddedPath(addedImagePath)
        updatePickerListItem()
    }

    override fun getDesignViewData() {
        val viewData = pickerRepository.getPickerViewData()
        with(pickerView) {
            initToolBar(viewData)
            initRecyclerView(viewData)
        }
        setToolbarTitle()
    }

    override fun onClickThumbCount(position: Int) {
        changeImageStatus(position)
    }

    override fun onClickMenuDone() {
        val selectedCount = pickerRepository.getSelectedImageList().size
        when {
            selectedCount == 0 -> {
                pickerView.showNothingSelectedMessage(pickerRepository.getMessageNotingSelected())
            }

            selectedCount < pickerRepository.getMinCount() -> {
                pickerView.showMinimumImageMessage(pickerRepository.getMinCount())
            }
            else -> {
                pickerView.finishActivity()
            }
        }
    }

    override fun onClickMenuAllDone() {
        pickerRepository.getPickerImages().forEach {
            if (pickerRepository.isLimitReached()) {
                return@forEach
            }
            if (pickerRepository.isNotSelectedImage(it)) {
                pickerRepository.selectImage(it)
            }
        }
        pickerView.finishActivity()
    }

    override fun release() {
        dirPathFuture?.cancel(true)
        imageListFuture?.cancel(true)
    }

    private fun changeImageStatus(position: Int) {
        val imagePosition = getImagePosition(position)
        val imageUri = pickerRepository.getPickerImage(imagePosition)

        if (pickerRepository.isNotSelectedImage(imageUri)) {
            selectImage(position, imageUri)
        } else {
            unselectImage(position, imageUri)
        }
    }

    private fun onCheckStateChange(position: Int, imageUri: Uri) {
        pickerView.onCheckStateChange(
            position,
            PickerListItem.Image(
                imageUri,
                pickerRepository.getSelectedIndex(imageUri),
                pickerRepository.getPickerViewData()
            )
        )
    }

    private fun selectImage(position: Int, imageUri: Uri) {
        if (pickerRepository.isLimitReached()) {
            pickerView.showLimitReachedMessage(pickerRepository.getMessageLimitReached())
            return
        }

        pickerRepository.selectImage(imageUri)

        if (pickerRepository.checkForFinish()) {
            finish()
        } else {
            onCheckStateChange(position, imageUri)
            setToolbarTitle()
        }
    }

    private fun unselectImage(position: Int, imageUri: Uri) {
        pickerRepository.unselectImage(imageUri)

        onCheckStateChange(position, imageUri)
        setToolbarTitle()
    }

    private fun setToolbarTitle() {
        val albumName = pickerRepository.getPickerAlbumData()?.albumName ?: ""
        pickerView.setToolbarTitle(
            pickerRepository.getPickerViewData(),
            pickerRepository.getSelectedImageList().size,
            albumName
        )
    }

    private fun getImagePosition(position: Int) = position

    private fun getAllMediaThumbnailsPath(
        albumId: Long,
        clearCache: Boolean = false
    ): CallableFutureTask<List<Uri>> {
        return pickerRepository.getAllBucketImageUri(albumId, clearCache)
    }

    private fun onSuccessAllMediaThumbnailsPath(imageUriList: List<Uri>) {
        pickerRepository.setCurrentPickerImageList(imageUriList)

        val viewData = pickerRepository.getPickerViewData()
        val selectedImageList = pickerRepository.getSelectedImageList().toMutableList()
        val pickerList = arrayListOf<PickerListItem>()

        imageUriList.map {
            PickerListItem.Image(it, selectedImageList.indexOf(it), viewData)
        }.also {
            pickerList.addAll(it)
            uiHandler.run {
                pickerView.showImageList(
                    pickerList,
                    pickerRepository.getImageAdapter(),
                    false
                )
                setToolbarTitle()
            }
        }
    }

    private fun getImageCount(uris: List<Uri>): Int {
        var imageCount = 0
        uris.forEach {
            val path = getRealPathFromURI(pickerView.getContext(), it)
            val mimeType: String = URLConnection.guessContentTypeFromName(path)
            if (mimeType.startsWith("image")) {
                imageCount++
            }
        }
        return imageCount
    }

    private fun updatePickerListItem() {
        val albumData = pickerRepository.getPickerAlbumData() ?: return

        imageListFuture = getAllMediaThumbnailsPath(albumData.albumId, true)
            .also {
                it.execute(object : FutureCallback<List<Uri>> {
                    override fun onSuccess(result: List<Uri>) {
                        onSuccessAllMediaThumbnailsPath(result)
                    }
                })
            }
    }

    private fun finish() {
        if (pickerRepository.isStartInAllView()) {
            pickerView.finishActivityWithResult(pickerRepository.getSelectedImageList())
        } else {
            pickerView.finishActivity()
        }
    }
}