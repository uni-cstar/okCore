package unics.test.okcore.mvi

import kotlinx.coroutines.runBlocking
import org.junit.Test
import unics.okcore.udf.UDFlow
import java.util.concurrent.CountDownLatch

/**
 * @author: unics
 * @date: 2020/7/21

 */
class NoSendLazyScene : BaseScene() {

    override val uiStateFlow: UDFlow<SampleIntent, SampleUiState> = UDFlow(scope) {
        SampleUiState("init", 0)
    }

    /**
     * 只订阅了state，并没有任何地方发送，并且5s后会自动关闭scope的测试场景
     * 【测试结果】
     * 10:58:14.962 NoSendLazyScene : task prepared
     * 10:58:14.962 NoSendLazyScene : collectUiState start
     * 10:58:14.966 NoSendLazyScene : collectUiState2 start
     * 10:58:14.966 NoSendLazyScene : collectUiState1 start
     * UDFlow: collect
     * UDFlow: collect
     * 10:58:19.972 NoSendLazyScene : cancel scope on delay
     * 10:58:19.992 NoSendLazyScene : collectUiState error : kotlinx.coroutines.JobCancellationException: Job was cancelled; job=SupervisorJobImpl{Cancelling}@3bb8d905
     * 10:58:19.992 NoSendLazyScene : collectUiState end
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