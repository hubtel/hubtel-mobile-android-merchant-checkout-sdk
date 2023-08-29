package com.hubtel.merchant.checkout.sdk.ux.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import kotlinx.coroutines.flow.Flow

fun Lifecycle.Event.isCreated(block: () -> Unit) {
    if(this == Lifecycle.Event.ON_CREATE) {
        block.invoke()
    }
}

fun Lifecycle.Event.hasStarted(block: () -> Unit) {
    if(this == Lifecycle.Event.ON_START) {
        block.invoke()
    }
}

fun Lifecycle.Event.hasResumed(block: () -> Unit) {
    if(this == Lifecycle.Event.ON_RESUME) {
        block.invoke()
    }
}

fun Lifecycle.Event.hasPaused(block: () -> Unit) {
    if(this == Lifecycle.Event.ON_PAUSE) {
        block.invoke()
    }
}

fun Lifecycle.Event.hasStopped(block: () -> Unit) {
    if(this == Lifecycle.Event.ON_STOP) {
        block.invoke()
    }
}

fun Lifecycle.Event.isDestroyed(block: () -> Unit) {
    if(this == Lifecycle.Event.ON_DESTROY) {
        block.invoke()
    }
}

/**
 * @return a [Flow] that emits values from the given [flow] when the [lifecycle] is
 * at least at [minActiveState] state. The emissions will be stopped when the lifecycle state
 * falls below [minActiveState] state.
 * */
@Composable
fun <T> rememberFlowWithLifecycle(
    flow: Flow<T>,
    lifecycle: Lifecycle = LocalLifecycleOwner.current.lifecycle,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED
): Flow<T> = remember(flow, lifecycle) {
    flow.flowWithLifecycle(
        lifecycle = lifecycle,
        minActiveState = minActiveState
    )
}