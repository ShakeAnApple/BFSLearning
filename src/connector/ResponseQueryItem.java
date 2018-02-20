package connector;

import automaton.State;
import automaton.Symbol;
import impl.SingleRequest;

import java.util.UUID;

/**
 * Created by Eskimos on 11.01.2018.
 */
public class ResponseQueryItem{
    private State _endState;
    private State _startState;
    private SingleRequest[] _seq;

    private UUID _id;


    public ResponseQueryItem(State startState, State endState, SingleRequest[] seq ){
        _startState = startState;
        _endState = endState;
        _seq = seq;
        _id = UUID.randomUUID();
    }

    public ResponseQueryItem(UUID id, State startState, State endState, SingleRequest[] seq ){
        _startState = startState;
        _endState = endState;
        _seq = seq;
        _id = id;
    }

    public State getStartState() {
        return _startState;
    }

    public State getEndState(){
        return _endState;
    }

    public void changeEndState(State newState){
        _endState = newState;
    }

    public SingleRequest[] getSequence(){
        return _seq;
    }

    public UUID getId() {
        return _id;
    }
}
