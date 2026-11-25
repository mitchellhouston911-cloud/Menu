package com.example.model

import androidx.annotation.DrawableRes
import com.example.R

enum class ProductCategory(val label: String) {
    ALL("All Finds"),
    SERUMS("Serums"),
    MOISTURIZERS("Moisturizers"),
    LIPS_AND_BODY("Lips & Body")
}

data class SkincareProduct(
    val id: String,
    val name: String,
    val brand: String,
    val category: ProductCategory,
    val price: Double,
    val compareTo: String,
    val badge: String,
    val tagline: String,
    @DrawableRes val imageRes: Int,
    val sourceUrl: String,
    val sourceLabel: String
)

data class FaqItem(
    val question: String,
    val answer: String
)

enum class ReplayProvider(
    val key: String,
    val displayName: String,
    val snippetTitle: String,
    val defaultProjectId: String,
    val docUrl: String,
    val bullets: List<String>
) {
    CLARITY(
        key = "clarity",
        displayName = "Microsoft Clarity",
        snippetTitle = "Microsoft Clarity — Consent-Gated Loader",
        defaultProjectId = "YOUR_CLARITY_PROJECT_ID",
        docUrl = "https://learn.microsoft.com/en-us/clarity/setup-and-installation/clarity-masking",
        bullets = listOf(
            "Free forever with unlimited heatmaps and session replays.",
            "Input boxes and dropdowns are automatically masked in all modes.",
            "Set dashboard to Balanced or Strict masking + use data-clarity-mask=\"true\"."
        )
    ),
    POSTHOG(
        key = "posthog",
        displayName = "PostHog Replay",
        snippetTitle = "PostHog — Consent-Gated Loader",
        defaultProjectId = "YOUR_POSTHOG_PROJECT_KEY",
        docUrl = "https://posthog.com/docs/session-replay/privacy",
        bullets = listOf(
            "Free tier includes 5,000 web recordings/month & 1M events/month.",
            "Browser-side masking ensures masked inputs never leave the visitor's device.",
            "Use class=\"ph-no-capture\" to completely exclude sensitive checkout blocks."
        )
    );

    companion object {
        fun fromKey(key: String?): ReplayProvider =
            entries.firstOrNull { it.key == key } ?: CLARITY
    }
}

enum class ConsentStatus(val key: String) {
    PENDING("pending"),
    GRANTED("granted"),
    DECLINED("declined");

    companion object {
        fun fromKey(key: String?): ConsentStatus =
            entries.firstOrNull { it.key == key } ?: PENDING
    }
}

enum class StoreDestination(val route: String) {
    SHOP("shop"),
    BUNDLE("bundle"),
    PRIVACY("privacy")
}

data class CartStats(
    val itemCount: Int = 0,
    val subtotal: Double = 0.0,
    val discount: Double = 0.0,
    val shipping: Double = 0.0,
    val total: Double = 0.0
) {
    val progressFraction: Float
        get() = when {
            itemCount <= 0 -> 0f
            itemCount >= 4 -> 1f
            else -> itemCount / 4f
        }

    val progressMessage: String
        get() = when {
            itemCount == 0 -> "Add 3 items to unlock 10% OFF (4 items for FREE shipping!)"
            itemCount < 3 -> {
                val left = 3 - itemCount
                "Add $left more item${if (left > 1) "s" else ""} to unlock 10% OFF your bundle!"
            }
            itemCount == 3 -> "10% OFF unlocked! Add 1 more item for FREE shipping."
            else -> "10% OFF + FREE lightweight shipping unlocked!"
        }
}

object StoreCatalog {
    const val FLAT_PRICE = 3.99
    const val STANDARD_SHIPPING = 3.99
    const val BUNDLE_DISCOUNT_RATE = 0.10
    const val BUNDLE_THRESHOLD = 3
    const val FREE_SHIPPING_THRESHOLD = 4

    const val MERCARI_TEMPLATE_URL = "https://www.mercari.com/u/your-shop-handle/"
    const val DEPOP_TEMPLATE_URL = "https://www.depop.com/your-shop-handle/"

