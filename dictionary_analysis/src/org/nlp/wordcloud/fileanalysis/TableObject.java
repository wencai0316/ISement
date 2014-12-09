package org.nlp.wordcloud.fileanalysis;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract interface TableObject {
	
	public abstract String GetSql();
	public abstract int BindInfo(PreparedStatement pStat) throws SQLException;
}
