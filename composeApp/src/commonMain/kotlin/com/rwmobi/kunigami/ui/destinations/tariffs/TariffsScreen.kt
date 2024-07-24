/*
 * Copyright (c) 2024. Ryan Wong
 * https://github.com/ryanw-mobile
 * Sponsored by RW MobiMedia UK Limited
 *
 */

package com.rwmobi.kunigami.ui.destinations.tariffs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import com.rwmobi.kunigami.ui.components.DualTitleBar
import com.rwmobi.kunigami.ui.components.ErrorScreenHandler
import com.rwmobi.kunigami.ui.components.LoadingScreen
import com.rwmobi.kunigami.ui.components.ScrollbarMultiplatform
import com.rwmobi.kunigami.ui.composehelper.conditionalBlur
import com.rwmobi.kunigami.ui.destinations.tariffs.components.ButtonTitleBar
import com.rwmobi.kunigami.ui.destinations.tariffs.components.DetailsScreenWrapper
import com.rwmobi.kunigami.ui.destinations.tariffs.components.PostcodeInputBar
import com.rwmobi.kunigami.ui.destinations.tariffs.components.ProductItemAdaptive
import com.rwmobi.kunigami.ui.destinations.tariffs.components.TariffBottomSheet
import com.rwmobi.kunigami.ui.model.SpecialErrorScreen
import com.rwmobi.kunigami.ui.theme.getDimension
import kunigami.composeapp.generated.resources.Res
import kunigami.composeapp.generated.resources.arrow_ios_back_fill
import kunigami.composeapp.generated.resources.close_fill
import kunigami.composeapp.generated.resources.content_description_dismiss_this_pane
import kunigami.composeapp.generated.resources.content_description_navigate_back
import kunigami.composeapp.generated.resources.navigation_tariffs
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TariffsScreen(
    modifier: Modifier = Modifier,
    uiState: TariffsUIState,
    uiEvent: TariffsUIEvent,
) {
    if (uiState.errorMessages.isNotEmpty()) {
        val errorMessage = remember(uiState) { uiState.errorMessages[0] }
        val errorMessageText = errorMessage.message

        LaunchedEffect(errorMessage.id) {
            uiEvent.onShowSnackbar(errorMessageText)
            uiEvent.onErrorShown(errorMessage.id)
        }
    }

    val dimension = LocalDensity.current.getDimension()
    val mainLazyListState = rememberLazyListState()

    Box(modifier = modifier) {
        when {
            uiState.requestedScreenType is TariffsScreenType.Error -> {
                ErrorScreenHandler(
                    modifier = Modifier.fillMaxSize(),
                    specialErrorScreen = uiState.requestedScreenType.specialErrorScreen,
                    onRefresh = { uiEvent.onRefresh() },
                )
            }

            uiState.requestedScreenType == TariffsScreenType.List -> {
                Row(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    ScrollbarMultiplatform(
                        modifier = Modifier.weight(weight = 1f),
                        lazyListState = mainLazyListState,
                    ) { contentModifier ->
                        Column(
                            modifier = contentModifier.fillMaxSize(),
                        ) {
                            DualTitleBar(
                                modifier = Modifier
                                    .background(color = MaterialTheme.colorScheme.secondary)
                                    .fillMaxWidth()
                                    .height(height = dimension.minListItemHeight),
                                title = stringResource(resource = Res.string.navigation_tariffs),
                            )

                            uiState.queryPostCode?.let { postcode ->
                                PostcodeInputBar(
                                    modifier = Modifier.fillMaxWidth(),
                                    postcode = postcode,
                                    onUpdatePostcode = { uiEvent.onQueryPostcode(it) },
                                )
                            }

                            LazyColumn(
                                state = mainLazyListState,
                            ) {
                                if (uiState.productSummaries.isNotEmpty()) {
                                    itemsIndexed(
                                        items = uiState.productSummaries,
                                        key = { _, product -> product.code },
                                    ) { index, product ->
                                        ProductItemAdaptive(
                                            modifier = Modifier
                                                .clickable(
                                                    onClick = {
                                                        uiState.queryPostCode?.let { uiEvent.onProductItemClick(product.code, it) }
                                                    },
                                                )
                                                .fillMaxWidth()
                                                .padding(vertical = dimension.grid_1),
                                            productSummary = product,
                                            useWideLayout = uiState.requestedWideListLayout,
                                        )

                                        if (index < uiState.productSummaries.lastIndex) {
                                            HorizontalDivider(
                                                modifier = Modifier.fillMaxWidth(),
                                                color = MaterialTheme.colorScheme.surfaceContainerHighest,
                                            )
                                        }
                                    }
                                } else if (!uiState.isLoading) {
                                    item {
                                        ErrorScreenHandler(
                                            modifier = Modifier.fillMaxSize(),
                                            specialErrorScreen = SpecialErrorScreen.NoData,
                                            onRefresh = {
                                                uiEvent.onRefresh()
                                            },
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (uiState.requestedLayout == TariffScreenLayout.ListDetailPane) {
                        VerticalDivider(
                            modifier = Modifier.fillMaxHeight(),
                            color = MaterialTheme.colorScheme.surfaceContainerHighest,
                        )

                        DetailsScreenWrapper(
                            modifier = Modifier.weight(weight = 1f),
                            productDetails = uiState.productDetails,
                        ) {
                            uiState.productDetails?.let { productDetails ->
                                ButtonTitleBar(
                                    modifier = Modifier
                                        .background(color = MaterialTheme.colorScheme.secondary)
                                        .fillMaxWidth()
                                        .height(height = dimension.minListItemHeight),
                                    title = productDetails.displayName,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    rightButton = {
                                        IconButton(onClick = uiEvent.onProductDetailsDismissed) {
                                            Icon(
                                                painter = painterResource(resource = Res.drawable.close_fill),
                                                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                                contentDescription = stringResource(resource = Res.string.content_description_dismiss_this_pane),
                                            )
                                        }
                                    },
                                )
                            }
                        }
                    }
                }
            }

            uiState.requestedScreenType == TariffsScreenType.FullScreenDetail -> {
                DetailsScreenWrapper(
                    modifier = Modifier
                        .fillMaxSize()
                        .conditionalBlur(enabled = uiState.isLoading && uiState.productSummaries.isEmpty()),
                    productDetails = uiState.productDetails,
                ) {
                    ButtonTitleBar(
                        modifier = Modifier
                            .background(color = MaterialTheme.colorScheme.secondary)
                            .fillMaxWidth()
                            .height(height = dimension.minListItemHeight),
                        title = uiState.productDetails?.displayName ?: "",
                        color = MaterialTheme.colorScheme.onSecondary,
                        leftButton = {
                            IconButton(onClick = uiEvent.onProductDetailsDismissed) {
                                Icon(
                                    painter = painterResource(resource = Res.drawable.arrow_ios_back_fill),
                                    tint = MaterialTheme.colorScheme.onSecondary,
                                    contentDescription = stringResource(resource = Res.string.content_description_navigate_back),
                                )
                            }
                        },
                    )
                }
            }
        }

        if (uiState.shouldShowLoadingScreen()) {
            LoadingScreen(
                modifier = Modifier.fillMaxSize(),
            )
        }
    }

    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    if (bottomSheetState.isVisible &&
        uiState.requestedLayout != TariffScreenLayout.ListDetailPane &&
        uiState.requestedScreenType !is TariffsScreenType.Error
    ) {
        TariffBottomSheet(
            modifier = Modifier.fillMaxSize(),
            productDetails = uiState.productDetails,
            bottomSheetState = bottomSheetState,
            onDismissRequest = uiEvent.onProductDetailsDismissed,
        )
    }

    LaunchedEffect(true) {
        uiEvent.onRefresh()
    }

    LaunchedEffect(uiState.productDetails, uiState.requestedLayout) {
        if (uiState.shouldUseBottomSheet()) {
            when {
                uiState.productDetails != null && !bottomSheetState.isVisible && (uiState.requestedScreenType !is TariffsScreenType.Error) -> bottomSheetState.show()
                (uiState.productDetails == null || uiState.requestedScreenType is TariffsScreenType.Error) && bottomSheetState.isVisible -> bottomSheetState.hide()
            }
        }
    }

    // We assign this to the main view only. The secondary pane (if any) won't be scrolled.
    LaunchedEffect(uiState.requestScrollToTop) {
        if (uiState.requestScrollToTop) {
            mainLazyListState.scrollToItem(index = 0)
            uiEvent.onScrolledToTop()
        }
    }
}
