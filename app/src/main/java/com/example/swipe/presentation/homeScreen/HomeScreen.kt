package com.example.swipe.presentation.homeScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.example.swipe.R
import com.example.swipe.datamodels.ProductListItem
import com.example.swipe.presentation.baseWidgets.HomeTopBar
import com.example.swipe.presentation.baseWidgets.ProductItem
import com.example.swipe.presentation.baseWidgets.StateScreen
import com.example.swipe.presentation.navigation.NavigationDestinations
import com.example.swipe.ui.theme.LocalNetworkStatus
import com.example.swipe.utils.connectionStateHelper.ConnectionState
import com.theapache64.rebugger.Rebugger

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val status = LocalNetworkStatus.current
    var isOfflineToOnline by remember { mutableStateOf(false) }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

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
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
        topBar = {
            HomeTopBar(
                scrollBehavior = scrollBehavior,
                onSearchClicked = dropUnlessResumed{navHostController.navigate(NavigationDestinations.SearchScreen.route)}
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /*TODO*/ },
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
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        val item = viewModel.getPaginatedProducts().collectAsLazyPagingItems()
        ProductItemContent(modifier = Modifier.padding(innerPadding), dataList = item)
    }
}

@Composable
fun ProductItemContent(
    modifier: Modifier = Modifier,
    dataList: LazyPagingItems<ProductListItem>
) {
    val loadState = dataList.loadState
    val finishedLoading = loadState.refresh !is LoadState.Loading &&
                loadState.prepend !is LoadState.Loading &&
                loadState.append  !is LoadState.Loading

    if (finishedLoading && dataList.itemCount > 0){
        ProductItemGrid(modifier,dataList = dataList)
    }
    if (finishedLoading && dataList.itemCount < 1){
        StateScreen(icon = R.drawable.ic_empty_feed, textPrimary = R.string.nothing_here, textSecondary = R.string.no_data_available)
    }
}

@Composable
fun ProductItemGrid(modifier: Modifier = Modifier, dataList: LazyPagingItems<ProductListItem>) {
    LazyVerticalStaggeredGrid(
        modifier = modifier,
        columns = StaggeredGridCells.Adaptive(150.dp),
        state = rememberLazyStaggeredGridState(),
        verticalItemSpacing = 10.dp,
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        items(dataList.itemCount , key = dataList.itemKey { it.key }) {
            dataList[it]?.let { it1 -> ProductItem(
                Modifier
                    .width(80.dp)
                    .height(200.dp), productListItem = it1) }
        }
        item {
            if (dataList.loadState.append is LoadState.Loading) {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            }
        }
    }
}