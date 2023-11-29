package id.worx.mobile.domain.request

import android.content.Context
import id.worx.mobile.data.database.Session

data class GetSubmitLocationRequest(
    val context: Context,
    val session: Session
)