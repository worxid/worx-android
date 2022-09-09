package id.worx.device.client.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.worx.device.client.R
import id.worx.device.client.model.Form
import id.worx.device.client.theme.Typography
import id.worx.device.client.viewmodel.DetailFormViewModel
import id.worx.device.client.viewmodel.HomeViewModel

@Composable
fun FormScreen(
    data: List<Form>,
    viewModel: HomeViewModel,
    detailFormViewModel: DetailFormViewModel
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxHeight()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items = data, itemContent = { item ->
            ListItemValidForm(item, viewModel, detailFormViewModel)
        })
    }
}

@Composable
fun ListItemValidForm(item: Form, viewModel: HomeViewModel, detailFormViewModel: DetailFormViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.5.dp, color = Color.Black)
            .clickable(
                onClick = {
                    viewModel.goToDetailScreen()
                    detailFormViewModel.navigateFromHomeScreen(item)
                })
    ) {
        Image(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .align(Alignment.CenterVertically),
            painter = painterResource(id = R.drawable.ic_form_gray),
            contentDescription = "Form Icon"
        )
        Column(modifier = Modifier.padding(vertical = 13.dp)) {
            Text(
                text = item.title,
                fontSize = 14.sp,
                fontFamily = FontFamily.Monospace,
                color = Color.Black,
                fontWeight = FontWeight.W400
            )
            Text(
                text = item.description,
                style = Typography.body1.copy(color = Color.Black.copy(alpha = 0.54f))
            )
        }
    }
}