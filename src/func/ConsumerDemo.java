package func;

import java.util.function.Consumer;

/**
 * @Author: dongyantong
 * @Date: 2019/11/18
 */
public class ConsumerDemo {
    public static void main(String[] args) {

    }

    private static void show(Integer context,Consumer consumer){
        consumer.accept(context);
        consumer.andThen(consumer);
    }
}
