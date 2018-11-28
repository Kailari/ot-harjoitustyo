package toilari.otlite.game.event;

import lombok.NonNull;
import lombok.val;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EventSystemTest {
    @Test
    @SuppressWarnings("ConstantConditions")
    void subscribeThrowsWhenGivenNullArguments() {
        val es = new EventSystem();
        assertThrows(NullPointerException.class, () -> es.subscribeTo(null, event -> { }));
        assertThrows(NullPointerException.class, () -> es.subscribeTo(IEvent.class, null));
        assertThrows(NullPointerException.class, () -> es.subscribeTo(null, null));
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void fireThrowsIfEventIsNull() {
        assertThrows(NullPointerException.class, () -> new EventSystem().fire(null));
    }

    @Test
    void callingFireCallsListeners() {
        val es = new EventSystem();
        val listener = new TestEventListener<TestEventA>();
        es.subscribeTo(TestEventA.class, listener);
        es.fire(new TestEventA());

        assertTrue(listener.fired);
    }

    @Test
    void callingFireCallsAllListenersOfCorrectType() {
        val es = new EventSystem();
        val listener1 = new TestEventListener<TestEventA>();
        val listener2 = new TestEventListener<TestEventA>();
        val listener3 = new TestEventListener<TestEventA>();
        es.subscribeTo(TestEventA.class, listener1);
        es.subscribeTo(TestEventA.class, listener2);
        es.subscribeTo(TestEventA.class, listener3);
        es.fire(new TestEventA());

        assertTrue(listener1.fired);
        assertTrue(listener2.fired);
        assertTrue(listener3.fired);
    }

    @Test
    void callingFireCallsNothingIfThereAreNoListenersForThatType() {
        val es = new EventSystem();
        val listener = new TestEventListener<TestEventB>();
        es.fire(new TestEventA());

        assertFalse(listener.fired);
    }

    @Test
    void callingFireCallsListenersOnlyForCorrectEventClass() {
        val es = new EventSystem();
        val listenerA = new TestEventListener<TestEventA>();
        val listenerB = new TestEventListener<TestEventB>();
        es.subscribeTo(TestEventA.class, listenerA);
        es.subscribeTo(TestEventB.class, listenerB);
        es.fire(new TestEventA());

        assertTrue(listenerA.fired);
        assertFalse(listenerB.fired);
    }

    private static class TestEventA implements IEvent {
    }

    private static class TestEventB implements IEvent {
    }

    private static class TestEventListener<T extends IEvent> implements IEventListener<T> {
        boolean fired;

        @Override
        public void onEvent(@NonNull T event) {
            this.fired = true;
        }
    }
}
