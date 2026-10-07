import java.io.*;
import java.util.Locale;
import java.util.Scanner;

public class Matrix implements AutoCloseable {
    private static final double EPS = 1e-10;

    private double[][] rows;
    private int nRows;
    private int nCols;

    public Matrix() {
        this(0, 0);
    }

    public Matrix(int nRows, int nCols) {
        if (nRows < 0 || nCols < 0) {
            throw new IllegalArgumentException("Размеры матрицы не могут быть отрицательными");
        }
        this.nRows = nRows;
        this.nCols = nCols;
        this.rows = new double[nRows][];
        for (int i = 0; i < nRows; i++) {
            rows[i] = new double[nCols];
        }
    }

    public Matrix(double[][] data) {
        this(data.length, data.length == 0 ? 0 : data[0].length);
        for (int i = 0; i < nRows; i++) {
            if (data[i].length != nCols) {
                throw new IllegalArgumentException("Строки массива имеют разную длину");
            }
            System.arraycopy(data[i], 0, rows[i], 0, nCols);
        }
    }

    public Matrix(Matrix other) {
        this(other.nRows, other.nCols);
        for (int i = 0; i < nRows; i++) {
            System.arraycopy(other.rows[i], 0, rows[i], 0, nCols);
        }
    }

    public static Matrix identity(int n) {
        Matrix m = new Matrix(n, n);
        for (int i = 0; i < n; i++) {
            m.rows[i][i] = 1;
        }
        return m;
    }

    @Override
    public void close() {
        if (rows != null) {
            for (int i = 0; i < rows.length; i++) {
                rows[i] = null;
            }
        }
        rows = new double[0][];
        nRows = 0;
        nCols = 0;
    }

    public int getRows() {
        return nRows;
    }

    public int getCols() {
        return nCols;
    }

    public double get(int i, int j) {
        checkIndex(i, j);
        return rows[i][j];
    }

    public void set(int i, int j, double value) {
        checkIndex(i, j);
        rows[i][j] = value;
    }

    public double[] getRow(int i) {
        checkRow(i);
        return rows[i].clone();
    }

    public void setRow(int i, double[] row) {
        checkRow(i);
        if (row.length != nCols) {
            throw new IllegalArgumentException("Длина строки должна быть " + nCols);
        }
        rows[i] = row.clone();
    }

    public void swapRows(int i, int k) {
        checkRow(i);
        checkRow(k);
        double[] tmp = rows[i];
        rows[i] = rows[k];
        rows[k] = tmp;
    }

    public void insertRow(int pos, double[] row) {
        if (pos < 0 || pos > nRows) {
            throw new IndexOutOfBoundsException("Неверная позиция строки: " + pos);
        }
        if (nRows == 0 && nCols == 0) {
            nCols = row.length;
        }
        if (row.length != nCols) {
            throw new IllegalArgumentException("Длина строки должна быть " + nCols);
        }
        double[][] newRows = new double[nRows + 1][];
        System.arraycopy(rows, 0, newRows, 0, pos);
        newRows[pos] = row.clone();
        System.arraycopy(rows, pos, newRows, pos + 1, nRows - pos);
        rows = newRows;
        nRows++;
    }

    public void addRow(double[] row) {
        insertRow(nRows, row);
    }

    public void removeRow(int pos) {
        checkRow(pos);
        double[][] newRows = new double[nRows - 1][];
        System.arraycopy(rows, 0, newRows, 0, pos);
        System.arraycopy(rows, pos + 1, newRows, pos, nRows - pos - 1);
        rows = newRows;
        nRows--;
        if (nRows == 0) {
            nCols = 0;
        }
    }

    public void addColumn(double value) {
        for (int i = 0; i < nRows; i++) {
            double[] r = new double[nCols + 1];
            System.arraycopy(rows[i], 0, r, 0, nCols);
            r[nCols] = value;
            rows[i] = r;
        }
        nCols++;
    }

    public void removeColumn(int pos) {
        if (pos < 0 || pos >= nCols) {
            throw new IndexOutOfBoundsException("Неверный индекс столбца: " + pos);
        }
        for (int i = 0; i < nRows; i++) {
            double[] r = new double[nCols - 1];
            System.arraycopy(rows[i], 0, r, 0, pos);
            System.arraycopy(rows[i], pos + 1, r, pos, nCols - pos - 1);
            rows[i] = r;
        }
        nCols--;
    }

