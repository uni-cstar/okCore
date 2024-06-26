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
class SendThenCollectEagerlyScene : BaseScene() {

    override val uiStateFlow: UDFlow<SampleIntent, SampleUiState> =
        UDFlow(scope, stateStarted = SharingStarted.Eagerly) {
            SampleUiState("init", 0)
        }

    /**
     * 先订阅、再发送init意图
     * 【测试结果】
     */
    @Test
    fun run(): Unit = runBlocking {
        val countDownLatch = CountDownLatch(3)
        delayCloseScope(countDownLatch)
        sendInitIntent(countDownLatch)
        suspend {
            delay(500)
            l("订阅State")
            collectUiState(countDownLatch)
        }.invoke()

        l("task prepared")
        countDownLatch.await()
    }

    /**
     * 先订阅、再发送2次initIntent
     * 【测试结果】
     */
    @Test
    fun run2(): Unit = runBlocking {
        val countDownLatch = CountDownLatch(3)
        delayCloseScope(countDownLatch)
        sendAccIntent(1, countDownLatch)
        delay(500)
        collectUiState1(countDownLatch)
        delay(500)
        sendAccIntent(1, countDownLatch)
        delay(1000)
        collectUiState2(countDownLatch)
        l("task prepared")
        countDownLatch.await()
    }


}