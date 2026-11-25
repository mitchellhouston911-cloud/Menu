package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.StorePreferencesRepository
import com.example.model.CartStats
import com.example.model.ConsentStatus
import com.example.model.ProductCategory
import com.example.model.ReplayProvider
import com.example.model.SkincareProduct
import com.example.model.StoreCatalog
import com.example.model.StoreDestination
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StorefrontUiState(
    val currentDestination: StoreDestination = StoreDestination.SHOP,
    val activeCategory: ProductCategory = ProductCategory.ALL,
    val products: List<SkincareProduct> = StoreCatalog.products,
    val cart: Map<String, Int> = emptyMap(),
    val cartStats: CartStats = CartStats(),
    val selectedProduct: SkincareProduct? = null,
    val isBagOpen: Boolean = false,
    val isCheckoutOpen: Boolean = false,
    val checkoutSuccess: Boolean = false,
    val openFaqIndex: Int = 0,
    val replayProvider: ReplayProvider = ReplayProvider.CLARITY,
    val replayProjectId: String = ReplayProvider.CLARITY.defaultProjectId,
    val consentStatus: ConsentStatus = ConsentStatus.PENDING,
    val maskHighlighted: Boolean = false
) {
    val filteredProducts: List<SkincareProduct>
        get() = if (activeCategory == ProductCategory.ALL) {
            products
        } else {
            products.filter { it.category == activeCategory }
        }

    val replaySnippet: String
        get() = StoreCatalog.buildReplaySnippet(replayProvider, replayProjectId)
}

