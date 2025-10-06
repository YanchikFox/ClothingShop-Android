package com.shop.app.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.shop.app.data.model.ProfileResponse
import com.shop.app.data.model.ProfileUpdateRequest
import com.shop.app.data.model.UpdateAddressRequest
import com.shop.app.R
import com.shop.app.ui.viewmodels.ProfileUpdateState
import androidx.compose.ui.window.Dialog
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import com.shop.app.localization.rememberCurrentLocale


@Composable
fun ProfileScreen(
    isLoggedIn: Boolean,
    userProfile: ProfileResponse?,
    profileUpdateState: ProfileUpdateState,
    onLoginClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onSaveProfile: (ProfileUpdateRequest) -> Unit,
    onProfileUpdateHandled: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showEditDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(profileUpdateState) {
        when (profileUpdateState) {
            is ProfileUpdateState.Success -> {
                showEditDialog = false
                Toast.makeText(context, context.getString(R.string.profile_update_success), Toast.LENGTH_SHORT).show()
                onProfileUpdateHandled()
            }
            is ProfileUpdateState.Error -> {
                Toast.makeText(context, profileUpdateState.message, Toast.LENGTH_LONG).show()
                onProfileUpdateHandled()
            }
            else -> Unit
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (isLoggedIn && userProfile != null) {
            ProfileDetails(
                profile = userProfile,
                onEditClick = { showEditDialog = true },
                onLogoutClick = onLogoutClick,
                onSettingsClick = onSettingsClick,
                modifier = Modifier.fillMaxSize()
            )
            if (showEditDialog) {
                ProfileEditDialog(
                    profile = userProfile,
                    isSaving = profileUpdateState is ProfileUpdateState.Loading,
                    onDismiss = {
                        if (profileUpdateState !is ProfileUpdateState.Loading) {
                            showEditDialog = false
                            onProfileUpdateHandled()
                        }
                    },
                    onSave = onSaveProfile
                )
            }
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
private fun ProfileEditDialog(
    profile: ProfileResponse,
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onSave: (ProfileUpdateRequest) -> Unit,
) {
    val scrollState = rememberScrollState()
    var name by remember { mutableStateOf(profile.name.orEmpty()) }
    var phone by remember { mutableStateOf(profile.phone.orEmpty()) }
    val addresses = remember { mutableStateListOf<EditableAddress>() }

    LaunchedEffect(profile.id, profile.addresses) {
        name = profile.name.orEmpty()
        phone = profile.phone.orEmpty()
        addresses.clear()
        val currentAddresses = profile.addresses.orEmpty()
        if (currentAddresses.isEmpty()) {
            return@LaunchedEffect
        }
        addresses.addAll(
            currentAddresses.map { address ->
                EditableAddress(
                    label = address.label,
                    line1 = address.line1,
                    line2 = address.line2.orEmpty(),
                    city = address.city,
                    postalCode = address.postalCode,
                    country = address.country,
                    isDefault = address.isDefault
                )
            }
        )
    }

    val isPhoneValid = phone.isBlank() || phone.length >= 5
    val areAddressesValid = addresses.all { editable ->
        editable.label.isNotBlank() &&
            editable.line1.isNotBlank() &&
            editable.city.isNotBlank() &&
            editable.postalCode.isNotBlank() &&
            editable.country.isNotBlank()
    }
    val canSave = name.isNotBlank() && isPhoneValid && (addresses.isEmpty() || areAddressesValid)

    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = MaterialTheme.shapes.large, tonalElevation = 4.dp) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.profile_edit_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.field_name)) },
                    singleLine = true,
                    enabled = !isSaving,
                    modifier = Modifier.fillMaxWidth()
                )

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text(stringResource(R.string.field_phone)) },
                        singleLine = true,
                        enabled = !isSaving,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (!isPhoneValid) {
                        Text(
                            text = stringResource(R.string.profile_phone_error),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = stringResource(R.string.profile_addresses_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    if (addresses.isEmpty()) {
                        Text(
                            text = stringResource(R.string.profile_addresses_empty),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    addresses.forEachIndexed { index, editable ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = editable.label,
                                    onValueChange = {
                                        addresses[index] = editable.copy(label = it)
                                    },
                                    label = { Text(stringResource(R.string.address_label_label)) },
                                    singleLine = true,
                                    enabled = !isSaving,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                OutlinedTextField(
                                    value = editable.line1,
                                    onValueChange = {
                                        addresses[index] = editable.copy(line1 = it)
                                    },
                                    label = { Text(stringResource(R.string.address_line1_label)) },
                                    enabled = !isSaving,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                OutlinedTextField(
                                    value = editable.line2,
                                    onValueChange = {
                                        addresses[index] = editable.copy(line2 = it)
                                    },
                                    label = { Text(stringResource(R.string.address_line2_label)) },
                                    enabled = !isSaving,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                OutlinedTextField(
                                    value = editable.city,
                                    onValueChange = {
                                        addresses[index] = editable.copy(city = it)
                                    },
                                    label = { Text(stringResource(R.string.address_city_label)) },
                                    enabled = !isSaving,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                OutlinedTextField(
                                    value = editable.postalCode,
                                    onValueChange = {
                                        addresses[index] = editable.copy(postalCode = it)
                                    },
                                    label = { Text(stringResource(R.string.address_postal_code_label)) },
                                    enabled = !isSaving,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                OutlinedTextField(
                                    value = editable.country,
                                    onValueChange = {
                                        addresses[index] = editable.copy(country = it)
                                    },
                                    label = { Text(stringResource(R.string.address_country_label)) },
                                    enabled = !isSaving,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    RadioButton(
                                        selected = editable.isDefault,
                                        onClick = {
                                            if (!isSaving) {
                                                addresses.indices.forEach { i ->
                                                    val current = addresses[i]
                                                    addresses[i] = current.copy(isDefault = i == index)
                                                }
                                            }
                                        },
                                        enabled = !isSaving
                                    )
                                    Text(text = stringResource(R.string.profile_edit_make_default))
                                }

                                TextButton(
                                    onClick = {
                                        if (!isSaving) {
                                            addresses.removeAt(index)
                                            if (addresses.isNotEmpty() && addresses.none { it.isDefault }) {
                                                addresses[0] = addresses[0].copy(isDefault = true)
                                            }
                                        }
                                    },
                                    enabled = !isSaving,
                                    modifier = Modifier.align(Alignment.End)
                                ) {
                                    Text(stringResource(R.string.address_remove))
                                }
                            }
                        }
                    }

                    OutlinedButton(
                        onClick = {
                            addresses.add(
                                EditableAddress(
                                    isDefault = addresses.isEmpty()
                                )
                            )
                        },
                        enabled = !isSaving,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.address_add))
                    }

                    if (addresses.isNotEmpty() && !areAddressesValid) {
                        Text(
                            text = stringResource(R.string.profile_edit_address_error),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TextButton(
                        onClick = onDismiss,
                        enabled = !isSaving,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(stringResource(R.string.action_cancel))
                    }
                    Button(
                        onClick = {
                            val sanitizedAddresses = addresses.map { editable ->
                                UpdateAddressRequest(
                                    label = editable.label.trim(),
                                    line1 = editable.line1.trim(),
                                    line2 = editable.line2.trim().takeIf { it.isNotBlank() },
                                    city = editable.city.trim(),
                                    postalCode = editable.postalCode.trim(),
                                    country = editable.country.trim(),
                                    isDefault = editable.isDefault
                                )
                            }
                            onSave(
                                ProfileUpdateRequest(
                                    name = name.trim(),
                                    phone = phone.trim(),
                                    addresses = sanitizedAddresses
                                )
                            )
                        },
                        enabled = canSave && !isSaving,
                        modifier = Modifier.weight(1f)
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp))
                        } else {
                            Text(stringResource(R.string.action_save))
                        }
                    }
                }
            }
        }
    }
}

private data class EditableAddress(
    val label: String = "",
    val line1: String = "",
    val line2: String = "",
    val city: String = "",
    val postalCode: String = "",
    val country: String = "",
    val isDefault: Boolean = false,
)

@Composable
private fun ProfileDetails(
    profile: ProfileResponse,
    onEditClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val locale = rememberCurrentLocale()
    val memberSinceDate = remember(profile.createdAt, locale) {
        runCatching {
            OffsetDateTime.parse(profile.createdAt)
                .format(
                    DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)
                        .withLocale(locale)
                )
        }.getOrElse { profile.createdAt }
    }
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
                    Text(text = stringResource(R.string.profile_member_since, memberSinceDate))
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
                                val labelLine = buildString {
                                    append(address.label)
                                    if (address.isDefault) {
                                        append(" · ")
                                        append(context.getString(R.string.profile_address_default_badge))
                                    }
                                }
                                appendLine(labelLine)
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
            Button(
                onClick = onEditClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.profile_edit_action))
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
