/**
 * @author: unics
 * @date: 2020/7/21
 * @desc: 轻量级单向数据流实现，符合mvi原则
 */
package unics.okcore.udf

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted

/**
 * 唯一单向流
 */
fun <State : UDFState> UDFlow(
    scope: CoroutineScope,
    initialState: State,
    intentCapacity: Int = Channel.RENDEZVOUS,
    intentOnBufferOverflow: BufferOverflow = BufferOverflow.SUSPEND,
    intentOnUndeliveredElement: ((UDFIntent<State>) -> Unit)? = null,
    stateStarted: SharingStarted = SharingStarted.Eagerly,
    stateReplay: Int = 1
): UDFlow<State> {
    return UDFlowImpl(
        scope,
        intentCapacity,
        intentOnBufferOverflow,
        intentOnUndeliveredElement,
        stateStarted,
        stateReplay
    ) {
        initialState
    }
}

fun <State : UDFState> UDFlow(
    scope: CoroutineScope,
    intentCapacity: Int = Channel.RENDEZVOUS,
    intentOnBufferOverflow: BufferOverflow = BufferOverflow.SUSPEND,
    intentOnUndeliveredElement: ((UDFIntent<State>) -> Unit)? = null,
    stateStarted: SharingStarted = SharingStarted.Eagerly,
    stateReplay: Int = 1,
    stateInitializer: () -> State
): UDFlow<State> {
    return UDFlowImpl(
        scope,
        intentCapacity,
        intentOnBufferOverflow,
        intentOnUndeliveredElement,
        stateStarted,
        stateReplay,
        stateInitializer
    )
}

/**
 *
 * SSOTUDF: single source of truth ans unidirectional data flow
 * SSOT:single source of truth 单一数据源
 * UDF:unidirectional data flow 单向数据流
 * 一个UDF 对应的MVI架构所需要的定义
 * 参考链接：https://developer.android.com/topic/architecture?hl=zh-cn#single-source-of-truth
 */
interface UDFlow<State : UDFState> : Flow<State> {

    /**
     * 发送意图
     */
    fun sendIntent(scope: CoroutineScope, intent: UDFIntent<State>)

    /**
     * 发送意图
     */
    suspend fun sendIntent(intent: UDFIntent<State>)

    /**
     * 当前状态值
     */
    val value: State

}


var UDFLOG: Boolean = true