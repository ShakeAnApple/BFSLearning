package utils;

public class Tuple<T1, T2> {
    private T1 _obj1;
    private T2 _obj2;

    public Tuple(T1 obj1, T2 obj2){
        _obj1 = obj1;
        _obj2 = obj2;
    }

    public T1 getObj1() {
        return _obj1;
    }

    public T2 getObj2() {
        return _obj2;
    }

    @Override
    public String toString() {
        return "(" + _obj1.toString() + "; " + _obj2.toString() + ")";
    }

    @Override
    public boolean equals(Object obj) {
        Tuple<T1, T2> other = (Tuple<T1,T2>) obj;
        if (obj == null){
            return false;
        }

        return _obj1.equals(other._obj1) && _obj2.equals(other._obj2);
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }
}
