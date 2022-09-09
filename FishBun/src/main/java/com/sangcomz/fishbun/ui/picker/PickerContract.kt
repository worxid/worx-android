package com.sangcomz.fishbun.ui.picker

import android.content.Context
import android.net.Uri
import com.sangcomz.fishbun.adapter.image.ImageAdapter
import com.sangcomz.fishbun.ui.picker.model.PickerListItem
import com.sangcomz.fishbun.ui.picker.model.PickerViewData

interface PickerContract {
    interface View {
        fun showImageList(
            imageList: List<PickerListItem>,
            adapter: ImageAdapter,
            hasCameraInPickerPage: Boolean
        )

        fun setToolbarTitle(
            pickerViewData: PickerViewData,
            selectedCount: Int,
            albumName: String
        )

        fun initToolBar(pickerViewData: PickerViewData)
        fun initRecyclerView(pickerViewData: PickerViewData)
        fun showLimitReachedMessage(messageLimitReached: String)
        fun showMinimumImageMessage(currentSelectedCount: Int)
        fun showNothingSelectedMessage(messageNotingSelected: String)
        fun onCheckStateChange(position: Int, image: PickerListItem.Image)
        fun finishActivity()
        fun finishActivityWithResult(selectedImages: List<Uri>)
        fun takeANewPictureWithFinish(position: Int, addedImageList: List<Uri>)
        fun addImage(pickerListImage: PickerListItem.Image)
        fun setBottomBar(imageCount: Int, videoCount: Int)
        fun getContext(): Context
    }

    interface Presenter {
        fun getAddedImagePathList(): List<Uri>
        fun addAddedPath(addedImagePath: Uri)
        fun addAllAddedPath(addedImagePathList: List<Uri>)
        fun release()
        fun successTakePicture(addedImagePath: Uri)
        fun getPickerListItem()
        fun transImageFinish()
        fun getDesignViewData()
        fun onClickThumbCount(position: Int)
        fun onClickMenuDone()
        fun onClickMenuAllDone()
    }
}