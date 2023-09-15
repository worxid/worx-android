package id.worx.mobile.repository

import id.worx.mobile.data.api.WorxApi
import id.worx.mobile.model.DeviceInfo
import id.worx.mobile.model.JoinTeamForm
import id.worx.mobile.model.NewTeamForm
import javax.inject.Inject

class DeviceInfoRepository @Inject constructor(
    private val retrofitService: WorxApi
) {

    suspend fun getDeviceStatus() = retrofitService.getDeviceInfo()

    suspend fun updateDeviceInfo(deviceInfo: DeviceInfo) =
        retrofitService.updateDeviceInfo(deviceInfo)

    suspend fun joinTeam(joinTeamForm: JoinTeamForm) = retrofitService.joinTeam(joinTeamForm)

    suspend fun leaveTeam(deviceCode: String) =
        retrofitService.leaveTeam()

    suspend fun createNewTeam(newTeamForm: NewTeamForm) = retrofitService.createNewTeam(newTeamForm)
}