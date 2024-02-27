package unics.test.okcore.mvi

import kotlinx.coroutines.delay
import unics.okcore.udf.UDFIntent

sealed class SampleIntent : UDFIntent<SampleUiState> {

    class Init : SampleIntent() {
        override suspend fun reduce(old: SampleUiState): SampleUiState {
            //每个任务模拟200ms耗时
            delay(200)
            return old.copy(text = "start", count = 0, time = System.currentTimeMillis())
        }

    }

    class Acc : SampleIntent() {
        override suspend fun reduce(old: SampleUiState): SampleUiState {
            //每个任务模拟200ms耗时
            delay(200)
            return old.copy(text = "Acc", count = old.count + 1, time = System.currentTimeMillis())
        }
    }

}