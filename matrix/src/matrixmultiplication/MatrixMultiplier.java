package matrixmultiplication;

import java.util.concurrent.*;
import java.util.ArrayList;
import java.util.List;

public class MatrixMultiplier {
    private final int[][] matrix1;
    private final int[][] matrix2;
    private final int[][] result;
    private final int threadCount;
    private final List<Long> threadTimes;

    public MatrixMultiplier(int[][] matrix1, int[][] matrix2, int threadCount) {
        this.matrix1 = matrix1;
        this.matrix2 = matrix2;
        this.threadCount = threadCount;
        this.result = new int[matrix1.length][matrix2[0].length];
        this.threadTimes = new ArrayList<>();
    }

    public int[][] multiply() {
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        long startTime = System.currentTimeMillis();

        try {
            CountDownLatch latch = new CountDownLatch(matrix1.length * matrix2[0].length);

            for (int i = 0; i < matrix1.length; i++) {
                for (int j = 0; j < matrix2[0].length; j++) {
                    int finalI = i;
                    int finalJ = j;
                    executor.submit(() -> {
                        long threadStartTime = System.currentTimeMillis(); // Başlangıç zamanı
                        multiplyRowByColumn(finalI, finalJ);
                        long threadEndTime = System.currentTimeMillis(); // Bitiş zamanı
                        synchronized (threadTimes) {
                            threadTimes.add(threadEndTime - threadStartTime);
                        }
                        latch.countDown();
                    });
                }
            }

            // Tüm iş parçacıklarının tamamlanmasını bekleyin
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Toplam işlem süresi: " + totalTime + " milisaniye");

        // Toplam işlem süresini iş parçacıklarının süreleri ile güncelle
        for (Long threadTime : threadTimes) {
            totalTime += threadTime;
        }

        // Her iş parçacığının çalışma süresini yazdır
        for (int i = 0; i < threadTimes.size(); i++) {
            System.out.println("İş Parçacığı " + (i + 1) + " çalışma süresi: " + threadTimes.get(i) + " milisaniye");
        }

        System.out.println("Güncellenmiş toplam işlem süresi: " + totalTime + " milisaniye");

        return result;
    }

    private void multiplyRowByColumn(int row, int col) {
        for (int k = 0; k < matrix1[0].length; k++) {
            result[row][col] += matrix1[row][k] * matrix2[k][col];
        }
    }
}
