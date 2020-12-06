package worth.exceptions;

public class UserAlreadyPresent extends Throwable {
    public UserAlreadyPresent(String s) {
        super(s);
    }
    public UserAlreadyPresent() {
        super();
    }
}
