package unics.test.okcore.mvi

import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.runBlocking
import org.junit.Test
import unics.okcore.udf.UDFlow
import java.util.concurrent.CountDownLatch

/**
 * @author: unics
 * @date: 2020/7/21
 * 只订阅了state，并没有任何地方发送，并且5s后会自动关闭scope的测试场景
 * 【测试结果】
 * EarlierNoSendScene : task prepared
 * EarlierNoSendScene : collect start
 * EarlierNoSendScene : cancel scope on delay
 */
class NoSendEagerlyScene : BaseScene() {

    override val uiStateFlow: UDFlow<SampleIntent, SampleUiState> =
        UDFlow(scope, stateStarted = SharingStarted.Eagerly) {
            SampleUiState("init", 0)
        }

    /**
     * 只订阅了state，并没有任何地方发送，并且5s后会自动关闭scope的测试场景
     * 【测试结果】
     * 10:59:15.598 NoSendEarlierScene : collectUiState start
     * 10:59:15.598 NoSendEarlierScene : task prepared
     * 10:59:15.601 NoSendEarlierScene : collectUiState1 start
     * 10:59:15.601 NoSendEarlierScene : collectUiState2 start
     * UDFlow: collect
     * UDFlow: collect
     * 10:59:20.601 NoSendEarlierScene : cancel scope on delay
     * 10:59:20.621 NoSendEarlierScene : collectUiState error : kotlinx.coroutines.JobCancellationException: Job was cancelled; job=SupervisorJobImpl{Cancelling}@5f5092e0
     * 10:59:20.621 NoSendEarlierScene : collectUiState end
     */
    @Test
    fun run(): Unit = runBlocking {
        val countDownLatch = CountDownLatch(2)
        delayCloseScope(countDownLatch)
        collectUiState(countDownLatch)
        l("task prepared")
        countDownLatch.await()
    }


}