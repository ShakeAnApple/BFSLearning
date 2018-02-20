package automaton;

import java.io.Serializable;

public class StateValue implements Serializable {
    private Symbol _value;

    public StateValue(Symbol value) {
        _value = value;
    }

    public Symbol getSymbol(){
        return _value;
    }

    @Override
    public String toString() {
        return String.format("%1$s", _value);
    }

    @Override
    public boolean equals(Object obj) {
        StateValue other = (StateValue)obj;
        return _value.equals(other._value);
    }
}
