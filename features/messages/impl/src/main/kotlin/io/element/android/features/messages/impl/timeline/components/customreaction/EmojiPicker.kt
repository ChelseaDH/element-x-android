/*
 * Copyright (c) 2023 New Vector Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.element.android.features.messages.impl.timeline.components.customreaction

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.emojibasebindings.Emoji
import io.element.android.emojibasebindings.EmojibaseCategory
import io.element.android.emojibasebindings.EmojibaseDatasource
import io.element.android.emojibasebindings.EmojibaseStore
import io.element.android.emojibasebindings.allEmojis
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.text.toSp
import io.element.android.libraries.designsystem.theme.components.ElementSearchBarDefaults
import io.element.android.libraries.designsystem.theme.components.Icon
import io.element.android.libraries.designsystem.theme.components.IconButton
import io.element.android.libraries.designsystem.theme.components.SearchBarResultState
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.ui.strings.CommonStrings
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun EmojiPicker(
    onEmojiSelected: (Emoji) -> Unit,
    emojibaseStore: EmojibaseStore,
    selectedEmojis: ImmutableSet<String>,
    state: EmojiPickerState,
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()
    val categories = remember { emojibaseStore.categories }
    val pagerState = rememberPagerState(pageCount = { EmojibaseCategory.entries.size })

    Column(modifier) {
        EmojiPickerSearchBar(
            query = state.searchQuery,
            active = state.isSearchActive,
            onActiveChange = { state.eventSink(EmojiPickerEvents.OnSearchActiveChanged(it)) },
            onQueryChange = { state.eventSink(EmojiPickerEvents.UpdateSearchQuery(it)) },
        )

        if (state.searchQuery.isEmpty()) {
            SecondaryTabRow(
                selectedTabIndex = pagerState.currentPage,
            ) {
                EmojibaseCategory.entries.forEachIndexed { index, category ->
                    Tab(icon = {
                        Icon(
                            imageVector = category.icon, contentDescription = stringResource(id = category.title)
                        )
                    }, selected = pagerState.currentPage == index, onClick = {
                        coroutineScope.launch { pagerState.animateScrollToPage(index) }
                    })
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth(),
            ) { index ->
                val category = EmojibaseCategory.entries[index]
                val emojis = categories[category] ?: listOf()
                EmojiGrid(emojis = emojis, selectedEmojis = selectedEmojis, onEmojiSelected = onEmojiSelected)
            }
        } else {
            when (state.searchResults) {
                is SearchBarResultState.Results<ImmutableList<Emoji>> -> {
                    EmojiGrid(
                        emojis = state.searchResults.results,
                        selectedEmojis = selectedEmojis,
                        onEmojiSelected = onEmojiSelected,
                    )
                }

                is SearchBarResultState.NoResultsFound<ImmutableList<Emoji>> -> {
                    // No results found, show a message
                    Spacer(Modifier.size(80.dp))

                    Text(
                        text = stringResource(CommonStrings.common_no_results),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                else -> {
                    // Not searching - nothing to show.
                }
            }
        }
    }
}

@Composable
private fun EmojiGrid(
    emojis: List<Emoji>,
    selectedEmojis: ImmutableSet<String>,
    onEmojiSelected: (Emoji) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        modifier = modifier.fillMaxSize(),
        columns = GridCells.Adaptive(minSize = 48.dp),
        contentPadding = PaddingValues(vertical = 10.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        items(emojis, key = { it.unicode }) { item ->
            EmojiItem(
                modifier = Modifier.aspectRatio(1f),
                item = item,
                isSelected = selectedEmojis.contains(item.unicode),
                onEmojiSelected = onEmojiSelected,
                emojiSize = 32.dp.toSp(),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EmojiPickerSearchBar(
    query: String,
    active: Boolean,
    onActiveChange: (Boolean) -> Unit,
    onQueryChange: (String) -> Unit,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
            .focusRequester(focusRequester)
            .onFocusChanged { if (it.isFocused) onActiveChange(true) },
        placeholder = {
            Text(text = stringResource(CommonStrings.common_search_for_emoji))
        },
        trailingIcon = when {
            query.isNotEmpty() -> {
                {
                    IconButton(onClick = {
                        onQueryChange("")
                    }) {
                        Icon(
                            imageVector = CompoundIcons.Close(),
                            contentDescription = stringResource(CommonStrings.action_clear),
                        )
                    }
                }
            }

            else -> {
                {
                    Icon(
                        imageVector = CompoundIcons.Search(),
                        contentDescription = stringResource(CommonStrings.action_search),
                        tint = MaterialTheme.colorScheme.tertiary,
                    )
                }
            }
        },
        shape = SearchBarDefaults.inputFieldShape,
        singleLine = true,
        colors = (
            if (active) ElementSearchBarDefaults.activeColors().inputFieldColors
            else ElementSearchBarDefaults.inactiveColors().inputFieldColors
            )
            .copy(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
        interactionSource = interactionSource,
    )

    // Automatically open the keyboard
    LaunchedEffect(focusRequester) {
        focusRequester.requestFocus()
    }

    val isFocused = interactionSource.collectIsFocusedAsState().value
    val shouldClearFocus = !active && isFocused
    LaunchedEffect(active) {
        if (shouldClearFocus) {
            focusManager.clearFocus()
        }
    }

    BackHandler(enabled = active) {
        onActiveChange(false)
    }
}

@PreviewsDayNight
@Composable
internal fun EmojiPickerPreview() = ElementPreview {
    val emojibaseStore = EmojibaseDatasource().load(LocalContext.current)
    EmojiPicker(
        onEmojiSelected = {},
        emojibaseStore = emojibaseStore,
        selectedEmojis = persistentSetOf("😀", "😄", "😃"),
        state = EmojiPickerState(false, "", SearchBarResultState.Results(emojibaseStore.allEmojis.subList(0, 20)), {}),
        modifier = Modifier.fillMaxWidth(),
    )
}
