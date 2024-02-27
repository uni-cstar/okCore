package bas.lib.core

class OuterClass{

    fun printName(){
        println("Hello world")
    }
}

class InnerClass{
    sealed class Holder{

        abstract fun printName()

        class Instance: Holder() {
            override fun printName() {
                println("Hello China.")
            }

            override fun equals(other: Any?): Boolean {
                return this === other
            }

            override fun hashCode(): Int {
                return System.identityHashCode(this)
            }

        }
    }
}

class Main{

    fun scene(){
        OuterClass().printName()
        InnerClass.Holder.Instance().printName()
    }
}