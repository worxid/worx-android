package id.worx.mobile.screen.welcome

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.worx.mobile.R
import id.worx.mobile.data.database.Session
import id.worx.mobile.screen.components.TransparentButton
import id.worx.mobile.screen.main.getAppLogoDrawable
import id.worx.mobile.screen.main.getAppTheme
import id.worx.mobile.theme.Typography
import id.worx.mobile.theme.LocalWorxColorsPalette
import id.worx.mobile.theme.WorxTheme

@Composable
fun VerificationRejectedScreen(session: Session, onEvent: (VerificationEvent) -> Unit) {
    val theme = session.theme
    val appLogoResourceId = theme.getAppTheme().getAppLogoDrawable()
    Surface(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .background(MaterialTheme.colors.secondary)
                .padding(vertical = 36.dp, horizontal = 16.dp)
        ) {
            Image(
                modifier = Modifier.size(78.dp, 24.dp),
                painter = painterResource(appLogoResourceId),
                contentDescription = "Worx Logo"
            )
            Spacer(modifier = Modifier.weight(1f))
            Image(
                painter = painterResource(R.drawable.ic_icon_reject),
                contentDescription = "Rejected icon",
                colorFilter = ColorFilter.tint(color = LocalWorxColorsPalette.current.icon)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                stringResource(R.string.verification_rejected),
                style = Typography.h6.copy(color = MaterialTheme.colors.onSecondary)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                stringResource(R.string.verification_rejected_description),
                style = Typography.body2.copy(color = MaterialTheme.colors.onSecondary.copy(alpha = 0.54f)),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            TransparentButton(
                onClickCallback = { onEvent(VerificationEvent.MakeNewRequest) },
                label = stringResource(R.string.make_new_request),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.weight(1.5f))
        }
    }
}


@Preview(name = "Rejected Screen", showSystemUi = true)
@Composable
fun RejectedScreenPreview() {
    WorxTheme {
        VerificationRejectedScreen(
            session = Session(LocalContext.current),
            onEvent = {}
        )
    }
}