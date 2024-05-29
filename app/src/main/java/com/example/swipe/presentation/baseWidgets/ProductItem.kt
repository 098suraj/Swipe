package com.example.swipe.presentation.baseWidgets

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.requiredWidthIn
import androidx.compose.foundation.layout.size
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
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.swipe.R
import com.example.swipe.datamodels.ProductListItem
import kotlin.math.min

@Composable
fun ProductItem(modifier: Modifier = Modifier, productListItem: ProductListItem) {
    val imageLink = remember {
        productListItem.image.ifBlank { R.drawable.placeholder }
    }
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
    ) {
        SubcomposeAsyncImage(
            modifier = Modifier
                .fillMaxSize()
                .requiredHeightIn(min = 200.dp)
                .requiredWidthIn(min = 80.dp),
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
            Box(modifier = Modifier.fillMaxSize()) {
                if (state is AsyncImagePainter.State.Loading) {
                    LoadingAnimation(
                        Modifier
                            .align(Alignment.Center)
                            .size(60.dp)
                    )
                }
            }
            if (state is AsyncImagePainter.State.Success) {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .requiredHeightIn(min = 200.dp)
                    .requiredWidthIn(min = 80.dp)
                    .drawWithCache {
                        val gradient = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = .4f)
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
                            .fillMaxSize()
                            .requiredHeightIn(min = 200.dp)
                            .requiredWidthIn(min = 80.dp)
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
                        modifier = Modifier.align(Alignment.BottomCenter),
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        text = productListItem.product_name,
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.White
                    )
                }
            }
        }
    }
}