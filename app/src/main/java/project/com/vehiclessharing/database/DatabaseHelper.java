package project.com.vehiclessharing.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import project.com.vehiclessharing.model.BirthDay;
import project.com.vehiclessharing.model.User;
import project.com.vehiclessharing.model.UserAddress;

/**
 * Created by Tuan on 16/04/2017.
 */

/**
 * This class create to using SQLite
 */
public class DatabaseHelper extends SQLiteOpenHelper{

    private static final String TAG = "DatabaseHelper";

    private static final String DATABASE_NAME = "vehicles_sharing.db";
    private static final int DATABASE_VERSION = 1;

    //TABLE users
    private static final String TABLE_USER = "users";
    private static final String USER_ID = "user_id";
    private static final String EMAIL_COLUMN = "email";
    private static final String IMAGE_COLUMN = "image";
    private static final String FULL_NAME_COLUMN = "full_name";
    private static final String PHONE_NUMBER_COLUMN = "phone_number";
    private static final String SEX_COLUMN = "sex";

    //TABLE address
    private static final String TABLE_ADDRESS = "address";
    private static final String COUNTRY_COLUMN = "country";
    private static final String PROVINCE_COLUMN = "province";
    private static final String DISTRICT_COLUMN = "district";

    //TABLE birthday
    private static final String TABLE_BIRTHDAY = "birthday";
    private static final String YEAR_COLUMN = "year";
    private static final String MONTH_COLUMN = "month";
    private static final String DAY_COLUMN = "day";

    private static final String CREATE_ADDRESS_TABLE_SQL = "CREATE TABLE IF NOT EXISTS " + TABLE_ADDRESS + " (" +
            USER_ID + " TEXT NOT NULL," +
            COUNTRY_COLUMN + " TEXT," +
            DISTRICT_COLUMN + " TEXT," +
            PROVINCE_COLUMN + " TEXT," +
            " FOREIGN KEY ("+USER_ID+") REFERENCES " + TABLE_USER + "("+USER_ID+")" +
            ")";

    private static final String CREATE_USER_TABLE_SQL = "CREATE TABLE IF NOT EXISTS " + TABLE_USER + " (" +
            USER_ID + " TEXT NOT NULL PRIMARY KEY," +
            EMAIL_COLUMN + " TEXT NOT NULL," +
            IMAGE_COLUMN + " TEXT," +
            FULL_NAME_COLUMN + " TEXT NOT NULL," +
            PHONE_NUMBER_COLUMN + " TEXT NOT NULL," +
            SEX_COLUMN + " TEXT NOT NULL" +
            ")";

    private static final String CREATE_BIRTHDAY_TABLE_SQL = "CREATE TABLE IF NOT EXISTS " + TABLE_BIRTHDAY + " (" +
            USER_ID + " TEXT NOT NULL PRIMARY KEY," +
            DAY_COLUMN + " INTEGER," +
            MONTH_COLUMN + " INTEGER," +
            YEAR_COLUMN + " INTEGER" +
            ")";


