package unics.test.okcore.converter

//import libcore.converter.fastjson.FastJsonConverter
import org.junit.Test
import unics.okcore.converter.toJson
import unics.okcore.converter.toObject
import unics.okcore.converter.toObjectList

/**
 * Created by Lucio on 2021/7/22.
 */
class ConverterUnitTestKt {
//    @Test
//    fun testFastJsonConverter() {
//        Converters.setJsonConverter(FastJsonConverter())
//        testConverter("FastJson:")
//    }

    @Test
    fun testJacksonConverter() {
        unics.okcore.converter.Converters.setJsonConverter(unics.okcore.converter.jackson.JacksonConverter())
        testConverter("Jackson:")
    }

    @Test
    fun testGsonConverter() {
        unics.okcore.converter.Converters.setJsonConverter(unics.okcore.converter.gson.GsonConverter())
        testConverter("Gson:")
    }

    private fun testConverter(tag: String) {
        println(tag + unics.okcore.converter.Converters.getJsonConverter())
        val zhangsan = Person("Zhangsan", 18)
        val zhangsanJson = zhangsan.toJson()
        println(tag + zhangsanJson)
        println(tag + "zhangsan=" + zhangsanJson.toObject<Person>())
        val wang5 = Person("Wang5", 20)
        val persons: List<*> = unics.okcore.lang.CollectionUtils.newList(zhangsan, wang5)
        val personsJson = persons.toJson()
        println("$tag\npersons=$personsJson")
        println(
            """
            $tag
            persons=${
                personsJson.toObjectList<Person>()
            }
            """.trimIndent()
        )
    }
}