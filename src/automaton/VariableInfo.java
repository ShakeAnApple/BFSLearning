package automaton;

import values.ValueHandler;

import java.util.List;
import java.util.function.Supplier;

public class VariableInfo<VType extends ValueHandler> {
    private String _name;
    private List<VType> _possibleValues;
    private Supplier<VType> _supplier;
    private int _order;

//    public VariableInfo(String name, List<VType> possibleValues) {
//        _name = name;
//        _possibleValues = possibleValues;
//    }

    public VariableInfo(String name, int order, List<VType> possibleValues, Supplier<VType> supplier) {
        _name = name;
        _possibleValues = possibleValues;
        _supplier = supplier;
        _order = order;
    }

    public int getOrder() {
        return _order;
    }

    public VariableValue<VType> createVariableValue(){
        return new VariableValue<VType>(_name, _order, _supplier);
    }

    public String getName(){
        return _name;
    }

    public List<VType> getPossibleValues(){
        return _possibleValues;
    }
}
