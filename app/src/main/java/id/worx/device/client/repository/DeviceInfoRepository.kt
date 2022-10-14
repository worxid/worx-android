package id.worx.device.client.repository

import id.worx.device.client.data.api.WorxApi
import id.worx.device.client.model.JoinTeamForm
import javax.inject.Inject

class DeviceInfoRepository @Inject constructor(
    private val retrofitService: WorxApi
) {

    suspend fun getDeviceStatus() =
        retrofitService.getDeviceInfo()

    suspend fun joinTeam(joinTeamForm: JoinTeamForm) =
        retrofitService.joinTeam(joinTeamForm)

    suspend fun leaveTeam(deviceCode : String) =
        retrofitService.leaveTeam()
}