    public void resize(int newRows, int newCols) {
        if (newRows < 0 || newCols < 0) {
            throw new IllegalArgumentException("Размеры матрицы не могут быть отрицательными");
        }
        double[][] r = new double[newRows][];
        for (int i = 0; i < newRows; i++) {
            r[i] = new double[newCols];
            if (i < nRows) {
                System.arraycopy(rows[i], 0, r[i], 0, Math.min(nCols, newCols));
            }
        }
        rows = r;
        nRows = newRows;
        nCols = newCols;
    }

    public Matrix add(Matrix other) {
        checkSameSize(other);
        Matrix res = new Matrix(nRows, nCols);
        for (int i = 0; i < nRows; i++) {
            for (int j = 0; j < nCols; j++) {
                res.rows[i][j] = rows[i][j] + other.rows[i][j];
            }
        }
        return res;
    }

    public Matrix subtract(Matrix other) {
        checkSameSize(other);
        Matrix res = new Matrix(nRows, nCols);
        for (int i = 0; i < nRows; i++) {
            for (int j = 0; j < nCols; j++) {
                res.rows[i][j] = rows[i][j] - other.rows[i][j];
            }
        }
        return res;
    }

    public Matrix multiply(double k) {
        Matrix res = new Matrix(nRows, nCols);
        for (int i = 0; i < nRows; i++) {
            for (int j = 0; j < nCols; j++) {
                res.rows[i][j] = rows[i][j] * k;
            }
        }
        return res;
    }

    public Matrix multiply(Matrix other) {
        if (nCols != other.nRows) {
            throw new IllegalArgumentException("Нельзя умножить матрицы " + sizeStr() + " и " + other.sizeStr());
        }
        Matrix res = new Matrix(nRows, other.nCols);
        for (int i = 0; i < nRows; i++) {
            for (int k = 0; k < nCols; k++) {
                double a = rows[i][k];
                for (int j = 0; j < other.nCols; j++) {
                    res.rows[i][j] += a * other.rows[k][j];
                }
            }
        }
        return res;
    }

    public Matrix transpose() {
        Matrix res = new Matrix(nCols, nRows);
        for (int i = 0; i < nRows; i++) {
            for (int j = 0; j < nCols; j++) {
                res.rows[j][i] = rows[i][j];
            }
        }
        return res;
    }

    public Matrix power(int p) {
        checkSquare();
        if (p < 0) {
            throw new IllegalArgumentException("Степень должна быть неотрицательной");
        }
        Matrix result = identity(nRows);
        Matrix base = new Matrix(this);
        while (p > 0) {
            if ((p & 1) == 1) {
                result = result.multiply(base);
            }
            base = base.multiply(base);
            p >>= 1;
        }
        return result;
    }

    public double determinant() {
        checkSquare();
        Matrix m = new Matrix(this);
        double det = 1;
        for (int c = 0; c < nRows; c++) {
            int pivot = m.findPivot(c, c);
            if (Math.abs(m.rows[pivot][c]) < EPS) {
                return 0;
            }
            if (pivot != c) {
                m.swapRows(pivot, c);
                det = -det;
            }
            det *= m.rows[c][c];
            for (int i = c + 1; i < nRows; i++) {
                double f = m.rows[i][c] / m.rows[c][c];
                for (int j = c; j < nCols; j++) {
                    m.rows[i][j] -= f * m.rows[c][j];
                }
            }
        }
        return det;
    }

    public Matrix inverse() {
        checkSquare();
        int n = nRows;
        Matrix a = new Matrix(this);
        Matrix inv = identity(n);
        for (int c = 0; c < n; c++) {
            int pivot = a.findPivot(c, c);
            if (Math.abs(a.rows[pivot][c]) < EPS) {
                throw new ArithmeticException("Матрица вырожденная, обратной не существует");
            }
            a.swapRows(pivot, c);
            inv.swapRows(pivot, c);
            double d = a.rows[c][c];
            for (int j = 0; j < n; j++) {
                a.rows[c][j] /= d;
                inv.rows[c][j] /= d;
            }
            for (int i = 0; i < n; i++) {
                if (i != c) {
                    double f = a.rows[i][c];
                    for (int j = 0; j < n; j++) {
                        a.rows[i][j] -= f * a.rows[c][j];
                        inv.rows[i][j] -= f * inv.rows[c][j];
                    }
                }
            }
        }
        return inv;
    }

