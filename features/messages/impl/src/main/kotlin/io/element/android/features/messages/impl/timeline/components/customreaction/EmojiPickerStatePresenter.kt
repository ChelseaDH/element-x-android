/*
 * Copyright (c) 2024 New Vector Ltd
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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import io.element.android.emojibasebindings.Emoji
import io.element.android.emojibasebindings.allEmojis
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.designsystem.theme.components.SearchBarResultState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import timber.log.Timber
import javax.inject.Inject

class EmojiPickerStatePresenter @Inject constructor(
    private val emojibaseProvider: EmojibaseProvider,
) : Presenter<EmojiPickerState> {
    @Composable
    override fun present(): EmojiPickerState {
        var searchQuery by rememberSaveable { mutableStateOf("") }
        var searchActive by rememberSaveable { mutableStateOf(true) }
        val searchResults = remember { mutableStateOf<SearchBarResultState<ImmutableList<Emoji>>>(SearchBarResultState.Initial()) }

        LaunchedEffect(searchQuery) {
            val filter = emojibaseProvider.emojibaseStore.allEmojis.filter { emoji ->
                emoji.label.contains(searchQuery, true)
                    || emoji.tags?.any { it.contains(searchQuery, true) } ?: false
                    || emoji.shortcodes.any { it.contains(searchQuery, true) }
            }
            searchResults.value = SearchBarResultState.Results(filter.toImmutableList())
        }

        return EmojiPickerState(
            isSearchActive = searchActive,
            searchQuery = searchQuery,
            searchResults = searchResults.value,
            eventSink = {
                when (it) {
                    is EmojiPickerEvents.OnSearchActiveChanged -> {
                        searchActive = it.active
                        Timber.tag("EmojiPicker").d("Search active changed: ${it.active}")
                    }
                    is EmojiPickerEvents.UpdateSearchQuery -> {
                        searchQuery = it.query
                    }
                    is EmojiPickerEvents.Reset -> {
                        searchActive = true
                        searchQuery = ""
                    }
                }
            }
        )
    }
}
