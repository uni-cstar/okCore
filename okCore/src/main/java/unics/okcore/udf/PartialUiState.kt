package unics.okcore.udf

/**
 * UDF 组件之一 ： 部分UiState
 * @param R 指最终的UiState类型
 */
interface PartialUiState<R> {

    /**
     * 通过旧的状态产生新的状态
     * @param old 旧的UiState
     * @return 新的UiState
     */
    suspend fun reduce(old: R): R

}