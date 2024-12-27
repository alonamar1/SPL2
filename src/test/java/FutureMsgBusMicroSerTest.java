
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
        CountDownLatch lanch = new CountDownLatch(4); // 4 microservices

        //initialize microservices when "testMicroServiceX_Y" means X is the number of the microservice type and Y is the number of the microservice
        TestMicroService1 testMicroService1_1 = new TestMicroService1("TestMicroService1_1", lanch); 
        TestMicroService1 testMicroService1_2 = new TestMicroService1("TestMicroService1_2", lanch); 
        TestMicroService2 testMicroService2_1 = new TestMicroService2("TestMicroService2_1", lanch); 
        TestMicroService2 testMicroService2_2 = new TestMicroService2("TestMicroService2_2", lanch); 
        MicroService[] microServices = {testMicroService1_1, testMicroService1_2, testMicroService2_1, testMicroService2_2}; //array of microservices
        Class[] events = {TestEvent1.class, TestEvent2.class, terminate.class}; //array of events
        Class[] broadcasts = {TestBroadcast1.class, TestBroadcast2.class}; //array of broadcasts

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

        //להוסיך מה זה עושה
        try {
            lanch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //calls the fields from messageBusImpl to check everything is working
        Map<Class<? extends Event>, LinkedBlockingQueue<MicroService>> eventSubscribers = MessageBusImpl.getInstance().getEventSubscribers();
        Map<Class<? extends Broadcast>, LinkedBlockingQueue<MicroService>> broadcastSubscribers = MessageBusImpl.getInstance().getBroadcastSubscribers();

        //checks if the microservices are registered and prints the results
        for (int i = 0; i < microServices.length; i++) {
            assertTrue(isRegistered(microServices[i])); 
        }
        System.out.println();
       
        //checks if the events are subscribed and prints the results
        for (int i = 0; i < events.length; i++) {
            System.out.println("event number " + (i+1) + " subscribers:");
            assertTrue(subsribeEventTest(events[i]));
            System.out.println();
            assertEquals(eventSubscribers.get(events[i]).size(), microServices.length); // check if all the events are subscribed
        }

        //checks if the broadcasts are subscribed and prints the results
        for (int i = 0; i < broadcasts.length; i++){
                System.out.println("broadcast number " + (i+1) + " subscribers:");
                assertTrue(subsribeBroadcastTest(broadcasts[i]));
                System.out.println();
                assertEquals(broadcastSubscribers.get(broadcasts[i]).size(), microServices.length); // check if all the broadcasts are subscribed
        }
            
        //sends broadcast to all the microservices לדבר על זה עם רן
        MessageBusImpl.getInstance().sendBroadcast(new TestBroadcast1("Broadcast 2"));

        //sends event to all the microservices
        for (int i = 0; i < microServices.length; i++) {
            MessageBusImpl.getInstance().sendEvent(new terminate("terminate" + (i + 1)));
        }
        
        //waits for all the microservices to terminate
        try {
            thread1.join();
            thread2.join();
            thread3.join();
            thread4.join();
            }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        //checks if the microservices are unregistered and prints the results
        //NOTICE: if the microservice is unregisterd, sendevent works as well
        for (int i = 0; i < microServices.length; i++) {
            assertTrue(unregisterTest(microServices[i]));
        }

        //להוסיף מה עושים עם await

    }

    //checks if a microservice is registered and prints the results
    public static Boolean isRegistered(MicroService m) {
        Map<MicroService, BlockingQueue<Message>> messageQueues = MessageBusImpl.getInstance().getMessageQueues();
        if (messageQueues.get(m) != null) {
            System.out.println(m.getName() + " registered");
            return true;
        } 
        else {
            System.out.println(m.getName() + " not found");
            return false;
        }
    }

    
    @Test
    public static boolean isRoundRobin(MicroService m)
    {

        return false;
    }

    //checks the microservices that are subscribed to an event 
    public static Boolean subsribeEventTest(Class<? extends Event> e) {
        Map<Class<? extends Event>, LinkedBlockingQueue<MicroService>> eventSubscribers = MessageBusImpl.getInstance().getEventSubscribers();
        if (!eventSubscribers.get(e).isEmpty()) {
            for (MicroService microService : eventSubscribers.get(e)) {
                System.out.println(microService.getName() + "subscribed to " + e.getName());
            }
            return true;
        }
        else{
            System.out.println("no subscribers");}

        return false;    
    }
    
    //checks the microservices that are subscribed to a broadcast
    public static Boolean subsribeBroadcastTest(Class<? extends Broadcast> b) {
        Map<Class<? extends Broadcast>, LinkedBlockingQueue<MicroService>> broadcastSubscribers = MessageBusImpl.getInstance().getBroadcastSubscribers();
        if (!broadcastSubscribers.get(b).isEmpty()) {
            for (MicroService microService : broadcastSubscribers.get(b)) {
                System.out.println(microService.getName() + "subscribed to " + b.getName());
            }
            return true;
        }
        else{
            System.out.println("no subscribers");}

        return false;    
    }

    //checks if a microservice is unregistered and prints the results
    public static Boolean unregisterTest(MicroService m) 
    {
        if (MessageBusImpl.getInstance().getMessageQueues().containsKey(m))
        {
            System.out.println( "contains key " + m.getName());
            if (!MessageBusImpl.getInstance().getMessageQueues().get(m).isEmpty())
            {   
                System.out.println(m.getName() + " is not empty");
            }
            return false;
        }
        return true;
    }



}
