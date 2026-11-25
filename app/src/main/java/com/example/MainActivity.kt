package com.example

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.StorePreferencesRepository
import com.example.model.ConsentStatus
import com.example.model.StoreDestination
import com.example.ui.components.GlowVaultBottomBar
import com.example.ui.components.GlowVaultNavigationRail
import com.example.ui.components.GlowVaultTopHeader
import com.example.ui.components.MaskedCheckoutDialog
import com.example.ui.components.PrivacyConsentBanner
import com.example.ui.components.ProductDetailSheet
import com.example.ui.components.ShoppingBagSheet
import com.example.ui.screens.BundleFaqScreen
import com.example.ui.screens.PrivacyReplayScreen
import com.example.ui.screens.ShopScreen
import com.example.ui.theme.Cream
import com.example.ui.theme.Espresso
import com.example.ui.theme.GlowVaultTheme
import com.example.viewmodel.StorefrontViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        )
        setContent {
            GlowVaultTheme {
                GlowVaultApp()
            }
        }
    }
}

@Composable
fun GlowVaultApp() {
    val context = LocalContext.current
    val repository = remember(context.applicationContext) {
        StorePreferencesRepository(context.applicationContext)
    }
    val viewModel: StorefrontViewModel = viewModel(
        factory = StorefrontViewModel.Factory(repository)
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel) {
        viewModel.toastMessages.collect { message ->
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
        }
    }

    // Return to Shop tab when pressing system Back from secondary tabs
    BackHandler(enabled = uiState.currentDestination != StoreDestination.SHOP) {
        viewModel.selectDestination(StoreDestination.SHOP)
    }

    val openExternalUrl: (String) -> Unit = { url ->
        runCatching {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        }.onFailure {
            viewModel.notifyToast("Unable to open link on this device.")
        }
    }

    val copyToClipboard: (String, String, String) -> Unit = { label, text, confirmation ->
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        clipboard?.setPrimaryClip(ClipData.newPlainText(label, text))
        viewModel.notifyToast(confirmation)
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val useNavigationRail = maxWidth >= 840.dp
        val showConsentBanner = uiState.consentStatus == ConsentStatus.PENDING
        val bannerClearance = if (showConsentBanner) 148.dp else 0.dp

        Scaffold(
            topBar = {
                GlowVaultTopHeader(
                    bagItemCount = uiState.cartStats.itemCount,
                    onOpenBag = viewModel::openBag
                )
            },
            bottomBar = {
                if (!useNavigationRail) {
                    GlowVaultBottomBar(
                        currentDestination = uiState.currentDestination,
                        onSelectDestination = viewModel::selectDestination
                    )
                }
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState) { data ->
                    Snackbar(
                        snackbarData = data,
                        containerColor = Espresso,
                        contentColor = Cream,
                        shape = RoundedCornerShape(14.dp)
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Row(modifier = Modifier.fillMaxSize()) {
                    if (useNavigationRail) {
                        GlowVaultNavigationRail(
                            currentDestination = uiState.currentDestination,
                            onSelectDestination = viewModel::selectDestination
                        )
                    }

                    Box(modifier = Modifier.weight(1f).fillMaxSize()) {
                        when (uiState.currentDestination) {
                            StoreDestination.SHOP -> ShopScreen(
                                uiState = uiState,
                                onSelectCategory = viewModel::selectCategory,
                                onAddProduct = { id -> viewModel.addToCart(id) },
                                onAddWinterBundle = viewModel::addWinterBundle,
                                onOpenProductDetail = viewModel::openProductDetail,
                                onOpenExternalUrl = openExternalUrl,
                                onOpenPrivacyPrefs = {
                                    viewModel.updateConsent(ConsentStatus.PENDING)
                                    viewModel.selectDestination(StoreDestination.PRIVACY)
                                },
                                extraBottomPadding = bannerClearance
                            )

                            StoreDestination.BUNDLE -> BundleFaqScreen(
                                uiState = uiState,
                                onAddWinterBundle = viewModel::addWinterBundle,
                                onCopyMarketplaceLink = { link ->
                                    copyToClipboard(
                                        "Shop Link",
                                        link,
                                        "Shop link template copied! Replace with your handle."
                                    )
                                },
                                onToggleFaq = viewModel::toggleFaq,
                                extraBottomPadding = bannerClearance
                            )

                            StoreDestination.PRIVACY -> PrivacyReplayScreen(
                                uiState = uiState,
                                onSelectProvider = viewModel::selectReplayProvider,
                                onProjectIdChange = viewModel::updateReplayProjectId,
                                onCopySnippet = { snippet ->
                                    copyToClipboard(
                                        "Session Replay Snippet",
                                        snippet,
                                        "Consent-gated session replay snippet copied!"
                                    )
                                },
                                onOpenDocs = openExternalUrl,
                                onToggleMaskHighlight = viewModel::toggleMaskHighlight,
                                onResetConsentBanner = {
                                    viewModel.updateConsent(ConsentStatus.PENDING)
                                },
                                onInspectMaskedCheckout = viewModel::inspectMaskedCheckout,
                                extraBottomPadding = bannerClearance
                            )
                        }

                        PrivacyConsentBanner(
                            visible = showConsentBanner,
                            onAccept = { viewModel.updateConsent(ConsentStatus.GRANTED) },
                            onDecline = { viewModel.updateConsent(ConsentStatus.DECLINED) },
                            modifier = Modifier.align(Alignment.BottomCenter)
                        )
                    }
                }
            }
        }
    }

    // Shopping Bag Modal Bottom Sheet
    if (uiState.isBagOpen) {
        ShoppingBagSheet(
            cart = uiState.cart,
            cartStats = uiState.cartStats,
            onDismiss = viewModel::closeBag,
            onUpdateQty = viewModel::updateCartQty,
            onRemove = viewModel::removeFromCart,
            onQuickAddBundle = viewModel::addWinterBundle,
            onProceedCheckout = viewModel::openCheckout
        )
    }

    // Product Detail Modal Bottom Sheet
    uiState.selectedProduct?.let { product ->
        ProductDetailSheet(
            product = product,
            qtyInBag = uiState.cart[product.id] ?: 0,
            onDismiss = viewModel::closeProductDetail,
            onAddToBag = { viewModel.addToCart(product.id) },
            onOpenSource = openExternalUrl
        )
    }

    // Privacy-Masked Express Checkout Modal
    if (uiState.isCheckoutOpen) {
        MaskedCheckoutDialog(
            cartStats = uiState.cartStats,
            maskHighlighted = uiState.maskHighlighted,
            checkoutSuccess = uiState.checkoutSuccess,
            onDismiss = viewModel::closeCheckout,
            onSubmit = viewModel::submitDemoCheckout,
            onCompleteAndReturn = viewModel::completeCheckoutAndResetBag
        )
    }
}
