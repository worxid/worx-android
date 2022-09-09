package com.sangcomz.fishbun.ui.album.model.repository

import com.sangcomz.fishbun.datasource.FishBunDataSource
import com.sangcomz.fishbun.datasource.ImageDataSource
import com.sangcomz.fishbun.ui.album.model.Album
import com.sangcomz.fishbun.ui.album.model.AlbumMetaData
import com.sangcomz.fishbun.ui.album.model.AlbumViewData
import com.sangcomz.fishbun.util.future.CallableFutureTask

class AlbumRepositoryImpl(
    private val imageDataSource: ImageDataSource,
    private val fishBunDataSource: FishBunDataSource
) : AlbumRepository {

    private var viewData: AlbumViewData? = null

    override fun getAlbumList(): CallableFutureTask<List<Album>> {
        return imageDataSource.getAlbumList(
            fishBunDataSource.getAllViewTitle(),
            fishBunDataSource.getExceptMimeTypeList(),
            fishBunDataSource.getSpecifyFolderList()
        )
    }

    override fun getAlbumMetaData(albumId: Long): CallableFutureTask<AlbumMetaData> {
        return imageDataSource.getAlbumMetaData(
            albumId,
            fishBunDataSource.getExceptMimeTypeList(),
            fishBunDataSource.getSpecifyFolderList()
        )
    }

    override fun getAlbumViewData(): AlbumViewData {
        return viewData ?: fishBunDataSource.getAlbumViewData().also { viewData = it }
    }

    override fun getImageAdapter() = fishBunDataSource.getImageAdapter()

    override fun getSelectedImageList() = fishBunDataSource.getSelectedImageList()

    override fun getAlbumMenuViewData() = fishBunDataSource.gatAlbumMenuViewData()

    override fun getMessageNotingSelected() = fishBunDataSource.getMessageNothingSelected()

    override fun getMinCount() = fishBunDataSource.getMinCount()
}