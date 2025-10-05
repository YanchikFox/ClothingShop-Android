package com.shop.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.shop.app.BuildConfig
import com.shop.app.data.model.Product
import com.shop.app.data.model.ProductFeature
import com.shop.app.data.model.ProductReview
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ProductCard(
    product: Product,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val currencyFormat = remember {
        NumberFormat.getCurrencyInstance(Locale("uk", "UA")).apply {
            maximumFractionDigits = 0
        }
    }

    val mainImagePath = remember(product.imageUrls, product.imagePath) {
        product.imageUrls.firstOrNull() ?: product.imagePath
    }

    Card(modifier = modifier.clickable(onClick = onClick)) {
        Column {
            // Box for displaying image and "Hit" badge
            Box {
                AsyncImage(
                    // Build full URL for image
                    model = mainImagePath?.let { BuildConfig.IMAGES_BASE_URL + it },
                    contentDescription = product.name,
                    modifier = Modifier.fillMaxWidth().aspectRatio(0.8f),
                    contentScale = ContentScale.Crop
                )
                // Show badge if product is bestseller
                if (product.isBestseller) {
                    Text(
                        text = "HIT",
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                            .background(MaterialTheme.colorScheme.primary, shape = MaterialTheme.shapes.small)
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Text block
            Column(
                modifier = Modifier.padding(12.dp).defaultMinSize(minHeight = 80.dp)
            ) {
                Text(
                    text = currencyFormat.format(product.price),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}


@Preview
@Composable
fun ProductCardPreview() {
    val sampleProduct = Product(
        id = "su001",
        article = "1023",
        categoryId = "unisex",
        name = "Sample Product",
        description = "Description",
        price = 1200.0,
        priceString = "1 200 ₴",
        isBestseller = true,
        imageUrls = listOf("images/1.jpg", "images/1_detail.jpg"),
        composition = "100% cotton",
        careInstructions = "Machine wash cold",
        features = listOf(
            ProductFeature(title = "Fit", value = "Relaxed"),
            ProductFeature(title = "Length", value = "Standard")
        ),
        reviews = listOf(
            ProductReview(author = "Iryna", rating = 5, comment = "Great")
        )
    )
    ProductCard(
        product = sampleProduct,
        modifier = Modifier.width(200.dp),
        onClick = {}
    )
}