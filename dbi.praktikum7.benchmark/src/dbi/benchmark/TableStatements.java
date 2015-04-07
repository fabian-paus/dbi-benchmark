package dbi.benchmark;

/**
 * Diese Klasse enthält Konstanten für die CREATE TABLE und DROP TABLE
 * Anweisungen für die zu befüllende Datenbank.
 *
 */
public final class TableStatements {
	public static final String CREATE_BRANCHES = "create table branches( "
			+ "branchid int not null, "
			+ "branchname char(20) not null, "
			+ "balance int not null, "
			+ "address char(72) not null, "
			+ "primary key (branchid) );";
	
	public static final String CREATE_ACCOUNTS = "create table accounts( "
			+ "accid int not null, "
			+ "name char(20) not null, "
			+ "balance int not null, "
			+ "branchid int not null, "
			+ "address char(68) not null, "
			+ "primary key (accid), "
			+ "foreign key (branchid) references branches );";
	
	public static final String CREATE_TELLERS = "create table tellers( "
			+ "tellerid int not null, "
			+ "tellername char(20) not null, "
			+ "balance int not null, "
			+ "branchid int not null, "
			+ "address char(68) not null, "
			+ "primary key (tellerid), "
			+ "foreign key (branchid) references branches );";
	
	public static final String CREATE_HISTORY = "create table history( "
			+ "accid int not null, "
			+ "tellerid int not null, "
			+ "delta int not null, "
			+ "branchid int not null, "
			+ "accbalance int not null, "
			+ "cmmnt char(30) not null, "
			+ "foreign key (accid) references accounts, "
			+ "foreign key (tellerid) references tellers, "
			+ "foreign key (branchid) references branches );";
	
	public static final String DROP_TABLES = "drop table history; "
			+ "drop table tellers; "
			+ "drop table accounts; "
			+ "drop table branches;";
	
	public static final String DROP_BRANCHES = "drop table branches;";
	public static final String DROP_ACCOUNTS = "drop table accounts;";
	public static final String DROP_TELLERS = "drop table tellers;";
	public static final String DROP_HISTORY = "drop table history;";
}
