
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

import org.junit.jupiter.api.Test;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.Event;
import bgu.spl.mics.Message;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;

public class FutureMsgBusMicroSerTest {

    public static class TestEvent1 implements Event<String> {

        private String message;

        public TestEvent1(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public String getName() {
            return "TestEvent1";
        }

    }

    public static class terminate implements Event<String> {

        private String message;

        public terminate(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public String getName() {
            return "terminate";
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

        public String getName() {
            return "TestEvent2";
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

        private final CountDownLatch lanch;

        public TestMicroService1(String name, CountDownLatch lanch) {
            super(name);
            this.lanch = lanch;
        }

        @Override
        protected void initialize() {
            subscribeEvent(TestEvent1.class, (event) -> {
                complete(event, getName() + " handled Event 1: " + event.getMessage());
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
            subscribeEvent(terminate.class, (event) -> {
                System.out.println(getName() + " handled terminate: " + event.getMessage());
                terminate();
                if (MessageBusImpl.getInstance().getMessageQueues().containsKey(this)){
                    System.out.println(getName() + " unregisterd");
                }
            });
            lanch.countDown();
        }
    }

    private static class TestMicroService2 extends MicroService {

        private final CountDownLatch lanch;

        public TestMicroService2(String name, CountDownLatch lanch) {
            super(name);
            this.lanch = lanch;
        }

        @Override
        protected void initialize() {

            subscribeEvent(TestEvent1.class, (event) -> {
                complete(event, getName() + " handled Event 1: " + event.getMessage());
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
            subscribeEvent(terminate.class, (event) -> {
                System.out.println(getName() + " handled terminate: " + event.getMessage());
                terminate();
                if (MessageBusImpl.getInstance().getMessageQueues().containsKey(this)){
                    System.out.println(getName() + " unregisterd");
                }
            });
            lanch.countDown();
        }
    }

    @Test
    public void setUp() {
        System.out.println(("test has started"));
        CountDownLatch lanch = new CountDownLatch(4);

        TestMicroService1 testMicroService1_1 = new TestMicroService1("TestMicroService1_1", lanch);
        TestMicroService1 testMicroService1_2 = new TestMicroService1("TestMicroService1_2", lanch);
        TestMicroService2 testMicroService2_1 = new TestMicroService2("TestMicroService2_1", lanch);
        TestMicroService2 testMicroService2_2 = new TestMicroService2("TestMicroService2_2", lanch);
        MicroService[] microServices = {testMicroService1_1, testMicroService1_2, testMicroService2_1, testMicroService2_2};
        Class[] events = {TestEvent1.class, TestEvent2.class, terminate.class};
        Class[] broadcasts = {TestBroadcast1.class, TestBroadcast2.class};
        int counter = 0;

        Thread thread1 = new Thread(testMicroService1_1);
        Thread thread2 = new Thread(testMicroService1_2);
        Thread thread3 = new Thread(testMicroService2_1);
        Thread thread4 = new Thread(testMicroService2_2);

        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();

        try {
            lanch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Map<Class<? extends Event>, LinkedBlockingQueue<MicroService>> eventSubscribers = MessageBusImpl.getInstance().getEventSubscribers();
        Map<Class<? extends Broadcast>, LinkedBlockingQueue<MicroService>> broadcastSubscribers = MessageBusImpl.getInstance().getBroadcastSubscribers();

        for (int i = 0; i < microServices.length; i++) {
            isRegistered(microServices[i]);     
        }
        System.out.println();
       

        for (int i = 0; i < eventSubscribers.size(); i++) {
            System.out.println("event number " + (i+1) + " subscribers:");
            if (!eventSubscribers.get(events[i]).isEmpty()) {
                for (MicroService microService : eventSubscribers.get(events[i])) {
                    System.out.println(microService.getName());
                }
            }
            else
                System.out.println("no subscribers");
            System.out.println();
        }

        for (int i = 0; i < broadcastSubscribers.size(); i++){
                System.out.println("broadcast number " + (i+1) + " subscribers:");
                if (!broadcastSubscribers.get(broadcasts[i]).isEmpty()) {
                    for (MicroService microService : broadcastSubscribers.get(broadcasts[i])) {
                    System.out.println(microService.getName());
                }
            }
                else
                    System.out.println("no subscribers");
                System.out.println();
        }
            
        MessageBusImpl.getInstance().sendBroadcast(new TestBroadcast1("Broadcast 1"));
        MessageBusImpl.getInstance().sendBroadcast(new TestBroadcast2("Broadcast 2"));

            
        MessageBusImpl.getInstance().sendEvent(new terminate("terminate 1"));
        MessageBusImpl.getInstance().sendEvent(new terminate("terminate 2"));
        MessageBusImpl.getInstance().sendEvent(new terminate("terminate 3"));
        MessageBusImpl.getInstance().sendEvent(new terminate("terminate 4"));
            
        try {
            thread1.join();
            thread2.join();
            thread3.join();
            thread4.join();
            }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        
    }

    public static void isRegistered(MicroService m) {
        Map<MicroService, BlockingQueue<Message>> messageQueues = MessageBusImpl.getInstance().getMessageQueues();
        if (messageQueues.get(m) != null) {
            System.out.println(m.getName() + " registered");
        } else {
            System.out.println(m.getName() + " not found");
        }

    }

    
    @Test
    public static boolean isRoundRobin(MicroService m)
    {
        
        return false;
    }

}
