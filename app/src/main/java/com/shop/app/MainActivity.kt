package com.shop.app

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.shop.app.ui.screens.*
import com.shop.app.ui.theme.TShopAppTheme
import com.shop.app.ui.utils.rememberPriceFormatter
import com.shop.app.ui.viewmodels.*
import com.shop.app.ui.viewmodels.OnboardingViewModel.OnboardingCompletionState
import com.shop.app.ui.viewmodels.OnboardingViewModel.OnboardingEvent
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppNavigation()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val application = LocalContext.current.applicationContext as MyApplication
    val cartViewModel: CartViewModel = viewModel(factory = CartViewModel.provideFactory(application.container.cartRepository, application.container.authRepository))
    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModel.provideFactory(application.container.userRepository, application.container.authRepository))
    val catalogViewModel: CatalogViewModel = viewModel(factory = CatalogViewModel.provideFactory(application.container.catalogRepository))
    val productsViewModel: ProductsViewModel = viewModel(
        factory = ProductsViewModel.provideFactory(
            application.container.productRepository,
            application.container.onboardingPreferencesRepository
        )
    )
    val context = LocalContext.current
    val languageViewModel: LanguageViewModel = viewModel(
        factory = LanguageViewModel.provideFactory(application.container.languageRepository)
    )
    val currencyViewModel: CurrencyViewModel = viewModel(
        factory = CurrencyViewModel.provideFactory(context)
    )
    val onboardingViewModel: OnboardingViewModel = viewModel(
        factory = OnboardingViewModel.provideFactory(application.container.onboardingPreferencesRepository)
    )

    val productsUiState by productsViewModel.uiState.collectAsStateWithLifecycle()
    val cartItems by cartViewModel.cartItems.collectAsStateWithLifecycle()
    val authState by authViewModel.authUiState.collectAsStateWithLifecycle()
    val isLoggedIn by authViewModel.isLoggedIn.collectAsStateWithLifecycle()
    val userProfile by authViewModel.userProfile.collectAsStateWithLifecycle()
    val profileUpdateState by authViewModel.profileUpdateState.collectAsStateWithLifecycle()
    val totalPrice by cartViewModel.totalPrice.collectAsStateWithLifecycle()
    val languageUiState by languageViewModel.uiState.collectAsStateWithLifecycle()
    val currencyUiState by currencyViewModel.uiState.collectAsStateWithLifecycle()
    val onboardingUiState by onboardingViewModel.uiState.collectAsStateWithLifecycle()
    val onboardingCompletionState by onboardingViewModel.completionState.collectAsStateWithLifecycle()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    TShopAppTheme {
        val priceFormatter = rememberPriceFormatter(currencyUiState)

        LaunchedEffect(Unit) {
            onboardingViewModel.events.collect { event ->
                if (event is OnboardingEvent.Completed) {
                    navController.navigate("home") {
                        popUpTo("onboarding") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
        }

        Scaffold(
            topBar = {
                // Show TopAppBar only on home screen
                if (currentRoute == "home") {
                    TopAppBar(
                        title = { Text(stringResource(id = R.string.app_title)) },
                        actions = {
                            BadgedBox(
                                badge = {
                                    if (cartItems.isNotEmpty()) {
                                        Badge { Text("${cartItems.sumOf { it.quantity }}") }
                                    }
                                }
                            ) {
                                IconButton(onClick = {
                                    navController.navigate("cart") {
                                        launchSingleTop = true
                                    }
                                }) {
                                    Icon(
                                        Icons.Default.ShoppingCart,
                                        contentDescription = stringResource(R.string.cd_open_cart)
                                    )
                                }
                            }
                        }
                    )
                }
            },
            bottomBar = {
                AppBottomBar(navController = navController)
            }
        ) { innerPadding ->
            when (val completionState = onboardingCompletionState) {
                OnboardingCompletionState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                else -> {
                    val startDestination = if (completionState is OnboardingCompletionState.Completed) {
                        "home"
                    } else {
                        "onboarding"
                    }
                    NavHost(
                        navController = navController,
                        startDestination = startDestination,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("onboarding") {
                            OnboardingScreen(
                                uiState = onboardingUiState,
                                onCategoryToggle = onboardingViewModel::toggleCategory,
                                onBrandToggle = onboardingViewModel::toggleBrand,
                                onPriceRangeChange = onboardingViewModel::updatePriceRange,
                                onContinueClick = onboardingViewModel::savePreferences,
                                onSkipClick = onboardingViewModel::skipOnboarding
                            )
                        }

                        composable("home") {
                            HomeScreen(
                                uiState = productsUiState,
                                formatPrice = priceFormatter,
                                onProductClick = { productId ->
                                    navController.navigate("product_detail/$productId")
                                },
                                onSearchClick = {
                                    navController.navigate("search")
                                },
                                onRefresh = {
                                    productsViewModel.refreshProducts()
                                }
                            )
                        }

                        composable("search") {
                            SearchScreen(
                                languageTag = languageUiState.selectedLanguageTag,
                                formatPrice = priceFormatter,
                                onProductClick = { productId ->
                                    navController.navigate("product_detail/$productId")
                                }
                            )
                        }

                        composable("catalog") {
                            CatalogScreen(
                                catalogViewModel = catalogViewModel,
                                onCategoryClick = { categoryId ->
                                    navController.navigate("product_list/$categoryId")
                                },
                                imagesBaseUrl = application.container.getImagesBaseUrl()
                            )
                        }

                        composable("product_list/{categoryId}") {
                            ProductListScreen(
                                languageTag = languageUiState.selectedLanguageTag,
                                formatPrice = priceFormatter,
                                onProductClick = { productId ->
                                    navController.navigate("product_detail/$productId")
                                }
                            )
                        }

                        composable("product_detail/{productId}") { backStackEntry ->
                            val productDetailViewModel: ProductDetailViewModel = viewModel(
                                factory = ProductDetailViewModel.provideFactory(
                                    application.container.productRepository,
                                    backStackEntry.savedStateHandle
                                )
                            )
                            val similarUiState by productDetailViewModel.similarUiState.collectAsStateWithLifecycle()

                            if (productsUiState is ProductsUiState.Success) {
                                ProductDetailScreen(
                                    productId = backStackEntry.arguments?.getString("productId"),
                                    products = (productsUiState as ProductsUiState.Success).products,
                                    formatPrice = priceFormatter,
                                    imagesBaseUrl = application.container.getImagesBaseUrl(),
                                    onAddToCartClick = { product, quantity ->
                                        cartViewModel.addToCart(product, quantity)
                                        Toast.makeText(
                                            context,
                                            context.getString(
                                                R.string.product_added_to_cart_toast,
                                                product.name,
                                                quantity
                                            ),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    },
                                    similarUiState = similarUiState,
                                    onProductClick = { productId ->
                                        navController.navigate("product_detail/$productId")
                                    }
                                )
                            }
                        }

                        composable("cart") {
                            CartScreen(
                                cartItems = cartItems,
                                totalPrice = totalPrice,
                                formatPrice = priceFormatter,
                                imagesBaseUrl = application.container.getImagesBaseUrl(),
                                onRemoveClick = { productId -> cartViewModel.removeFromCart(productId) },
                                onIncrement = { productId -> cartViewModel.incrementQuantity(productId) },
                                onDecrement = { productId -> cartViewModel.decrementQuantity(productId) }
                            )
                        }

                        composable("profile") {
                            ProfileScreen(
                                isLoggedIn = isLoggedIn,
                                userProfile = userProfile,
                                profileUpdateState = profileUpdateState,
                                onLoginClick = { navController.navigate("login") },
                                onLogoutClick = { authViewModel.logout() },
                                onSettingsClick = { navController.navigate("settings") },
                                onSaveProfile = { request -> authViewModel.updateProfile(request) },
                                onProfileUpdateHandled = { authViewModel.resetProfileUpdateState() }
                            )
                        }

                        composable("login") {
                            LoginScreen(
                                onLoginClick = { email, password ->
                                    authViewModel.login(email, password)
                                },
                                onNavigateToRegister = {
                                    navController.navigate("register")
                                }
                            )
                        }

                        composable("register") {
                            RegisterScreen(
                                onRegisterClick = { email, password ->
                                    authViewModel.register(email, password)
                                },
                                onNavigateToLogin = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        composable("settings") {
                            val activity = LocalContext.current as? Activity
                            val scope = rememberCoroutineScope()
                            SettingsScreen(
                                languageUiState = languageUiState,
                                currencyUiState = currencyUiState,
                                onLanguageSelected = { option ->
                                    languageViewModel.updateLanguage(option.languageTag)
                                },
                                onCurrencySelected = { option ->
                                    currencyViewModel.updateCurrency(option.code)
                                },
                                onResetOnboarding = {
                                    scope.launch {
                                        onboardingViewModel.resetOnboarding()
                                        navController.navigate("onboarding") {
                                            popUpTo("home") { inclusive = false }
                                            launchSingleTop = true
                                        }
                                    }
                                },
                                onBackClick = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }

            val authStateValue = authState
            LaunchedEffect(authStateValue) {
                when (val state = authStateValue) {
                    is AuthUiState.Error -> {
                        Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                        authViewModel.resetUiState()
                    }
                    is AuthUiState.Success -> {
                        val message = if (state.response != null) {
                            context.getString(R.string.auth_login_success)
                        } else {
                            context.getString(R.string.auth_registration_success)
                        }
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        navController.navigate("profile") {
                            popUpTo(navController.graph.findStartDestination().id)
                        }
                        authViewModel.resetUiState()
                    }
                    else -> Unit
                }
            }

            if (authState is AuthUiState.Loading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
fun AppBottomBar(navController: NavController) {
    val navItems = listOf(
        BottomNavItem(R.string.nav_home, "home", Icons.Default.Home),
        BottomNavItem(R.string.nav_catalog, "catalog", Icons.Default.Search),
        BottomNavItem(R.string.nav_profile, "profile", Icons.Default.Person)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val isMainScreen = navItems.any { it.route == currentRoute }

    if (isMainScreen) {
        NavigationBar {
            navItems.forEach { item ->
                NavigationBarItem(
                    selected = currentRoute == item.route,
                    onClick = {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = {
                        Icon(
                            item.icon,
                            contentDescription = stringResource(id = item.titleRes)
                        )
                    },
                    label = { Text(stringResource(id = item.titleRes)) }
                )
            }
        }
    }
}

data class BottomNavItem(
    val titleRes: Int,
    val route: String,
    val icon: ImageVector
)
