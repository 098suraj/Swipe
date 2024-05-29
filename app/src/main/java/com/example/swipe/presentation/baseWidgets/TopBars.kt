package com.example.swipe.presentation.baseWidgets

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.dropUnlessResumed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
    onSearchClicked: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) {
    val collapsed = 22
    val expanded = 28

    val isCollapsed by remember { derivedStateOf { scrollBehavior.state.collapsedFraction > 0.5 } }
    val topAppBarTextSize by remember { derivedStateOf { (collapsed + (expanded - collapsed) * (1 - scrollBehavior.state.collapsedFraction)).sp } }

    val topAppBarElementColor = if (isCollapsed) {
        MaterialTheme.colorScheme.secondaryContainer
    } else {
        MaterialTheme.colorScheme.tertiaryContainer
    }

    LargeTopAppBar(
        title = { Text(text = "Home", fontSize = topAppBarTextSize) },
        actions = {
            IconButton(onClick = dropUnlessResumed { onSearchClicked.invoke() }) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Icon"
                )
            }
        },
        colors = TopAppBarDefaults.largeTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            scrolledContainerColor = MaterialTheme.colorScheme.primary,
            navigationIconContentColor = topAppBarElementColor,
            titleContentColor = topAppBarElementColor,
            actionIconContentColor = topAppBarElementColor,
        ),
        scrollBehavior = scrollBehavior
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchWidgetTopBar(
    modifier: Modifier = Modifier,
    onTextChange: (String) -> Unit,
    onSearchClicked: (String) -> Unit,
    onCloseClicked: () -> Unit
) {
    var text by remember {
        mutableStateOf("")
    }
    Surface(
        shape = SearchBarDefaults.dockedShape,
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        tonalElevation = 6.dp,
        shadowElevation = 0.dp,
        modifier = modifier
            .padding(
                top = WindowInsets.statusBars
                    .asPaddingValues()
                    .calculateTopPadding(),
                start = 6.dp,
                end = 6.dp,
                bottom = 6.dp
            )
            .zIndex(1f)
            .width(360.dp)
    ) {
        val keyboardController = LocalSoftwareKeyboardController.current
        TextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = text,
            maxLines = 1,
            onValueChange = {
                text = it
                onTextChange(it)
            },
            placeholder = {
                Text(
                    modifier = Modifier.alpha(alpha = 0.5f),
                    text = "Search here...",
                )
            },
            singleLine = true,
            leadingIcon = {
                IconButton(
                    modifier = Modifier
                        .alpha(alpha = 0.5f),
                    onClick = onCloseClicked
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Search Icon",
                    )
                }
            },
            trailingIcon = {
                IconButton(
                    modifier = Modifier,
                    onClick = {
                        text = ""
                        onTextChange("")
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Icon",
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    onSearchClicked(text)
                    keyboardController?.hide()
                }
            ),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                cursorColor = MaterialTheme.colorScheme.onSecondaryContainer,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedLeadingIconColor = MaterialTheme.colorScheme.tertiary,
                unfocusedLeadingIconColor = MaterialTheme.colorScheme.tertiary,
                focusedTrailingIconColor = MaterialTheme.colorScheme.tertiary,
                unfocusedTrailingIconColor = MaterialTheme.colorScheme.tertiary,
                focusedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
                unfocusedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
            )
        )
    }
}


@Composable
@Preview
fun SearchWidgetPreview() {
    SearchWidgetTopBar(
        onTextChange = {},
        onSearchClicked = {},
        onCloseClicked = {}
    )
}
