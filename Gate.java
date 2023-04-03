public class Gate {
    private static final int CLOSED = 0;
    private static final int OPENED = 1;
    private Bar bar = new Bar();
    private TicketEntry entry = new TicketEntry();
    private int state = CLOSED;
    private int state = OPENED;

    public void pass(){
        switch(state){
            case CLOSED:
            bar.alarm();
            case OPENED:
            bar.close();
            default:
            break;
        }
    }

    public void ticket(){
        switch(state){
            case CLOSED:
            bar.open();
            case OPENED:
            entry.reject();
            default:
            break;
        }
    }
}