    val winterBundleProductIds = listOf(
        "gbc-power-serum",
        "dermasil-recovery",
        "bpure-lip-mask"
    )

    val products: List<SkincareProduct> = listOf(
        SkincareProduct(
            id = "gbc-power-serum",
            name = "Global Beauty Care Power Serum",
            brand = "Global Beauty Care",
            category = ProductCategory.SERUMS,
            price = FLAT_PRICE,
            compareTo = "Olay Super Serum",
            badge = "Viral 6-in-1",
            tagline = "Targets skin firmness, uneven tone, and dullness in one lightweight step.",
            imageRes = R.drawable.img_gbc_power_serum,
            sourceUrl = "https://www.lemon8-app.com/@xoxoessence__/7450967304789131822?region=us",
            sourceLabel = "Lemon8 Review"
        ),
        SkincareProduct(
            id = "bpure-glow-serum",
            name = "B. Pure Glow Up Revitalizing Serum",
            brand = "B. Pure",
            category = ProductCategory.SERUMS,
            price = FLAT_PRICE,
            compareTo = "Glow Recipe Serums (~\$40)",
            badge = "Dewy Finish",
            tagline = "Pomegranate and strawberry extract formula for a bouncy, radiant winter glow.",
            imageRes = R.drawable.img_bpure_glow_serum,
            sourceUrl = "https://www.youtube.com/shorts/FB9wuemFlR4",
            sourceLabel = "Krazy Coupon Lady"
        ),
        SkincareProduct(
            id = "dermasil-recovery",
            name = "Dermasil Whipped Recovery Cream",
            brand = "Dermasil",
            category = ProductCategory.MOISTURIZERS,
            price = FLAT_PRICE,
            compareTo = "Drunk Elephant Skincare",
            badge = "Winter Staple",
            tagline = "Rich, soothing barrier moisturizer designed to calm dry, tight winter skin.",
            imageRes = R.drawable.img_dermasil_recovery,
            sourceUrl = "https://www.brit.co/dollar-tree-beauty-dupes/",
            sourceLabel = "Brit + Co"
        ),
        SkincareProduct(
            id = "xtracare-hydro-gel",
            name = "XtraCare Hydro Plus Water Gel",
            brand = "XtraCare",
            category = ProductCategory.MOISTURIZERS,
            price = FLAT_PRICE,
            compareTo = "Prestige Water-Gel Creams",
            badge = "Featherlight",
            tagline = "Fast-absorbing daily gel moisturizer that hydrates without heaviness or grease.",
            imageRes = R.drawable.img_xtracare_hydro_gel,
            sourceUrl = "https://financebuzz.com/news/dollar-tree-beauty-dupes-3",
            sourceLabel = "FinanceBuzz"
        ),
        SkincareProduct(
            id = "bpure-lip-mask",
            name = "B. Pure Hydrating Night Lip Mask",
            brand = "B. Pure",
            category = ProductCategory.LIPS_AND_BODY,
            price = FLAT_PRICE,
            compareTo = "Luxury Overnight Lip Masks",
            badge = "Bestseller",
            tagline = "Cushiony overnight balm that locks in moisture for soft, flake-free morning lips.",
            imageRes = R.drawable.img_bpure_lip_mask,
            sourceUrl = "https://financebuzz.com/news/dollar-tree-beauty-dupes-3",
            sourceLabel = "FinanceBuzz"
        ),
        SkincareProduct(
            id = "spa-luxury-butter",
            name = "Spa Luxury Brazilian Sol Body Butter",
            brand = "Spa Luxury",
            category = ProductCategory.LIPS_AND_BODY,
            price = FLAT_PRICE,
            compareTo = "Sol de Janeiro Bum Bum (\$48)",
            badge = "Viral Scent",
            tagline = "Warm pistachio and caramel scented whipped body butter for silky winter skin.",
            imageRes = R.drawable.img_spa_luxury_butter,
            sourceUrl = "https://www.brit.co/dollar-tree-beauty-dupes/",
            sourceLabel = "Brit + Co"
        )
    )

