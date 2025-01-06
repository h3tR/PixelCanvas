package ml.jozefpeeterslaan72wuustwezel;

import ml.jozefpeeterslaan72wuustwezel.jade.Window;

public class Main {
    public static void main(String[] args) {
        if(args.length>0)
        {
            //TODO: Window.setDimensions(width,height);
        }
        Window window = Window.get();
        window.run();
    }
}