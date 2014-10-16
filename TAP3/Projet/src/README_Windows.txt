Tools
-----

    Install CMAKE and add the bin\ folder from the installation to your PATH
    Install MinGW (or use another toolchain such as Visual Studio, LLVM, ...)
        and add the bin\ folder to your PATH.

    You should now be able to open the Terminal (cmd.exe) and run
        cmake --version
        g++ --version
        mingw32-make --version

Compiling
---------

From the src/ folder:

    mkdir build
    cd build
    cmake-gui
        Set the source folder and the build folder
        Click the [Configure] button
        Click the [Generate] button
    mingw32-cmake

The executable is compiled with debug options.

Running
-------

From the build/ folder:

    .\TAP3Project
