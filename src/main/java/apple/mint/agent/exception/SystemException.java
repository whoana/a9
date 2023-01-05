package apple.mint.agent.exception;

public class SystemException extends Exception {
    
    public SystemException(){
        super();
    }

    public SystemException(Exception e){
        super(e);
    }

    public SystemException(String msg, Exception e){
        super(msg, e);
    }
}
