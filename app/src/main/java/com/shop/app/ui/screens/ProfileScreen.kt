package com.shop.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.shop.app.data.model.ProfileResponse
import com.shop.app.R

@Composable
fun ProfileScreen(
    isLoggedIn: Boolean,
    userProfile: ProfileResponse?,
    onLoginClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (isLoggedIn && userProfile != null) {
            ProfileDetails(
                profile = userProfile,
                onLogoutClick = onLogoutClick,
                onSettingsClick = onSettingsClick,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.profile_prompt_title),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.profile_prompt_subtitle),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onLoginClick) {
                    Text(stringResource(R.string.profile_prompt_cta))
                }

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(onClick = onSettingsClick) {
                    Text(stringResource(R.string.profile_open_settings))
                }
            }
        }
    }
}

@Composable
private fun ProfileDetails(
    profile: ProfileResponse,
    onLogoutClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = stringResource(R.string.profile_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = profile.name?.takeIf { it.isNotBlank() }
                            ?: stringResource(R.string.profile_default_name),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(text = stringResource(R.string.profile_email_format, profile.email))
                    val phoneText = profile.phone?.takeIf { it.isNotBlank() }
                        ?: stringResource(R.string.profile_phone_unknown)
                    Text(text = stringResource(R.string.profile_phone_format, phoneText))
                    Text(text = stringResource(R.string.profile_member_since, profile.createdAt))
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.profile_addresses_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    val addressesText = profile.addresses
                        ?.takeIf { it.isNotEmpty() }
                        ?.joinToString(separator = "\n\n") { address ->
                            buildString {
                                appendLine(address.label)
                                appendLine(address.line1)
                                address.line2?.takeIf { it.isNotBlank() }?.let { appendLine(it) }
                                appendLine(
                                    context.getString(
                                        R.string.profile_address_city_country,
                                        address.city,
                                        address.country
                                    )
                                )
                                append(
                                    context.getString(
                                        R.string.profile_address_postal_code,
                                        address.postalCode
                                    )
                                )
                            }
                        }
                        ?: stringResource(R.string.profile_addresses_empty)
                    Text(text = addressesText)
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.profile_orders_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    val ordersText = profile.orderHistory
                        ?.takeIf { it.isNotEmpty() }
                        ?.joinToString(separator = "\n\n") { order ->
                            context.getString(
                                R.string.profile_order_item_format,
                                order.orderNumber,
                                order.status,
                                order.totalAmount,
                                order.placedAt
                            )
                        }
                        ?: stringResource(R.string.profile_orders_empty)
                    Text(text = ordersText)
                }
            }
        }

        item {
            OutlinedButton(
                onClick = onSettingsClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.profile_open_settings))
            }
        }

        item {
            OutlinedButton(
                onClick = onLogoutClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.profile_logout))
            }
        }
    }
}
