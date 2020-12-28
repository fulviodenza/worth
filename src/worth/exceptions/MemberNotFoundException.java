package worth.exceptions;

public class MemberNotFoundException extends Throwable {
    public MemberNotFoundException(String s) {
        super(s);
    }
    public MemberNotFoundException() {
        super();
    }
}
