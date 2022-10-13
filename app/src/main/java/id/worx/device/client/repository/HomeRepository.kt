package id.worx.device.client.repository

import id.worx.device.client.data.api.WorxApi
import id.worx.device.client.data.dao.SubmitFormDAO
import id.worx.device.client.model.JoinTeamForm
import id.worx.device.client.model.SubmitForm
import javax.inject.Inject

class HomeRepository @Inject constructor(
    private val retrofitService: WorxApi,
    private val submitFormDAO: SubmitFormDAO
) {

    suspend fun submitForm(formFilled: SubmitForm) =
        retrofitService.submitForm(formFilled)

    suspend fun saveDraft(form: SubmitForm) =
        submitFormDAO.insertOrUpdateForm(form)

}