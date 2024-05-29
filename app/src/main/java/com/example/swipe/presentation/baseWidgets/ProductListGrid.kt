package com.example.swipe.presentation.baseWidgets

import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.requiredWidthIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.example.swipe.datamodels.ProductListItem

@Composable
fun ProductGridHost(
    modifier: Modifier = Modifier,
    productPagingItem: LazyPagingItems<ProductListItem>? = null,
    productItemList: List<ProductListItem>? = null
) {
    if (productPagingItem != null) {
        ProductPagingGrid(modifier = modifier, productPagingItem = productPagingItem)
    }

    if (productItemList != null) {
        ProductListGrid(modifier = modifier, productItemList = productItemList)
    }

}

@Composable
fun BaseProductItemGrid(
    modifier: Modifier = Modifier,
    columns: StaggeredGridCells = StaggeredGridCells.Adaptive(150.dp),
    state: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
    contentPadding: PaddingValues = PaddingValues(16.dp),
    verticalItemSpacing: Dp = 10.dp,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(10.dp),
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
    content: LazyStaggeredGridScope.() -> Unit
) {
    LazyVerticalStaggeredGrid(
        modifier = modifier,
        columns = columns,
        state = state,
        verticalItemSpacing = verticalItemSpacing,
        contentPadding = contentPadding,
        horizontalArrangement = horizontalArrangement,
        flingBehavior = flingBehavior,
        content = content
    )
}

@Composable
fun ProductListGrid(
    modifier: Modifier = Modifier,
    productItemList: List<ProductListItem>
) {
    BaseProductItemGrid(modifier = modifier) {
        items(productItemList, key = { it.key }) {
            ProductItem(
                modifier = Modifier
                    .requiredHeightIn(min = 200.dp)
                    .requiredWidthIn(min = 80.dp),
                productListItem = it,
            )
        }
    }
}


@Composable
fun ProductPagingGrid(
    modifier: Modifier = Modifier,
    productPagingItem: LazyPagingItems<ProductListItem>
) {
    BaseProductItemGrid(
        modifier = modifier
    ) {
        items(
            count = productPagingItem.itemCount,
            key = productPagingItem.itemKey { it.key }
        ) {
            productPagingItem[it]?.let { productListItem ->
                ProductItem(
                    modifier = Modifier
                        .requiredHeightIn(min = 200.dp)
                        .requiredWidthIn(min = 80.dp),
                    productListItem = productListItem
                )
            }

        }
        item {
            if (productPagingItem.loadState.append is LoadState.Loading) {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            }
        }
    }
}