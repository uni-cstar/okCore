/**
 * @author: unics
 * @date: 2020/7/21
 * @desc: 轻量级单向数据流实现，符合mvi原则
 */
package unics.okcore.udf

import kotlinx.coroutines.flow.Flow


/**
 * 用户意图：可理解未发起的操作行为，比如下拉刷新是一个意图，点击是一个意图；
 * 一个意图的结果通常是产生[UDFState]的部分，再借助原来的UiState产生新的UiState，因此意图提供转换成[]
 */
fun interface UDFIntent<State : UDFState> {

    /**
     * 转换成重新生产新状态的Flow
     * @param old 旧状态
     */
    fun toReduceFlow(old: State): Flow<State>

}