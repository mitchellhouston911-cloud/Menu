package com.example

import com.example.model.ReplayProvider
import com.example.model.StoreCatalog
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun `bundle discount and free shipping tiers calculate accurately`() {
        val empty = StoreCatalog.calculateCartStats(emptyMap())
        assertEquals(0, empty.itemCount)
        assertEquals(0.0, empty.total, 0.001)

        val twoItems = StoreCatalog.calculateCartStats(mapOf("gbc-power-serum" to 2))
        assertEquals(2, twoItems.itemCount)
        assertEquals(0.0, twoItems.discount, 0.001)
        assertEquals(3.99, twoItems.shipping, 0.001)
        assertEquals(11.97, twoItems.total, 0.001)

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

    @Test
    fun `session replay snippets embed custom project IDs`() {
        val claritySnippet = StoreCatalog.buildReplaySnippet(ReplayProvider.CLARITY, "abc123")
        assertTrue(claritySnippet.contains("\"abc123\""))
        assertTrue(claritySnippet.contains("clarity.ms/tag/"))

        val posthogSnippet = StoreCatalog.buildReplaySnippet(ReplayProvider.POSTHOG, "phc_999")
        assertTrue(posthogSnippet.contains("\"phc_999\""))
        assertTrue(posthogSnippet.contains("maskAllInputs: true"))
    }
}
