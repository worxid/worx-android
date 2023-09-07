package id.worx.device.client.screen.components

import android.R
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import id.worx.device.client.data.database.Session
import id.worx.device.client.model.fieldmodel.RatingField
import id.worx.device.client.model.fieldmodel.RatingValue
import id.worx.device.client.theme.SecondaryMain
import id.worx.device.client.theme.WorxCustomColorsPalette
import id.worx.device.client.viewmodel.DetailFormViewModel
import id.worx.device.client.viewmodel.EventStatus

@Composable
fun WorxRating(indexForm: Int, viewModel: DetailFormViewModel,validation : Boolean, session: Session) {
    val theme = session.theme

    val form = viewModel.uiState.collectAsState().value.detailForm!!.fields[indexForm] as RatingField
    val formStatus = viewModel.uiState.collectAsState().value.status
    val title = form.label ?: "Rating"

    val ratingValue = viewModel.uiState.collectAsState().value.values[form.id] as RatingValue?
    val rating = if (ratingValue == null){
        remember {
            mutableStateOf(0)
        }
    } else {
        remember {
            mutableStateOf(ratingValue.value!!)
        }
    }
    val warningInfo = if ((form.required == true) && (rating.value == 0)) "$title is required" else ""

    val starSize = if (form.maxStars!! <= 5) {
        42.dp
    } else if (form.maxStars!! in 6..7) {
        28.dp
    } else {
        21.dp
    }

    WorxBaseField(
        indexForm = indexForm,
        viewModel = viewModel,
        validation = validation,
        session = session,
        warningInfo = warningInfo
    ) {
        LazyRow(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(form.maxStars ?: 5) { index ->
                Icon(
                    modifier = Modifier
                        .clickable {
                            if (!arrayListOf(
                                    EventStatus.Done,
                                    EventStatus.Submitted
                                ).contains(formStatus)
                            ) {
                                rating.value = index + 1
                                viewModel.setComponentData(
                                    indexForm,
                                    RatingValue(value = index + 1)
                                )
                            }
                        }
                        .size(starSize),
                    painter = painterResource(id = R.drawable.star_big_off),
                    contentDescription = "Star Icon",
                    tint = if (index < (rating.value ?: 0)) {
                        WorxCustomColorsPalette.current.selectedStar
                    } else {
                        WorxCustomColorsPalette.current.unselectedStar
                    }
                )
            }
        }
    }
}