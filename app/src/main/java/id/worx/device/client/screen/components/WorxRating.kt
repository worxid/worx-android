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
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import id.worx.device.client.theme.GrayDivider
import id.worx.device.client.theme.SecondaryMain
import id.worx.device.client.theme.Typography
import id.worx.device.client.viewmodel.DetailFormViewModel

@Composable
fun WorxRating(indexForm: Int, viewModel: DetailFormViewModel) {
    val form = viewModel.uiState.detailForm!!.componentList[indexForm]
    val title = form.inputData.title

    val rating = if (!form.Outputdata.isNullOrEmpty()){
        remember{ mutableStateOf(Integer.parseInt(form.Outputdata))}
    } else {
        remember{ mutableStateOf(0)}
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            title,
            style = Typography.body2,
            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
        )
        LazyRow(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(5) { index ->
                Icon(
                    modifier = Modifier.clickable {
                        rating.value = index+1
                        viewModel.setComponentData(indexForm, (index + 1).toString())
                    },
                    painter = painterResource(id = R.drawable.star_big_off),
                    contentDescription = "Star Icon",
                    tint = if (index < rating.value) {
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