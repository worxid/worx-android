package com.sangcomz.fishbun.ui.picker.listener

interface OnPickerActionListener {
    fun onDeselect()
    fun onClickImage(position: Int)
    fun onClickThumbCount(position: Int)
}