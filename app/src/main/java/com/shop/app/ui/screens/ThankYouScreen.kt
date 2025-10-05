package com.shop.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shop.app.ui.theme.TShopAppTheme

@Composable
fun ThankYouScreen(
    orderId: String?,
    onContinueShopping: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Дякуємо!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = if (orderId != null) {
                "Ваше замовлення №$orderId успішно оформлено."
            } else {
                "Ваше замовлення успішно оформлено."
            },
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
        Button(
            onClick = onContinueShopping,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Продовжити покупки")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ThankYouScreenPreview() {
    TShopAppTheme {
        ThankYouScreen(orderId = "1234", onContinueShopping = {})
    }
}
