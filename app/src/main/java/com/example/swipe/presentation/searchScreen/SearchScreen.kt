package com.example.swipe.presentation.searchScreen

import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.swipe.datamodels.ProductListItem

@Composable
fun SearchScreen(
    navController: NavHostController,
    searchViewModel: SearchViewModel = hiltViewModel<SearchViewModel>()
) {
    val uiState by searchViewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        topBar = {
            SearchWidget(
                text = uiState.data?.searchText?:"",
                onTextChange = {
                    searchViewModel.updateSearchQuery(query = it)
                },
                onSearchClicked = {
                    searchViewModel.searchItem(query = it)
                },
                onCloseClicked = {
                    navController.popBackStack()
                }
            )
        },
        content = {
            it
            if (uiState.isLoading) {

            }

            if (uiState.isLoading && !uiState.data?.dataList.isNullOrEmpty()) {

            }

            if (uiState.error) {

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
        columns = StaggeredGridCells.Adaptive(150.dp),
        state = rememberLazyStaggeredGridState(),
        verticalItemSpacing = 10.dp,
        contentPadding = PaddingValues(4.dp),
    ) {
        items(list, key = {it.product_name}){

        }
    }
}


@Composable
fun ProductItem (modifier: Modifier = Modifier) {

}