class StorefrontViewModel(
    private val preferencesRepository: StorePreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StorefrontUiState())
    val uiState: StateFlow<StorefrontUiState> = _uiState.asStateFlow()

    private val _toastChannel = Channel<String>(Channel.BUFFERED)
    val toastMessages: Flow<String> = _toastChannel.receiveAsFlow()

    init {
        viewModelScope.launch {
            val saved = preferencesRepository.preferencesFlow.first()
            _uiState.update { state ->
                state.copy(
                    consentStatus = saved.consentStatus,
                    replayProvider = saved.replayProvider,
                    replayProjectId = saved.replayProjectId,
                    cart = saved.cartQuantities,
                    cartStats = StoreCatalog.calculateCartStats(saved.cartQuantities)
                )
            }
        }
    }

    fun selectDestination(destination: StoreDestination) {
        _uiState.update { it.copy(currentDestination = destination) }
    }

    fun selectCategory(category: ProductCategory) {
        _uiState.update { it.copy(activeCategory = category) }
    }

    fun openProductDetail(product: SkincareProduct) {
        _uiState.update { it.copy(selectedProduct = product) }
    }

    fun closeProductDetail() {
        _uiState.update { it.copy(selectedProduct = null) }
    }

    fun addToCart(productId: String, qty: Int = 1, openBag: Boolean = false) {
        val currentCart = _uiState.value.cart.toMutableMap()
        currentCart[productId] = (currentCart[productId] ?: 0) + qty
        val stats = StoreCatalog.calculateCartStats(currentCart)
        _uiState.update {
            it.copy(
                cart = currentCart,
                cartStats = stats,
                isBagOpen = if (openBag) true else it.isBagOpen
            )
        }
        persistCart(currentCart)
        val product = StoreCatalog.findById(productId)
        if (product != null && !openBag) {
            emitToast("Added ${product.name}")
        }
    }

    fun updateCartQty(productId: String, delta: Int) {
        val currentCart = _uiState.value.cart.toMutableMap()
        val next = (currentCart[productId] ?: 0) + delta
        if (next <= 0) {
            currentCart.remove(productId)
        } else {
            currentCart[productId] = next
        }
        val stats = StoreCatalog.calculateCartStats(currentCart)
        _uiState.update { it.copy(cart = currentCart, cartStats = stats) }
        persistCart(currentCart)
    }

    fun removeFromCart(productId: String) {
        val currentCart = _uiState.value.cart.toMutableMap()
        currentCart.remove(productId)
        val stats = StoreCatalog.calculateCartStats(currentCart)
        _uiState.update { it.copy(cart = currentCart, cartStats = stats) }
        persistCart(currentCart)
    }

    fun addWinterBundle() {
        val currentCart = _uiState.value.cart.toMutableMap()
        StoreCatalog.winterBundleProductIds.forEach { id ->
            currentCart[id] = (currentCart[id] ?: 0) + 1
        }
        val stats = StoreCatalog.calculateCartStats(currentCart)
        _uiState.update {
            it.copy(
                cart = currentCart,
                cartStats = stats,
                isBagOpen = true
            )
        }
        persistCart(currentCart)
        emitToast("Winter Glow 3-Pack added (10% off applied!)")
    }

    fun openBag() {
        _uiState.update { it.copy(isBagOpen = true) }
    }

    fun closeBag() {
        _uiState.update { it.copy(isBagOpen = false) }
    }

    fun openCheckout() {
        _uiState.update {
            it.copy(
                isBagOpen = false,
                isCheckoutOpen = true,
                checkoutSuccess = false
            )
        }
    }

    fun closeCheckout() {
        _uiState.update { it.copy(isCheckoutOpen = false, checkoutSuccess = false) }
    }

    fun submitDemoCheckout() {
        _uiState.update { it.copy(checkoutSuccess = true) }
    }

    fun completeCheckoutAndResetBag() {
        _uiState.update {
            it.copy(
                cart = emptyMap(),
                cartStats = CartStats(),
                isCheckoutOpen = false,
                checkoutSuccess = false
            )
        }
        persistCart(emptyMap())
    }

    fun toggleFaq(index: Int) {
        _uiState.update { state ->
            state.copy(openFaqIndex = if (state.openFaqIndex == index) -1 else index)
        }
    }

    fun selectReplayProvider(provider: ReplayProvider) {
        val state = _uiState.value
        val updatedId = when {
            provider == ReplayProvider.CLARITY && state.replayProjectId == ReplayProvider.POSTHOG.defaultProjectId ->
                ReplayProvider.CLARITY.defaultProjectId
            provider == ReplayProvider.POSTHOG && state.replayProjectId == ReplayProvider.CLARITY.defaultProjectId ->
                ReplayProvider.POSTHOG.defaultProjectId
            else -> state.replayProjectId
        }
        _uiState.update { it.copy(replayProvider = provider, replayProjectId = updatedId) }
        viewModelScope.launch {
            preferencesRepository.saveReplayConfig(provider, updatedId)
        }
    }

    fun updateReplayProjectId(newId: String) {
        val provider = _uiState.value.replayProvider
        _uiState.update { it.copy(replayProjectId = newId) }
        viewModelScope.launch {
            preferencesRepository.saveReplayConfig(provider, newId)
        }
    }

    fun toggleMaskHighlight() {
        val next = !_uiState.value.maskHighlighted
        _uiState.update { it.copy(maskHighlighted = next) }
        emitToast(
            if (next) "Highlighting PII-masked zones (open Checkout to see!)"
            else "Mask highlight turned off"
        )
    }

    fun inspectMaskedCheckout() {
        val currentCart = _uiState.value.cart.toMutableMap()
        if (StoreCatalog.calculateCartStats(currentCart).itemCount == 0) {
            currentCart["gbc-power-serum"] = 1
            persistCart(currentCart)
        }
        val stats = StoreCatalog.calculateCartStats(currentCart)
        _uiState.update {
            it.copy(
                cart = currentCart,
                cartStats = stats,
                maskHighlighted = true,
                isBagOpen = false,
                isCheckoutOpen = true,
                checkoutSuccess = false
            )
        }
    }

    fun updateConsent(status: ConsentStatus) {
        _uiState.update { it.copy(consentStatus = status) }
        viewModelScope.launch {
            preferencesRepository.saveConsent(status)
        }
        when (status) {
            ConsentStatus.GRANTED ->
                emitToast("Session replay consent granted (PII fields remain masked)")
            ConsentStatus.DECLINED ->
                emitToast("Essential-only mode saved — replay script stays off")
            ConsentStatus.PENDING -> Unit
        }
    }

    fun notifyToast(message: String) {
        emitToast(message)
    }

    private fun persistCart(cart: Map<String, Int>) {
        viewModelScope.launch {
            preferencesRepository.saveCart(cart)
        }
    }

    private fun emitToast(message: String) {
        _toastChannel.trySend(message)
    }

    class Factory(
        private val preferencesRepository: StorePreferencesRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return StorefrontViewModel(preferencesRepository) as T
        }
    }
}
