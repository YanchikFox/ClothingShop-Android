package com.shop.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.shop.app.data.model.Product
import com.shop.app.di.ServiceLocator
import com.shop.app.ui.theme.TShopAppTheme
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ProductDetailScreen(
    productId: String?,
    products: List<Product>,
    modifier: Modifier = Modifier,
    onAddToCartClick: (product: Product, quantity: Int) -> Unit
) {
    val product = products.firstOrNull { it.id == productId }
    var quantity by remember { mutableStateOf(1) }

    if (product == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Product not found")
        }
    } else {
        val currencyFormat = remember {
            NumberFormat.getCurrencyInstance(Locale("uk", "UA")).apply {
                maximumFractionDigits = 0
            }
        }

        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Product image, keep centered
            AsyncImage(
                model = ServiceLocator.imagesBaseUrl + product.imagePath,
                contentDescription = product.name,
                modifier = Modifier.fillMaxWidth().aspectRatio(1f),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Text block aligned to the left
            Column(horizontalAlignment = Alignment.Start) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = currencyFormat.format(product.price),
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = product.description,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Start // Explicit alignment for paragraph
                )
            }

            // Keep quantity selector and button centered
            Spacer(modifier = Modifier.weight(1f)) // Pushes the block down
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = { if (quantity > 1) quantity-- }) {
                    Icon(Icons.Default.Remove, contentDescription = "Decrease quantity")
                }
                Text(
                    text = "$quantity",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.width(40.dp),
                    textAlign = TextAlign.Center
                )
                IconButton(onClick = { quantity++ }) {
                    Icon(Icons.Default.Add, contentDescription = "Increase quantity")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { onAddToCartClick(product, quantity) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Add to Cart")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProductDetailScreenPreview() {
    // Preview data should match the new model
    val sampleProduct = Product(
        id = "su001",
        article = "1023",
        gender = "unisex",
        name = "Sample Product",
        description = "Complete product description for preview...",
        imagePath = "images/1.jpg",
        price = 1200.0,
        priceString = "1 200 ₴",
        isBestseller = true
    )
    TShopAppTheme {
        ProductDetailScreen(
            productId = "su001",
            products = listOf(sampleProduct),
            onAddToCartClick = { _, _ -> }
        )
    }
}