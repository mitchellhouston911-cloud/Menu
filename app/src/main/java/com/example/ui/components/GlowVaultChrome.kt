package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.StoreDestination
import com.example.ui.theme.AmberSoft
import com.example.ui.theme.Cream
import com.example.ui.theme.EmeraldBright
import com.example.ui.theme.Espresso
import com.example.ui.theme.PlayfairDisplayFontFamily
import com.example.ui.theme.Terracotta

@Composable
fun GlowVaultTopHeader(
    bagItemCount: Int,
    onOpenBag: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Announcement bar with status bar safe-area handling
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Espresso)
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = buildAnnotatedString {
                    append("Every Viral Find is ")
                    withStyle(SpanStyle(color = AmberSoft, fontWeight = FontWeight.Bold)) {
                        append("\$3.99 Flat")
                    }
                    append("  •  Buy 3+ Save ")
                    withStyle(SpanStyle(color = AmberSoft, fontWeight = FontWeight.Bold)) {
                        append("10%")
                    }
                    append("  •  Free Shipping 4+")
                },
                style = MaterialTheme.typography.labelMedium,
                color = Cream,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Brand bar + Shopping Bag pill button
        Surface(
            color = MaterialTheme.colorScheme.background.copy(alpha = 0.96f),
            tonalElevation = 1.dp
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Terracotta),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "G",
                                fontFamily = PlayfairDisplayFontFamily,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White
                            )
                        }
                        Text(
                            text = stringResource(R.string.app_name),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Button(
                        onClick = onOpenBag,
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Espresso,
                            contentColor = Cream
                        ),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                        modifier = Modifier
                            .defaultMinSize(minHeight = 48.dp)
                            .testTag("open_bag_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ShoppingBag,
                            contentDescription = stringResource(R.string.cd_open_bag),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(R.string.bag_label),
                            style = MaterialTheme.typography.labelLarge
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        AnimatedContent(
                            targetState = bagItemCount,
                            label = "bag_count_badge"
                        ) { count ->
                            Box(
                                modifier = Modifier
                                    .defaultMinSize(minWidth = 22.dp, minHeight = 22.dp)
                                    .clip(CircleShape)
                                    .background(Terracotta)
                                    .padding(horizontal = 6.dp, vertical = 2.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = count.toString(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            }
        }
    }
}

@Composable
fun GlowVaultBottomBar(
    currentDestination: StoreDestination,
    onSelectDestination: (StoreDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        windowInsets = WindowInsets.navigationBars
    ) {
        StoreDestination.entries.forEach { destination ->
            val selected = destination == currentDestination
            val (label, selectedIcon, unselectedIcon, tag) = when (destination) {
                StoreDestination.SHOP -> Quad(
                    stringResource(R.string.nav_shop),
                    Icons.Filled.Storefront,
                    Icons.Outlined.Storefront,
                    "nav_shop"
                )
                StoreDestination.BUNDLE -> Quad(
                    stringResource(R.string.nav_bundle),
                    Icons.Filled.CardGiftcard,
                    Icons.Outlined.CardGiftcard,
                    "nav_bundle"
                )
                StoreDestination.PRIVACY -> Quad(
                    stringResource(R.string.nav_privacy),
                    Icons.Filled.Shield,
                    Icons.Outlined.Shield,
                    "nav_privacy"
                )
            }

            NavigationBarItem(
                selected = selected,
                onClick = { onSelectDestination(destination) },
                icon = {
                    Icon(
                        imageVector = if (selected) selectedIcon else unselectedIcon,
                        contentDescription = label
                    )
                },
                label = {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium,
                        maxLines = 1
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.testTag(tag)
            )
        }
    }
}

@Composable
fun GlowVaultNavigationRail(
    currentDestination: StoreDestination,
    onSelectDestination: (StoreDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationRail(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        StoreDestination.entries.forEach { destination ->
            val selected = destination == currentDestination
            val (label, selectedIcon, unselectedIcon, tag) = when (destination) {
                StoreDestination.SHOP -> Quad(
                    stringResource(R.string.nav_shop),
                    Icons.Filled.Storefront,
                    Icons.Outlined.Storefront,
                    "rail_shop"
                )
                StoreDestination.BUNDLE -> Quad(
                    stringResource(R.string.nav_bundle),
                    Icons.Filled.CardGiftcard,
                    Icons.Outlined.CardGiftcard,
                    "rail_bundle"
                )
                StoreDestination.PRIVACY -> Quad(
                    stringResource(R.string.nav_privacy),
                    Icons.Filled.Shield,
                    Icons.Outlined.Shield,
                    "rail_privacy"
                )
            }

            NavigationRailItem(
                selected = selected,
                onClick = { onSelectDestination(destination) },
                icon = {
                    Icon(
                        imageVector = if (selected) selectedIcon else unselectedIcon,
                        contentDescription = label
                    )
                },
                label = {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                colors = NavigationRailItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.testTag(tag)
            )
        }
    }
}

@Composable
fun PrivacyConsentBanner(
    visible: Boolean,
    onAccept: () -> Unit,
    onDecline: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
        modifier = modifier
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Espresso,
            contentColor = Cream,
            shadowElevation = 10.dp,
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.14f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Shield,
                        contentDescription = null,
                        tint = EmeraldBright,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = stringResource(R.string.consent_banner_title),
                        style = MaterialTheme.typography.titleSmall,
                        color = Cream
                    )
                }
                Text(
                    text = stringResource(R.string.consent_banner_body),
                    style = MaterialTheme.typography.bodySmall,
                    color = Cream.copy(alpha = 0.78f)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDecline,
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.28f)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Cream),
                        modifier = Modifier
                            .weight(1f)
                            .defaultMinSize(minHeight = 48.dp)
                            .testTag("consent_decline_button")
                    ) {
                        Text(
                            text = stringResource(R.string.consent_essential_only),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                    Button(
                        onClick = onAccept,
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Terracotta,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .defaultMinSize(minHeight = 48.dp)
                            .testTag("consent_accept_button")
                    ) {
                        Text(
                            text = stringResource(R.string.consent_accept),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        }
    }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
