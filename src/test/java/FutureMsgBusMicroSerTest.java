import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import bgu.spl.mics.Broadcast;
import bgu.spl.mics.Event;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.DetectedObjectsEvent;
import bgu.spl.mics.application.messages.PoseEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.CloudPoint;
import bgu.spl.mics.application.objects.Pose;

import org.junit.jupiter.api.Test; // For writing test methods
import static org.junit.jupiter.api.Assertions.*; // For assertions like assertEquals, assertTrue, etc.


public class FutureMsgBusMicroSerTest {

    public static class TestEvent1 implements Event<String> {
        private String message;

        public TestEvent1(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    public static class TestBroadcast1 implements Broadcast {
        private String message;

        public TestBroadcast1(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    public static class TestEvent2 implements Event<String> {
        private String message;

        public TestEvent2(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    public static class TestBroadcast2 implements Broadcast {
        private String message;

        public TestBroadcast2(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    private static class TestMicroService1 extends MicroService {
        public TestMicroService1(String name) {
            super(name);
        }

        @Override
        protected void initialize() {
            subscribeEvent(TestEvent1.class, (event) -> {
                terminate();
            });

            subscribeEvent(TestEvent2.class, (event) -> {
                complete(event, getName() + " handled Event 2: " + event.getMessage());
            });

            subscribeBroadcast(TestBroadcast1.class, (broadcast) -> {
                System.out.println(getName() + " handled Broadcast 1: " + broadcast.getMessage());
            });

            subscribeBroadcast(TestBroadcast2.class, (broadcast) -> {
                System.out.println(getName() + " handled Broadcast 2: " + broadcast.getMessage());
            });
        }
    }

    private static class TestMicroService2 extends MicroService {
        public TestMicroService2(String name) {
            super(name);
        }

        @Override
        protected void initialize() {
            subscribeEvent(TestEvent1.class, (event) -> {
                complete(event, getName() + " handled Event 1: " + event.getMessage());
            });

            subscribeEvent(TestEvent2.class, (event) -> {
                terminate();
            });

            subscribeBroadcast(TestBroadcast1.class, (broadcast) -> {
                System.out.println(getName() + " handled Broadcast 1: " + broadcast.getMessage());
            });

            subscribeBroadcast(TestBroadcast2.class, (broadcast) -> {
                System.out.println(getName() + " handled Broadcast 2: " + broadcast.getMessage());
            });
        }
    }

    @Test
    public static void setUp() {
        TestMicroService1 testMicroService1_1 = new TestMicroService1("TestMicroService1_1");
        TestMicroService1 testMicroService1_2 = new TestMicroService1("TestMicroService1_2");

        TestMicroService2 testMicroService2_1 = new TestMicroService2("TestMicroService2_1");
        TestMicroService2 testMicroService2_2 = new TestMicroService2("TestMicroService2_2");

        Thread thread1 = new Thread(testMicroService1_1);
        Thread thread2 = new Thread(testMicroService1_2);
        Thread thread3 = new Thread(testMicroService2_1);
        Thread thread4 = new Thread(testMicroService2_2);

        MessageBusImpl bus = MessageBusImpl.getInstance();
        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();

        bus.sendEvent(new TestEvent1("Event 1"));

    }
}
