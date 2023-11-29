package id.worx.mobile.repository

import androidx.sqlite.db.SimpleSQLiteQuery
import id.worx.mobile.data.api.WorxApi
import id.worx.mobile.data.dao.FormDAO
import id.worx.mobile.data.dao.SubmitFormDAO
import id.worx.mobile.model.EmptyForm
import id.worx.mobile.model.FormSortModel
import id.worx.mobile.model.ListFormResponse
import id.worx.mobile.model.ListSubmissionResponse
import id.worx.mobile.model.SubmitForm
import id.worx.mobile.model.fieldmodel.FilePresignedUrlResponse
import retrofit2.Response
import javax.inject.Inject

/**
 * All method related to collect, retrieve, or post online from Form database
 */
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

    suspend fun getAllFormFromDB(formSortModel: FormSortModel? = null): List<EmptyForm> {
        if (formSortModel == null) {
            return dao.getAllForm()
        }
        val rawQuery = "SELECT * FROM form ORDER BY ${formSortModel.getSortQuery()}"
        val query = SimpleSQLiteQuery(rawQuery)
        return dao.getSortedAllForms(query)
    }

    suspend fun getAllDraftForm(formSortModel: FormSortModel): List<SubmitForm> {
        val rawQuery = "SELECT * FROM submit_form WHERE status = 0 ORDER BY ${formSortModel.getSortQuery()}"
        val query = SimpleSQLiteQuery(rawQuery)
        return submitFormDAO.getSubmitForm(query)
    }

    suspend fun getAllSubmission(formSortModel: FormSortModel): List<SubmitForm> {
        val rawQuery = "SELECT * FROM submit_form WHERE status IN (1,2) ORDER BY ${formSortModel.getSortQuery()}"
        val query = SimpleSQLiteQuery(rawQuery)
        return submitFormDAO.getSubmitForm(query)
    }

    suspend fun getAllUnsubmitted() = submitFormDAO.getAllUnsubmitted()

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

    suspend fun getEmptyFormByID(id: Int): EmptyForm? {
        val list = dao.loadFormById(id)
        if (list.isNotEmpty()) {
            return list[0]
        }
        return null
    }

    suspend fun getSubmissionById(id: Int): SubmitForm? {
        val list = submitFormDAO.loadSubmitFormById(id)
        if (list.isNotEmpty()) {
            return list[0]
        }
        return null
    }
}