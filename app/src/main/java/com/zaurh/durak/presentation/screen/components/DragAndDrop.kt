package com.zaurh.durak.presentation.screen.components

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.zaurh.durak.data.remote.durak.CardPair
import com.zaurh.durak.presentation.viewmodel.DurakViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal val LocalDragTargetInfo = compositionLocalOf { DragTargetInfo() }

@Composable
fun DragTarget(
    modifier: Modifier,
    onDragStart: () -> Unit,
    onDragEnd: () -> Unit,
    dataToDrop: CardPair,
    durakViewModel: DurakViewModel,
    content: @Composable () -> Unit,
) {
    var currentPosition by remember { mutableStateOf(Offset.Zero) }
    val currentState = LocalDragTargetInfo.current
    val d = LocalDensity.current
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    Box(modifier = modifier
        .offset(
            (offsetX / d.density).dp,
            (offsetY / d.density).dp
        )
        .onGloballyPositioned {
            currentPosition = it.localToWindow(Offset.Zero)
        }
        .pointerInput(Unit) {
                detectDragGestures(onDragStart = {
                    onDragStart()
                    currentState.dataToDrop = dataToDrop
                    currentState.isDragging = true
                    currentState.dragPosition = currentPosition + it
                    currentState.draggableComposable = content
                }, onDrag = { change, dragAmount ->
                    durakViewModel.dropCard.value = dataToDrop
                    durakViewModel.isDragging.value = true
                    offsetX += dragAmount.x
                    offsetY += dragAmount.y
                    change.consumeAllChanges()
                    currentState.dragOffset += Offset(dragAmount.x, dragAmount.y)
                }, onDragEnd = {
                    onDragEnd()
                    offsetX = 0f
                    offsetY = 0f
                    currentState.isDragging = false
                    currentState.dragOffset = Offset.Zero
                    scope.launch {
                        delay(10)
                        durakViewModel.isDragging.value = false
                    }

                }, onDragCancel = {
                    onDragEnd()
                    // Reset offsets and drag state on drag cancel
                    offsetX = 0f
                    offsetY = 0f
                    currentState.dragOffset = Offset.Zero
                    currentState.isDragging = false
                })

        }) {
        content()
    }
}




@Composable
fun <CardPair> DropTarget(
    modifier: Modifier,
    content: @Composable() (BoxScope.(isInBound: Boolean, data: CardPair?) -> Unit)
) {
    val dragInfo = LocalDragTargetInfo.current
    val dragPosition = dragInfo.dragPosition
    val dragOffset = dragInfo.dragOffset
    var isCurrentDropTarget by remember {
        mutableStateOf(false)
    }

    Box(modifier = modifier.onGloballyPositioned {
        it.boundsInWindow().let { rect ->
            isCurrentDropTarget = rect.contains(dragPosition + dragOffset)
        }
    }) {
        val data =
            if (isCurrentDropTarget && !dragInfo.isDragging) dragInfo.dataToDrop as CardPair? else null
        content(isCurrentDropTarget, data)
    }
}

internal class DragTargetInfo {
    var isDragging by mutableStateOf(false)
    var dragPosition by mutableStateOf(Offset.Zero)
    var dragOffset by mutableStateOf(Offset.Zero)
    var draggableComposable by mutableStateOf<(@Composable () -> Unit)?>(null)
    var dataToDrop by mutableStateOf<CardPair?>(null)
}