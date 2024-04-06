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

package io.element.android.libraries.matrix.test.roomdirectory

import io.element.android.libraries.matrix.api.roomdirectory.RoomDirectoryList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class FakeRoomDirectoryList(
    override val state: Flow<RoomDirectoryList.State> = emptyFlow(),
    val filterLambda: (String?, Int) -> Result<Unit> = { _, _ -> Result.success(Unit) },
    val loadMoreLambda: () -> Result<Unit> = { Result.success(Unit) }
) : RoomDirectoryList {
    override suspend fun filter(filter: String?, batchSize: Int) = filterLambda(filter, batchSize)

    override suspend fun loadMore(): Result<Unit> = loadMoreLambda()
}
