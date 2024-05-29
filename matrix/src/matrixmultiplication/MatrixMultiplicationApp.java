package matrixmultiplication;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * MatrixMultiplicationApp, iki matrisin çarpılmasını sağlayan ve hesaplama işlemini paralel olarak gerçekleştiren bir uygulamadır.
 * Bu uygulama, Java komut satırından çalıştırılır ve matris verilerini dosyalardan veya rastgele oluşturarak alabilir.
 */
public class MatrixMultiplicationApp {
    /**
     * Uygulamanın ana metodu.
     * @param args Komut satırı argümanları: <matrix1_file> <matrix2_file> <thread_count>
     *             veya random random <matrix1_row> <matrix1_col> <matrix2_row> <matrix2_col> <thread_count>
     */
    public static void main(String[] args) {
        if (args.length < 3) {
            printUsage();
            return;
        }

        String matrix1File = args[0];
        String matrix2File = args[1];
        int threadCount = Integer.parseInt(args[2]);

        int[][] matrix1;
        int[][] matrix2;

        if (matrix1File.equals("random") && matrix2File.equals("random")) {
            if (args.length != 7) {
                printUsage();
                return;
            }
            int matrix1Row = Integer.parseInt(args[3]);
            int matrix1Col = Integer.parseInt(args[4]);
            int matrix2Row = Integer.parseInt(args[5]);
            int matrix2Col = Integer.parseInt(args[6]);

            matrix1 = generateRandomMatrix(matrix1Row, matrix1Col);
            matrix2 = generateRandomMatrix(matrix2Row, matrix2Col);
        } else {
            matrix1 = readMatrixFromFile(matrix1File);
            matrix2 = readMatrixFromFile(matrix2File);

            if (!isValidMatrix(matrix1) || !isValidMatrix(matrix2) || !areCompatibleForMultiplication(matrix1, matrix2)) {
                System.err.println("Matrislerin çarpılabilmesi için uygun boyutlarda olmaları gerekir.");
                return;
            }
        }

        long startTime = System.currentTimeMillis();
        MatrixMultiplier multiplier = new MatrixMultiplier(matrix1, matrix2, threadCount);
        int[][] result = multiplier.multiply();
        long endTime = System.currentTimeMillis();

        printMatrix(result);

        long totalTime = endTime - startTime;
        System.out.println("Toplam işlem süresi: " + totalTime + " milisaniye");
    }

    /**
     * Kullanım talimatlarını konsola yazdırır.
     */
    private static void printUsage() {
        System.err.println("Kullanım: java MatrixMultiplicationApp <matrix1_file> <matrix2_file> <thread_count>");
        System.err.println("          java MatrixMultiplicationApp random random <matrix1_row> <matrix1_col> <matrix2_row> <matrix2_col> <thread_count>");
    }

    /**
     * Belirtilen dosyadan matris verisini okur.
     * @param filename Dosya adı
     * @return Matris verisi
     */
    private static int[][] readMatrixFromFile(String filename) {
        List<int[]> rows = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.trim().split(",");
                int[] row = new int[values.length];
                for (int i = 0; i < values.length; i++) {
                    row[i] = Integer.parseInt(values[i]);
                }
                rows.add(row);
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
            return null;
        }

        int[][] matrix = new int[rows.size()][];
        for (int i = 0; i < rows.size(); i++) {
            matrix[i] = rows.get(i);
        }
        return matrix;
    }

    /**
     * Verilen boyutlarda rastgele bir matris oluşturur.
     * @param rows Satır sayısı
     * @param cols Sütun sayısı
     * @return Oluşturulan rastgele matris
     */
    private static int[][] generateRandomMatrix(int rows, int cols) {
        int[][] matrix = new int[rows][cols];
        Random random = new Random();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                matrix[i][j] = random.nextInt(100);
            }
        }
        return matrix;
    }

    /**
     * Verilen matrisin geçerli olup olmadığını kontrol eder.
     * @param matrix Kontrol edilecek matris
     * @return Matris geçerliyse true, değilse false
     */
    private static boolean isValidMatrix(int[][] matrix) {
        if (matrix == null || matrix.length == 0 || matrix[0] == null || matrix[0].length == 0) {
            return false;
        }
        return true;
    }

    /**
     * İki matrisin çarpılabilir olup olmadığını kontrol eder.
     * @param matrix1 Birinci matris
     * @param matrix2 İkinci matris
     * @return Matrisler çarpılabilirse true, değilse false
     */
    private static boolean areCompatibleForMultiplication(int[][] matrix1, int[][] matrix2) {
        return matrix1[0].length == matrix2.length;
    }

    /**
     * Verilen matrisi konsola yazdırır.
     * @param matrix Yazdırılacak matris
     */
    private static void printMatrix(int[][] matrix) {
        for (int[] row : matrix) {
            for (int val : row) {
                System.out.print(val + " ");
            }
            System.out.println();
        }
    }
}
