package unics.okcore.udf

/**
 * UDF 组件之一 ： 部分UiState
 * @param US  指最终的UiState类型
 */
interface PartialUiState<T : UiState> {

    /**
     * 通过旧的状态产生新的状态
     * @param old 旧的UiState
     * @return 新的UiState
     */
    suspend fun reduce(old: T): T

}