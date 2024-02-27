package unics.test.okcore;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class MapUnitTest {

    @Test
    public void testReturnValue(){
        Map<String,Integer> map = new HashMap<String,Integer>();
        Integer it = map.put("id", 1);
        Integer it1 =  map.put("id", 333);
        Integer it2 =  map.remove("id");
        // 在放入第一个 id 元素的时候，因为之前没有 id，所以返回值为 null
        // 在第二次放入 id 的时候，因为之前已经有了一个 id 了，所以在放入新元素之前需要将原来的元素给返回给调用者
        //map在remove返回值为key对应的value
        System.out.println(it);
        System.out.println(it1);
        System.out.println(it2);
    }

}
