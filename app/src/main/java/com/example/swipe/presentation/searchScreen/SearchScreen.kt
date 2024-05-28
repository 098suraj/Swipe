package com.example.swipe.presentation.searchScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation.NavHostController
import com.example.swipe.datamodels.ProductListItem
import com.example.swipe.presentation.baseWidgets.LoadingAnimation
import com.example.swipe.presentation.baseWidgets.ProductItem
import com.example.swipe.presentation.baseWidgets.SearchWidget
import com.example.swipe.presentation.baseWidgets.StateScreen
import com.example.swipe.ui.theme.LocalNetworkStatus
import com.example.swipe.utils.connectionStateHelper.ConnectionState

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    searchViewModel: SearchViewModel = hiltViewModel(),
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val status = LocalNetworkStatus.current
    var isOfflineToOnline by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = Unit) {
        searchViewModel.fetchItems()
    }

    val uiState by searchViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = status) {
        when (status) {
            ConnectionState.Available -> {
                if (isOfflineToOnline) {
                    snackbarHostState.showSnackbar("You are online")
                }
            }

            ConnectionState.Empty -> {}
            ConnectionState.Unavailable -> {
                isOfflineToOnline = true
                snackbarHostState.showSnackbar("You are offline")
            }
        }
    }

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
        topBar = {
            SearchWidget(
                onTextChange = {
                    searchViewModel.searchItem(query = it)
                },
                onSearchClicked = {
                    searchViewModel.searchItem(query = it)
                },
                onCloseClicked = dropUnlessResumed { navController.popBackStack() }
            )
        },
        content = { innerPadding ->
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    LoadingAnimation(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(120.dp)
                    )
                }
            }

            if (uiState.isLoading.not() &&  uiState.error == null) {
                uiState.data?.let {
                    when (it) {
                        is SearchScreenState.EmptyState -> {
                            StateScreen(icon = it.icon, textPrimary = it.textPrimary, textSecondary = it.textSecondary)
                        }

                        is SearchScreenState.SuccessState -> {
                            ProductItemGrid(
                                modifier = Modifier.padding(innerPadding),
                                list = it.dataList
                            )
                        }
                    }
                }
            }

            if (uiState.error != null){
                uiState.error?.let { StateScreen(icon = it.icon, textPrimary = it.message, textSecondary = 0) }
            }
        }
    )
}

@Composable
fun ProductItemGrid(
    modifier: Modifier = Modifier,
    list: List<ProductListItem>
) {
    LazyVerticalStaggeredGrid(
        modifier = modifier,
        columns = StaggeredGridCells.Adaptive(150.dp),
        state = rememberLazyStaggeredGridState(),
        verticalItemSpacing = 10.dp,
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(list, key = { it.key }) {
            ProductItem(
                Modifier
                    .width(80.dp)
                    .height(200.dp),
                productListItem = it
            )
        }
    }
}




