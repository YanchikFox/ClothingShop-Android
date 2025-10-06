package com.shop.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.shop.app.data.model.CartItem
import com.shop.app.data.model.Product
import com.shop.app.data.model.ProductFeature
import com.shop.app.data.model.ProductReview
import com.shop.app.di.ServiceLocator
import com.shop.app.ui.theme.TShopAppTheme
import com.shop.app.R

@Composable
fun CartItemRow(
    cartItem: CartItem,
    modifier: Modifier = Modifier,
    formatPrice: (Double) -> String,
    onRemoveClick: () -> Unit,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val previewImage = remember(cartItem.product.imageUrls, cartItem.product.imagePath) {
            cartItem.product.imageUrls.firstOrNull() ?: cartItem.product.imagePath
        }

        AsyncImage(
            // Use first image and build full URL
            model = previewImage?.let { ServiceLocator.imagesBaseUrl + it },
            contentDescription = cartItem.product.name,
            modifier = Modifier
                .size(80.dp),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = cartItem.product.name,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = formatPrice(cartItem.product.price),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onDecrement) {
                Icon(
                    Icons.Default.Remove,
                    contentDescription = stringResource(R.string.cd_decrease_quantity)
                )
            }
            Text("${cartItem.quantity}")
            IconButton(onClick = onIncrement) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(R.string.cd_increase_quantity)
                )
            }
        }

        IconButton(onClick = onRemoveClick) {
            Icon(
                Icons.Default.Close,
                contentDescription = stringResource(R.string.cd_remove_item)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CartItemRowPreview() {
    val sampleProduct = Product(
        id = "su001",
        article = "1023",
        name = "Your Product 1",
        price = 1200.0,
        priceString = "1 200 ₴",
        description = "Description...",
        isBestseller = true,
        categoryId = "unisex",
        imageUrls = listOf("images/1.jpg"),
        composition = "100% cotton",
        careInstructions = "Machine wash cold",
        features = listOf(ProductFeature("Fit", "Regular")),
        reviews = listOf(ProductReview("Oleh", 5, "Love it"))
    )
    val sampleCartItem = CartItem(product = sampleProduct, quantity = 2)
    TShopAppTheme {
        CartItemRow(
            cartItem = sampleCartItem,
            formatPrice = { price -> "${price.toInt()} ₴" },
            onRemoveClick = {},
            onIncrement = {},
            onDecrement = {}
        )
    }
}