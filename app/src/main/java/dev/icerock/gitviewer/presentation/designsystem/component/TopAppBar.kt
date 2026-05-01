package dev.icerock.gitviewer.presentation.designsystem.component

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import dev.icerock.gitviewer.R
import dev.icerock.gitviewer.presentation.designsystem.icon.GVIcons
import dev.icerock.gitviewer.presentation.designsystem.theme.Blue20
import dev.icerock.gitviewer.presentation.designsystem.theme.GVTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MainTopAppBar(
    title: @Composable () -> Unit,
    onSignOutClick: () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
) {
    TopAppBar(
        title = title,
        modifier = modifier,
        navigationIcon = navigationIcon,
        actions = {
            IconButton(onClick = onSignOutClick) {
                Icon(
                    painter = painterResource(id = GVIcons.LogOut),
                    contentDescription = stringResource(id = R.string.logout_icon_description)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Blue20,
            navigationIconContentColor = Color.White,
            titleContentColor = Color.White,
            actionIconContentColor = Color.White
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MainTopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
) {
    TopAppBar(
        title = title,
        modifier = modifier,
        navigationIcon = navigationIcon,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Blue20,
            navigationIconContentColor = Color.White,
            titleContentColor = Color.White,
            actionIconContentColor = Color.White
        )
    )
}

@Preview
@Composable
private fun TopAppBarPreview() {
    GVTheme {
        MainTopAppBar(
            title = { Text(text = "Sample title") },
            onSignOutClick = {}
        )
    }
}