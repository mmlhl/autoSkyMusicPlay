# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-keep class me.mm.auto.audio.list.database.** { *; }

-keep class me.mm.sky.auto.music.context.MyContext{
*;
}
#-keep class me.mm.sky.auto.music.net.** { *; }
-keep class me.mm.sky.auto.music.service.MyService{
*;
}
# 保留 KSP 生成的代码
-keep class **_Impl { *; }
-keep class **_Factory { *; }
-keep class **_Creator { *; }
-keep class **_Adapter { *; }
# 保留 Room 生成的代码
-keep class androidx.room.RoomDatabase { *; }

-keep class * extends androidx.room.RoomDatabase {
    *;
}


-keep @androidx.room.Dao class * {
    *;
}

-keepclassmembers class * {
    @androidx.room.* <methods>;
}
# 保留实体类及其字段
-keep class * extends androidx.room.RoomDatabase {
    @androidx.room.Entity public *;
}

-keepclassmembers class * {
    @androidx.room.ColumnInfo <fields>;
    @androidx.room.PrimaryKey <fields>;
}

