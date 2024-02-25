package me.mm.sky.auto.music.ui.data

import androidx.annotation.StringRes

data class SettingItem(
var key:String,
var type:Any,
var value:Any,
@StringRes var title:Int,
@StringRes var description:Int
)
