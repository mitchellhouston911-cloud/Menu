package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.StoreCatalog
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read app name from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("GlowVault Finds", appName)
    }

    @Test
    fun `bundle discount and free shipping tiers calculate accurately`() {
        val twoItems = StoreCatalog.calculateCartStats(mapOf("gbc-power-serum" to 2))
        assertEquals(2, twoItems.itemCount)
        assertEquals(0.0, twoItems.discount, 0.001)
        assertEquals(3.99, twoItems.shipping, 0.001)

        val threeItems = StoreCatalog.calculateCartStats(
            mapOf("gbc-power-serum" to 1, "dermasil-recovery" to 1, "bpure-lip-mask" to 1)
        )
        assertEquals(3, threeItems.itemCount)
        assertEquals(11.97, threeItems.subtotal, 0.001)
        assertEquals(1.197, threeItems.discount, 0.001)
        assertEquals(3.99, threeItems.shipping, 0.001)

        val fourItems = StoreCatalog.calculateCartStats(mapOf("gbc-power-serum" to 4))
        assertEquals(4, fourItems.itemCount)
        assertEquals(0.0, fourItems.shipping, 0.001)
        assertEquals(1.596, fourItems.discount, 0.001)
    }
}
