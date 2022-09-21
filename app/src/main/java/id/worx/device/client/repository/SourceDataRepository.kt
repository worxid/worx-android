package id.worx.device.client.repository

import id.worx.device.client.data.api.WorxApi
import id.worx.device.client.data.dao.FormDAO
import id.worx.device.client.data.dao.SubmitFormDAO
import id.worx.device.client.model.EmptyForm
import id.worx.device.client.model.ListFormResponse
import id.worx.device.client.model.SubmitForm
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import javax.inject.Inject

class SourceDataRepository @Inject constructor(
    private val retrofitService: WorxApi,
    private val dao: FormDAO,
    private val submitFormDAO: SubmitFormDAO,
) {

    companion object {
        const val TAG = "Worx FormDataRepo"
    }

    suspend fun fetchAllForm(): Response<ListFormResponse> =
        retrofitService.fetchAllTemplate()

    suspend fun addAllFormTemplate(list: List<EmptyForm>) {
        dao.deleteAndCreate(list)
    }

    fun getAllFormFromDB(): Flow<List<EmptyForm>> =
        dao.getAllForm()

    fun getAllDraftForm() =
        submitFormDAO.getAllDraft()

    fun getAllSubmission() =
        submitFormDAO.getAllSubmission()

    fun getAllUnsubmitted() =
        submitFormDAO.getAllUnsubmitted()

    suspend fun submitForm(form: SubmitForm) =
        retrofitService.submitForm(form)

    suspend fun insertOrUpdateSubmitForm(submitForm: SubmitForm) =
        submitFormDAO.insertOrUpdateForm(submitForm)

    suspend fun deleteSubmitFormById(id: Int) =
        submitFormDAO.deleteSubmitForm(id)
}