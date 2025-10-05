package com.shop.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.shop.app.data.model.CartItem
import com.shop.app.data.model.Product
import com.shop.app.di.ServiceLocator
import com.shop.app.ui.theme.TShopAppTheme
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CartItemRow(
    cartItem: CartItem,
    modifier: Modifier = Modifier,
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
        val currencyFormat = remember {
            NumberFormat.getCurrencyInstance(Locale("uk", "UA")).apply {
                maximumFractionDigits = 0
            }
        }

        AsyncImage(
            // Use imagePath and build full URL
            model = ServiceLocator.imagesBaseUrl + cartItem.product.imagePath,
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
                text = currencyFormat.format(cartItem.product.price),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onDecrement) { Icon(Icons.Default.Remove, "Decrease") }
            Text("${cartItem.quantity}")
            IconButton(onClick = onIncrement) { Icon(Icons.Default.Add, "Increase") }
        }

        IconButton(onClick = onRemoveClick) {
            Icon(Icons.Default.Close, contentDescription = "Remove item")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CartItemRowPreview() {
    val sampleProduct = Product(
        id = "su001",
        article = "1023",
        imagePath = "images/1.jpg",
        name = "Your Product 1",
        price = 1200.0,
        priceString = "1 200 ₴",
        description = "Description...",
        isBestseller = true,
        gender = "unisex"
    )
    val sampleCartItem = CartItem(product = sampleProduct, quantity = 2)
    TShopAppTheme {
        CartItemRow(
            cartItem = sampleCartItem,
            onRemoveClick = {},
            onIncrement = {},
            onDecrement = {}
        )
    }
}