package vars.queryfx.messages;

/**
 * @author Brian Schlining
 * @since 2015-07-19T13:38:00
 */
public class FatalExceptionMsg extends AbstractExceptionMsg  {


    public FatalExceptionMsg(String msg, Exception exception) {
        super(msg, exception);
    }

}
