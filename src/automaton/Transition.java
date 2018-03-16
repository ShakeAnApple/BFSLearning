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

    @Override
    public String toString() {
        return String.format("From: %1$s To: %2$s By: %3$s RC: %4$s", _from, _to, _symbol, _repeatCount);
    }

    @Override
    public boolean equals(Object obj) {
        Transition other = (Transition)obj;
        return this._from.equals(other._from) &&
                this._to.equals(other._to) &&
                this._symbol.equals(other._symbol) &&
                this._repeatCount == other._repeatCount;
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }
}
