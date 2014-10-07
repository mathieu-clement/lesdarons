#ifndef DEBUG
#    define printDebugMsg(msg) ((void)0)
#    define printDebugMsgEndl(msg) ((void)0)
#    define printDebugValue(x) ((void)0)
#    define printDebugValueEndl(x) ((void)0)
#else
#    define printDebugMsg(msg) std::cout << " * " << (msg)
#    define printDebugMsgEndl(msg) std::cout << " * " << (msg) << std::endl
#    define printDebugValue(x) std::cout << " * " <<  #x " = " << (x) << "  "
#    define printDebugValueEndl(x) std::cout << " * " << #x " = " << (x) << std::endl
#endif

