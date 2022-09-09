package com.sangcomz.fishbun.ui.picker

import android.content.Context
import android.graphics.Color
import android.media.MediaPlayer
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.sangcomz.fishbun.R
import com.sangcomz.fishbun.adapter.image.ImageAdapter
import com.sangcomz.fishbun.ui.picker.listener.OnPickerActionListener
import com.sangcomz.fishbun.ui.picker.model.PickerListItem
import com.sangcomz.fishbun.util.RadioWithTextButton
import com.sangcomz.fishbun.util.getRealPathFromURI
import java.net.URLConnection
import java.util.concurrent.TimeUnit

class PickerAdapter(
    private val context: Context,
    private val imageAdapter: ImageAdapter,
    private val onPickerActionListener: OnPickerActionListener,
    private val hasCameraInPickerPage: Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var pickerList: List<PickerListItem> = listOf()

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long {
        return pickerList[position].getItemId()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolderImage(
            context,
            LayoutInflater.from(parent.context)
                .inflate(R.layout.picker_item, parent, false),
            imageAdapter,
            onPickerActionListener
        ).apply {
            btnThumbCount.setOnClickListener {
                onPickerActionListener.onClickThumbCount(adapterPosition)
            }
            imgThumbImage.setOnClickListener {
                onPickerActionListener.onClickImage(adapterPosition)
            }
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.contains(UPDATE_PAYLOAD)) {
            (holder as? ViewHolderImage)?.update(pickerList[position])
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        (holder as? ViewHolderImage)?.bindData(pickerList[position])
    }

    override fun getItemCount(): Int {
        return pickerList.size
    }

    fun setPickerList(pickerList: List<PickerListItem>) {
        this.pickerList = pickerList
        notifyDataSetChanged()
    }

    fun updatePickerListItem(position: Int, image: PickerListItem.Image) {
        this.pickerList = this.pickerList.toMutableList().apply {
            set(position, image)
        }
        notifyItemChanged(position, UPDATE_PAYLOAD)
    }

    fun addImage(path: PickerListItem.Image) {
        val addedIndex = if (hasCameraInPickerPage) 1 else 0

        pickerList.toMutableList()
            .apply { add(addedIndex, path) }
            .also(this::setPickerList)
    }


    class ViewHolderImage(
        private val context: Context,
        itemView: View,
        private val imageAdapter: ImageAdapter,
        private val onPickerActionListener: OnPickerActionListener
    ) :
        RecyclerView.ViewHolder(itemView) {
        val imgThumbImage: ImageView = itemView.findViewById(R.id.img_thumb_image)
        val btnThumbCount: RadioWithTextButton = itemView.findViewById(R.id.btn_thumb_count)
        val videoIcon: ImageView = itemView.findViewById(R.id.iv_video)
        val videoLength: TextView = itemView.findViewById(R.id.tv_video_length)

        fun bindData(item: PickerListItem) {
            if (item !is PickerListItem.Image) return

            itemView.tag = item.imageUri
            val viewData = item.viewData

            if (isFileTypeVideo(context, item.imageUri)) {
                videoIcon.visibility = View.VISIBLE
                videoLength.visibility = View.VISIBLE
                videoLength.text = getVideoLength(context, item.imageUri)
            } else {
                videoIcon.visibility = View.INVISIBLE
                videoLength.visibility = View.INVISIBLE
            }

            btnThumbCount.run {
                //unselect()
                setCircleColor(viewData.themeColor)
                setTextColor(Color.WHITE)
                setStrokeColor(Color.WHITE)
            }

            initState(item.selectedIndex, viewData.maxCount == 1)

            imageAdapter.loadImage(imgThumbImage, item.imageUri)
        }

        private fun initState(selectedIndex: Int, isUseDrawable: Boolean) {
            if (selectedIndex != -1) {
                setScale(imgThumbImage, true)
                setRadioButton(isUseDrawable, (selectedIndex + 1).toString())
            } else {
                setScale(imgThumbImage, false)
            }
        }

        fun update(item: PickerListItem) {
            if (item !is PickerListItem.Image) return

            val selectedIndex = item.selectedIndex
            animScale(imgThumbImage, selectedIndex != -1, true)

            if (selectedIndex != -1) {
                setRadioButton(item.viewData.maxCount == 1, (selectedIndex + 1).toString())
            } else {
                btnThumbCount.unselect()
            }
        }

        private fun animScale(view: View, isSelected: Boolean, isAnimation: Boolean) {
            var duration = 200
            if (!isAnimation) duration = 0
            val toScale: Float = if (isSelected) .8f else 1.0f
            ViewCompat.animate(view)
                .setDuration(duration.toLong())
                .scaleX(toScale)
                .scaleY(toScale)
                .withEndAction { if (isAnimation && !isSelected) onPickerActionListener.onDeselect() }
                .start()
        }

        private fun setScale(view: View, isSelected: Boolean) {
            val toScale: Float = if (isSelected) .8f else 1.0f
            view.scaleX = toScale
            view.scaleY = toScale
        }

        private fun setRadioButton(isUseDrawable: Boolean, text: String) {
            if (isUseDrawable) {
                ContextCompat.getDrawable(
                    btnThumbCount.context,
                    R.drawable.ic_done_white_24dp
                )?.let {
                    btnThumbCount.setDrawable(it)
                }
            } else {
                btnThumbCount.setText(text)
            }
        }

        fun isFileTypeVideo(context: Context, uri: Uri): Boolean {
            val path = getRealPathFromURI(context, uri)
            val mimeType: String = URLConnection.guessContentTypeFromName(path)
            return mimeType.startsWith("video")
        }

        fun getVideoLength(context: Context, uri: Uri): String {
            var durationTime: Long = 0
            val mp = MediaPlayer.create(context, uri)
            if (mp != null) {
                durationTime = mp.duration.toLong()
                mp.release()
            }
            return String.format(
                "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(durationTime),
                TimeUnit.MILLISECONDS.toSeconds(durationTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(durationTime))
            )
        }
    }

    companion object {
        private const val UPDATE_PAYLOAD = "payload_update"
    }

}