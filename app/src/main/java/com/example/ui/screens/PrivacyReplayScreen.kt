package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.ConsentStatus
import com.example.model.ReplayProvider
import com.example.ui.theme.AmberContainer
import com.example.ui.theme.AmberOnContainer
import com.example.ui.theme.AmberSoft
import com.example.ui.theme.Cream
import com.example.ui.theme.Espresso
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.Sage
import com.example.ui.theme.SageSoft
import com.example.viewmodel.StorefrontUiState

@Composable
fun PrivacyReplayScreen(
    uiState: StorefrontUiState,
    onSelectProvider: (ReplayProvider) -> Unit,
    onProjectIdChange: (String) -> Unit,
    onCopySnippet: (String) -> Unit,
    onOpenDocs: (String) -> Unit,
    onToggleMaskHighlight: () -> Unit,
    onResetConsentBanner: () -> Unit,
    onInspectMaskedCheckout: () -> Unit,
    extraBottomPadding: Dp,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 20.dp,
            bottom = extraBottomPadding + 28.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header & Consent Status Controls
        item(key = "replay_header") {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 880.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = stringResource(R.string.replay_kicker).uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = stringResource(R.string.replay_title),
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = stringResource(R.string.replay_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Live Consent Status Pill
                val (pillBg, pillText, dotColor, statusText) = when (uiState.consentStatus) {
                    ConsentStatus.GRANTED -> StatusStyle(
                        SageSoft,
                        Sage,
                        Sage,
                        "Consent: Granted (Masked Replay Ready)"
                    )
                    ConsentStatus.DECLINED -> StatusStyle(
                        MaterialTheme.colorScheme.surfaceVariant,
                        MaterialTheme.colorScheme.onSurfaceVariant,
                        MaterialTheme.colorScheme.onSurfaceVariant,
                        "Consent: Declined (Essential Only)"
                    )
                    ConsentStatus.PENDING -> StatusStyle(
                        AmberContainer,
                        AmberOnContainer,
                        Color(0xFFD97706),
                        "Consent: Pending (Script Blocked)"
                    )
                }

                Surface(
                    shape = RoundedCornerShape(50),
                    color = pillBg,
                    contentColor = pillText,
                    modifier = Modifier.testTag("consent_status_pill")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(dotColor)
                        )
                        Text(
                            text = statusText,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onToggleMaskHighlight,
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(1.dp, Sage),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (uiState.maskHighlighted) SageSoft else Color.Transparent,
                            contentColor = Sage
                        ),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
                        modifier = Modifier
                            .weight(1.3f)
                            .defaultMinSize(minHeight = 48.dp)
                            .testTag("toggle_mask_highlight_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.VisibilityOff,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (uiState.maskHighlighted) {
                                stringResource(R.string.replay_highlight_active)
                            } else {
                                stringResource(R.string.replay_highlight_zones)
                            },
                            style = MaterialTheme.typography.labelMedium,
                            maxLines = 1
                        )
                    }

                    OutlinedButton(
                        onClick = onResetConsentBanner,
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
                        modifier = Modifier
                            .weight(0.9f)
                            .defaultMinSize(minHeight = 48.dp)
                            .testTag("reset_consent_banner_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(R.string.replay_reset_banner),
                            style = MaterialTheme.typography.labelMedium,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        // Provider Config Card
        item(key = "provider_config") {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 880.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = stringResource(R.string.replay_step_provider).uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ReplayProvider.entries.forEach { provider ->
                            val selected = provider == uiState.replayProvider
                            Button(
                                onClick = { onSelectProvider(provider) },
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (selected) Espresso else MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = if (selected) Cream else MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .defaultMinSize(minHeight = 48.dp)
                                    .testTag("provider_${provider.key}")
                            ) {
                                Text(
                                    text = provider.displayName,
                                    style = MaterialTheme.typography.labelMedium,
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    // Provider Highlights
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerLow,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            uiState.replayProvider.bullets.forEach { bullet ->
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(
                                        text = "•",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Sage,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = bullet,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    // Live Project ID Input
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = stringResource(R.string.replay_step_id).uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        OutlinedTextField(
                            value = uiState.replayProjectId,
                            onValueChange = onProjectIdChange,
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            placeholder = { Text("Paste your Project ID") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("replay_project_id_input")
                        )
                        Text(
                            text = stringResource(R.string.replay_id_hint),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { onCopySnippet(uiState.replaySnippet) },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Sage,
                                contentColor = Color.White
                            ),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
                            modifier = Modifier
                                .weight(1.35f)
                                .defaultMinSize(minHeight = 48.dp)
                                .testTag("copy_snippet_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ContentCopy,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = stringResource(R.string.replay_copy_snippet),
                                style = MaterialTheme.typography.labelMedium,
                                maxLines = 1
                            )
                        }

                        OutlinedButton(
                            onClick = { onOpenDocs(uiState.replayProvider.docUrl) },
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
                            modifier = Modifier
                                .weight(0.85f)
                                .defaultMinSize(minHeight = 48.dp)
                                .testTag("open_masking_docs_button")
                        ) {
                            Text(
                                text = stringResource(R.string.replay_masking_docs),
                                style = MaterialTheme.typography.labelMedium,
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                }
            }
        }

        // Live Code Snippet Preview Panel
        item(key = "code_preview") {
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = Espresso,
                contentColor = Cream,
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 880.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = uiState.replayProvider.snippetTitle.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = AmberSoft,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.White.copy(alpha = 0.12f),
                            contentColor = Cream
                        ) {
                            Text(
                                text = stringResource(R.string.replay_zero_pii),
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    HorizontalDivider(color = Color.White.copy(alpha = 0.12f))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = uiState.replaySnippet,
                            fontFamily = JetBrainsMonoFontFamily,
                            fontSize = 11.5.sp,
                            lineHeight = 17.sp,
                            color = Cream.copy(alpha = 0.92f)
                        )
                    }

                    HorizontalDivider(color = Color.White.copy(alpha = 0.12f))

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = stringResource(R.string.replay_protected_fields),
                            style = MaterialTheme.typography.bodySmall,
                            color = Cream.copy(alpha = 0.72f)
                        )
                        TextButton(
                            onClick = onInspectMaskedCheckout,
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier
                                .defaultMinSize(minHeight = 48.dp)
                                .testTag("inspect_masked_checkout_button")
                        ) {
                            Text(
                                text = stringResource(R.string.replay_inspect_checkout),
                                style = MaterialTheme.typography.labelLarge,
                                color = AmberSoft
                            )
                        }
                    }
                }
            }
        }
    }
}

private data class StatusStyle(
    val bg: Color,
    val content: Color,
    val dot: Color,
    val text: String
)
