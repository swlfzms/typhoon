package model;

public class SQLStatement {

	
	//TyphoonYear
	public static final String GETTYPHOONYEAR = "SELECT YEAR FROM typhoonyear;";
	
	//TyphoonData
	public static final String INSERTTYPHOONDATA = "LOAD DATA LOCAL INFILE FILENAME INTO TABLE dataTABLENAME FIELDS TERMINATED BY ','";
	public static final String INSERTTYPHOONSN = "LOAD DATA LOCAL INFILE FILENAME INTO TABLE snTABLENAME FIELDS TERMINATED BY ','";
	//public static final String QUERYTABLEISEXIST = "";
	public static final String CREATETYPHOONDATATABLE = "CREATE TABLE IF NOT EXISTS dataTABLENAME (serialNumber int(11) NOT NULL,time varchar(20) character set utf8 default NULL,lat double NOT NULL,lng double NOT NULL,pressure double default NULL,centerSpeed double default NULL,moveSpeed double default NULL,direction varchar(20) default NULL,sevenSolarHalo double default NULL,tenSolarHalo double default NULL) ENGINE=MyISAM DEFAULT CHARSET=utf8;";
	public static final String CREATETYPHOONSNTABLE = "CREATE TABLE IF NOT EXISTS snTABLENAME (serialNumber int(11) NOT NULL, name_zh varchar(12) default NULL, name_en varchar(25) default NULL, similar_sn1 int(11) default NULL COMMENT '最相似的',distance1 double default NULL COMMENT '最小结果',similar_sn2 int(11) default NULL,distance2 double default NULL,similar_sn3 int(11) default NULL,distance3 double default NULL,PRIMARY KEY (serialNumber)) ENGINE=InnoDB DEFAULT CHARSET=utf8;";
	
	//获取所有的表名
	public static final String GETTYPHOONTABLENAME = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'typhoon'";
	public static final String GETTABLEDATABYNAME = "SELECT serialNumber, TIME, lat, lng FROM dataTABLENAME;";
	
	//插入年份名字
	public static final String INSERTTYPHOONYEARNUMBER = "INSERT INTO typhoonyear(year) VALUES(?)";

	//根绝年份获取台风编号和名称
	public static final String GETTYPHOONDATABYYEAR = "SELECT serialNumber,name_zh,name_en FROM snTABLENAME";
	
	//根据台风序列号获取台风部分数据
	public static final String GETTYPHOONDATABYSN = "SELECT time,lng,lat FROM dataTABLENAME WHERE serialNumber=?";
	
	//根据台风序列号获取台风全部数据
	public static final String GETTYPHOONFULLDATABYSN = "SELECT * FROM dataTABLENAME WHERE serialNumber=?";
	
	//获取相似路径
	public static final String GETTYPHOONSIMILARROUTESNBYSN = "SELECT similar_sn1 FROM snTABLENAME WHERE serialNumber=?";
	
	//用户操作表
	public static final String ADMINLOGIN = "SELECT username FROM USER WHERE username=? AND PASSWORD=?";
	public static final String CREATEADMIN = "INSERT INTO USER(username, password) values(?,?)";
	public static final String UPDATEADMIN = "UPDATE USER SET PASSWORD=? WHERE username=?";	
	
	//根据表名获取序列号
	public static final String GETSNBYTABLENAME = "SELECT serialNumber FROM TABLENAME;";
	
}