    public int rank() {
        Matrix m = new Matrix(this);
        int rank = 0;
        for (int c = 0; c < nCols && rank < nRows; c++) {
            int pivot = m.findPivot(rank, c);
            if (Math.abs(m.rows[pivot][c]) < EPS) {
                continue;
            }
            m.swapRows(pivot, rank);
            for (int i = rank + 1; i < nRows; i++) {
                double f = m.rows[i][c] / m.rows[rank][c];
                for (int j = c; j < nCols; j++) {
                    m.rows[i][j] -= f * m.rows[rank][j];
                }
            }
            rank++;
        }
        return rank;
    }

    public double trace() {
        checkSquare();
        double s = 0;
        for (int i = 0; i < nRows; i++) {
            s += rows[i][i];
        }
        return s;
    }

    public void writeText(Writer out) throws IOException {
        PrintWriter pw = new PrintWriter(out);
        pw.println(nRows + " " + nCols);
        for (int i = 0; i < nRows; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < nCols; j++) {
                if (j > 0) {
                    sb.append(' ');
                }
                sb.append(rows[i][j]);
            }
            pw.println(sb);
        }
        pw.flush();
    }

    public static Matrix readText(Reader in) {
        Scanner sc = new Scanner(in).useLocale(Locale.US);
        int r = sc.nextInt();
        int c = sc.nextInt();
        Matrix m = new Matrix(r, c);
        for (int i = 0; i < r; i++) {
            for (int j = 0; j < c; j++) {
                m.rows[i][j] = sc.nextDouble();
            }
        }
        return m;
    }

    public void writeBinary(OutputStream out) throws IOException {
        DataOutputStream dos = new DataOutputStream(out);
        dos.writeInt(nRows);
        dos.writeInt(nCols);
        for (int i = 0; i < nRows; i++) {
            for (int j = 0; j < nCols; j++) {
                dos.writeDouble(rows[i][j]);
            }
        }
        dos.flush();
    }

    public static Matrix readBinary(InputStream in) throws IOException {
        DataInputStream dis = new DataInputStream(in);
        int r = dis.readInt();
        int c = dis.readInt();
        Matrix m = new Matrix(r, c);
        for (int i = 0; i < r; i++) {
            for (int j = 0; j < c; j++) {
                m.rows[i][j] = dis.readDouble();
            }
        }
        return m;
    }

    public void saveText(String fileName) throws IOException {
        try (Writer w = new FileWriter(fileName)) {
            writeText(w);
        }
    }

    public static Matrix loadText(String fileName) throws IOException {
        try (Reader r = new FileReader(fileName)) {
            return readText(r);
        }
    }

    public void saveBinary(String fileName) throws IOException {
        try (OutputStream os = new BufferedOutputStream(new FileOutputStream(fileName))) {
            writeBinary(os);
        }
    }

    public static Matrix loadBinary(String fileName) throws IOException {
        try (InputStream is = new BufferedInputStream(new FileInputStream(fileName))) {
            return readBinary(is);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < nRows; i++) {
            sb.append("| ");
            for (int j = 0; j < nCols; j++) {
                sb.append(String.format(Locale.US, "%9.3f ", rows[i][j]));
            }
            sb.append("|\n");
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Matrix other) || nRows != other.nRows || nCols != other.nCols) {
            return false;
        }
        for (int i = 0; i < nRows; i++) {
            for (int j = 0; j < nCols; j++) {
                if (Math.abs(rows[i][j] - other.rows[i][j]) > EPS) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return 31 * nRows + nCols;
    }

    private int findPivot(int fromRow, int col) {
        int pivot = fromRow;
        for (int i = fromRow + 1; i < nRows; i++) {
            if (Math.abs(rows[i][col]) > Math.abs(rows[pivot][col])) {
                pivot = i;
            }
        }
        return pivot;
    }

    private String sizeStr() {
        return nRows + "x" + nCols;
    }

    private void checkRow(int i) {
        if (i < 0 || i >= nRows) {
            throw new IndexOutOfBoundsException("Неверный индекс строки: " + i);
        }
    }

    private void checkIndex(int i, int j) {
        checkRow(i);
        if (j < 0 || j >= nCols) {
            throw new IndexOutOfBoundsException("Неверный индекс столбца: " + j);
        }
    }

    private void checkSameSize(Matrix other) {
        if (nRows != other.nRows || nCols != other.nCols) {
            throw new IllegalArgumentException("Размеры матриц не совпадают: " + sizeStr() + " и " + other.sizeStr());
        }
    }

    private void checkSquare() {
        if (nRows != nCols) {
            throw new ArithmeticException("Операция определена только для квадратной матрицы");
        }
    }
}
