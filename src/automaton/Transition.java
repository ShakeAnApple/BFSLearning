package automaton;


import java.io.Serializable;

public class Transition implements Serializable{

    private State _from;
    private State _to;
    private Symbol _symbol;
    private int _repeatCount;

    public Transition(State from, State to, Symbol symbol, int repeatCount) {
        _from = from;
        _to = to;
        _symbol = symbol;
        _repeatCount = repeatCount;
    }

    public State getFrom(){
        return _from;
    }

    public Symbol getSymbol(){
        return _symbol;
    }

    public State getTo(){
        return _to;
    }

    public int getRepeatCount(){
        return _repeatCount;
    }
}
