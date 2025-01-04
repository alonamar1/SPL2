

import java.util.concurrent.CountDownLatch;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.Event;
import bgu.spl.mics.MicroService;

public class testData {

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
                    terminate();

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