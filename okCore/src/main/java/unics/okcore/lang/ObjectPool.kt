package unics.okcore.lang

import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.ArrayBlockingQueue

class ObjectPoolFullException : RuntimeException {
    constructor() : this("Pool is full")
    constructor(message: String?) : super(message)
}

// ==================== 基础对象池接口 ====================
interface ObjectPool<T> {

    /**
     * 对象源：提供对象创建和对象重置
     */
    interface Source<T> {
        fun create(): T
        fun reset(obj: T)
    }

    /**
     * 获取一个对象
     */
    fun acquire(): T

    /**
     * 归还一个对象
     */
    fun giveback(obj: T)

    /**
     * 清空对象池：只清空闲置对象
     */
    fun clear()

    /**
     * 完全清空：清空闲置对象并重置活跃计数
     */
    fun clearAll()

    /**
     * 池中空闲对象数量
     */
    fun getPoolSize(): Int

    /**
     * 活跃对象数：正在使用的对象的数量
     */
    fun getActiveCount(): Int

    companion object {


        /**
         * 简单对象池（不关心对象reset）
         */
        fun <T> simplePool(
            factory: () -> T
        ): ObjectPool<T> {
            return SimpleObjectPool(object : Source<T> {
                override fun create(): T {
                    return factory()
                }

                override fun reset(obj: T) {
                }
            })
        }

        /**
         * 简单对象池
         */
        fun <T> simplePool(
            source: Source<T>
        ): ObjectPool<T> {
            return SimpleObjectPool(source)
        }

        /**
         * 有界对象池
         * @param maxSize 最大创建的对象数，当达到最大值时，acquire会抛出异常，注意有借有还，acquire的对象必须归还，否则计数会一直增加导致可用对象数减少
         *
         */
        fun <T> boundedPool(
            maxSize: Int, source: Source<T>
        ): ObjectPool<T> {
            return BoundedObjectPool(maxSize, source)
        }

        fun <T> threadSafePool(
            source: Source<T>
        ): ObjectPool<T> {
            return ThreadSafeObjectPool(source)
        }

    }
}


// ==================== 简单对象池实现 ====================
class SimpleObjectPool<T>(
    private val source: ObjectPool.Source<T>
) : ObjectPool<T> {

    private val pool = ConcurrentLinkedQueue<T>()
    private val activeCount = AtomicInteger(0)

    override fun acquire(): T {
        val obj = pool.poll() ?: source.create()
        activeCount.incrementAndGet()
        return obj
    }

    override fun giveback(obj: T) {
        source.reset(obj)
        pool.offer(obj)
        activeCount.decrementAndGet()
    }

    override fun clear() {
        pool.clear()
    }

    override fun clearAll() {
        pool.clear()
        activeCount.set(0)
    }

    override fun getPoolSize(): Int = pool.size
    override fun getActiveCount(): Int = activeCount.get()
}

// ==================== 有界对象池实现（线程安全） ====================
class BoundedObjectPool<T>(
    private val maxSize: Int, private val source: ObjectPool.Source<T>
) : ObjectPool<T> {

    init {
        require(maxSize > 0) { "Max size must be greater than zero" }
    }

    // 使用 ArrayBlockingQueue：有界队列，固定大小，性能稳定
    private val pool = ArrayBlockingQueue<T>(maxSize)
    private var activeCount = 0

    @Synchronized
    override fun acquire(): T {
        val obj = pool.poll()
        if (obj != null) {
            activeCount++
            return obj
        }
        
        // 检查总对象数是否超过最大容量
        if (activeCount + pool.size >= maxSize) {
            throw ObjectPoolFullException("Pool is full, maxSize: $maxSize, activeCount: $activeCount, poolSize: ${pool.size}")
        }
        
        // 创建新对象
        val newObj = source.create()
        activeCount++
        return newObj
    }

    @Synchronized
    override fun giveback(obj: T) {
        source.reset(obj)
        // 如果池已满，忽略归还操作
        pool.offer(obj)
        activeCount--
    }

    @Synchronized
    override fun clear() {
        pool.clear()
    }

    @Synchronized
    override fun clearAll() {
        pool.clear()
        activeCount = 0
    }

    @Synchronized
    override fun getPoolSize(): Int = pool.size

    @Synchronized
    override fun getActiveCount(): Int = activeCount
}

// ==================== 线程安全对象池 ====================
class ThreadSafeObjectPool<T>(
    private val source: ObjectPool.Source<T>
) : ObjectPool<T> {

    // 使用 ArrayList：由于方法已同步，选择读写性能最好的容器
    private val pool = mutableListOf<T>()
    private var activeCount = 0

    @Synchronized
    override fun acquire(): T {
        val obj = if (pool.isNotEmpty()) pool.removeAt(pool.size - 1) else source.create()
        activeCount++
        return obj
    }

    @Synchronized
    override fun giveback(obj: T) {
        source.reset(obj)
        pool.add(obj)
        activeCount--
    }

    @Synchronized
    override fun clear() {
        pool.clear()
    }

    @Synchronized
    override fun clearAll() {
        pool.clear()
        activeCount = 0
    }

    @Synchronized
    override fun getPoolSize(): Int = pool.size

    @Synchronized
    override fun getActiveCount(): Int = activeCount

}
