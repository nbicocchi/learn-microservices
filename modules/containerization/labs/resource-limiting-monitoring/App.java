public class App {
    public static void main(String[] args) {
        long n = 100; // Change this value for more intensive computation
        System.out.println("Fibonacci of " + n + " is " + fib(n));
    }

    public static long fib(long n) {
        if (n <= 1) return n;
        else return fib(n-1) + fib(n-2);
    }
}
