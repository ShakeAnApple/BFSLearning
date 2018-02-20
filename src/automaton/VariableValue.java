package automaton;

import values.ValueHandler;

import java.io.Serializable;
import java.util.function.Supplier;

public class VariableValue<VHandlerType extends ValueHandler> implements Serializable {
    private String _name;
    private VHandlerType _value;
    private int _order;

    private transient Supplier<VHandlerType> _supplier;

//    public VariableValue(String name) {
//        _name = name;
//    }

    public VariableValue(String name, int order, Supplier<VHandlerType> supplier) {
        _name = name;
        _order = order;

        _supplier = supplier;

        _value = _supplier.get();
    }

    public VariableValue clone(){
        VariableValue res = new VariableValue<VHandlerType>(_name, _order, _supplier);
        res._value = _value.clone();
        return res;
    }

    public int getOrder(){
        return _order;
    }

    public void setValue(VHandlerType value){
        _value = value;
    }

    public void parseAndSetValue(Object val) throws Exception {
        _value.parseAndSetValue(val);
    }

    public VHandlerType getValue(){
        return _value;
    }

    public VariableValue<VHandlerType> createInstance(String name){
        return new VariableValue<VHandlerType>(name, _order, _supplier);
    }

    public String getName(){
        return _name;
    }

    @Override
    public String toString() {
        return String.format("%1$s: %2$s", _name, _value);
    }

    @Override
    public boolean equals(Object obj) {
        VariableValue other = (VariableValue)obj;

        if (_name != other._name){
            return false;
        }

        return _value.equals(other._value);
    }
}
