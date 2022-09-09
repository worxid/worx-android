package com.sangcomz.fishbun.datasource

import android.net.Uri
import com.sangcomz.fishbun.Fishton
import com.sangcomz.fishbun.ui.album.model.AlbumMenuViewData
import com.sangcomz.fishbun.ui.album.model.AlbumViewData
import com.sangcomz.fishbun.ui.picker.model.PickerViewData

class FishBunDataSourceImpl(private val fishton: Fishton) : FishBunDataSource {
    override fun getSelectedImageList(): List<Uri> = fishton.selectedImages

    override fun getPickerImages(): List<Uri> = fishton.currentPickerImageList

    override fun getMaxCount() = fishton.maxCount
    override fun getMinCount() = fishton.minCount

    override fun getIsAutomaticClose(): Boolean = fishton.isAutomaticClose

    override fun getMessageLimitReached() = fishton.messageLimitReached
    override fun getMessageNothingSelected() = fishton.messageNothingSelected

    override fun getExceptMimeTypeList() = fishton.exceptMimeTypeList
    override fun getSpecifyFolderList() = fishton.specifyFolderList
    override fun getAllViewTitle() = fishton.titleAlbumAllView
    override fun getImageAdapter() = fishton.imageAdapter

    override fun selectImage(imageUri: Uri) {
        fishton.selectedImages.add(imageUri)
    }

    override fun unselectImage(imageUri: Uri) {
        fishton.selectedImages.remove(imageUri)
    }

    override fun getAlbumViewData() = AlbumViewData(
        fishton.colorStatusBar,
        fishton.isStatusBarLight,
        fishton.colorActionBar,
        fishton.colorActionBarTitle,
        fishton.titleActionBar,
        fishton.drawableHomeAsUpIndicator,
        fishton.albumPortraitSpanCount,
        fishton.albumLandscapeSpanCount,
        fishton.albumThumbnailSize,
        fishton.maxCount,
        fishton.isShowCount,
        fishton.themeColor
    )

    override fun gatAlbumMenuViewData() = AlbumMenuViewData(
        fishton.hasButtonInAlbumActivity,
        fishton.drawableDoneButton,
        fishton.strDoneMenu,
        fishton.colorTextMenu
    )

    override fun getPickerViewData() = PickerViewData(
        fishton.colorStatusBar,
        fishton.isStatusBarLight,
        fishton.colorActionBar,
        fishton.colorActionBarTitle,
        fishton.titleActionBar,
        fishton.drawableHomeAsUpIndicator,
        fishton.albumPortraitSpanCount,
        fishton.albumLandscapeSpanCount,
        fishton.albumThumbnailSize,
        fishton.maxCount,
        fishton.isShowCount,
        fishton.colorSelectCircleStroke,
        fishton.isAutomaticClose,
        fishton.photoSpanCount,
        fishton.themeColor
    )

    override fun setCurrentPickerImageList(pickerImageList: List<Uri>) {
        fishton.currentPickerImageList = pickerImageList
    }

    override fun isStartInAllView() = fishton.isStartInAllView
}