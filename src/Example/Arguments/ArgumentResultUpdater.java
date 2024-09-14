package Example.Arguments;

public interface ArgumentResultUpdater<T> {
    void updateFromArgs(String arg, T argResult, ArgumentReader<T> argumentReader);
}
