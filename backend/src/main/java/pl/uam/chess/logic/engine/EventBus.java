package pl.uam.chess.logic.engine;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public final class EventBus {

    private final Map<Class<?>, List<Consumer<Object>>> handlers = new LinkedHashMap<>();

    public <T> void subscribe(Class<T> type, Consumer<T> handler) {
        handlers.computeIfAbsent(type, key -> new ArrayList<>())
                .add(event -> handler.accept(type.cast(event)));
    }

    public void publish(Object event) {
        List<Consumer<Object>> found = handlers.get(event.getClass());
        if (found == null) {
            return;
        }
        for (Consumer<Object> handler : List.copyOf(found)) {
            handler.accept(event);
        }
    }
}
