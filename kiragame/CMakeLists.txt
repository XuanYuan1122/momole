set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${PROJECT_SOURCE_DIR}/libs/${ANDROID_ABI})

cmake_minimum_required(VERSION 3.4.1)

add_library( # Sets the name of the library.
             kira
             SHARED
             src/main/cpp/kira.cpp )

find_library( # Sets the name of the path variable.
              log-lib
              log )

target_link_libraries( # Specifies the target library.
                       kira
                       ${log-lib} )

#设置生成的so动态库最后输出的路径