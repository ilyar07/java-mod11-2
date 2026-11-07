import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class PoolExample {

    public static void main(String[] args) throws InterruptedException {

        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                3, 3, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<>(3));

        AtomicInteger count = new AtomicInteger(0);
        AtomicInteger inProgress = new AtomicInteger(0);

        for (int i = 0; i < 30; i++) {
            final int number = i;

            while (executor.getQueue().remainingCapacity() == 0) {
                Thread.sleep(10);
            }

            System.out.println("creating #" + number);

            // Используем execute вместо submit (нам не нужен Future, это быстрее)
            executor.submit(() -> {
                int working = inProgress.incrementAndGet();
                System.out.println("start #" + number + ", in progress: " + working);
                try {
                    Thread.sleep(Math.round(1000 + Math.random() * 2000));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                working = inProgress.decrementAndGet();
                System.out.println("end #" + number + ", in progress: " + working + ", done tasks: " + count.incrementAndGet());
            });
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        System.out.println("All tasks completed!");
    }
}