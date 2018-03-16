package automaton;

import values.IntervalValueHandler;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class State implements Serializable{
    private boolean _isStart;
    private StateValue _stateValue;

    public State(StateValue value, boolean isStart) {
        _isStart = isStart;

        _stateValue = value;
    }

    public StateValue getStateValue(){
        return _stateValue;
    }

    public boolean isStart(){
        return _isStart;
    }

    @Override
    public int hashCode() {
        StringBuilder sb = new StringBuilder();
        List<VariableValue> orderedValues = _stateValue
                .getSymbol()
                .getVariablesValues()
                .stream()
                .sorted(Comparator.comparing(VariableValue::getOrder))
                .collect(Collectors.toList());
        for(VariableValue v: orderedValues){
            if (v.getValue() instanceof IntervalValueHandler){
                sb.append(String.format("%1$1s: %2$s; ", v.getName(), ((IntervalValueHandler)(v.getValue())).getCurrentIntervalNum()));
            } else{
                sb.append(String.format("%1$1s: %2$s; ", v.getName(), v.getValue()));
            }
        }
        sb.append("isStart: " + _isStart);
        return sb.toString().hashCode();
    }

    @Override
    public String toString() {
        return _stateValue.toString();
    }

    @Override
    public boolean equals(Object obj) {
        State other = (State)obj;
        return this._stateValue.equals(other._stateValue) && this._isStart == other._isStart;
    }

    public void setIsStart(boolean isStart) {
        _isStart = isStart;
    }
}
