package id.worx.device.client.repository

import id.worx.device.client.data.api.WorxApi
import id.worx.device.client.data.dao.DraftDAO
import id.worx.device.client.data.dao.FormDAO
import id.worx.device.client.model.EmptyForm
import id.worx.device.client.model.ListFormResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import javax.inject.Inject

class SourceDataRepository @Inject constructor(
    private val retrofitService: WorxApi,
    private val dao: FormDAO,
    private val draftDAO: DraftDAO
) {

    companion object {
        const val TAG = "Worx FormDataRepo"
    }

    suspend fun fetchAllForm(): Response<ListFormResponse> =
        retrofitService.fetchAllTemplate()

    suspend fun addAllFormTemplate(list: List<EmptyForm>) {
        dao.deleteAndCreate(list)
    }

    fun getAllFormFromDB(): Flow<List<EmptyForm>> = dao.getAllForm()

    fun getAllDraftForm() =
        draftDAO.getAllDraft()
}