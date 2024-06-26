package com.example.swipe.presentation.homeScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.swipe.R
import com.example.swipe.datamodels.ProductListItem
import com.example.swipe.presentation.addProductScreen.AddProductBottomSheet
import com.example.swipe.presentation.baseWidgets.BaseScaffold
import com.example.swipe.presentation.baseWidgets.HomeTopBar
import com.example.swipe.presentation.baseWidgets.LoadingAnimation
import com.example.swipe.presentation.baseWidgets.ProductGridHost
import com.example.swipe.presentation.baseWidgets.StateScreen
import com.example.swipe.presentation.navigation.NavigationDestinations

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    var showBottomSheet by remember {
        mutableStateOf(false)
    }
    val productPagingItem = viewModel.getPaginatedProducts().collectAsLazyPagingItems()

    BaseScaffold(
        modifier = modifier,
        topBar = {
            HomeTopBar(
                onSearchClicked = dropUnlessResumed {
                    navHostController.navigate(
                        NavigationDestinations.SearchScreen.route
                    ){
                        launchSingleTop = true
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showBottomSheet = true },
                modifier = Modifier.padding(top = 8.dp, bottom = 32.dp),
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Image",
                    modifier = Modifier.size(18.dp)
                )
            }
        },
        floatingActionButtonPosition = remember { FabPosition.End }
    )
    { innerPadding ->
        HomeScreenContentHost(
            modifier = Modifier.padding(innerPadding),
            productPagingItem = productPagingItem
        )

        if (showBottomSheet) {
            AddProductBottomSheet(
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(bottom = 30.dp),
                onDismissRequest = { showBottomSheet = false },
                invalidatedCallback = {
                    productPagingItem.refresh()
                }
            )
        }

    }
}

/**
 * Composable function for hosting the content of the home screen.
 *
 * This function serves as a host for displaying the content of the home screen,
 * which typically includes a grid of product items loaded from a paginated list.
 * It also handles different loading states and displays appropriate UI components
 * such as loading animations or empty state screens.
 *
 * @param modifier The modifier for the content host layout. Defaults to [Modifier].
 * @param productPagingItem The lazy paging items representing the paginated list of product items.
 */

@Composable
fun HomeScreenContentHost(
    modifier: Modifier = Modifier,
    productPagingItem: LazyPagingItems<ProductListItem>
) {
    val loadState = productPagingItem.loadState
    val finishedLoading = loadState.refresh !is LoadState.Loading &&
            loadState.prepend !is LoadState.Loading &&
            loadState.append !is LoadState.Loading

    if (!finishedLoading) {
        Box(modifier = modifier.fillMaxSize()) {
            LoadingAnimation(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(120.dp)
            )
        }
    }
    if (finishedLoading && productPagingItem.itemCount > 0) {
        ProductGridHost(modifier = modifier, productPagingItem = productPagingItem)
    }
    if (finishedLoading && productPagingItem.itemCount < 1) {
        StateScreen(
            modifier = modifier,
            icon = R.drawable.ic_empty_feed,
            textPrimary = R.string.nothing_here,
            textSecondary = R.string.no_data_available
        )
    }
}

