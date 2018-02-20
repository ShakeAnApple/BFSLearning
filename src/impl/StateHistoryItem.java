package impl;

import java.util.List;

public class StateHistoryItem {
    private List<VariableHistoryItem> _variableHistoryItems;
    private int _iterationsCount;

    public StateHistoryItem(List<VariableHistoryItem> variableHistoryItems, int iterationsCount){
        _variableHistoryItems = variableHistoryItems;
        _iterationsCount = iterationsCount;
    }

    public int getIterationsCount() {
        return _iterationsCount;
    }

    public List<VariableHistoryItem> getVariableHistoryItems(){
        return _variableHistoryItems;
    }
}
