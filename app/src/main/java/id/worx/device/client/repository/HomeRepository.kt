package id.worx.device.client.repository

import id.worx.device.client.data.api.WorxApi
import id.worx.device.client.model.SubmitForm
import javax.inject.Inject

class HomeRepository @Inject constructor(
    private val retrofitService: WorxApi
) {
    suspend fun fetchAllListTemplate() =
        retrofitService.fetchAllTemplate()

    suspend fun submitForm(formFilled: SubmitForm) =
        retrofitService.submitForm(formFilled)
}