import org.paninij.runtime.check.Ownership;

public class IT {
    public static void main(String[] args) {
        System.out.println("Hello, from a `main()` method.");
        Ownership.move(null, null, null);
    }
}