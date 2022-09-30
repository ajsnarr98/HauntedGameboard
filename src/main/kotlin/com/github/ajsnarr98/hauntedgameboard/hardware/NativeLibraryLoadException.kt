package com.github.ajsnarr98.hauntedgameboard.hardware

import java.io.IOException

class NativeLibraryLoadException : IOException {
    constructor() : super()
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
    constructor(cause: Throwable) : super(cause)
}