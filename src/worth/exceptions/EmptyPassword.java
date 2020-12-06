package worth.exceptions;

public class EmptyPassword extends Throwable{
    public EmptyPassword () {
        super();
    }
    public EmptyPassword(String s) {
        super(s);
    }
}
