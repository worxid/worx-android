package id.worx.device.client.repository

import id.worx.device.client.data.DAO.DraftDAO
import id.worx.device.client.data.api.WorxApi
import id.worx.device.client.model.SubmitForm
import javax.inject.Inject

class HomeRepository @Inject constructor(
    private val retrofitService: WorxApi,
    private val draftDAO: DraftDAO
) {
    suspend fun fetchAllListTemplate() =
        retrofitService.fetchAllTemplate()

    suspend fun submitForm(formFilled: SubmitForm) =
        retrofitService.submitForm(formFilled)

    fun getAllDraftForm() =
        draftDAO.getAllDraft()

    suspend fun saveDraft(form: SubmitForm) =
        draftDAO.insertOrUpdateDraft(form)

}