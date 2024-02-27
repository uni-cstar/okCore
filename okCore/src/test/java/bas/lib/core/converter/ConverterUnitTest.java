package libcore.converter;

import org.junit.Test;

import java.util.List;

import libcore.converter.Converters;
import libcore.converter.gson.GsonConverter;
import libcore.converter.jackson.JacksonConverter;
import libcore.lang.CollectionUtils;

/**
 * Created by Lucio on 2021/7/21.
 */
public class ConverterUnitTest {

//    @Test
//    public void testFastJsonConverter() {
//        Converters.setJsonConverter(new FastJsonConverter());
//        testConverter("FastJson:");
//    }

    @Test
    public void testJacksonConverter() {
        Converters.setJsonConverter(new JacksonConverter());
        testConverter("Jackson:");
    }

    @Test
    public void testGsonConverter() {
        Converters.setJsonConverter(new GsonConverter());
        testConverter("Gson:");
    }

    private void testConverter(String tag){
        System.out.println(tag + Converters.getJsonConverter());
        Person zhangsan = new Person("Zhangsan", 18);
        String zhangsanJson = Converters.toJson(zhangsan,Person.class);
        System.out.println(tag + zhangsanJson);
        System.out.println(tag+"zhangsan=" + Converters.toObject(zhangsanJson,Person.class));
        Person wang5 = new Person("Wang5", 20);
        List persons = CollectionUtils.newList(zhangsan,wang5);
        String personsJson = Converters.toJson(persons,Person.class);
        System.out.println(tag+"\npersons=" + personsJson);
        System.out.println(tag+"\npersons=" +  Converters.toObjectList(personsJson,Person.class));
    }
}
