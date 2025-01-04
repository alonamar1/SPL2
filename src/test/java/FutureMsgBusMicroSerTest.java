import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import bgu.spl.mics.Broadcast;
import bgu.spl.mics.Event;
import bgu.spl.mics.Message;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;

public class FutureMsgBusMicroSerTest {

    private TestMicroService1 testMicroService1_1;
    private TestMicroService1 testMicroService1_2;
    private TestMicroService2 testMicroService2_1;
    private TestMicroService2 testMicroService2_2;
    private LinkedList<MicroService> microServices;
    private LinkedList<Class> events;
    private LinkedList<Class> broadcasts;
    private Map<Class<? extends Event>, LinkedBlockingQueue<MicroService>> eventSubscribers;
    private Map<Class<? extends Broadcast>, LinkedBlockingQueue<MicroService>> broadcastSubscribers;
    private CountDownLatch latch;
    private Map<MicroService, BlockingQueue<Message>> messageQueues;
    private MessageBusImpl messageBus;

    @BeforeEach
    public void before() {
        messageBus = MessageBusImpl.getInstance();
        latch = new CountDownLatch(4); // 4 microservices
        messageQueues = MessageBusImpl.getInstance().getMessageQueues();
        eventSubscribers = MessageBusImpl.getInstance().getEventSubscribers();
        broadcastSubscribers = MessageBusImpl.getInstance().getBroadcastSubscribers();
        // initialize microservices when "testMicroServiceX_Y" means X is the number of
        // the microservice type and Y is the number of the microservice
        testMicroService1_1 = new TestMicroService1("TestMicroService1_1", latch);
        testMicroService1_2 = new TestMicroService1("TestMicroService1_2", latch);
        testMicroService2_1 = new TestMicroService2("TestMicroService2_1", latch);
        testMicroService2_2 = new TestMicroService2("TestMicroService2_2", latch);
        microServices = new LinkedList<>(); // list of microservices
        microServices.add(testMicroService1_1);
        microServices.add(testMicroService1_2);
        microServices.add(testMicroService2_1);
        microServices.add(testMicroService2_2);
        events = new LinkedList<>(); // list of events
        events.add(TestEvent1.class);
        events.add(TestEvent2.class);
        events.add(terminate.class);
        broadcasts = new LinkedList<>(); // list of events
        broadcasts.add(TestBroadcast1.class);
        broadcasts.add(TestBroadcast2.class); // list of broadcasts

        // initialize threads
        Thread thread1 = new Thread(testMicroService1_1, "Thread 1");
        Thread thread2 = new Thread(testMicroService1_2, "Thread 2");
        Thread thread3 = new Thread(testMicroService2_1, "Thread 3");
        Thread thread4 = new Thread(testMicroService2_2, "Thread 4");

        // start threads and run the microservices
        // Make Them subscribe to the Events in the right order.
        thread1.start();
        try {
            Thread.sleep(15);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        thread2.start();
        try {
            Thread.sleep(15);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        thread3.start();
        try {
            Thread.sleep(15);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        thread4.start();

        // waits for all the microservices to initialize
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    private void close() {

        messageQueues.clear();
        broadcastSubscribers.clear();
        eventSubscribers.clear();
    }

    @Test
    // test register and unregister together
    public void registeredANDUnregisteredGeneralTest() {

        System.out.println(("registeredGeneralTest has started"));

        // checks if the microservices are registered and prints the results
        for (int i = 0; i < microServices.size(); i++) {
            assertTrue(isRegistered(microServices.get(i)));
        }

        // try to register a registered microservice
        messageBus.register(testMicroService1_1);
        assertTrue(isRegistered(testMicroService1_1));
        assertEquals(messageQueues.size(), 4);

        // checks that unregister is working
        assertFalse(unregisterTest(testMicroService1_1));
        messageBus.unregister(testMicroService1_1);
        assertTrue(unregisterTest(testMicroService1_1));
        assertEquals(messageQueues.size(), 3);

        // try to unregister an unregistered microservice
        messageBus.unregister(testMicroService1_1);
        assertTrue(unregisterTest(testMicroService1_1));
        assertEquals(messageQueues.size(), 3);

        // try to resiger an unregistered microservice
        messageBus.register(testMicroService1_1);
        assertTrue(isRegistered(testMicroService1_1));
        assertEquals(messageQueues.size(), 4);

        MicroService[] tempArray = new MicroService[300];
        // try to register a large number of microservices
        for (int i = 0; i < 300; i++) {
            if (i <= 99) {// register unsubscribed to Events microservice
                tempArray[i] = new TestMicroService3(((Integer) i).toString(), latch);
                messageBus.register(tempArray[i]);
            }

            if (i > 99 && i <= 199) {
                // register subscribed microservice
                tempArray[i] = new TestMicroService2(((Integer) i).toString(), latch);
                messageBus.register(tempArray[i]);
            }

            if (i > 199) { // register unsubscribed to Broadcasts microservice
                tempArray[i] = new TestMicroService4(((Integer) i).toString(), latch);
                messageBus.register(tempArray[i]);
            }

        }
        assertTrue(messageQueues.size() == 304);

        for (int i = 0; i < microServices.size(); i++) {
            messageBus.unregister(microServices.get(i));
        }

        // checks if the microservices are unregistered and prints the results
        // NOTICE: if the microservice is unregisterd, sendevent works as well
        // NOTICE: we unregister microservices that are already set for threads
        for (int i = 0; i < microServices.size(); i++) {
            assertTrue(unregisterTest(microServices.get(i)));
        }
        assertTrue(messageQueues.size() == 300);

        for (int i = 0; i < 300; i++) {
            messageBus.unregister(tempArray[i]);
        }
        assertTrue(messageQueues.size() == 0);

        for (MicroService m : microServices) {
            if (m instanceof TestMicroService1) {
                ((TestMicroService1) m).teminate();
            } else
                ((TestMicroService2) m).teminate();
        }

        System.out.println(("registeredGeneralTest has finished"));

    }

    @Test
    public void sendEventANDBroadcastTest() {
        System.out.println("sendEventANDBroadcastTest has started");

        // Sending an event only to one microservice
        messageBus.sendEvent(new TestEvent2("One EVENT sent to one microservice"));

        // wait for the message to Arrive
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int howManyGot = 0;
        for (MicroService m : microServices) {
            if (m instanceof TestMicroService1 && ((TestMicroService1) m).getTest()) {
                howManyGot++;
            } else if (m instanceof TestMicroService2 && ((TestMicroService2) m).getTest()) {
                howManyGot++;
            }
        }
        assertEquals(1, howManyGot);

        // Sending a broadcast to all microservices
        messageBus.sendBroadcast(new TestBroadcast1(""));
        int howManyGotB = 0;

        // wait for the message to Arrive
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // check if messages arrived
        for (MicroService m : microServices) {
            if (m instanceof TestMicroService1 && ((TestMicroService1) m).getTestB()) {
                System.out.println(m.getName() + " received TestBroadcast1");
                howManyGotB++;
            } else if (m instanceof TestMicroService2 && ((TestMicroService2) m).getTestB()) {
                System.out.println(m.getName() + " received TestBroadcast1");
                howManyGotB++;
            }
        }
        assertEquals(4, howManyGotB);

        // Sending terminate events to all microservices
        for (int i = 0; i < microServices.size(); i++) {
            messageBus.sendEvent(new terminate("terminate" + (i + 1)));
        }

        // wait for the terminate message to Arrive
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertTrue(messageQueues.isEmpty());
        System.out.println("sendEventANDBroadcastTest has finished");
    }

    @Test
    public void subsribeEventGeneralTest() {

        System.out.println(("subsribeEventGeneralTest has started"));

        // checks if the events are subscribed and prints the results
        for (int i = 0; i < events.size(); i++) {
            System.out.println("event number " + (i + 1) + " subscribers:");
            assertTrue(subsribeEventTest(events.get(i)));
            System.out.println();
            assertEquals(eventSubscribers.get(events.get(i)).size(), microServices.size()); // check if all the events
                                                                                            // are subscribed
        }

        // checks if the broadcasts are subscribed and prints the results
        for (int i = 0; i < broadcasts.size(); i++) {
            System.out.println("broadcast number " + (i + 1) + " subscribers:");
            assertTrue(subsribeBroadcastTest(broadcasts.get(i)));
            System.out.println();
            assertEquals(broadcastSubscribers.get(broadcasts.get(i)).size(), microServices.size()); // check if all the
                                                                                                    // broadcasts are
                                                                                                    // subscribed
        }

        // checks if double subsription to Event is allowed
        testMicroService2_1.doubleSubsribeEvent();
        assertFalse(testMicroService2_1.getCheckedIfDoubleSubsribeToEvent());

        // checks if double subsription to Broadcast is allowed
        testMicroService2_2.doubleSubsribeBroadcast();
        assertFalse(testMicroService2_2.getCheckedIfDoubleSubsribeToBroadcast());

        // checks an event without subscribers
        assertNull(testNoSubscribers());

        // subscribe large number of microservice to a broadcast and an event
        assertEquals(4, broadcastSubscribers.get(TestBroadcast1.class).size());
        assertEquals(4, eventSubscribers.get(terminate.class).size());
        Thread[] t = new Thread[20];
        CountDownLatch latch2 = new CountDownLatch(20);
        for (int i = 0; i < t.length; i++) {
            final String microserviceName = "microservice" + i;
            t[i] = new Thread(() -> {
                TestMicroService3 temp = new TestMicroService3(microserviceName, latch2);
                messageBus.register(temp);
                messageBus.subscribeBroadcast(TestBroadcast1.class, temp);
                messageBus.subscribeEvent(terminate.class, temp);
                latch2.countDown();
            });
            t[i].start();
        }
        try {
            latch2.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        assertEquals(24, broadcastSubscribers.get(TestBroadcast1.class).size());
        assertEquals(24, eventSubscribers.get(terminate.class).size());

        System.out.println(("subsribeEventGeneralTest has finished"));

    }

    @Test
    public void isRoundRobinTest() {
        System.out.println("isRoundRobinTest has started");
        MicroService[] microServices = { testMicroService1_1, testMicroService1_2, testMicroService2_1,
                testMicroService2_2 };

        // Sending the Same event (Simulate the lidar worker excepting the same event
        // (DetectedObjectsEvent only))
        for (int i = 0; i < 24; i++) {
            messageBus.sendEvent(new TestEvent1("Event" + i));
        }

        // wait for the messages to Arrive
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Check if events were distributed in a round-robin fashion
        for (MicroService m : microServices) {
            if (m instanceof TestMicroService1) {
                assertEquals(6, ((TestMicroService1) m).getEventCount());
            } else if (m instanceof TestMicroService2) {
                assertEquals(6, ((TestMicroService2) m).getEventCount());
            }
        }

        // Additional tests to verify round-robin distribution
        messageBus.sendEvent(new TestEvent1("ExtraEvent1"));
        messageBus.sendEvent(new TestEvent1("ExtraEvent2"));

        // wait for the messages to Arrive
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Check if the extra events were distributed correctly
        // after 6 rounds (because the 24 events were already distributed) it is need to
        // be the first and second microservice turn
        assertEquals(7, ((TestMicroService1) testMicroService1_1).getEventCount());
        assertEquals(7, ((TestMicroService1) testMicroService1_2).getEventCount());

        System.out.println("isRoundRobinTest has finished");
    }

    @Test
    public void awaitMessageTest() {
        // to check a specific microservice
        System.out.println("awaitMessageTest has started");

        for (MicroService m : microServices) {
            messageBus.unregister(m);
        }

        // Register a microservice and send an event to it
        CountDownLatch latch = new CountDownLatch(1);
        TestMicroService1 testMicroService = new TestMicroService1("TestMicroServiceAwait", latch);
        messageBus.register(testMicroService);
        Thread thread = new Thread(testMicroService);
        thread.start();

        // Wait for the microservice to initialize
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Send an event to the microservice
        TestBroadcast2 broad = new TestBroadcast2("Test Event for awaitMessage");
        messageBus.sendBroadcast(broad);

        // Wait for the message to be processed
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Check if the message was received and processed
        assertTrue(testMicroService.getAwaitTest());

        // Send a termination event to stop the microservice
        messageBus.sendEvent(new terminate("terminate"));

        // Wait for the microservice to terminate
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Unregister the microservice
        messageBus.unregister(testMicroService);

        // Check that awaitMessage throws an IllegalStateException
        assertThrows(IllegalStateException.class, () -> {
            messageBus.awaitMessage(testMicroService);
        });

        System.out.println("awaitMessageTest has finished");
    }

    // checks if a microservice is registered and prints the results
    public boolean isRegistered(MicroService m) {
        boolean test = false;
        if (messageQueues.get(m) != null) {
            System.out.println(m.getName() + " registered");
            test = true;
        } else {
            System.out.println(m.getName() + " not found");
        }
        return test;
    }

    // checks if a microservice is unregistered and prints the results
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

    // checks the microservices that are subscribed to an event
    public boolean subsribeEventTest(Class<? extends Event> e) {
        boolean test = false;
        Map<Class<? extends Event>, LinkedBlockingQueue<MicroService>> eventSubscribers = MessageBusImpl.getInstance()
                .getEventSubscribers();
        if (!eventSubscribers.get(e).isEmpty()) {
            for (MicroService microService : eventSubscribers.get(e)) {
                System.out.println(microService.getName() + "subscribed to " + e.getName());
            }
            test = true;
        } else {
            System.out.println("no subscribers");
        }
        return test;
    }

    // checks the microservices that are subscribed to a broadcast
    public boolean subsribeBroadcastTest(Class<? extends Broadcast> b) {
        boolean test = false;
        Map<Class<? extends Broadcast>, LinkedBlockingQueue<MicroService>> broadcastSubscribers = MessageBusImpl
                .getInstance().getBroadcastSubscribers();
        if (!broadcastSubscribers.get(b).isEmpty()) {
            for (MicroService microService : broadcastSubscribers.get(b)) {
                System.out.println(microService.getName() + "subscribed to " + b.getName());
            }
            test = true;
        } else {
            System.out.println("no subscribers");
        }

        return test;
    }

    public LinkedBlockingQueue<MicroService> testNoSubscribers() {
        Map<Class<? extends Event>, LinkedBlockingQueue<MicroService>> eventSubscribers = MessageBusImpl.getInstance()
                .getEventSubscribers();
        return eventSubscribers.get(TestEvent4.class);

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
        private boolean test;
        private boolean testB;
        private boolean awaitTest;
        private int eventCount;

        public TestMicroService1(String name, CountDownLatch latch) {
            super(name);
            this.latch = latch;
            test = false;
            testB = false;
            eventCount = 0;
            awaitTest = false;
        }

        public void teminate() {
            super.terminate();
        }

        public boolean getTest() {
            return test;
        }

        public boolean getTestB() {
            return testB;
        }

        public boolean getAwaitTest() {
            return awaitTest;
        }

        public int getEventCount() {
            return eventCount;
        }

        @Override
        protected void initialize() {
            subscribeEvent(TestEvent1.class, (event) -> {
                System.out.println(getName() + " received TestEvent1: " + event.getMessage());
                complete(event, getName() + " handled Event 1: " + event.getMessage());
                eventCount++;
            });

            subscribeEvent(TestEvent2.class, (event) -> {
                System.out.println(getName() + " received TestEvent2: " + event.getMessage());
                complete(event, getName() + " handled Event 2: " + event.getMessage());
                test = true;
                eventCount++;
            });

            subscribeBroadcast(TestBroadcast1.class, (broadcast) -> {
                System.out.println(getName() + " handled Broadcast 1: " + broadcast.getMessage());
                testB = true;
            });

            subscribeBroadcast(TestBroadcast2.class, (broadcast) -> {
                System.out.println(getName() + " handled Broadcast 2: " + broadcast.getMessage());
                awaitTest = true;
            });

            subscribeEvent(terminate.class, (event) -> {
                System.out.println(getName() + " received terminate: " + event.getMessage());
                terminate();
            });

            latch.countDown();
        }
    }

    private static class TestMicroService2 extends MicroService {

        private final CountDownLatch lanch;
        private final CountDownLatch sendLatch;
        private boolean checkedIfDoubleSubsribeToEvent;
        private boolean checkedIfDoubleSubsribeToBroadcast;
        private boolean test;
        private boolean testB;
        private int eventCount;

        public TestMicroService2(String name, CountDownLatch lanch) {
            super(name);
            this.lanch = lanch;
            sendLatch = new CountDownLatch(2);
            checkedIfDoubleSubsribeToEvent = false;
            checkedIfDoubleSubsribeToBroadcast = false;
            test = false;
            testB = false;
            eventCount = 0;
        }

        public void getLatch() {
            try {
                sendLatch.await();
            } catch (InterruptedException e) {
                fail("Thread interruption occurred");
            }
        }

        public void doubleSubsribeEvent() {
            subscribeEvent(TestEvent3.class, (event) -> {
                checkedIfDoubleSubsribeToEvent = true;
            });
        }

        public boolean getTestB() {
            return testB;
        }

        public void doubleSubsribeBroadcast() {
            subscribeBroadcast(TestBroadcast2.class, (Broadcast) -> {
                checkedIfDoubleSubsribeToBroadcast = true;
            });
        }

        public boolean getTest() {
            return test;
        }

        public boolean getCheckedIfDoubleSubsribeToEvent() {
            return checkedIfDoubleSubsribeToEvent;
        }

        public boolean getCheckedIfDoubleSubsribeToBroadcast() {
            return checkedIfDoubleSubsribeToBroadcast;
        }

        public int getEventCount() {
            return eventCount;
        }

        public void teminate() {
            super.terminate();
        }

        @Override
        protected void initialize() {

            subscribeEvent(TestEvent1.class, (event) -> {
                System.out.println(getName() + " received TestEvent1: " + event.getMessage());
                complete(event, getName() + " handled Event 1: " + event.getMessage());
                eventCount++;
            });

            subscribeEvent(TestEvent2.class, (event) -> {
                System.out.println(getName() + " received TestEvent2: " + event.getMessage());
                complete(event, getName() + " handled Event 2: " + event.getMessage());
                test = true;
                eventCount++;
            });

            subscribeBroadcast(TestBroadcast1.class, (broadcast) -> {
                System.out.println(getName() + " handled Broadcast 1: " + broadcast.getMessage());
                testB = true;
                sendLatch.countDown();
            });

            subscribeEvent(TestEvent3.class, (event) -> {
            });

            subscribeBroadcast(TestBroadcast2.class, (broadcast) -> {
                System.out.println(getName() + " handled Broadcast 2: " + broadcast.getMessage());
            });

            subscribeEvent(terminate.class, (event) -> {
                System.out.println(getName() + " received terminate: " + event.getMessage());
                terminate();
            });

            lanch.countDown();
        }
    }

    private static class TestMicroService3 extends MicroService {

        private final CountDownLatch lanch;

        public TestMicroService3(String name, CountDownLatch lanch) {
            super(name);
            this.lanch = lanch;
        }

        public void teminate() {
            super.terminate();
        }

        @Override
        protected void initialize() {
            subscribeBroadcast(TestBroadcast1.class, (broadcast) -> {
                System.out.println(getName() + " handled Broadcast 1: " + broadcast.getMessage());
            });

            subscribeBroadcast(TestBroadcast2.class, (broadcast) -> {
                System.out.println(getName() + " handled Broadcast 2: " + broadcast.getMessage());
                terminate();
            });

            lanch.countDown();
        }
    }

    private static class TestMicroService4 extends MicroService {

        private final CountDownLatch lanch;

        public TestMicroService4(String name, CountDownLatch lanch) {
            super(name);
            this.lanch = lanch;
        }

        public void teminate() {
            super.terminate();
        }

        @Override
        protected void initialize() {
            subscribeEvent(TestEvent1.class, (event) -> {
                complete(event, getName() + " handled Event 1: " + event.getMessage());
            });

            subscribeEvent(TestEvent2.class, (event) -> {
                complete(event, getName() + " handled Event 2: " + event.getMessage());
            });
            lanch.countDown();
        }
    }
}
