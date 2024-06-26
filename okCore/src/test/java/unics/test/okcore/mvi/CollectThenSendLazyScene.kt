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
class CollectThenSendLazyScene : BaseScene() {

    override val uiStateFlow: UDFlow<SampleIntent, SampleUiState> = UDFlow(scope) {
        SampleUiState("init", 0)
    }

    /**
     * 先订阅、再发送init意图
     * 【测试结果】
     * 11:03:03.317 CollectThenSendLazyScene : collectUiState start
     * 11:03:03.318 CollectThenSendLazyScene : task prepared
     * UDFlow: sendIntent unics.test.okcore.mvi.SampleIntent$Init@13ac416b
     * 11:03:03.321 CollectThenSendLazyScene : collectUiState1 start
     * 11:03:03.322 CollectThenSendLazyScene : sendInitIntent: release lock.
     * 11:03:03.322 CollectThenSendLazyScene : collectUiState2 start
     * UDFlow: mapIntent unics.test.okcore.mvi.SampleIntent$Init@13ac416b
     * UDFlow: collect
     * UDFlow: collect
     * UDFlow: mapIntent result: SampleUiState(text=start, count=0, time=1719198183539)
     * 11:03:03.604 CollectThenSendLazyScene : collectUiState2 collectLatest:SampleUiState(text=start, count=0, time=1719198183539)
     * 11:03:03.604 CollectThenSendLazyScene : collectUiState1 collectLatest:SampleUiState(text=start, count=0, time=1719198183539)
     * 11:03:08.334 CollectThenSendLazyScene : cancel scope on delay
     * 11:03:08.361 CollectThenSendLazyScene : collectUiState error : kotlinx.coroutines.JobCancellationException: Job was cancelled; job=SupervisorJobImpl{Cancelling}@5421047a
     * 11:03:08.362 CollectThenSendLazyScene : collectUiState end
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
     * 11:09:31.770 CollectThenSendLazyScene : collectUiState start
     * UDFlow: sendIntent unics.test.okcore.mvi.SampleIntent$Init@293760ef
     * 11:09:31.771 CollectThenSendLazyScene : task prepared
     * UDFlow: sendIntent unics.test.okcore.mvi.SampleIntent$Init@66d959cf
     * 11:09:31.772 CollectThenSendLazyScene : collectUiState1 start
     * 11:09:31.772 CollectThenSendLazyScene : sendInitIntent: release lock.
     * 11:09:31.772 CollectThenSendLazyScene : collectUiState2 start
     * UDFlow: mapIntent unics.test.okcore.mvi.SampleIntent$Init@293760ef
     * UDFlow: collect
     * UDFlow: collect
     * UDFlow: mapIntent result: SampleUiState(text=start, count=0, time=1719198571988)
     * UDFlow: mapIntent unics.test.okcore.mvi.SampleIntent$Init@66d959cf
     * 11:09:31.991 CollectThenSendLazyScene : sendInitIntent: release lock.
     * 11:09:32.051 CollectThenSendLazyScene : collectUiState2 collectLatest:SampleUiState(text=start, count=0, time=1719198571988)
     * 11:09:32.051 CollectThenSendLazyScene : collectUiState1 collectLatest:SampleUiState(text=start, count=0, time=1719198571988)
     * UDFlow: mapIntent result: SampleUiState(text=start, count=0, time=1719198572191)
     * 11:09:32.254 CollectThenSendLazyScene : collectUiState1 collectLatest:SampleUiState(text=start, count=0, time=1719198572191)
     * 11:09:32.254 CollectThenSendLazyScene : collectUiState2 collectLatest:SampleUiState(text=start, count=0, time=1719198572191)
     * 11:09:36.788 CollectThenSendLazyScene : cancel scope on delay
     * 11:09:36.828 CollectThenSendLazyScene : collectUiState error : kotlinx.coroutines.JobCancellationException: Job was cancelled; job=SupervisorJobImpl{Cancelling}@3f131877
     * 11:09:36.828 CollectThenSendLazyScene : collectUiState end
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