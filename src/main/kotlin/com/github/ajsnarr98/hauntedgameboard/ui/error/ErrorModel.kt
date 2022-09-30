package com.github.ajsnarr98.hauntedgameboard.ui.error

import kotlin.math.log

sealed interface ErrorModel {
    val isFatal: Boolean

    /** Considered non-retryable if onRetry is null. */
    val onRetry: (() -> Unit)?

    val userFacingMessage: String
    val loggableMessage: String
    val cause: Throwable? // optional
}

class GenericErrorModel private constructor(
    override val isFatal: Boolean,
    override val onRetry: (() -> Unit)?,
    override val userFacingMessage: String,
    override val loggableMessage: String,
    override val cause: Throwable?,
) : ErrorModel {
    constructor(
        userFacingMessage: String,
        loggableMessage: String,
        isFatal: Boolean = false,
        onRetry: (() -> Unit)? = null,
    ) : this(
        isFatal = isFatal,
        onRetry = onRetry,
        userFacingMessage = userFacingMessage,
        loggableMessage = loggableMessage,
        cause = null,
    )

    constructor(
        message: String,
        isFatal: Boolean = false,
        onRetry: (() -> Unit)? = null,
    ) : this(
        isFatal = isFatal,
        onRetry = onRetry,
        userFacingMessage = message,
        loggableMessage = message,
    )

    constructor(
        cause: Throwable,
        userFacingMessage: String? = null,
        isFatal: Boolean = true,
        onRetry: (() -> Unit)? = null,
    ) : this(
        isFatal = isFatal,
        onRetry = onRetry,
        userFacingMessage = userFacingMessage ?: cause.message ?: cause.javaClass.name,
        loggableMessage = cause.message ?: cause.javaClass.name,
        cause = cause,
    )
}