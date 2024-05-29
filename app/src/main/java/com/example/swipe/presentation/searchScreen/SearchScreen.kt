package com.example.swipe.presentation.searchScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation.NavHostController
import com.example.swipe.presentation.baseViewState.ScreenState
import com.example.swipe.presentation.baseWidgets.BaseScaffold
import com.example.swipe.presentation.baseWidgets.LoadingAnimation
import com.example.swipe.presentation.baseWidgets.ProductGridHost
import com.example.swipe.presentation.baseWidgets.SearchWidgetTopBar
import com.example.swipe.presentation.baseWidgets.StateScreen

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    searchViewModel: SearchViewModel = hiltViewModel(),
) {
    LaunchedEffect(key1 = Unit) { searchViewModel.fetchItems() }

    val uiState by searchViewModel.uiState.collectAsStateWithLifecycle()

    BaseScaffold(
        modifier = modifier,
        topBar = {
            SearchWidgetTopBar(
                modifier = Modifier.fillMaxWidth(),
                onTextChange = {
                    searchViewModel.searchItem(query = it)
                },
                onSearchClicked = {
                    searchViewModel.searchItem(query = it)
                },
                onCloseClicked = dropUnlessResumed { navController.popBackStack() }
            )
        }
    ) { innerPadding ->
        SearchScreenContentHost(
            modifier = modifier.padding(innerPadding),
            uiState = uiState
        )
    }

}


/**
 * Composable function for hosting the content of the search screen based on the provided UI state.
 *
 * This function serves as a host for displaying the content of the search screen based on the provided
 * UI state, which includes loading state, success state, and error state. It dynamically renders loading
 * animations, product grid, or error screens based on the provided UI state.
 *
 * @param modifier The modifier for the content host layout. Defaults to [Modifier].
 * @param uiState The screen state representing the UI state of the [SearchScreenState].
 */

@Composable
fun SearchScreenContentHost(
    modifier: Modifier = Modifier,
    uiState: ScreenState<SearchScreenState>,
) {
    if (uiState.isLoading) {
        Box(modifier = modifier.fillMaxSize()) {
            LoadingAnimation(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(120.dp)
            )
        }
    }

    if (uiState.isLoading.not() && uiState.error == null && uiState.data != null) {
        when (uiState.data) {
            is SearchScreenState.EmptyState -> {
                StateScreen(
                    modifier = modifier,
                    icon = uiState.data.icon,
                    textPrimary = uiState.data.textPrimary,
                    textSecondary = uiState.data.textSecondary
                )
            }

            is SearchScreenState.SuccessState -> {
                ProductGridHost(modifier = modifier, productItemList = uiState.data.dataList)
            }
        }
    }

    if (uiState.error != null) {
        StateScreen(
            icon = uiState.error.icon,
            textPrimary = uiState.error.message,
            textSecondary = 0
        )
    }
}

