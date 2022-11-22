package id.worx.device.client.screen.components

import android.R
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import id.worx.device.client.model.RatingField
import id.worx.device.client.model.RatingValue
import id.worx.device.client.theme.GrayDivider
import id.worx.device.client.theme.SecondaryMain
import id.worx.device.client.theme.Typography
import id.worx.device.client.theme.textFormDescription
import id.worx.device.client.viewmodel.DetailFormViewModel
import id.worx.device.client.viewmodel.EventStatus

@Composable
fun WorxRating(indexForm: Int, description: String, viewModel: DetailFormViewModel) {
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

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            title,
            style = Typography.body2.copy(MaterialTheme.colors.onSecondary),
            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
        )
        if (description.isNotBlank()) {
            Text(
                text = description,
                style = MaterialTheme.typography.body1.copy(textFormDescription),
                modifier = Modifier.padding(bottom = 8.dp, start = 17.dp)
            )
        }
        LazyRow(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(form.maxStars ?: 5) { index ->
                Icon(
                    modifier = Modifier.clickable {
                        if (!arrayListOf(
                                EventStatus.Done,
                                EventStatus.Submitted
                            ).contains(formStatus)
                        ) {
                            rating.value = index + 1
                            viewModel.setComponentData(indexForm, RatingValue(value = index + 1))
                        }
                    },
                    painter = painterResource(id = R.drawable.star_big_off),
                    contentDescription = "Star Icon",
                    tint = if (index < (rating.value ?: 0)) {
                        SecondaryMain
                    } else {
                        Color(0xFFE0E0E0)
                    }
                )
            }
        }
        Divider(color = GrayDivider, modifier = Modifier.padding(top = 12.dp))
    }
}