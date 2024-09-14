package Example.Arguments;

import java.util.List;

public class ArgumentReader<T> {
    private int index;
    private List<String> args;
    private T argResult;
    private ArgumentResultUpdater<T> argUpdater;
    private ArgumentResultChecker<T> argChecker;

    public ArgumentReader(List<String> args, T argResult, ArgumentResultUpdater<T> argUpdater, ArgumentResultChecker<T> argChecker) {
        this.args = args;
        this.index = 0;
        this.argResult = argResult;
        this.argUpdater = argUpdater;
        this.argChecker = argChecker;
    }

    public boolean isFinished() { return index >= args.size(); }
    public String nextArg() { return args.get(index++); }

    public void readAllArgs() {
        while (!isFinished()) {
            String arg = nextArg();
            updateFromArgsInternal(arg, argResult);
        }
        if (argChecker != null)
            argChecker.checkArgs(argResult);
    }

    private void updateFromArgsInternal(String arg, T argResult) {
        if (arg.equals("-f")) {
            String fileName = nextArg();
            List<String> fileArgs = ArgFileReader.read(fileName);
            ArgumentReader<T> fileArgReader = new ArgumentReader<>(fileArgs, argResult, argUpdater, null);
            fileArgReader.readAllArgs();
        }
        else
            argUpdater.updateFromArgs(arg, argResult, this);
    }

    public static void error(String message) {
        System.out.println(message);
        System.exit(1);
    }
}
