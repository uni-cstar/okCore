package unics.test.okcore.mvi

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Test
import unics.okcore.udf.UDFlow
import java.util.concurrent.CountDownLatch

/**
 * @author: unics
 * @date: 2020/7/21
 */
class SendThenCollectLazyScene : BaseScene() {

    override val uiStateFlow: UDFlow<SampleIntent, SampleUiState> = UDFlow(scope) {
        SampleUiState("init", 0)
    }

    /**
     * 先订阅、再发送init意图
     * 【测试结果】
     * 13:35:31.569 SendThenCollectLazyScene : task prepared
     * 13:35:31.568 SendThenCollectLazyScene : collectUiState start
     * UDFlow: sendIntent unics.test.okcore.mvi.SampleIntent$Init@26057049
     * 13:35:31.570 SendThenCollectLazyScene : collectUiState1 start
     * 13:35:31.570 SendThenCollectLazyScene : collectUiState2 start
     * 13:35:31.570 SendThenCollectLazyScene : sendInitIntent: release lock.
     * UDFlow: mapIntent unics.test.okcore.mvi.SampleIntent$Init@26057049
     * UDFlow: collect
     * UDFlow: collect
     * UDFlow: mapIntent result:  -892483618@ text:start count:0 time:1719207331776
     * 13:35:31.839 SendThenCollectLazyScene : collectUiState1 collectLatest: -892483618@ text:start count:0 time:1719207331776
     * 13:35:31.839 SendThenCollectLazyScene : collectUiState2 collectLatest: -892483618@ text:start count:0 time:1719207331776
     * 13:35:36.576 SendThenCollectLazyScene : cancel scope on delay
     * 13:35:36.596 SendThenCollectLazyScene : collectUiState error : kotlinx.coroutines.JobCancellationException: Job was cancelled; job=SupervisorJobImpl{Cancelling}@23b07c78
     * 13:35:36.596 SendThenCollectLazyScene : collectUiState end
     */
    @Test
    fun run(): Unit = runBlocking {
        val countDownLatch = CountDownLatch(3)
        sendInitIntent(countDownLatch)
        collectUiState(countDownLatch)
        delayCloseScope(countDownLatch)
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
        sendAccIntent(1,countDownLatch)
        delay(500)
        collectUiState1(countDownLatch)
        delay(500)
        sendAccIntent(1,countDownLatch)
        delay(1000)
        collectUiState2(countDownLatch)
        l("task prepared")
        countDownLatch.await()
    }


}