package unics.test.okcore.mvi

import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.runBlocking
import org.junit.Test
import unics.okcore.udf.UDFlow
import java.util.concurrent.CountDownLatch

/**
 * @author: unics
 * @date: 2020/7/21
 * stateStarted = SharingStarted.Eagerly,没有collect场景
 */
class NoRcvEagerlyScene : BaseScene() {

    override val uiStateFlow: UDFlow<SampleIntent, SampleUiState> =
        //注意State模式，Eagerly
        UDFlow(scope, stateStarted = SharingStarted.Eagerly) {
            SampleUiState("init", 0)
        }

    /**
     * 没有订阅，发送一次init意图
     * 结果：发送Init意图，执行Intent意图并产生最终的State，但是没有接收者
     *
     * 10:27:56.685 NoRcvEarlierScene : task prepared
     * UDFlow: sendIntent unics.test.okcore.mvi.SampleIntent$Init@1e8c382e
     * UDFlow: mapIntent unics.test.okcore.mvi.SampleIntent$Init@1e8c382e
     * UDFlow: mapIntent result: SampleUiState(text=start, count=0, time=1719196076892)
     * 10:28:01.702 NoRcvEarlierScene : cancel scope on delay
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
     * 结果：正常发送9次Acc意图，Scope被取消后，后续的Acc意图没有再继续发送，State也对应执行了9次，即产生了9个结果，但是没有订阅者
     *
     * 10:22:54.561 EarlierNoRcvScene : task prepared
     * 10:22:55.074 EarlierNoRcvScene : sendAccIntent: the 1 send
     * UDFlow: sendIntent unics.test.okcore.mvi.SampleIntent$Acc@2666d88e
     * UDFlow: mapIntent unics.test.okcore.mvi.SampleIntent$Acc@2666d88e
     * UDFlow: mapIntent result: SampleUiState(text=Acc, count=1, time=1719195775287)
     * 。。。中间还继续发送了8次 Acc意图，总共9次（500*9=450），第10次发送时，scope被取消了
     * 10:22:59.571 EarlierNoRcvScene : cancel scope on delay
     * 10:22:59.596 EarlierNoRcvScene : sendAccIntent: error kotlinx.coroutines.JobCancellationException: Job was cancelled; job=SupervisorJobImpl{Cancelling}@3a4f921.
     * 10:22:59.596 EarlierNoRcvScene : sendAccIntent: release lock.
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