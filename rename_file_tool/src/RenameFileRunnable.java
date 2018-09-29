import java.io.File;

public class RenameFileRunnable implements  Runnable {

    private File[] files;
    private Callback callback;
    private String rule;
    private int startNum;

    public RenameFileRunnable(File[] files, int startNum, Callback callback, String rule){
        this.files = files;
        this.callback = callback;
        this.rule = rule;
        this.startNum = startNum;
    }

    @Override
    public void run() {
        for (File f: files) {
            try{
                String newFileName = rule.replace("%num", String.valueOf(startNum++));
                f.renameTo(new File(f.getParentFile(), newFileName));
                callback.success();
            }catch (Exception ex){
                callback.fail();
            }
        }
    }
}
