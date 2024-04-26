package unics.okcore.udf

import kotlinx.coroutines.flow.Flow

/**
 * UDF Ui意图
 */
interface UiIntent<T : UiState> {

    /**
     * 转换成部分UiState
     */
    fun toPartialUiStateFlow(): Flow<PartialUiState<T>>

}