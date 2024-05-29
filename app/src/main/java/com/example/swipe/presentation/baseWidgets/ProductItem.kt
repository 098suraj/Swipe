package com.example.swipe.presentation.baseWidgets

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.requiredWidthIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.swipe.R
import com.example.swipe.datamodels.ProductListItem
import kotlin.math.min

/**
 * Composable function representing the layout of a product item.
 * This function is responsible for rendering the UI layout of a single product item,
 *  displayed in a grid.
 * @param modifier The modifier for the product item layout. Defaults to [Modifier].
 * @param productListItem The data class representing the details of the product item to be displayed.
 * @param onClick Lambda function to be executed when the product item is clicked.
 */
@Composable
fun ProductItem(modifier: Modifier = Modifier, productListItem: ProductListItem, onClick:()->Unit) {
    val imageLink = remember {
        productListItem.image.ifBlank { R.drawable.placeholder }
    }
    Card(
        modifier = modifier
            .requiredHeightIn(min = 200.dp)
            .requiredWidthIn(min = 80.dp),
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
    ) {

        SubcomposeAsyncImage(
            modifier = Modifier
                .fillMaxSize(),
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageLink)
                .crossfade(true)
                .networkCachePolicy(CachePolicy.ENABLED)
                .diskCachePolicy(CachePolicy.ENABLED)
                .build(),
            contentDescription = null
        ) {
            val state = painter.state

            val transition by animateFloatAsState(
                targetValue = if (state is AsyncImagePainter.State.Success) 1f else 0f,
                label = "Image transition"
            )

            if (state is AsyncImagePainter.State.Loading) {
                Box(modifier = Modifier
                    .requiredHeightIn(min = 200.dp)
                    .requiredWidthIn(min = 80.dp)
                    .fillMaxSize()) {
                    LoadingAnimation(
                        Modifier
                            .align(Alignment.Center)
                            .size(60.dp)
                    )
                }
            }

            if (state is AsyncImagePainter.State.Success) {
                Box(modifier = Modifier
                    .requiredHeightIn(min = 200.dp)
                    .requiredWidthIn(min = 80.dp)
                    .fillMaxSize()
                    .drawWithCache {
                        val gradient = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = .6f)
                            ),
                            startY = size.height / 3,
                            endY = size.height
                        )
                        onDrawWithContent {
                            drawContent()
                            drawRect(gradient, blendMode = BlendMode.Multiply)
                        }
                    }
                ) {
                    Image(
                        state.painter,
                        modifier = Modifier
                            .requiredHeightIn(min = 200.dp)
                            .requiredWidthIn(min = 80.dp)
                            .fillMaxSize()
                            .graphicsLayer {
                                scaleX = (.8f + (.2f * transition))
                                scaleY = (.8f + (.2f * transition))
                                rotationX = (1f - transition) * 5f
                                alpha = min(1f, transition / .2f)
                            },
                        contentDescription = "custom transition based on painter state",
                        contentScale = ContentScale.Crop
                    )
                    Text(
                        modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 10.dp),
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        text = productListItem.product_name,
                        style = MaterialTheme.typography.titleMedium.copy(Color.White),
                        color = Color.White
                    )
                }
            }
        }
    }
}

val shapeForSharedElement = RoundedCornerShape(16.dp)

/**
 * Composable function representing the product item details screen with shared transition support.
 *
 * This function is responsible for rendering the UI layout of the product item details screen,
 * with support for shared transitions between [ProductItem] and [ProductItemDetailsContent].
 *
 * @param productListItem The data class representing the details of the product item.
 * @param modifier The modifier for the product item details layout. Defaults to [Modifier].
 * @param onConfirmClick Lambda function to be executed when the user confirms an action related to the product item.
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.ProductItemDetails(
    productListItem: ProductListItem?,
    modifier: Modifier = Modifier,
    onConfirmClick: () -> Unit
) {
    AnimatedContent(
        modifier = modifier,
        targetState = productListItem,
        transitionSpec = {
            fadeIn() togetherWith fadeOut()
        },
        label = "SnackEditDetails"
    ) { targetProduct ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (targetProduct != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable {
                            onConfirmClick()
                        }
                        .background(Color.Black.copy(alpha = 0.5f))
                )
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .sharedBounds(
                            sharedContentState = rememberSharedContentState(key = "${targetProduct.key}-bounds"),
                            animatedVisibilityScope = this@AnimatedContent,
                            clipInOverlayDuringTransition = OverlayClip(shapeForSharedElement)
                        )
                        .background(Color.White, shapeForSharedElement)
                        .clip(shapeForSharedElement)
                ) {
                   ProductItemDetailsContent(
                       modifier = Modifier.sharedElement(
                           state = rememberSharedContentState(key = "${targetProduct.key}--item"),
                           animatedVisibilityScope = this@AnimatedContent,
                       ),
                       productListItem = targetProduct,
                       onConfirmClick = onConfirmClick
                   )
                }
            }
        }
    }
}

/**
 * Composable function representing the content  product item details .
 * @param modifier The modifier for the product item details content layout. Defaults to [Modifier].
 * @param productListItem The data class representing the details of the product item.
 * @param onConfirmClick Lambda function to be executed when the user confirms an action related to the product item.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProductItemDetailsContent(
    modifier: Modifier = Modifier,
    productListItem: ProductListItem,
    onConfirmClick: () -> Unit
) {
    val imageLink = remember {
        productListItem.image.ifBlank { R.drawable.placeholder }
    }
    val imagePainterState = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageLink)
            .size(500)
            .crossfade(true)
            .networkCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .build()
    )
    Card(
        modifier = modifier,
        onClick = onConfirmClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    ) {
        if (imagePainterState.state is AsyncImagePainter.State.Loading) {
            Box(modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(20f / 9f)) {
                LoadingAnimation(
                    Modifier
                        .align(Alignment.Center)
                        .size(60.dp)
                )
            }
        }

        if (imagePainterState.state is AsyncImagePainter.State.Success) {
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(20f / 9f),
                painter = imagePainterState,
                contentDescription = "Product Details",
                contentScale = ContentScale.FillWidth,
            )

            Text(
                text = "Product Details",
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(8.dp),
                style = MaterialTheme.typography.titleSmall
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = "Name: ${productListItem.product_name}",
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(8.dp),
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Type: ${productListItem.product_type}",
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(8.dp),
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Price${productListItem.price}",
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(8.dp),
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = "Tax: ${productListItem.tax}",
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(8.dp),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