    private static DatabaseHelper sInstance;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.e(TAG, "DatabaseHelper: ");
    }

    @Override

    /**
     * Thực thi các câu lệnh tạo bảng
     * Sử dụng db.execSQL(sql); chạy câu lênh sql tạo bảng
     */
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.e(TAG, "onCreate: ");
        sqLiteDatabase.execSQL(CREATE_USER_TABLE_SQL);
        sqLiteDatabase.execSQL(CREATE_ADDRESS_TABLE_SQL);
        sqLiteDatabase.execSQL(CREATE_BIRTHDAY_TABLE_SQL);

    }

    /**
     * Gọi khi bạn thay đổi DATABASE_VERSION
     * Thường sử dụng để thay đổi cấu trúc bảng (ALTER, DROP, ADD CONSTRAIN...)
     * @param sqLiteDatabase instance SQLite databse
     * @param i old version
     * @param i1 new version
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        Log.e(TAG, "onUpgrade: ");
//        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_ADDRESS);
//        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
//        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_BIRTHDAY);
//        onCreate(sqLiteDatabase);
    }

    public static DatabaseHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    /**
     * Check user exists or not exists on device
     * @param userId
     * @return true - exists | false - not exists
     */
    public boolean isUserExists(String userId){
        SQLiteDatabase db = getReadableDatabase();
        boolean exists = false;
        Cursor cursor = db.query(TABLE_USER, new String[]{EMAIL_COLUMN,
                        IMAGE_COLUMN,
                        FULL_NAME_COLUMN,
                        PHONE_NUMBER_COLUMN,
                        SEX_COLUMN}, USER_ID + " = ?",
                new String[]{userId}, null, null, null);
        if (cursor != null && cursor.moveToFirst())
            exists = true;
        cursor.close();
        db.close();
        return exists;
    }

    /**
     * Insert data into SQLite
     * @param userId
     * @return True if insert success
     */
    private boolean insertBirthday(String userId){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();//Sử dụng đối tượng ContentValues để put các giá trị ứng với tên colum và sử dụng phương thức insert của SQLiteDatabase để tiến hành ghi xuống database.
        values.put(USER_ID, userId);
        values.put(DAY_COLUMN, 0);
        values.put(MONTH_COLUMN, 0);
        values.put(YEAR_COLUMN, 0);
        long rowId = db.insert(TABLE_BIRTHDAY, null, values);
        db.close();
        if (rowId != -1)//success
            return true;
        return false;
    }

    /**
     * Select data into Sqlite
     * @param userId
     * @return object birthday
     */
    private BirthDay getBirthDay(String userId){
        SQLiteDatabase db = getReadableDatabase();
        BirthDay birthDay = null;
        Cursor cursor = db.query(TABLE_BIRTHDAY, new String[]{DAY_COLUMN,
                        MONTH_COLUMN,
                        YEAR_COLUMN}, USER_ID + " = ?",
                new String[]{userId}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            birthDay = new BirthDay(cursor.getInt(0), cursor.getInt(1), cursor.getInt(2));
            cursor.close();
        }
        db.close();
        return birthDay;
    }


    /**
     * Insert data into SQLite
     * @param userId
     * @return True if insert success
     */
    private boolean insertAddress(String userId){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();//Sử dụng đối tượng ContentValues để put các giá trị ứng với tên colum và sử dụng phương thức insert của SQLiteDatabase để tiến hành ghi xuống database.
        values.put(USER_ID, userId);
        values.put(COUNTRY_COLUMN, "");
        values.put(DISTRICT_COLUMN, "");
        values.put(PROVINCE_COLUMN, "");
        long rowId = db.insert(TABLE_ADDRESS, null, values);
        db.close();
        if (rowId != -1)//success
            return true;
        return false;
    }

    /**
     * Select data from SQLite
     * @param userId
     * @return object UserAddress
     */
    private UserAddress getAddress(String userId){
        SQLiteDatabase db = getReadableDatabase();
        UserAddress userAddress = null;
        Cursor cursor = db.query(TABLE_ADDRESS, new String[]{COUNTRY_COLUMN,
                        DISTRICT_COLUMN,
                        PROVINCE_COLUMN}, USER_ID + " = ?",
                new String[]{userId}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            userAddress = new UserAddress(cursor.getString(0), cursor.getString(1), cursor.getString(2));
            cursor.close();
        }
        db.close();
        return userAddress;
    }

    /**
     * Insert data into SQLite
     * @param user object to storage
     * @param userId id's user
     * @return true if insert success
     */
    public boolean insertUser(User user, String userId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();//Sử dụng đối tượng ContentValues để put các giá trị ứng với tên colum và sử dụng phương thức insert của SQLiteDatabase để tiến hành ghi xuống database.
        values.put(USER_ID, userId);
        values.put(EMAIL_COLUMN, user.getEmail());
        values.put(IMAGE_COLUMN, "");
        values.put(FULL_NAME_COLUMN, user.getFullName());
        values.put(PHONE_NUMBER_COLUMN, user.getPhoneNumber());
        values.put(SEX_COLUMN, user.getSex());
        long rowId = db.insert(TABLE_USER, null, values);
        db.close();
        if (rowId != -1)
            if(insertAddress(userId) && insertBirthday(userId)) return true;//success
        return false;
    }

    /**
     * Select data from SQLite
     * @param userId
     * @return object User
     */
    public User getUser(String userId) {
        SQLiteDatabase db = getReadableDatabase();
        User user = null;
        Cursor cursor = db.query(TABLE_USER, new String[]{EMAIL_COLUMN,
                        IMAGE_COLUMN,
                        FULL_NAME_COLUMN,
                        PHONE_NUMBER_COLUMN,
                        SEX_COLUMN}, USER_ID + " = ?",
                new String[]{userId}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            user = new User(cursor.getString(0), cursor.getString(1), cursor.getString(2),cursor.getString(3),cursor.getString(4),null,null);
            //Get Address
            UserAddress userAddress = getAddress(userId);
            if(userAddress != null)
                user.setAddress(userAddress);
            //Get Birthday
            BirthDay birthDay = getBirthDay(userId);
            if(birthDay != null)
                user.setBirthDay(birthDay);

            cursor.close();
        }
        db.close();
        return user;
    }

    /**
     * update fullname's user
     * @param userId
     * @param value new value of fullname
     * @return
     */
    public boolean updateFullName(String userId, String value){
        if(value == null || value.isEmpty()) return false;
        SQLiteDatabase db = getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(FULL_NAME_COLUMN, value);
        int ret = db.update(TABLE_USER,values,USER_ID + "=?",new String[]{userId});
        if(ret != 0) return true;//success
        return false;//failed
    }

    /**
     * update phonenumber's user
     * @param userId
     * @param value new value of phonenumber
     * @return
     */
    public boolean updatePhoneNumber(String userId, String value){
        if(value == null || value.isEmpty()) return false;
        SQLiteDatabase db = getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(PHONE_NUMBER_COLUMN, value);
        int ret = db.update(TABLE_USER,values,USER_ID + "=?",new String[]{userId});
        if(ret != 0) return true;//success
        return false;//failed
    }

    /**
     * update sex's user
     * @param userId
     * @param value new value of sex
     * @return
     */
    public boolean updateSex(String userId, String value){
        if(value == null || value.isEmpty()) return false;
        SQLiteDatabase db = getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(SEX_COLUMN, value);
        int ret = db.update(TABLE_USER,values,USER_ID + "=?",new String[]{userId});
        if(ret != 0) return true;//success
        return false;//failed
    }

    /**
     * update country's user
     * @param userId
     * @param value new value of country
     * @return
     */
    public boolean updateCountry(String userId, String value){
        if(value == null) value = "";
        SQLiteDatabase db = getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(COUNTRY_COLUMN, value);
        int ret = db.update(TABLE_ADDRESS,values,USER_ID + "=?",new String[]{userId});
        if(ret != 0) return true;//success
        return false;//failed
    }

    /**
     * update province's user
     * @param userId
     * @param value new value of province
     * @return
     */
    public boolean updateProvince(String userId, String value){
        if(value == null) value = "";
        SQLiteDatabase db = getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(PROVINCE_COLUMN, value);
        int ret = db.update(TABLE_ADDRESS,values,USER_ID + "=?",new String[]{userId});
        if(ret != 0) return true;//success
        return false;//failed
    }

    /**
     * update district's user
     * @param userId
     * @param value new value of district
     * @return
     */
    public boolean updateDistrict(String userId, String value){
        if(value == null) value = "";
        SQLiteDatabase db = getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(DISTRICT_COLUMN, value);
        int ret = db.update(TABLE_ADDRESS,values,USER_ID + "=?",new String[]{userId});
        if(ret != 0) return true;//success
        return false;//failed
    }

    /**
     * update day's birthday user
     * @param userId
     * @param value new value of day
     * @return
     */
    public boolean updateDay(String userId, int value){
        if(value <= 0) return false;
        SQLiteDatabase db = getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(DAY_COLUMN, value);
        int ret = db.update(TABLE_BIRTHDAY,values,USER_ID + "=?",new String[]{userId});
        if(ret != 0) return true;//success
        return false;//failed
    }

    /**
     * update month's birthday user
     * @param userId
     * @param value new value of month
     * @return
     */
    public boolean updateMonth(String userId, int value){
        if(value <= 0) return false;
        SQLiteDatabase db = getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(MONTH_COLUMN, value);
        int ret = db.update(TABLE_BIRTHDAY,values,USER_ID + "=?",new String[]{userId});
        if(ret != 0) return true;//success
        return false;//failed
    }

    /**
     * update year's birthday user
     * @param userId
     * @param value new value of year
     * @return
     */
    public boolean updateYear(String userId, int value){
        if(value <= 0) return false;
        SQLiteDatabase db = getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(YEAR_COLUMN, value);
        int ret = db.update(TABLE_BIRTHDAY,values,USER_ID + "=?",new String[]{userId});
        if(ret != 0) return true;//success
        return false;//failed
    }

    /**
     * update url avatar user
     * @param userId
     * @param url
     * @return
     */
    public boolean uploadURLImage(String userId,String url){
        if(url.isEmpty()) return false;
        SQLiteDatabase db = getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(IMAGE_COLUMN, url);
        int ret = db.update(TABLE_USER,values,USER_ID + "=?",new String[]{userId});
        if(ret != 0) return true;//success
        return false;//failed
    }
}
