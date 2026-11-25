package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.model.ConsentStatus
import com.example.model.ReplayProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.storeDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "glowvault_preferences"
)

data class PersistedStorePrefs(
    val consentStatus: ConsentStatus = ConsentStatus.PENDING,
    val replayProvider: ReplayProvider = ReplayProvider.CLARITY,
    val replayProjectId: String = ReplayProvider.CLARITY.defaultProjectId,
    val cartQuantities: Map<String, Int> = emptyMap()
)

class StorePreferencesRepository(private val context: Context) {

    private object Keys {
        val REPLAY_CONSENT = stringPreferencesKey("replay_consent")
        val REPLAY_PROVIDER = stringPreferencesKey("replay_provider")
        val REPLAY_PROJECT_ID = stringPreferencesKey("replay_project_id")
        val CART_SERIALIZED = stringPreferencesKey("cart_serialized")
    }

    val preferencesFlow: Flow<PersistedStorePrefs> = context.storeDataStore.data.map { prefs ->
        val provider = ReplayProvider.fromKey(prefs[Keys.REPLAY_PROVIDER])
        val projectId = prefs[Keys.REPLAY_PROJECT_ID] ?: provider.defaultProjectId
        val consent = ConsentStatus.fromKey(prefs[Keys.REPLAY_CONSENT])
        val cart = parseCart(prefs[Keys.CART_SERIALIZED].orEmpty())
        PersistedStorePrefs(
            consentStatus = consent,
            replayProvider = provider,
            replayProjectId = projectId,
            cartQuantities = cart
        )
    }

    suspend fun saveConsent(status: ConsentStatus) {
        context.storeDataStore.edit { prefs ->
            if (status == ConsentStatus.PENDING) {
                prefs.remove(Keys.REPLAY_CONSENT)
            } else {
                prefs[Keys.REPLAY_CONSENT] = status.key
            }
        }
    }

    suspend fun saveReplayConfig(provider: ReplayProvider, projectId: String) {
        context.storeDataStore.edit { prefs ->
            prefs[Keys.REPLAY_PROVIDER] = provider.key
            prefs[Keys.REPLAY_PROJECT_ID] = projectId
        }
    }

    suspend fun saveCart(cart: Map<String, Int>) {
        context.storeDataStore.edit { prefs ->
            if (cart.isEmpty()) {
                prefs.remove(Keys.CART_SERIALIZED)
            } else {
                prefs[Keys.CART_SERIALIZED] = serializeCart(cart)
            }
        }
    }

    private fun serializeCart(cart: Map<String, Int>): String =
        cart.entries
            .filter { it.value > 0 }
            .joinToString(",") { "${it.key}:${it.value}" }

    private fun parseCart(serialized: String): Map<String, Int> {
        if (serialized.isBlank()) return emptyMap()
        return buildMap {
            serialized.split(",").forEach { pair ->
                val parts = pair.split(":")
                if (parts.size == 2) {
                    val qty = parts[1].toIntOrNull() ?: 0
                    if (parts[0].isNotBlank() && qty > 0) {
                        put(parts[0], qty)
                    }
                }
            }
        }
    }
}
