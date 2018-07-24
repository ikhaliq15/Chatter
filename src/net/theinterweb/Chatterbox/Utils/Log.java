package net.theinterweb.Chatterbox.Utils;

public class Log {

	public static final int NORMAL = 0;
	public static final int ERROR = 1;
	public static final int FATAL = 2;
	public static final int WARNING = 3;
	
	public static void log(String s, int type){
		String class_name = Thread.currentThread().getStackTrace()[2].getFileName();
		class_name = class_name.substring(0, class_name.length()-5);
		if (type == Log.NORMAL) {
			System.out.println("[" + class_name + "]: " + s.toUpperCase());
		} else if (type == Log.ERROR) {
			System.err.println("[" + class_name + "| ERROR]: " + s.toUpperCase());
		} else if (type == Log.WARNING) {
			System.out.println("[" + class_name + " | WARNING]: " + s.toUpperCase());
		} else if (type == Log.FATAL) {
			System.err.println("[" + class_name + " | FATAL]: " + s.toUpperCase());
			System.exit(0);
		}
	}
}
