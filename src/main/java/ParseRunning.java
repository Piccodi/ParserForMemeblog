import java.util.Timer;

public class ParseRunning {
    public void ParseStart(){
        try{
            var timerTask = new Parser();
            var timer = new Timer(false);
            timer.scheduleAtFixedRate(timerTask, 0, 3600*1000);
        }
        catch (Exception e){e.printStackTrace();}
    }
}
