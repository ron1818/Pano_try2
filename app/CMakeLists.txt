cmake_minimum_required(VERSION 3.4.1)

# ##################### OpenCV 环境 ############################
#设置OpenCV-android-sdk路径
set( OpenCV_DIR "G:\\workspace\\AndroidStudioProjects\\OpenCV-3.4.10-android-sdk\\sdk\\native\\jni")

find_package(OpenCV REQUIRED )
if(OpenCV_FOUND)
    include_directories(${OpenCV_INCLUDE_DIRS})
    message(STATUS "OpenCV library status:")
    message(STATUS "    version: ${OpenCV_VERSION}")
    message(STATUS "    libraries: ${OpenCV_LIBS}")
    message(STATUS "    include path: ${OpenCV_INCLUDE_DIRS}")
else(OpenCV_FOUND)
    message(FATAL_ERROR "OpenCV library not found")
endif(OpenCV_FOUND)
# set(CMAKE_MODULE_LINKER_FLAGS "-Wl, --hash-style=sysv, -v")
set(CMAKE_EXE_LINKER_FLAGS "${CMAKE_EXE_LINKER_FLAGS_RELEASE} -Wl,--hash-style=both,-lc++")
set(CMAKE_SHARED_LINKER_FLAGS "${CMAKE_SHARED_LINKER_FLAGS_RELEASE} -Wl,--hash-style=both,-lc++")
set(CMAKE_MODULE_LINKER_FLAGS "${CMAKE_MODULE_LINKER_FLAGS_RELEASE} -Wl,--hash-style=both,-lc++")

# ###################### 项目原生模块 ###########################

# add_library( native-stitch2
#              SHARED
#              src/main/cpp/native-stitch2.cpp)
#
# target_link_libraries( native-stitch2
#                        ${OpenCV_LIBS}
#                        log
#                        jnigraphics)
add_library( native-lib
        SHARED
        src/main/cpp/native-lib.cpp)

target_link_libraries( native-lib
        ${OpenCV_LIBS}
        log
        jnigraphics)
