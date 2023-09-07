package id.worx.device.client.repository

import androidx.sqlite.db.SimpleSQLiteQuery
import id.worx.device.client.data.api.WorxApi
import id.worx.device.client.data.dao.DraftDAO
import id.worx.device.client.data.dao.FormDAO
import id.worx.device.client.data.dao.SubmitFormDAO
import id.worx.device.client.model.DraftForm
import id.worx.device.client.model.EmptyForm
import id.worx.device.client.model.FormSortModel
import id.worx.device.client.model.ListFormResponse
import id.worx.device.client.model.ListSubmissionResponse
import id.worx.device.client.model.SubmitForm
import id.worx.device.client.model.fieldmodel.FilePresignedUrlResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import javax.inject.Inject

/**
 * All method related to collect, retrieve, or post online from Form database
 */
class SourceDataRepository @Inject constructor(
    private val retrofitService: WorxApi,
    private val dao: FormDAO,
    private val submitFormDAO: SubmitFormDAO,
    private val draftFormDAO: DraftDAO
) {

    companion object {
        const val TAG = "Worx FormDataRepo"
    }

    suspend fun fetchAllForm(): Response<ListFormResponse> =
        retrofitService.fetchAllTemplate()

    suspend fun addAllFormTemplate(list: List<EmptyForm>) {
        dao.deleteAndCreate(list)
    }

    fun getAllFormFromDB(formSortModel: FormSortModel? = null): Flow<List<EmptyForm>> {
        if (formSortModel == null) {
            return dao.getAllForm()
        }
        val rawQuery = "SELECT * FROM form ORDER BY ${formSortModel.getSortQuery()}"
        val query = SimpleSQLiteQuery(rawQuery)
        return dao.getSortedAllForms(query)
    }

    fun getAllDraftForm(formSortModel: FormSortModel): Flow<List<DraftForm>> {
        val rawQuery = "SELECT * FROM draft_form WHERE status = 0 ORDER BY ${formSortModel.getSortQuery()}"
        val query = SimpleSQLiteQuery(rawQuery)
        return draftFormDAO.getSortedDraftForms(query)
    }

    fun getAllSubmission(formSortModel: FormSortModel): Flow<List<SubmitForm>> {
        val rawQuery = "SELECT * FROM submit_form WHERE status IN (1,2) ORDER BY ${formSortModel.getSortQuery()}"
        val query = SimpleSQLiteQuery(rawQuery)
        return submitFormDAO.getSubmitForm(query)
    }

    fun getAllUnsubmitted() =
        submitFormDAO.getAllUnsubmitted()

    suspend fun getPresignedUrl(fileName: String): Response<FilePresignedUrlResponse> =
        retrofitService.getPresignedUrl(fileName)

    suspend fun submitForm(form: SubmitForm) =
        retrofitService.submitForm(form)

    suspend fun insertOrUpdateSubmitForm(submitForm: SubmitForm) =
        submitFormDAO.insertOrUpdateForm(submitForm)

    suspend fun deleteSubmitFormById(id: Int) =
        submitFormDAO.deleteSubmitForm(id)

    suspend fun fetchAllSubmission(): Response<ListSubmissionResponse> =
        retrofitService.fetchAllSubmission()

    suspend fun addSubmissionFromApiToDb(list: List<SubmitForm>) =
        submitFormDAO.addSubmissionFromApi(list)

    suspend fun getEmptyFormByID (id:Int) : EmptyForm? {
        val list = dao.loadFormById(id)
        if (list.isNotEmpty()){
            return list[0]
        }
        return null
    }

    suspend fun getSubmissionById(id: Int) : SubmitForm? {
        val list = submitFormDAO.loadSubmitFormById(id)
        if (list.isNotEmpty()){
            return list[0]
        }
        return null
    }

    suspend fun insertDraft(draftForm: DraftForm) = draftFormDAO.insertOrUpdateForm(draftForm)

    suspend fun deleteDraft(draftId: Int) = draftFormDAO.deleteDraftForm(draftId)
}