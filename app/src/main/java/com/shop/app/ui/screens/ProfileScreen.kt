package com.shop.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shop.app.data.model.ProfileResponse

@Composable
fun ProfileScreen(
    isLoggedIn: Boolean,
    userProfile: ProfileResponse?, // New parameter for profile data
    onLoginClick: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (isLoggedIn && userProfile != null) {
            // UI for logged in user
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Profile", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(16.dp))
                // Display user email
                Text("Email: ${userProfile.email}")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onLogoutClick) {
                    Text("Sign Out")
                }
            }
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