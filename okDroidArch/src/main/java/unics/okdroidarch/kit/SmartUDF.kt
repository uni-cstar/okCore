/**
 * @author: chaoluo10
 * @date: 2024/6/24
 * @desc:
 */
package unics.okdroidarch.kit

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import unics.okcore.udf.UDFIntent
import unics.okcore.udf.UDFState
import unics.okmultistate.uistate.LoaderUiState

data class UiState<T>(
    @JvmField val data: T, @JvmField val loaderUiState: LoaderUiState = LoaderUiState.LAZY
) : UDFState

object UiIntent {

    /**
     * 普通工作流,先执行[onStart]，然后执行[onWork]，如果出现异常则执行[onCatch]
     * 在[onStart]执行时发送一个loading状态
     * 在[onCatch]执行时发送一个Error状态
     * [onWork]执行完成时发送一个Content状态
     *
     * @param catchCancellationException 是否捕获[CancellationException]
     * @param revertStateOnCancellationException 当[catchCancellationException]是false，并且异常为[CancellationException]类型时，是否复原状态
     */
    class NormalWorkFlow<T>(
        private val onStart: suspend FlowCollector<UiState<T>>.(old: UiState<T>) -> Unit = { old ->
            emit(old.copyByState(LoaderUiState.LOADING))
        },
        private val onCatch: suspend FlowCollector<UiState<T>>.(old: UiState<T>, Throwable) -> Unit = { old, err ->
            emit(old.copyByState(LoaderUiState.error(err)))
        },
        private val catchCancellationException: Boolean = false,
        private val revertStateOnCancellationException: Boolean = true,
        private val onWork: suspend (old: UiState<T>) -> T
    ) : UDFIntent<UiState<T>> {
        override fun toReduceFlow(old: UiState<T>): Flow<UiState<T>> {
            var current = old
            return flowOf(old).map {
                //这里需要使用current去传递
                it.copyByData(onWork.invoke(current))
            }.onStart {
                onStart.invoke(this, current)
            }.catch {
                if (it is CancellationException) {
                    if (catchCancellationException) {
                        onCatch.invoke(this, current, it)
                    } else if (revertStateOnCancellationException) {
                        //发送原来的状态，用于复原
                        emit(old)
                    }
                } else {
                    onCatch.invoke(this, current, it)
                }
            }.onEach {
                current = it
            }
        }
    }

    /**
     * 普通工作流(无onStart流程),执行[onWork]，如果出现异常则执行[onCatch]；用于不关心start状态的工作流
     * [onWork]执行完成时发送一个Content状态,如果发生异常则执行[onCatch]发送一个Error状态
     * @param catchCancellationException 是否捕获[CancellationException]
     * @see NormalWorkFlow
     */
    class NoneStartWorkFlow<T>(
        private val onCatch: suspend FlowCollector<UiState<T>>.(old: UiState<T>, Throwable) -> Unit = { old, err ->
            emit(old.copyByState(LoaderUiState.error(err)))
        },
        private val catchCancellationException: Boolean = false,
        private val onWork: suspend (old: UiState<T>) -> T
    ) : UDFIntent<UiState<T>> {
        override fun toReduceFlow(old: UiState<T>): Flow<UiState<T>> {
            return flowOf(old).map {
                //这里需要使用current去传递
                it.copyByData(onWork.invoke(it))
            }.catch {
                if (catchCancellationException || it !is CancellationException) {
                    onCatch.invoke(this, old, it)
                }
            }
        }
    }

    private fun <T> UiState<T>.copyByData(newData: T): UiState<T> {
        return this.copy(data = newData, loaderUiState = LoaderUiState.CONTENT)
    }

    private fun <T> UiState<T>.copyByState(newState: LoaderUiState): UiState<T> {
        return this.copy(loaderUiState = newState)
    }

}
