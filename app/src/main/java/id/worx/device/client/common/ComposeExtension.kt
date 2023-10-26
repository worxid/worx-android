package id.worx.device.client.common

import androidx.compose.ui.Modifier

fun Modifier.applyIf(condition : Boolean, modifier : Modifier.() -> Modifier) : Modifier {
    return if (condition) {
        then(modifier(Modifier))
    } else {
        this
    }
}