
/*import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.example.services.ExampleBroadcastListenerService;
import bgu.spl.mics.example.services.ExampleEventHandlerService;
import bgu.spl.mics.example.services.ExampleMessageSenderService;

public class MessageBusImpTest {

    private MessageBusImpl messageBus;
    private MicroService microService1;
    private MicroService microService2;
    private MicroService microService3;
    private MicroService microService4;
    private Thread thread1;
    private Thread thread2;
    private Thread thread3;
    private Thread thread4;

    @Before
    public void setUp() {
        messageBus = MessageBusImpl.getInstance(); // Get singleton instance

        String[] args1 = {"3"};
        microService1 = new ExampleBroadcastListenerService("listener1", args1);

        String[] args2 = {"2"};
        microService2 = new ExampleEventHandlerService("handler1", args2);

        String[] args3 = {"broadcast"};
        microService3 = new ExampleMessageSenderService("message1", args3);

        String[] args4 = {"event"};
        microService4 = new ExampleMessageSenderService("message2", args4);

        // Start threads for the MicroServices
        thread1 = new Thread(microService1);
        thread2 = new Thread(microService2);
        thread3 = new Thread(microService3);
        thread4 = new Thread(microService4);
        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
    }
*/
    /* 
    @After
    public void tearDown() {
        // Terminate the MicroServices
        thread1.interrupt();
        thread2.interrupt();

        // Join threads to ensure proper cleanup
        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            fail("Thread interruption during cleanup.");
        }
    }
     */ 
    // @Test
    // public void testRegisterAndUnregister() {
    //     System.out.println("test 1");
    //     //messageBus.register(microService1);
    //     //messageBus.register(microService2);

    //     if (messageBus.getMessageQueues().get(microService1) != null) {
    //         System.out.println("microservice 1 registerd");
    //     }
    //     if (messageBus.getMessageQueues().get(microService2) != null) {
    //         System.out.println("microservice 2 registerd");
    //     }

        //Broadcast broadcast1 = new ExampleBroadcast("1");
        //messageBus.sendBroadcast(broadcast1);
        //ExampleEvent event1 = new ExampleEvent("2");
        //messageBus.sendEvent(event1);

        /* 
        if (messageBus.getMessageQueues().get(messageBus.getBroadcastSubscribers().get(ExampleBroadcast.class).peek()).peek() == broadcast1) {
            System.out.println("sendbroadcast successed");
        }
        if (messageBus.getMessageQueues().get(messageBus.getBroadcastSubscribers().get(ExampleEvent.class).peek()).peek() == event1) {
            System.out.println("sendevent successed");
        }
            */
/*
        try {
            // Ensure MicroServices can receive messages
          //  assertNotNull(messageBus.awaitMessage(microService1));
            assertNotNull(messageBus.awaitMessage(microService2));
        } catch (InterruptedException e) {
            fail("Unexpected interruption.");
        }

        // Unregister MicroServices
        messageBus.unregister(microService1);
        messageBus.unregister(microService2);

        if (messageBus.getMessageQueues().get(microService1) == null) {
            System.out.println("microservice 1 unregisterd");
        }
        if (messageBus.getMessageQueues().get(microService2) == null) {
            System.out.println("microservice 2 unregisterd");
        }


        // Verify awaitMessage throws IllegalStateException for unregistered services
        try {
            messageBus.awaitMessage(microService1);
            fail("Expected IllegalStateException for unregistered MicroService.");
        } catch (IllegalStateException e) {
            assertTrue(true); // Exception is expected
        } catch (InterruptedException e) {
            fail("Unexpected interruption.");
        }
    }*/
/* 
    @Test(timeout = 5000)
    public void testSubscribeEvent() {
        messageBus.register(microService1);
        ExampleEvent testEvent = new ExampleEvent("event1");

        // Subscribe microService1 to the event
        messageBus.subscribeEvent(testEvent.getClass(), microService1);

        // Send the event
        Future<String> future = messageBus.sendEvent(testEvent);
        assertNotNull(future); // Ensure the event is sent

        // Retrieve the event and ensure it's received
        try {
            Message message = messageBus.awaitMessage(microService1);
            assertEquals(testEvent, message);
        } catch (InterruptedException e) {
            fail("Unexpected interruption.");
        }
    }

    @Test(timeout = 5000)
    public void testSubscribeBroadcast() {
        messageBus.register(microService1);
        messageBus.register(microService2);
        Broadcast testBroadcast = new Broadcast() {
        };

        // Subscribe both MicroServices to the broadcast
        messageBus.subscribeBroadcast(testBroadcast.getClass(), microService1);
        messageBus.subscribeBroadcast(testBroadcast.getClass(), microService2);

        // Send the broadcast
        messageBus.sendBroadcast(testBroadcast);

        // Retrieve the broadcast and ensure it's received by both MicroServices
        try {
            Message message1 = messageBus.awaitMessage(microService1);
            Message message2 = messageBus.awaitMessage(microService2);
            assertEquals(testBroadcast, message1);
            assertEquals(testBroadcast, message2);
        } catch (InterruptedException e) {
            fail("Unexpected interruption.");
        }
    }

    @Test(timeout = 5000)
    public void testCompleteEvent() {
        messageBus.register(microService1);
        ExampleEvent testEvent = new ExampleEvent("event2");

        // Subscribe microService1 to the event
        messageBus.subscribeEvent(testEvent.getClass(), microService1);

        // Send the event and get its Future
        Future<String> future = messageBus.sendEvent(testEvent);
        assertNotNull(future); // Ensure the Future is created

        try {
            Message message = messageBus.awaitMessage(microService1);
            assertEquals(testEvent, message);

            // Complete the event
            String result = "Completed!";
            messageBus.complete(testEvent, result);

            // Ensure the future is resolved
            assertEquals(result, future.get(1, TimeUnit.SECONDS));
        } catch (InterruptedException e) {
            fail("Unexpected interruption.");
        }
    }

    @Test(timeout = 5000)
    public void testSendEventToNoSubscriber() {
        ExampleEvent testEvent = new ExampleEvent("event3");

        // Send the event without any subscribers
        Future<String> future = messageBus.sendEvent(testEvent);

        // Ensure the future is null (no subscribers)
        assertNull(future);
    }
}*/

