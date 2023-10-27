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

package io.element.android.features.messages.impl.voicemessages.composer

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.libraries.textcomposer.model.VoiceMessageState
import kotlinx.collections.immutable.toPersistentList
import kotlin.time.Duration.Companion.seconds

internal open class VoiceMessageComposerStateProvider : PreviewParameterProvider<VoiceMessageComposerState> {
    override val values: Sequence<VoiceMessageComposerState>
        get() = sequenceOf(
            aVoiceMessageComposerState(voiceMessageState = VoiceMessageState.Recording(duration = 61.seconds, levels = aWaveformLevels)),
        )
}

internal fun aVoiceMessageComposerState(
    voiceMessageState: VoiceMessageState = VoiceMessageState.Idle,
    showPermissionRationaleDialog: Boolean = false,
) = VoiceMessageComposerState(
    voiceMessageState = voiceMessageState,
    showPermissionRationaleDialog = showPermissionRationaleDialog,
    eventSink = {},
)

internal var aWaveformLevels = List(100) { it.toFloat() / 100 }.toPersistentList()

