package connector;

import automaton.State;
import automaton.Symbol;
import impl.SingleRequest;

import java.util.UUID;

/**
 * Created by Eskimos on 11.01.2018.
 */
public class RequestQueryItem {
    private State _state;
    private SingleRequest[] _seq;
    private UUID _id;

    public RequestQueryItem(State state, SingleRequest[] seq ){
        _state = state;
        _seq = seq;
        _id = UUID.randomUUID();
    }

    public RequestQueryItem(UUID id, State state, SingleRequest[] seq ){
        _state = state;
        _seq = seq;
        _id = id;
    }

    public UUID getId() {
        return _id;
    }

    public State getState(){
        return _state;
    }

    public SingleRequest[] getSequence(){
        return _seq;
    }
}
