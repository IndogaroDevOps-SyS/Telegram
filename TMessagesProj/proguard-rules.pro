-keep public class com.google.android.gms.* { public *; }
-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
    @com.google.android.gms.common.annotation.KeepName *;
}

-keep @interface androidx.annotation.Keep
-keep @androidx.annotation.Keep class * { *; }
-keepclasseswithmembers class * { @androidx.annotation.Keep *; }

-keep class id.indogaro.webrtc.* { *; }
-keep class id.indogaro.webrtc.audio.* { *; }
-keep class id.indogaro.webrtc.voiceengine.* { *; }
-keep class id.indogaro.messenger.* { *; }
-keep class id.indogaro.messenger.camera.* { *; }
-keep class id.indogaro.messenger.secretmedia.* { *; }
-keep class id.indogaro.messenger.support.* { *; }
-keep class id.indogaro.messenger.support.* { *; }
-keep class id.indogaro.messenger.time.* { *; }
-keep class id.indogaro.messenger.video.* { *; }
-keep class id.indogaro.messenger.voip.* { *; }
-keep class id.indogaro.SQLite.** { *; }
-keep class id.indogaro.tgnet.ConnectionsManager { *; }
-keep class id.indogaro.tgnet.NativeByteBuffer { *; }
-keep class id.indogaro.tgnet.RequestTimeDelegate { *; }
-keep class id.indogaro.tgnet.RequestDelegate { *; }
-keep class id.indogaro.ui.Stories.recorder.FfmpegAudioWaveformLoader { *; }
-keep class androidx.mediarouter.app.MediaRouteButton { *; }
-keepclassmembers class ** {
    @android.webkit.JavascriptInterface <methods>;
}

# https://developers.google.com/ml-kit/known-issues#android_issues
-keep class com.google.mlkit.nl.languageid.internal.LanguageIdentificationJni { *; }

# Huawei Services
-keep class com.huawei.hianalytics.**{ *; }
-keep class com.huawei.updatesdk.**{ *; }
-keep class com.huawei.hms.**{ *; }

# Don't warn about checkerframework and Kotlin annotations
-dontwarn org.checkerframework.**
-dontwarn javax.annotation.**

-keep class io.nano.tex.** {*;}

# JLatexMath: macro/atom classes are loaded reflectively by Class.forName
-keep class org.scilab.forge.jlatexmath.** { *; }
-keep class ru.noties.jlatexmath.** { *; }
-dontwarn org.scilab.forge.jlatexmath.**

# Use -keep to explicitly keep any other classes shrinking would remove
#-dontoptimize
#-dontobfuscate

-keep class id.indogaro.tgnet.** { *; }