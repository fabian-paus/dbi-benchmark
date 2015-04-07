package my.benchmark;

public final class FixedData {
	public static final String HISTORY_COMMENT = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234";
	
	static {
		assert (HISTORY_COMMENT.length() == 30);
	}
}
