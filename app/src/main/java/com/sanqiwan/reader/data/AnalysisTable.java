package com.sanqiwan.reader.data;

import android.provider.BaseColumns;


public class AnalysisTable implements BaseColumns {

    public static final String TABLE_NAME = "analysis";

    public static final String DATA = "data";
    public static final String URI = "uri";

    public static final String CREATE_ANALYSIS_TABLE = " create table " +
            TABLE_NAME + " (" +
            _ID + " Integer primary key autoincrement, " +
            DATA + " text, " +
            URI + " text " +
            ")";

}
