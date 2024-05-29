package com.example.swipe.presentation.baseWidgets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.theapache64.rebugger.Rebugger
import com.example.swipe.datamodels.ProductListItem

/**
 * Composable function that acts as a host for displaying either a paginated grid or a list grid of products.
 *
 * This function is responsible for rendering the UI layout for displaying product items in a grid format,
 * based on either a paginated list of items or a static list of items.
 *
 * @param modifier The modifier for the product grid host layout. Defaults to [Modifier].
 * @param productPagingItem A [LazyPagingItems] object representing a paginated list of product items.
 *                          If provided, the product items will be loaded dynamically as the user scrolls.
 * @param productItemList A list of [ProductListItem] representing static product items to be displayed.
 *                        If provided, the product items will be displayed statically without pagination.
 */

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
/**
 * Base composable function for creating a staggered grid layout for displaying product items.
 *
 * This function provides a flexible foundation for rendering a staggered grid layout,
 * typically used for displaying product items in a grid format.
 */

@Composable
fun BaseProductItemGrid(
    modifier: Modifier = Modifier,
    columns: StaggeredGridCells = StaggeredGridCells.Adaptive(150.dp),
    state: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
    contentPadding: PaddingValues = PaddingValues(16.dp),
    verticalItemSpacing: Dp = 10.dp,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(10.dp),
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
    content: (LazyStaggeredGridScope.() -> Unit),

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


/**
 * Composable function for displaying a grid of product items using a static list.
 *
 * This function renders a grid layout of product items using a static list of product items.
 *
 * @param modifier The modifier for the grid layout. Defaults to [Modifier].
 * @param productItemList The list of product items to be displayed in the grid.
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ProductListGrid(
    modifier: Modifier = Modifier,
    productItemList: List<ProductListItem>
) {
    var selectedProduct by remember { mutableStateOf<ProductListItem?>(null) }

    SharedTransitionLayout(modifier = modifier) {
        BaseProductItemGrid(modifier = Modifier.fillMaxSize()) {
            items(productItemList, key = { it.key }) {
                SharedProductItemAnimatedVisibility(
                    modifier = Modifier.animateItem().fillMaxSize(),
                    productListItem = it,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    onClick = {selectedProduct = it}
                )
            }
        }
        ProductItemDetails(
            productListItem = selectedProduct,
            onConfirmClick = {
                selectedProduct = null
            }
        )
    }
}


/**
 * Composable function for displaying a grid of product items using a paginated list.
 *
 * This function renders a grid layout of product items using a paginated list of product items.
 *
 * @param modifier The modifier for the grid layout. Defaults to [Modifier].
 * @param productPagingItem The lazy paging items representing the paginated list of product items.
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ProductPagingGrid(
    modifier: Modifier = Modifier,
    productPagingItem: LazyPagingItems<ProductListItem>
) {
    var selectedProduct by remember { mutableStateOf<ProductListItem?>(null) }

    SharedTransitionLayout(modifier = modifier) {
        BaseProductItemGrid(modifier = Modifier.fillMaxSize()) {
            items(
                count = productPagingItem.itemCount,
                key = productPagingItem.itemKey { it.key }
            ) {
                productPagingItem[it]?.let { productListItem ->
                    SharedProductItemAnimatedVisibility(
                        modifier = Modifier.animateItem().fillMaxSize(),
                        productListItem = productListItem,
                        sharedTransitionScope = this@SharedTransitionLayout,
                        onClick = {selectedProduct = productListItem}
                    )
                }
            }
            item {
                if (productPagingItem.loadState.append is LoadState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                }
            }
        }
        ProductItemDetails(
            productListItem = selectedProduct,
            onConfirmClick = {
                selectedProduct = null
            }
        )
    }
}



/**
 * Composable function for animating the visibility of a product item with shared transition support.
 *
 * This function animates the visibility of a product item while supporting shared transitions between
 * components, such as the product item itself and its details.
 *
 * @param modifier The modifier for the animated visibility layout. Defaults to [Modifier].
 * @param productListItem The data class representing the details of the product item to be displayed.
 * @param isVisible Whether the product item should be visible. Defaults to true.
 * @param sharedTransitionScope The shared transition scope for coordinating shared element transitions.
 * @param onClick Lambda function to be executed when the product item is clicked. Defaults to an empty function.
 */

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedProductItemAnimatedVisibility(
    modifier: Modifier = Modifier,
    productListItem: ProductListItem,
    isVisible: Boolean = true,
    sharedTransitionScope: SharedTransitionScope,
    onClick: () -> Unit = {}
) {
    AnimatedVisibility(
        modifier = modifier,
        visible = isVisible,
        enter = fadeIn() + scaleIn(),
        exit = fadeOut() + scaleOut(),
    ) {
        with(sharedTransitionScope) {
            Box(
                modifier = Modifier
                    .sharedBounds(
                        sharedContentState = rememberSharedContentState(key = "${productListItem.key}-bounds"),
                        // Using the scope provided by AnimatedVisibility
                        animatedVisibilityScope = this@AnimatedVisibility,
                        clipInOverlayDuringTransition = OverlayClip(shapeForSharedElement)
                    )
                    .background(Color.White, shapeForSharedElement)
                    .clip(shapeForSharedElement)
            ) {
                ProductItem(
                    modifier = Modifier.sharedElement(
                        state = rememberSharedContentState(key = "${productListItem.key}--item"),
                        animatedVisibilityScope = this@AnimatedVisibility
                    ),
                    productListItem = productListItem,
                    onClick = onClick
                )
            }
        }
    }
}