    val faqs: List<FaqItem> = listOf(
        FaqItem(
            question = "Are all skincare items brand new and sealed?",
            answer = "Yes! Every single serum, cream, and lip mask is 100% brand new, unopened, factory sealed, and stored in a clean, climate-controlled, smoke-free space."
        ),
        FaqItem(
            question = "How does shipping work for \$3.99 items?",
            answer = "Because skincare tubes and serums are lightweight, orders of 1–3 items ship for a flat \$3.99 tracked rate in padded protective mailers within 24 hours. Grab 4+ items and your shipping is completely FREE."
        ),
        FaqItem(
            question = "How does the 10% bundle discount apply?",
            answer = "Just mix and match any 3 or more items in your bag. The 10% discount calculates automatically in your cart — no promo code needed."
        ),
        FaqItem(
            question = "What if I have sensitive skin?",
            answer = "We always recommend reviewing the full ingredient list on the packaging and performing a small patch test on your inner wrist before adding a new product to your daily routine."
        )
    )

    fun findById(id: String): SkincareProduct? = products.firstOrNull { it.id == id }

    fun calculateCartStats(cart: Map<String, Int>): CartStats {
        var itemCount = 0
        var subtotal = 0.0
        cart.forEach { (id, qty) ->
            val product = findById(id)
            if (product != null && qty > 0) {
                itemCount += qty
                subtotal += product.price * qty
            }
        }
        val discount = if (itemCount >= BUNDLE_THRESHOLD) subtotal * BUNDLE_DISCOUNT_RATE else 0.0
        val shipping = when {
            itemCount == 0 -> 0.0
            itemCount >= FREE_SHIPPING_THRESHOLD -> 0.0
            else -> STANDARD_SHIPPING
        }
        val total = (subtotal - discount + shipping).coerceAtLeast(0.0)
        return CartStats(
            itemCount = itemCount,
            subtotal = subtotal,
            discount = discount,
            shipping = shipping,
            total = total
        )
    }

    fun buildReplaySnippet(provider: ReplayProvider, projectId: String): String {
        val id = projectId.trim().ifEmpty { provider.defaultProjectId }
        return when (provider) {
            ReplayProvider.CLARITY -> """
                <!-- Privacy-Respecting Microsoft Clarity (Consent-Gated) -->
                <script>
                  function initClarityReplay() {
                    if (window.clarityLoaded) return;
                    window.clarityLoaded = true;
                    (function(c,l,a,r,i,t,y){
                      c[a]=c[a]||function(){(c[a].q=c[a].q||[]).push(arguments)};
                      t=l.createElement(r);t.async=1;t.src="https://www.clarity.ms/tag/"+i;
                      y=l.getElementsByTagName(r)[0];y.parentNode.insertBefore(t,y);
                    })(window, document, "clarity", "script", "$id");
                  }
                  // Fire ONLY after visitor clicks "Accept Replay"
                  if (localStorage.getItem('replay_consent') === 'granted') {
                    initClarityReplay();
                  }
                </script>
            """.trimIndent()

            ReplayProvider.POSTHOG -> """
                <!-- Privacy-Respecting PostHog Session Replay (Consent-Gated) -->
                <script>
                  function initPostHogReplay() {
                    if (window.posthogLoaded) return;
                    window.posthogLoaded = true;
                    // Initialize with strict browser-side input & element masking
                    posthog.init("$id", {
                      api_host: "https://us.i.posthog.com",
                      session_recording: {
                        maskAllInputs: true,
                        maskTextSelector: '[data-clarity-mask="true"], .ph-mask'
                      }
                    });
                  }
                  // Fire ONLY after visitor clicks "Accept Replay"
                  if (localStorage.getItem('replay_consent') === 'granted') {
                    initPostHogReplay();
                  }
                </script>
            """.trimIndent()
        }
    }
}
