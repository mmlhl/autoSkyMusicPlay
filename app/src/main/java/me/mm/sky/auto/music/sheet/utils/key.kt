package me.mm.sky.auto.music.sheet.utils

import android.graphics.Path
import me.mm.sky.auto.music.context.MyContext

class Key {
    companion object {
        val keyMap: MutableMap<String, Key> = mutableMapOf()

        fun init(x0: Int, y0: Int, x1: Int, y1: Int) {
            val perX = (x1 - x0) / 4
            val perY = (y1 - y0) / 2

            for (i in 0 until 15) {
                val x = x0 + (i % 5) * perX
                val y = y0 + (i / 5) * perY
                val keyName = i.toString()

                val key = Key().apply {
                    this.keyName = keyName
                    this.x = x
                    this.y = y
                    this.path = Path().apply {
                        moveTo(x.toFloat() - 2, y.toFloat() - 2)
                        lineTo(x.toFloat() + 2, y.toFloat() + 2)
                        lineTo(x.toFloat() - 2, y.toFloat() + 2)
                        lineTo(x.toFloat() + 2, y.toFloat() - 2)
                    }
                }
                keyMap[keyName] = key
            }

            MyContext.editInt("x0", x0)
            MyContext.editInt("y0", y0)
            MyContext.editInt("x1", x1)
            MyContext.editInt("y1", y1)
        }
    }

    private var keyName: String? = null
    var x: Int = 0
    var y: Int = 0
    private var path: Path? = null

    override fun toString(): String {
        return "Key(keyName=$keyName, x=$x, y=$y, path=$path)"
    }
}
