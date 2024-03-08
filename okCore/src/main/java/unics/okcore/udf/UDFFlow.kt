package unics.okcore.udf

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.CoroutineContext

fun <Intent : UiIntent<State>, State : UiState> UDF(
    initial: State,
    scope: CoroutineScope,
    started: SharingStarted = SharingStarted.Eagerly,
    replay: Int = 1,
    flowOn: CoroutineContext = Dispatchers.Default
): UDF<Intent, State> {
    return UDFImpl(initial, scope, started, replay, flowOn)
}

/**
 *
 * SSOTUDF: single source of truth ans unidirectional data flow
 * SSOT:single source of truth 单一数据源
 * UDF:unidirectional data flow 单向数据流
 * 一个UDF 对应的MVI架构所需要的定义
 * 参考链接：https://developer.android.com/topic/architecture?hl=zh-cn#single-source-of-truth
 */
interface UDF<Intent : UiIntent<State>, State : UiState> {

    /**
     * 发送意图
     */
    fun sendIntent(scope: CoroutineScope, intent: Intent)

    /**
     * 发送意图
     */
    suspend fun sendIntent(intent: Intent)

    /**
     * 提供的UiStateFlow
     */
    val uiStateFlow: SharedFlow<State>

//    val currentUiState: State? get() = uiStateFlow.replayCache.firstOrNull()
//
}

/**
 * 用户发起一个意图，这个意图经过执行之后会产生一个结果，称这个结果为PartialResult（即意图转换得到的数据可能只是最终所需UiState的部分值），
 * 然后再将PartialResult结合老的的UiState生成新的UiState，然后将新的值发射出去
 *
 * intent(用户的意图) -> PartialFlow（部分结果流）->(+ old UiState)->new Ui State
 */
private class UDFImpl<Intent : UiIntent<State>, State : UiState>(
    initial: State,
    scope: CoroutineScope,
    started: SharingStarted,
    replay: Int,
    flowOn: CoroutineContext = Dispatchers.Default
) : UDF<Intent, State> {

    private val _uiIntents = MutableSharedFlow<Intent>(replay = 1)

    private val _mutex: Mutex = Mutex()
    var currentState: State = initial
        private set

    /**
     * 转换之后的UiState
     */
    @OptIn(InternalCoroutinesApi::class)
    override val uiStateFlow: SharedFlow<State> = _uiIntents.flatMapConcat { intent ->
        intent.toPartialUiStateFlow()
    }.flatReduce().flowOn(flowOn).shareIn(scope, started, replay)

    /**
     * 发送意图
     */
    override fun sendIntent(scope: CoroutineScope, intent: Intent) {
        scope.launch {
            sendIntent(intent)
        }
    }

    override suspend fun sendIntent(intent: Intent) {
        _uiIntents.emit(intent)
    }

    @InternalCoroutinesApi
    private fun Flow<PartialUiState<State>>.flatReduce(): Flow<State> {
        return object : Flow<State> {
            override suspend fun collect(collector: FlowCollector<State>) {
                this@flatReduce.collect { partialResult ->
                    _mutex.withLock {
                        val new = partialResult.reduce(currentState)
                        currentState = new
                        collector.emit(new)
                    }
                }
            }
        }
    }

}
