package unics.test.okcore.mvi

import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.runBlocking
import org.junit.Test
import unics.okcore.udf.UDFlow
import java.util.concurrent.CountDownLatch

/**
 * @author: unics
 * @date: 2020/7/21
 */
class NoRcvLazyScene : BaseScene() {

    //注意State模式，Eagerly
    override val uiStateFlow: UDFlow<SampleIntent, SampleUiState> =
        UDFlow(scope, stateStarted = SharingStarted.Lazily) {
            SampleUiState("init", 0)
        }

    /**
     * 没有订阅，发送一次init意图
     * 结果：发送Init意图，没有产生State
     *
     * UDFlow: sendIntent unics.test.okcore.mvi.SampleIntent$Init@659b462
     * 10:31:29.107 NoRcvLazyScene : task prepared
     * 10:31:34.109 NoRcvLazyScene : cancel scope on delay
     * 10:31:34.133 NoRcvLazyScene : sendInitIntent: error kotlinx.coroutines.JobCancellationException: Job was cancelled; job=SupervisorJobImpl{Cancelling}@5145405e.
     * 10:31:34.133 NoRcvLazyScene : sendInitIntent: release lock.
     */
    @Test
    fun run(): Unit = runBlocking {
        val countDownLatch = CountDownLatch(2)
        delayCloseScope(countDownLatch)
        sendInitIntent(countDownLatch)
        l("task prepared")
        countDownLatch.await()
    }

    /**
     * 没有订阅，发送25次Acc意图
     * 结果：第一个Acc意图发送之后发送方就被阻塞了，直到sope被取消，没有产生任何State
     * 10:34:27.557 NoRcvLazyScene : task prepared
     * 10:34:28.061 NoRcvLazyScene : sendAccIntent: the 1 send
     * UDFlow: sendIntent unics.test.okcore.mvi.SampleIntent$Acc@66d06b0b
     * 10:34:32.570 NoRcvLazyScene : cancel scope on delay
     * 10:34:32.601 NoRcvLazyScene : sendAccIntent: error kotlinx.coroutines.JobCancellationException: Job was cancelled; job=SupervisorJobImpl{Cancelling}@4577de69.
     * 10:34:32.601 NoRcvLazyScene : sendAccIntent: release lock.
     */
    @Test
    fun run2(): Unit = runBlocking {
        val countDownLatch = CountDownLatch(2)
        delayCloseScope(countDownLatch)
        sendAccIntent(25, countDownLatch)
        l("task prepared")
        countDownLatch.await()
    }


}