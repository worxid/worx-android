package id.worx.device.client.domain

import android.content.Context
import android.location.Geocoder
import android.os.Build
import com.google.android.gms.maps.model.LatLng
import id.worx.device.client.common.base.BaseUseCase
import id.worx.device.client.common.exception.UnknownErrorException
import id.worx.device.client.data.database.Session
import id.worx.device.client.domain.request.GetSubmitLocationRequest
import id.worx.device.client.model.SubmitLocation
import java.util.Locale
import javax.inject.Inject
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class GetSubmitLocationUseCase @Inject constructor() :
    BaseUseCase<GetSubmitLocationRequest, SubmitLocation>() {

    override suspend fun execute(request: GetSubmitLocationRequest): SubmitLocation {
        return getSubmitLocation(request.context, getLatLngFromSession(request.session))
    }

    private suspend fun getSubmitLocation(context: Context, latLng: LatLng) =
        suspendCoroutine { continuation ->
            val geocoder = Geocoder(context, Locale.getDefault())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1) { p0 ->
                    val address = p0[0]
                    val userAddress = SubmitLocation(
                        address.getAddressLine(0),
                        latLng.latitude,
                        latLng.longitude
                    )
                    continuation.resumeWith(Result.success(userAddress))
                }
            } else {
                val address =
                    geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)?.get(0)
                if (address != null) {
                    val userAddress = SubmitLocation(
                        address.getAddressLine(0),
                        latLng.latitude,
                        latLng.longitude
                    )
                    continuation.resumeWith(Result.success(userAddress))
                } else {
                    continuation.resumeWithException(UnknownErrorException())
                }
            }
        }

    private fun getLatLngFromSession(session: Session): LatLng {
        val latitude = session.latitude ?: "0.0010"
        val longitude = session.longitude ?: "-109.3222"
        return LatLng(latitude.toDouble(), longitude.toDouble())
    }
}