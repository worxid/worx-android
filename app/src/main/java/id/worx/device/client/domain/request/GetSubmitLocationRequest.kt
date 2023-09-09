package id.worx.device.client.domain.request

import android.content.Context
import id.worx.device.client.data.database.Session

data class GetSubmitLocationRequest(
    val context: Context,
    val session: Session
)