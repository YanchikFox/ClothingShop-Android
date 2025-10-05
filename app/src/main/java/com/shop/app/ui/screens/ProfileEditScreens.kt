package com.shop.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.shop.app.data.model.ProfileResponse
import com.shop.app.data.model.UpdateAddressRequest
import androidx.compose.ui.res.stringResource
import com.shop.app.R

@Composable
fun EditProfileScreen(
    profile: ProfileResponse,
    onSave: (String, String) -> Unit,
    onBack: () -> Unit,
) {
    val initialName = profile.name.orEmpty()
    val initialPhone = profile.phone.orEmpty()

    val name = remember { mutableStateOf(initialName) }
    val phone = remember { mutableStateOf(initialPhone) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = stringResource(R.string.profile_edit_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            OutlinedTextField(
                value = name.value,
                onValueChange = { name.value = it },
                label = { Text(stringResource(R.string.field_name)) },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = phone.value,
                onValueChange = { phone.value = it },
                label = { Text(stringResource(R.string.field_phone)) },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.action_cancel))
                }

                Button(
                    onClick = { onSave(name.value.trim(), phone.value.trim()) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.action_save))
                }
            }
        }
    }
}

@Composable
fun ManageAddressesScreen(
    profile: ProfileResponse,
    onSave: (List<UpdateAddressRequest>) -> Unit,
    onBack: () -> Unit,
) {
    val addressState = remember {
        mutableStateListOf(*profile.addresses.orEmpty().map {
            EditableAddress(
                label = it.label,
                line1 = it.line1,
                line2 = it.line2.orEmpty(),
                city = it.city,
                postalCode = it.postalCode,
                country = it.country,
                isDefault = it.isDefault
            )
        }.toTypedArray())
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = stringResource(R.string.profile_addresses_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        itemsIndexed(addressState) { index, address ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = address.label,
                        onValueChange = {
                            addressState[index] = addressState[index].copy(label = it)
                        },
                        label = { Text(stringResource(R.string.address_label_label)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = address.line1,
                        onValueChange = {
                            addressState[index] = addressState[index].copy(line1 = it)
                        },
                        label = { Text(stringResource(R.string.address_line1_label)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = address.line2,
                        onValueChange = {
                            addressState[index] = addressState[index].copy(line2 = it)
                        },
                        label = { Text(stringResource(R.string.address_line2_label)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = address.city,
                        onValueChange = {
                            addressState[index] = addressState[index].copy(city = it)
                        },
                        label = { Text(stringResource(R.string.address_city_label)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = address.postalCode,
                        onValueChange = {
                            addressState[index] = addressState[index].copy(postalCode = it)
                        },
                        label = { Text(stringResource(R.string.address_postal_code_label)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = address.country,
                        onValueChange = {
                            addressState[index] = addressState[index].copy(country = it)
                        },
                        label = { Text(stringResource(R.string.address_country_label)) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        RadioButton(
                            selected = address.isDefault,
                            onClick = {
                                addressState.indices.forEach { position ->
                                    val current = addressState[position]
                                    addressState[position] = current.copy(isDefault = position == index)
                                }
                            }
                        )
                        Text(stringResource(R.string.address_mark_default))
                    }

                    TextButton(
                        onClick = {
                            if (index < addressState.size) {
                                addressState.removeAt(index)
                            }
                        }
                    ) {
                        Text(stringResource(R.string.address_remove))
                    }
                }
            }
        }

        item {
            TextButton(onClick = {
                addressState.add(
                    EditableAddress(
                        label = "",
                        line1 = "",
                        line2 = "",
                        city = "",
                        postalCode = "",
                        country = "",
                        isDefault = addressState.isEmpty()
                    )
                )
            }) {
                Text(stringResource(R.string.address_add))
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.action_cancel))
                }

                Button(
                    onClick = {
                        val normalized = addressState.map { editable ->
                            editable.copy(
                                label = editable.label.trim(),
                                line1 = editable.line1.trim(),
                                line2 = editable.line2.trim(),
                                city = editable.city.trim(),
                                postalCode = editable.postalCode.trim(),
                                country = editable.country.trim()
                            )
                        }
                            .filter {
                                it.label.isNotEmpty() &&
                                    it.line1.isNotEmpty() &&
                                    it.city.isNotEmpty() &&
                                    it.postalCode.isNotEmpty() &&
                                    it.country.isNotEmpty()
                            }

                        val ensuredDefault = if (normalized.none { it.isDefault } && normalized.isNotEmpty()) {
                            normalized.mapIndexed { index, address ->
                                if (index == 0) address.copy(isDefault = true) else address
                            }
                        } else {
                            normalized
                        }

                        onSave(
                            ensuredDefault.map {
                                UpdateAddressRequest(
                                    label = it.label,
                                    line1 = it.line1,
                                    line2 = it.line2.ifEmpty { null },
                                    city = it.city,
                                    postalCode = it.postalCode,
                                    country = it.country,
                                    isDefault = it.isDefault
                                )
                            }
                        )
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.action_save))
                }
            }
        }
    }
}

private data class EditableAddress(
    val label: String,
    val line1: String,
    val line2: String,
    val city: String,
    val postalCode: String,
    val country: String,
    val isDefault: Boolean,
)
