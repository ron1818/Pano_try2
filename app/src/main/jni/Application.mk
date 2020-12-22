APP_STL := gnustl_static
APP_CPPFLAGS := -frtti -fexceptions
 # : I need only arm64-v8a and armeabi-v7a. You can set it yourself.
APP_ABI := arm64-v8a armeabi-v7a
APP_PLATFORM := android-8