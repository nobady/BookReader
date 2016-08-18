/*
 * Copyright (C) 2007-2013 Geometer Plus <contact@geometerplus.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package org.geometerplus.zlibrary.core.sqliteconfig;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import org.geometerplus.zlibrary.core.config.ZLConfig;

import java.util.LinkedList;
import java.util.List;

public final class ZLSQLiteConfig extends ZLConfig {
    private final SQLiteDatabase mDatabase;
    private final SQLiteStatement mGetValueStatement;
    private final SQLiteStatement mSetValueStatement;
    private final SQLiteStatement mUnsetValueStatement;
    private final SQLiteStatement mDeleteGroupStatement;

    public ZLSQLiteConfig(Context context) {
        mDatabase = context.openOrCreateDatabase("config.db", Context.MODE_PRIVATE, null);
        switch (mDatabase.getVersion()) {
            case 0:
                mDatabase.execSQL("CREATE TABLE config (groupName VARCHAR, name VARCHAR, value VARCHAR, PRIMARY KEY(groupName, name) )");
                break;
            case 1:
                mDatabase.beginTransaction();
                SQLiteStatement removeStatement = mDatabase.compileStatement(
                        "DELETE FROM config WHERE name = ? AND groupName LIKE ?"
                );
                removeStatement.bindString(2, "/%");
                removeStatement.bindString(1, "Size");
                removeStatement.execute();
                removeStatement.bindString(1, "Title");
                removeStatement.execute();
                removeStatement.bindString(1, "Language");
                removeStatement.execute();
                removeStatement.bindString(1, "Encoding");
                removeStatement.execute();
                removeStatement.bindString(1, "AuthorSortKey");
                removeStatement.execute();
                removeStatement.bindString(1, "AuthorDisplayName");
                removeStatement.execute();
                removeStatement.bindString(1, "EntriesNumber");
                removeStatement.execute();
                removeStatement.bindString(1, "TagList");
                removeStatement.execute();
                removeStatement.bindString(1, "Sequence");
                removeStatement.execute();
                removeStatement.bindString(1, "Number in seq");
                removeStatement.execute();
                mDatabase.execSQL(
                        "DELETE FROM config WHERE name LIKE 'Entry%' AND groupName LIKE '/%'"
                );
                mDatabase.setTransactionSuccessful();
                mDatabase.endTransaction();
                mDatabase.execSQL("VACUUM");
                break;
        }
        mDatabase.setVersion(2);
        mGetValueStatement = mDatabase.compileStatement("SELECT value FROM config WHERE groupName = ? AND name = ?");
        mSetValueStatement = mDatabase.compileStatement("INSERT OR REPLACE INTO config (groupName, name, value) VALUES (?, ?, ?)");
        mUnsetValueStatement = mDatabase.compileStatement("DELETE FROM config WHERE groupName = ? AND name = ?");
        mDeleteGroupStatement = mDatabase.compileStatement("DELETE FROM config WHERE groupName = ?");
    }

    @Override
    synchronized public List<String> listGroups() {
        final LinkedList<String> list = new LinkedList<String>();
        final Cursor cursor = mDatabase.rawQuery("SELECT DISTINCT groupName FROM config", null);
        while (cursor.moveToNext()) {
            list.add(cursor.getString(0));
        }
        cursor.close();
        return list;
    }

    @Override
    synchronized public List<String> listNames(String group) {
        final LinkedList<String> list = new LinkedList<String>();
        final Cursor cursor = mDatabase.rawQuery("SELECT name FROM config WHERE groupName = ?", new String[]{group});
        while (cursor.moveToNext()) {
            list.add(cursor.getString(0));
        }
        cursor.close();
        return list;
    }

    @Override
    synchronized public void removeGroup(String name) {
        mDeleteGroupStatement.bindString(1, name);
        try {
            mDeleteGroupStatement.execute();
        } catch (SQLException e) {
        }
    }

    @Override
    synchronized public String getValue(String group, String name, String defaultValue) {
        String answer = defaultValue;
        mGetValueStatement.bindString(1, group);
        mGetValueStatement.bindString(2, name);
        try {
            answer = mGetValueStatement.simpleQueryForString();
        } catch (SQLException e) {
        }
        return answer;
    }

    @Override
    synchronized public void setValue(String group, String name, String value) {
        mSetValueStatement.bindString(1, group);
        mSetValueStatement.bindString(2, name);
        mSetValueStatement.bindString(3, value);
        try {
            mSetValueStatement.execute();
        } catch (SQLException e) {
        }
    }

    @Override
    synchronized public void unsetValue(String group, String name) {
        mUnsetValueStatement.bindString(1, group);
        mUnsetValueStatement.bindString(2, name);
        try {
            mUnsetValueStatement.execute();
        } catch (SQLException e) {
        }
    }
}
