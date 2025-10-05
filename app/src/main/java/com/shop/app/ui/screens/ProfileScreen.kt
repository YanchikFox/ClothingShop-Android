package com.shop.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.shop.app.data.model.ProfileResponse
import com.shop.app.data.model.UserAddress
import com.shop.app.data.model.OrderHistoryItem

@Composable
fun ProfileScreen(
    isLoggedIn: Boolean,
    userProfile: ProfileResponse?, // New parameter for profile data
    onLoginClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onEditProfileClick: () -> Unit,
    onManageAddressesClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (isLoggedIn && userProfile != null) {
            ProfileDetails(
                profile = userProfile,
                onEditProfileClick = onEditProfileClick,
                onManageAddressesClick = onManageAddressesClick,
                onLogoutClick = onLogoutClick,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // UI for guest user
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Sign in to your profile", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(8.dp))
                Text("To see order history and save items")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onLoginClick) {
                    Text("Sign In or Create Account")
                }
            }
        }
    }
}

@Composable
private fun ProfileDetails(
    profile: ProfileResponse,
    onEditProfileClick: () -> Unit,
    onManageAddressesClick: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Мой профиль",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = profile.name.takeIf { !it.isNullOrBlank() } ?: "Безымянный пользователь",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(text = "Email: ${profile.email}")
                    Text(text = "Телефон: ${profile.phone.takeIf { !it.isNullOrBlank() } ?: "не указан"}")
                    Text(text = "С нами с: ${profile.createdAt}")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onEditProfileClick) {
                            Text("Редактировать профиль")
                        }
                    }
                }
            }
        }

        item {
            AddressSection(addresses = profile.addresses, onManageAddressesClick = onManageAddressesClick)
        }

        item {
            OrderHistorySection(orderHistory = profile.orderHistory)
        }

        item {
            OutlinedButton(
                onClick = onLogoutClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Выйти из аккаунта")
            }
        }
    }
}

@Composable
private fun AddressSection(
    addresses: List<UserAddress>,
    onManageAddressesClick: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Адреса доставки",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            TextButton(onClick = onManageAddressesClick) {
                Text("Управлять")
            }
        }

        if (addresses.isEmpty()) {
            Text("Вы ещё не добавили адреса", style = MaterialTheme.typography.bodyMedium)
        } else {
            addresses.forEach { address ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(address.label, fontWeight = FontWeight.SemiBold)
                        Text(address.line1)
                        address.line2?.takeIf { it.isNotBlank() }?.let { Text(it) }
                        Text("${address.city}, ${address.country}")
                        Text("Индекс: ${address.postalCode}")
                        if (address.isDefault) {
                            Text(
                                text = "Основной адрес",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderHistorySection(orderHistory: List<OrderHistoryItem>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "История заказов",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        if (orderHistory.isEmpty()) {
            Text("У вас пока нет заказов", style = MaterialTheme.typography.bodyMedium)
        } else {
            orderHistory.forEach { order ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Заказ №${order.orderNumber}", fontWeight = FontWeight.SemiBold)
                        Text("Статус: ${order.status}")
                        Divider()
                        Text("Сумма: ${order.totalAmount}")
                        Text("Дата: ${order.placedAt}")
                    }
                }
            }
        }
    }
}