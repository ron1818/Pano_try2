cmake_minimum_required(VERSION 3.4.1)

# ##################### OpenCV 环境 ############################
#设置OpenCV-android-sdk路径
set(OpenCV_DIR G:/workspace/AndroidStudioProjects/opencv-4.0.1-android-sdk/sdk/native/jni)
set(CMAKE_VERBOSE_MAKEFILE on)

find_package(OpenCV REQUIRED core stitching)
if(OpenCV_FOUND)
    # set(OpenCV_INCLUDE_DIRS "${OpenCV_INCLUDE_DIRS};G:/workspace/AndroidStudioProjects/OpenCV-3.4.10-android-sdk/sdk/native/jni/include/opencv2")
    include_directories(${OpenCV_INCLUDE_DIRS})
    message(STATUS "OpenCV library status:")
    message(STATUS "    version: ${OpenCV_VERSION}")
    message(STATUS "    libraries: ${OpenCV_LIBS}")
    message(STATUS "    include path: ${OpenCV_INCLUDE_DIRS}")
else(OpenCV_FOUND)
    message(FATAL_ERROR "OpenCV library not found")
endif(OpenCV_FOUND)

if(OpenCV_stitching_FOUND)
    message(STATUS "stitching module found")
else(OpenCV_stitching_FOUND)
    message(STATUS "stitching module not found")
endif(OpenCV_stitching_FOUND)

# set(CMAKE_MODULE_LINKER_FLAGS "-Wl, --hash-style=sysv, -v")
# set(CMAKE_EXE_LINKER_FLAGS "${CMAKE_EXE_LINKER_FLAGS_RELEASE} -Wl,--hash-style=both,-lc++")
# set(CMAKE_SHARED_LINKER_FLAGS "${CMAKE_SHARED_LINKER_FLAGS_RELEASE} -Wl,--hash-style=both,-lc++")
# set(CMAKE_MODULE_LINKER_FLAGS "${CMAKE_MODULE_LINKER_FLAGS_RELEASE} -Wl,--hash-style=both,-lc++,-fuse-ld=lld, --strip-debug")
set(CMAKE_MODULE_LINKER_FLAGS  "-Wl,--gc-sections,--strip-debug -fvisibility=hidden -fuse-ld=lld")

# include_directories(${OpenCV_DIR}/include/opencv2)
#动态方式加载
add_library(lib_opencv STATIC IMPORTED ) #表示创建一个导入库，静态方式
#引入libopencv_java3.so文件
set_target_properties(lib_opencv
        PROPERTIES
        IMPORTED_LOCATION ${PROJECT_SOURCE_DIR}/src/main/jni/libs/${CMAKE_ANDROID_ARCH_ABI}/libopencv_java3.so
        )

find_library( # Sets the name of the path variable.
        log-lib
        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log )

# ###################### 项目原生模块 ###########################

# add_library( native-stitch2
#              SHARED
#              src/main/cpp/native-stitch2.cpp)
#
# target_link_libraries( native-stitch2
#         ${OPENCV_LIBS} ${log-lib} android -ljnigraphics lib_opencv opencv_core opencv_stitching)

add_library( native-lib
        SHARED
        src/main/cpp/native-lib.cpp)

target_link_libraries( native-lib
        ${OpenCV_LIBS} ${log-lib} android -ljnigraphics)
