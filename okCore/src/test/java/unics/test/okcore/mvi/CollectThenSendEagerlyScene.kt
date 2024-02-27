package unics.test.okcore.mvi

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.runBlocking
import org.junit.Test
import unics.okcore.udf.UDFlow
import java.util.concurrent.CountDownLatch

/**
 * @author: unics
 * @date: 2020/7/21
 */
class CollectThenSendEagerlyScene : BaseScene() {

    override val uiStateFlow: UDFlow<SampleIntent, SampleUiState> = UDFlow(scope, stateStarted = SharingStarted.Eagerly) {
        SampleUiState("init", 0)
    }

    /**
     * 先订阅、再发送init意图
     * 【测试结果】
     */
    @Test
    fun run(): Unit = runBlocking {
        val countDownLatch = CountDownLatch(3)
        collectUiState(countDownLatch)
        delayCloseScope(countDownLatch)
        sendInitIntent(countDownLatch)
        l("task prepared")
        countDownLatch.await()
    }

    /**
     * 先订阅、再发送2次initIntent
     * 【测试结果】
     */
    @Test
    fun run2(): Unit = runBlocking {
        val countDownLatch = CountDownLatch(4)
        collectUiState(countDownLatch)
        delayCloseScope(countDownLatch)
        sendInitIntent(countDownLatch)
        sendInitIntent(countDownLatch)
        l("task prepared")
        countDownLatch.await()
    }

    /**
     * 先订阅，然后发送InitIntent，延迟后再一直循环发送acc
     */
    @Test
    fun run3(): Unit = runBlocking {
        val countDownLatch = CountDownLatch(4)
        collectUiState(countDownLatch)
        delayCloseScope(countDownLatch)
        sendInitIntent(countDownLatch)
        delay(500)
        sendAccIntent(Int.MAX_VALUE, countDownLatch)
        l("task prepared")
        countDownLatch.await()
    }


}