
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bgu.spl.mics.testData;
import bgu.spl.mics.Broadcast;
import bgu.spl.mics.Event;
import bgu.spl.mics.Message;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;

public class FutureMsgBusMicroSerTest {

    @Test
    public void generalTest1() {

        System.out.println(("generalTest1 has started"));
        CountDownLatch lanch = new CountDownLatch(4); // 4 microservices

        //initialize microservices when "testMicroServiceX_Y" means X is the number of the microservice type and Y is the number of the microservice
        TestMicroService1 testMicroService1_1 = new TestMicroService1("TestMicroService1_1", lanch);
        TestMicroService1 testMicroService1_2 = new TestMicroService1("TestMicroService1_2", lanch);
        TestMicroService2 testMicroService2_1 = new TestMicroService2("TestMicroService2_1", lanch);
        TestMicroService2 testMicroService2_2 = new TestMicroService2("TestMicroService2_2", lanch);
        LinkedList<MicroService> microServices = new LinkedList<>(); //list of microservices
        microServices.add(testMicroService1_1);
        microServices.add(testMicroService1_2);
        microServices.add(testMicroService2_1);
        microServices.add(testMicroService2_2);
        LinkedList<Class> events = new LinkedList<>(); //list of events
        events.add(TestEvent1.class);
        events.add(TestEvent2.class);
        events.add(terminate.class);
        LinkedList<Class> broadcasts = new LinkedList<>(); //list of events
        broadcasts.add(TestBroadcast1.class);
        broadcasts.add(TestBroadcast2.class); //list of broadcasts

        //initialize threads
        Thread thread1 = new Thread(testMicroService1_1);
        Thread thread2 = new Thread(testMicroService1_2);
        Thread thread3 = new Thread(testMicroService2_1);
        Thread thread4 = new Thread(testMicroService2_2);

        //start threads and run the microservices
        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();

        //waits for all the microservices to initialize
        try {
            lanch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //calls the fields from messageBusImpl to check everything is working
        Map<Class<? extends Event>, LinkedBlockingQueue<MicroService>> eventSubscribers = MessageBusImpl.getInstance().getEventSubscribers();
        Map<Class<? extends Broadcast>, LinkedBlockingQueue<MicroService>> broadcastSubscribers = MessageBusImpl.getInstance().getBroadcastSubscribers();

        //checks if the microservices are registered and prints the results
        for (int i = 0; i < microServices.size(); i++) {
            isRegistered(microServices.get(i));
        }
        System.out.println();

        //checks if the events are subscribed and prints the results
        for (int i = 0; i < events.size(); i++) {
            System.out.println("event number " + (i + 1) + " subscribers:");
            subsribeEventTest(events.get(i));
            System.out.println();
            assertEquals(eventSubscribers.get(events.get(i)).size(), microServices.size()); // check if all the events are subscribed
        }

        //checks if the broadcasts are subscribed and prints the results
        for (int i = 0; i < broadcasts.size(); i++) {
            System.out.println("broadcast number " + (i + 1) + " subscribers:");
            subsribeBroadcastTest(broadcasts.get(i));
            System.out.println();
            assertEquals(broadcastSubscribers.get(broadcasts.get(i)).size(), microServices.size()); // check if all the broadcasts are subscribed
        }

        //checks if there is a double subsription 
        testMicroService2_1.doubleSubsribeEvent();
        assertFalse(testMicroService2_1.getCheckedIfDoubleSubsribe());

        //checks an event without subscribers
        //testNoSubscribers();        

        //sends broadcast to all the microservices
        MessageBusImpl.getInstance().sendBroadcast(new TestBroadcast1(""));

        //sends event to all the microservices
        for (int i = 0; i < microServices.size(); i++) {
            MessageBusImpl.getInstance().sendEvent(new terminate("terminate" + (i + 1)));
        }

        //waits for all the microservices to terminate
        try {
            thread1.join();
            thread2.join();
            thread3.join();
            thread4.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //checks if the microservices are unregistered and prints the results
        //NOTICE: if the microservice is unregisterd, sendevent works as well
        for (int i = 0; i < microServices.size(); i++) {
            assertTrue(unregisterTest(microServices.get(i)));
        }
        System.out.println(("generalTest1 has finished"));

    }

    /* 
    @Test
    public void generalTest2() {

        System.out.println(("generalTest2 has started"));
        CountDownLatch lanch = new CountDownLatch(2); // 2 microservices

        TestMicroService3 testMicroService3 = new TestMicroService3("TestMicroService3", lanch);
        TestMicroService4 testMicroService4 = new TestMicroService4("TestMicroService4", lanch);
        TestMicroService1 tempTempMicroService = new TestMicroService1("TestMicroService1", null);

        //initialize threads
        Thread thread1 = new Thread(testMicroService3);
        Thread thread2 = new Thread(testMicroService4);

        //start threads and run the microservices
        thread1.start();
        thread2.start();

        //waits for all the microservices to initialize
        try {
            lanch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        

        //unregister a microservice that is not registered
        MessageBusImpl.getInstance().unregister(tempTempMicroService);
        assertTrue(unregisterTest(tempTempMicroService));
        if (!unregisterTest(tempTempMicroService)) {
            System.out.println(tempTempMicroService.getName() + " found");
        }

        //check that the only relevant microservices are subscribed to the broadcast
        MessageBusImpl.getInstance().sendBroadcast(new TestBroadcast1(""));
        assertTrue(testMicroService3.getTest() == true);
        assertFalse(testMicroService4.getTest() == true);
        System.out.println(("testMicroService4 didnt get the broadcast as expacted"));
        MessageBusImpl.getInstance().sendBroadcast(new TestBroadcast2(""));
        MessageBusImpl.getInstance().sendBroadcast(new TestBroadcast1(""));
        assertTrue(testMicroService4.getTest());
        System.out.println(("testMicroService4 got the broadcast"));

        //check that the only relevant microservices are subscribed to the events
        MessageBusImpl.getInstance().sendEvent(new TestEvent1(""));
        assertTrue(testMicroService3.getTestEvent1());
        System.out.println(("testMicroService3 got the event"));
        assertTrue(unregisterTest(testMicroService4));
        testMicroService3.setTest();
        System.out.println(("test reterned to be false"));

        MessageBusImpl.getInstance().sendEvent(new terminate(""));

        //checks that awaitMessage throws exaptions for unregistered microservice
        try {
            MessageBusImpl.getInstance().awaitMessage(tempTempMicroService);
            fail("Expected IllegalStateException for unregistered MicroService.");
        } catch (IllegalStateException e) {
            assertTrue(true); // Exception is expected
            System.out.println(("awaitmessage throws exaptions"));

        } catch (InterruptedException e) {
            fail("Unexpected interruption.");
        }

        //waits for all the microservices to terminate
        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(("test has finished"));

    }
*/
    @Test
    //checks if a microservice is registered and prints the results
    public void isRegistered(MicroService m) {
        boolean test = false;
        Map<MicroService, BlockingQueue<Message>> messageQueues = MessageBusImpl.getInstance().getMessageQueues();
        if (messageQueues.get(m) != null) {
            System.out.println(m.getName() + " registered");
            test = true;
        } else {
            System.out.println(m.getName() + " not found");
        }
        assertTrue(test);
    }

    @Test
    public static boolean isRoundRobin(MicroService[] m) {
        CountDownLatch lanch = new CountDownLatch(4); // 4 microservices

        //initialize microservices when "testMicroServiceX_Y" means X is the number of the microservice type and Y is the number of the microservice
        TestMicroService1 testMicroService1_1 = new TestMicroService1("TestMicroService1_1", lanch);
        TestMicroService1 testMicroService1_2 = new TestMicroService1("TestMicroService1_2", lanch);
        TestMicroService2 testMicroService2_1 = new TestMicroService2("TestMicroService2_1", lanch);
        TestMicroService2 testMicroService2_2 = new TestMicroService2("TestMicroService2_2", lanch);
        MicroService[] microServices = {testMicroService1_1, testMicroService1_2, testMicroService2_1, testMicroService2_2}; //array of microservices
        Class[][] events = new Class[4][6]; //array of events
        //initialize threads
        Thread thread1 = new Thread(testMicroService1_1);
        Thread thread2 = new Thread(testMicroService1_2);
        Thread thread3 = new Thread(testMicroService2_1);
        Thread thread4 = new Thread(testMicroService2_2);

        //start threads and run the microservices
        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();

        //waits for all the microservices to initialize
        try {
            lanch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < 24; i++) {
            if (i % 3 == 0) {
                MessageBusImpl.getInstance().sendEvent(new TestEvent1("Event" + i));
            } else if (i % 3 == 1) {
                MessageBusImpl.getInstance().sendEvent(new TestEvent2("Event" + i));
            } else {
                MessageBusImpl.getInstance().sendEvent(new TestEvent3("Event" + i));
            }

        }

        return false;
    }

    @Test
    //checks the microservices that are subscribed to an event 
    public void subsribeEventTest(Class<? extends Event> e) {
        boolean test = false;
        Map<Class<? extends Event>, LinkedBlockingQueue<MicroService>> eventSubscribers = MessageBusImpl.getInstance().getEventSubscribers();
        if (!eventSubscribers.get(e).isEmpty()) {
            for (MicroService microService : eventSubscribers.get(e)) {
                System.out.println(microService.getName() + "subscribed to " + e.getName());
            }
            test = true;
        } else {
            System.out.println("no subscribers");
        }
        assertTrue(test);
    }

    @Test
    //checks the microservices that are subscribed to a broadcast
    public void subsribeBroadcastTest(Class<? extends Broadcast> b) {
        boolean test = false;
        Map<Class<? extends Broadcast>, LinkedBlockingQueue<MicroService>> broadcastSubscribers = MessageBusImpl.getInstance().getBroadcastSubscribers();
        if (!broadcastSubscribers.get(b).isEmpty()) {
            for (MicroService microService : broadcastSubscribers.get(b)) {
                System.out.println(microService.getName() + "subscribed to " + b.getName());
            }
            test = true;
        } else {
            System.out.println("no subscribers");
        }

        assertTrue(test);

    }

    //checks if a microservice is unregistered and prints the results
    public boolean unregisterTest(MicroService m) {
        if (MessageBusImpl.getInstance().getMessageQueues().get(m) != null) {
            System.out.println("contains key " + m.getName());
            if (!MessageBusImpl.getInstance().getMessageQueues().get(m).isEmpty()) {
                System.out.println(m.getName() + " is not empty");
            }
            return false;
        }
        return true;
    }

    @Test
    public void testNoSubscribers() {
        Map<Class<? extends Event>, LinkedBlockingQueue<MicroService>> eventSubscribers = MessageBusImpl.getInstance().getEventSubscribers();
        assertNull(eventSubscribers.get(TestEvent3.class));
        if (eventSubscribers.get(TestEvent4.class) == null) {
            System.out.println("no subscribers");
        }
    }

    public static class TestEvent1 implements Event<String> {

        private final String message;

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

        private final String message;

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

        private final String message;

        public TestBroadcast1(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    public static class TestEvent2 implements Event<String> {

        private final String message;

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

    public static class TestEvent3 implements Event<String> {

        private final String message;

        public TestEvent3(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public String getName() {
            return "TestEvent3";
        }
    }

    public static class TestEvent4 implements Event<String> {

        private final String message;

        public TestEvent4(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public String getName() {
            return "TestEvent4";
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

        private final CountDownLatch latch;

        public TestMicroService1(String name, CountDownLatch latch) {
            super(name);
            this.latch = latch;
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
            });
            latch.countDown();
        }
    }

    private static class TestMicroService2 extends MicroService {

        private final CountDownLatch lanch;
        boolean checkedIfDoubleSubsribe;

        public TestMicroService2(String name, CountDownLatch lanch) {
            super(name);
            this.lanch = lanch;
            checkedIfDoubleSubsribe = false;
        }

        public void doubleSubsribeEvent() {
            subscribeEvent(TestEvent3.class, (event) -> {
                checkedIfDoubleSubsribe = true;
            });
        }

        public boolean getCheckedIfDoubleSubsribe() { return checkedIfDoubleSubsribe;}

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

            subscribeEvent(TestEvent3.class, (event) -> {});

            subscribeBroadcast(TestBroadcast2.class, (broadcast) -> {
                System.out.println(getName() + " handled Broadcast 2: " + broadcast.getMessage());
            });
            subscribeEvent(terminate.class, (event) -> {
                System.out.println(getName() + " handled terminate: " + event.getMessage());
                terminate();

            });
            lanch.countDown();
        }
    }

    private static class TestMicroService3 extends MicroService {

        private final CountDownLatch lanch;
        private boolean test;
        private boolean testEvent1;

        public TestMicroService3(String name, CountDownLatch lanch) {
            super(name);
            this.lanch = lanch;
            test = false;
            testEvent1 = false;
        }

        public boolean getTest() {
            return test;
        }

        public boolean getTestEvent1() {
            return testEvent1;
        }

        public boolean setTest() {
            test = false;
            return test;
        }

        @Override
        protected void initialize() {

            subscribeEvent(TestEvent1.class, (event) -> {
                complete(event, getName() + " handled Event 1: " + event.getMessage());
                testEvent1 = true;

            });

            subscribeEvent(TestEvent2.class, (event) -> {
                complete(event, getName() + " handled Event 2: " + event.getMessage());
            });

            subscribeBroadcast(TestBroadcast1.class, (broadcast) -> {
                System.out.println(getName() + " handled Broadcast 1: " + broadcast.getMessage());
                test = true;
            });

            subscribeEvent(TestEvent3.class, (event) -> {
                System.out.println(getName() + " handled Event 3: " + event.getMessage());
            });

            subscribeEvent(terminate.class, (event) -> {
                System.out.println(getName() + " handled terminate: " + event.getMessage());
                terminate();
            });
            lanch.countDown();
        }
    }

    private static class TestMicroService4 extends MicroService {

        private final CountDownLatch lanch;
        private boolean test;
        private boolean testEvent2;

        public TestMicroService4(String name, CountDownLatch lanch) {
            super(name);
            this.lanch = lanch;
            test = false;
            testEvent2 = false;
        }

        public boolean getTest() {
            return test;
        }

        public boolean getTestEvent2() {
            return testEvent2;
        }

        public boolean setTest() {
            test = false;
            return test;
        }

        @Override
        protected void initialize() {
            subscribeEvent(TestEvent1.class, (event) -> {
                complete(event, getName() + " handled Event 1: " + event.getMessage());
                terminate();
            });
            subscribeEvent(TestEvent2.class, (event) -> {
                complete(event, getName() + " handled Event 2: " + event.getMessage());
                testEvent2 = true;
            });

            subscribeBroadcast(TestBroadcast2.class, (broadcast) -> {
                System.out.println(getName() + " handled Broadcast 2: " + broadcast.getMessage());
                test = true;
            });

            lanch.countDown();
        }
    }
}
