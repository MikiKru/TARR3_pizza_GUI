package pizza_gui.exam;

public class MultiThread {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Main Thread start");
        Thread thread = new Thread(() -> {
            System.out.println("T0 start");
            for (int i = 0; i < 5; i++) {
                System.out.println("Thread 0 " + i);
                try {
                    Thread.currentThread().sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Thread 0 stop");
        });
        thread.start();
        for (int i = 0; i < 5; i++) {
            System.out.println("Main Thread " + i);
            Thread.sleep(200);
        }
        System.out.println("Main Thread stop");
    }
}
