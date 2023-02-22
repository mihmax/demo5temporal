package ua.dp.maxym.demo5.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class StaticContextAccessor {
    private static ApplicationContext context;

    @Autowired
    public StaticContextAccessor(ApplicationContext applicationContext) {
        context = applicationContext;
    }

    /**
     * Make it public to use.
     * Found this very bad practice snippet on the Internet, and thought to save for later reference.
     * But don't want to actually use it, thus it's private now :)
     *
     * @param clazz Bean class to resolve
     * @param <T> Generic stuff to make it type safe
     * @return Instance of the Spring Bean of the given class
     */
    private static <T> T getBean(Class<T> clazz) {
        return context.getBean(clazz);
    }
}