package id.worx.mobile.util

import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitTouchSlopOrCancellation
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.*

data class PathProperties(
    val color: Color = Color.Black,
    val stroke: Float = 5f
)

data class TextProperties(
    val color: Color = Color.Black,
    var offset: Offset = Offset.Zero
)

enum class Menu {
    Default, Text, Draw
}

enum class MotionEvent {
    Idle, Down, Move, Up
}

suspend fun AwaitPointerEventScope.awaitDragMotionEvent(
    onTouchEvent: (MotionEvent, PointerInputChange) -> Unit
) {
    // Wait for at least one pointer to press down, and set first contact position
    val down: PointerInputChange = awaitFirstDown()
    onTouchEvent(MotionEvent.Down, down)

    var pointer = down

    // 🔥 Waits for drag threshold to be passed by pointer
    // or it returns null if up event is triggered
    val change: PointerInputChange? =
        awaitTouchSlopOrCancellation(down.id) { change: PointerInputChange, over: Offset ->
            // 🔥🔥 If consumePositionChange() is not consumed drag does not
            // function properly.
            // Consuming position change causes change.positionChanged() to return false.
            if (change.positionChange() != Offset.Zero) change.consume()
        }

    if (change != null) {
        // 🔥 Calls  awaitDragOrCancellation(pointer) in a while loop
        drag(change.id) { pointerInputChange: PointerInputChange ->
            pointer = pointerInputChange
            onTouchEvent(MotionEvent.Move, pointer)
        }

        // All of the pointers are up
        onTouchEvent(MotionEvent.Up, pointer)
    } else {
        // Drag threshold is not passed and last pointer is up
        onTouchEvent(MotionEvent.Up, pointer)
    }
}

fun Modifier.dragMotionEvent(onTouchEvent: (MotionEvent, PointerInputChange) -> Unit) = this.then(
    Modifier.pointerInput(Unit) {
        forEachGesture {
            awaitPointerEventScope {
                awaitDragMotionEvent(onTouchEvent)
            }
        }
    }
)


suspend fun AwaitPointerEventScope.awaitDragMotionEvent(
    onDragStart: (PointerInputChange) -> Unit = {},
    onDrag: (PointerInputChange) -> Unit = {},
    onDragEnd: (PointerInputChange) -> Unit = {}
) {
    // Wait for at least one pointer to press down, and set first contact position
    val down: PointerInputChange = awaitFirstDown()
    onDragStart(down)

    var pointer = down

    // 🔥 Waits for drag threshold to be passed by pointer
    // or it returns null if up event is triggered
    val change: PointerInputChange? =
        awaitTouchSlopOrCancellation(down.id) { change: PointerInputChange, over: Offset ->
            // 🔥🔥 If consumePositionChange() is not consumed drag does not
            // function properly.
            // Consuming position change causes change.positionChanged() to return false.
            if (change.positionChange() != Offset.Zero) change.consume()
        }

    if (change != null) {
        // 🔥 Calls  awaitDragOrCancellation(pointer) in a while loop
        drag(change.id) { pointerInputChange: PointerInputChange ->
            pointer = pointerInputChange
            onDrag(pointer)
        }

        // All of the pointers are up
        onDragEnd(pointer)
    } else {
        // Drag threshold is not passed and last pointer is up
        onDragEnd(pointer)
    }
}

fun Modifier.dragMotionEvent(
    onDragStart: (PointerInputChange) -> Unit = {},
    onDrag: (PointerInputChange) -> Unit = {},
    onDragEnd: (PointerInputChange) -> Unit = {}
) = this.then(
    Modifier.pointerInput(Unit) {
        forEachGesture {
            awaitPointerEventScope {
                awaitDragMotionEvent(onDragStart, onDrag, onDragEnd)
            }
        }
    }
)