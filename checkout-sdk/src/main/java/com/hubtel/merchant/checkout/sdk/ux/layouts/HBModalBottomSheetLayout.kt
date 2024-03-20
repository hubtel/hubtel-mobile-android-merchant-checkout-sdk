package com.hubtel.merchant.checkout.sdk.ux.layouts


import androidx.compose.animation.core.AnimationSpec
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.hubtel.merchant.checkout.sdk.ux.theme.HubtelTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HBModalBottomSheetLayout(
    sheetContent: @Composable BoxScope.() -> Unit,
    modifier: Modifier = Modifier,
    sheetState: ModalBottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden),
    sheetBackgroundColor: Color = HubtelTheme.colors.uiBackground2,
    scrimColor: Color = Color.Black.copy(alpha = 0.3f),
    sheetShape: Shape = HubtelTheme.shapes.large.copy(
        bottomStart = CornerSize(0.dp),
        bottomEnd = CornerSize(0.dp),
    ),
    sheetElevation: Dp = ModalBottomSheetDefaults.Elevation,
    content: @Composable () -> Unit,
) {
    ModalBottomSheetLayout(
        sheetState = sheetState,
        modifier = modifier,
        sheetBackgroundColor = sheetBackgroundColor,
        scrimColor = scrimColor,
        sheetShape = sheetShape,
        sheetElevation = sheetElevation,
        sheetContent = {
            Box(modifier = Modifier.defaultMinSize(minHeight = 1.dp)) {
                sheetContent()
            }
        }
    ) { content() }
}


@OptIn(
    ExperimentalMaterialNavigationApi::class, ExperimentalMaterialApi::class
)
@Composable
fun HBModalBottomSheetLayout(
    bottomSheetNavigator: BottomSheetNavigator,
    modifier: Modifier = Modifier,
    sheetBackgroundColor: Color = HubtelTheme.colors.uiBackground2,
    scrimColor: Color = Color.Black.copy(alpha = 0.3f),
    sheetShape: Shape = HubtelTheme.shapes.large.copy(
        bottomStart = CornerSize(0.dp),
        bottomEnd = CornerSize(0.dp),
    ),
    sheetElevation: Dp = ModalBottomSheetDefaults.Elevation,
    content: @Composable () -> Unit
) {
    ModalBottomSheetLayout(
        bottomSheetNavigator,
        modifier = modifier,
        sheetBackgroundColor = sheetBackgroundColor,
        scrimColor = scrimColor,
        sheetShape = sheetShape,
        sheetElevation = sheetElevation,
    ) { content() }
}


/**
 * Custom bottom sheet navigator with temporal work around to skip the
 * half expanded state for bottom sheets attached to the navation
 * [Read More](https://github.com/google/accompanist/issues/657#issuecomment-938249186)*/
@OptIn(
    ExperimentalMaterialApi::class,
    ExperimentalMaterialNavigationApi::class,
)
@Composable
fun rememberBottomSheetNavigator(
    animationSpec: AnimationSpec<Float> = SwipeableDefaults.AnimationSpec,
    skipHalfExpanded: Boolean = true,
): BottomSheetNavigator {
    val sheetState = rememberModalBottomSheetState(
        ModalBottomSheetValue.Hidden,
        animationSpec
    )

    if (skipHalfExpanded) {
        LaunchedEffect(sheetState) {
            snapshotFlow { sheetState.isAnimationRunning }
                .collect {
                    with(sheetState) {
                        val isOpening =
                            currentValue == ModalBottomSheetValue.Hidden && targetValue == ModalBottomSheetValue.HalfExpanded
                        val isClosing =
                            currentValue == ModalBottomSheetValue.Expanded && targetValue == ModalBottomSheetValue.HalfExpanded
                        when {
                            isOpening -> animateTo(ModalBottomSheetValue.Expanded)
                            isClosing -> animateTo(ModalBottomSheetValue.Hidden)
                        }
                    }
                }
        }
    }

    return remember(sheetState) {
        BottomSheetNavigator(sheetState = sheetState)
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun rememberModalBottomSheetController(
    initialValue: ModalBottomSheetValue = ModalBottomSheetValue.Hidden,
    animationSpec: AnimationSpec<Float> = SwipeableDefaults.AnimationSpec,
    confirmStateChange: (ModalBottomSheetValue) -> Boolean = { true },
    getContext: @DisallowComposableCalls () -> CoroutineContext = { EmptyCoroutineContext }
): ModalBottomSheetController {

    val coroutineScope = rememberCoroutineScope(getContext)
    val bottomSheetState = rememberModalBottomSheetState(
        initialValue,
        animationSpec,
        confirmStateChange,
    )


    return remember { ModalBottomSheetController(bottomSheetState, coroutineScope) }
}

@OptIn(ExperimentalMaterialApi::class)
class ModalBottomSheetController internal constructor(
    val sheetState: ModalBottomSheetState,
    private val coroutineScope: CoroutineScope,
) {

    fun hide() {
        coroutineScope.launch {
            sheetState.hide()
        }
    }

    fun show() {
        coroutineScope.launch {
            sheetState.show()
        }
    }

    fun animateTo(
        targetValue: ModalBottomSheetValue,
        anim: AnimationSpec<Float> = SwipeableDefaults.AnimationSpec
    ) {
        coroutineScope.launch {
            sheetState.animateTo(targetValue, anim)
        }
    }

    fun snapTo(targetValue: ModalBottomSheetValue) {
        coroutineScope.launch {
            sheetState.snapTo(targetValue)
        }
    }

    fun performFling(velocity: Float) {
        coroutineScope.launch {
            sheetState.performFling(velocity)
        }
    }

    fun performDrag(delta: Float): Float {
        return sheetState.performDrag(delta)
    }
}