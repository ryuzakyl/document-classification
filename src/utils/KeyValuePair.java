package utils;

/**
 * Created with IntelliJ IDEA.
 * User: DaniMesejo
 * Date: 1/14/13
 * Time: 9:31 AM
 * To change this template use File | Settings | File Templates.
 */
public class KeyValuePair<T, R>
{
    private T key;
    private R value;

    public KeyValuePair(T key, R value)
    {
        this.key = key;
        this.value = value;
    }

    public T getKey()
    {
        return key;
    }

    public R getValue()
    {
        return value;
    }
}
