package impl;

import automaton.VariableValue;
import values.IntervalValueHandler;

public class VariableHistoryItem {
    private VariableValue<IntervalValueHandler> _variableValue;
    private double _pearsonCoefficient;

    public VariableHistoryItem(VariableValue<IntervalValueHandler> variableValue){
        _variableValue = variableValue;
    }

    public IntervalValueHandler getValue(){
        return _variableValue.getValue();
    }

    public void setPearsonCoefficient(double pearsonCoefficient) {
        _pearsonCoefficient = pearsonCoefficient;
    }

    public double getPearsonCoefficient() {
        return _pearsonCoefficient;
    }

    public boolean isGreaterOrEqualCoefficient(VariableHistoryItem otherItem){
        return (_pearsonCoefficient - otherItem._pearsonCoefficient > 0.1);
    }
}
