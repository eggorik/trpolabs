import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Matrix a = new Matrix(new double[][]{
                {2, 1, 3},
                {0, -1, 4},
                {1, 2, 0}
        });
        Matrix b = new Matrix(new double[][]{
                {1, 0, 2},
                {3, 1, -1},
                {0, 5, 1}
        });

        System.out.println("Матрица A:\n" + a);
        System.out.println("Матрица B:\n" + b);

        System.out.println("A + B:\n" + a.add(b));
        System.out.println("A - B:\n" + a.subtract(b));
        System.out.println("A * 2:\n" + a.multiply(2));
        System.out.println("A * B:\n" + a.multiply(b));
        System.out.println("A^T:\n" + a.transpose());
        System.out.println("A^3:\n" + a.power(3));
        System.out.println("det(A) = " + a.determinant());
        System.out.println("trace(A) = " + a.trace());
        System.out.println("rank(A) = " + a.rank());

        Matrix inv = a.inverse();
        System.out.println("\nA^-1:\n" + inv);
        System.out.println("A * A^-1 == E: " + a.multiply(inv).equals(Matrix.identity(3)));

        System.out.println("\n--- Изменение элементов и размерности ---");
        Matrix c = new Matrix(a);
        c.set(0, 0, 10);
        System.out.println("Копия A, c[0][0] = 10:\n" + c);
        System.out.println("Исходная A не изменилась: a[0][0] = " + a.get(0, 0));
        c.swapRows(0, 2);
        System.out.println("\nПосле перестановки строк 0 и 2:\n" + c);
        c.addRow(new double[]{7, 8, 9});
        System.out.println("После добавления строки (" + c.getRows() + "x" + c.getCols() + "):\n" + c);
        c.addColumn(1);
        System.out.println("После добавления столбца (" + c.getRows() + "x" + c.getCols() + "):\n" + c);
        c.removeRow(1);
        c.removeColumn(0);
        System.out.println("После удаления строки 1 и столбца 0 (" + c.getRows() + "x" + c.getCols() + "):\n" + c);
        c.resize(2, 2);
        System.out.println("После resize(2, 2):\n" + c);

        System.out.println("--- Сохранение и загрузка ---");
        a.saveText("matrix.txt");
        a.saveBinary("matrix.bin");
        Matrix fromText = Matrix.loadText("matrix.txt");
        Matrix fromBin = Matrix.loadBinary("matrix.bin");
        System.out.println("Из текстового файла:\n" + fromText);
        System.out.println("Из двоичного файла:\n" + fromBin);
        System.out.println("Совпадают с A: " + (a.equals(fromText) && a.equals(fromBin)));

        System.out.println("\n--- Ошибки ---");
        try {
            a.multiply(new Matrix(2, 2));
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
        try {
            new Matrix(new double[][]{{1, 2}, {2, 4}}).inverse();
        } catch (ArithmeticException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }

        try (Matrix tmp = new Matrix(3, 3)) {
            tmp.set(1, 1, 5);
            System.out.println("\nВременная матрица до close():\n" + tmp);
        }
        System.out.println("Временная матрица освобождена через close() (try-with-resources)");
    }
}
