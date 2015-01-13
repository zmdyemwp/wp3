package com.fihtdc.smartbracelet.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public class BraceletInfo {

    private static final String SCHEME = "content://";
    public static final String AUTHORITY = "com.fihtdc.smartbracelet.provider";
    
    public static final class Profile implements BaseColumns{
        
        public static final String TABLE_NAME = "profile";
        
        /**
         * Path part for the profile URI
         */
        private static final String PATH_PROFILE = "/profile";

        /**
         * Path part for the profile ID URI
         */
        private static final String PATH_PROFILE_ID = "/profile/";
        
        /**
         * Path part for db content item size change
         */
        //private static final String CHANGE_SIZE_PATH = "/change";

        /**
         * 0-relative position of a profile ID segment in the path part of a profile ID URI
         */
        public static final int PROFILE_ID_PATH_POSITION = 1;

        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI = Uri.parse(SCHEME + AUTHORITY + PATH_PROFILE);
        
        /**
         * The content:// style URL for this table size change
         */
        //public static final Uri CONTENT_URI_SIZE_CHANGE = Uri.parse(SCHEME + AUTHORITY + CHANGE_SIZE_PATH);

        /**
         * The content URI base for a single profile. Callers must
         * append a numeric profile id to this Uri to retrieve a profile
         */
        public static final Uri CONTENT_ID_URI_BASE
            = Uri.parse(SCHEME + AUTHORITY + PATH_PROFILE_ID);

        /**
         * The content URI match pattern for a single profile, specified by its ID. Use this to match
         * incoming URIs or to construct an Intent.
         */
        public static final Uri CONTENT_ID_URI_PATTERN
            = Uri.parse(SCHEME + AUTHORITY + PATH_PROFILE_ID + "/#");

        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of profiles.
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.google.profile";

        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single
         * profile.
         */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.google.profiles";

        //Column definitions
        /**
         * Column name for the name of the profile
         * <P>Type: TEXT</P>
         */
        public static final String COLUMN_NAME_PROFILE_NAME = "name";

        /**
         * Column name for the gender of the profile
         * <P>Type: BOOLEAN</P>
         */
        public static final String COLUMN_NAME_PROFILE_GENDER = "gender";
        
        /**
         * Column name for the name of the weight
         * <P>Type: INTEGER</P>
         */
        public static final String COLUMN_NAME_WEIGHT = "weight";
        
        /**
         * Column name for the name of the weight unit
         * <P>Type: INTEGER</P>
         */
        public static final String COLUMN_NAME_WEIGHT_UNIT= "weight_unit";
        
        /**
         * Column name for the name of the profile's height
         * <P>Type: INTEGER</P>
         */
        public static final String COLUMN_NAME_HEIGHT = "height";
        
        /**
         * Column name for the name of the profile's height unit
         * <P>Type: INTEGER</P>
         */
        public static final String COLUMN_NAME_HEIGHT_UNIT = "height_unit";
        
        /**
         * Column name for the name of the birthday,for example:1987,11,21
         * <P>Type: TEXT</P>
         */
        public static final String COLUMN_NAME_BIRTHDAY = "birthday";
       
        /**
         * Column name to use in the future 
         * <P>Type: TEXT </P>
         */
        public static final String COLUMN_NAME_DATA1 = "data1";
        
        /**
         * Column name to use in the future 
         * <P>Type: TEXT </P>
         */
        public static final String COLUMN_NAME_DATA2 = "data2";
        
        /**
         * Column name to use in the future 
         * <P>Type: TEXT </P>
         */
        public static final String COLUMN_NAME_DATA3 = "data3";
        /**
         * Column name to use in the future 
         * <P>Type: TEXT </P>
         */
        public static final String COLUMN_NAME_DATA4 = "data4";
        
        /**
         * Column name to use in the future 
         * <P>Type: TEXT </P>
         */
        public static final String COLUMN_NAME_DATA5 = "data5";
        
        public static final int GENDER_FEMALE = 0;
        public static final int GENDER_MALE = 1;
    }
    
    public static final class Measure implements BaseColumns{
        
        public static final String TABLE_NAME = "measure";
        
        /**
         * Path part for the mesaure URI
         */
        private static final String PATH_MEASURE = "/measure";

        /**
         * Path part for the measure ID URI
         */
        private static final String PATH_MEASURE_ID = "/measure/";
        
        /**
         * Path part for db content item size change
         */
        //private static final String CHANGE_SIZE_PATH = "/change";

        /**
         * 0-relative position of a profile ID segment in the path part of a profile ID URI
         */
        public static final int MEASURE_ID_PATH_POSITION = 1;

        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI = Uri.parse(SCHEME + AUTHORITY + PATH_MEASURE);
        
        /**
         * The content:// style URL for this table size change
         */
        //public static final Uri CONTENT_URI_SIZE_CHANGE = Uri.parse(SCHEME + AUTHORITY + CHANGE_SIZE_PATH);

        /**
         * The content URI base for a single measure. Callers must
         * append a numeric measure id to this Uri to retrieve a profile
         */
        public static final Uri CONTENT_ID_URI_BASE
            = Uri.parse(SCHEME + AUTHORITY + PATH_MEASURE_ID);

        /**
         * The content URI match pattern for a single measure, specified by its ID. Use this to match
         * incoming URIs or to construct an Intent.
         */
        public static final Uri CONTENT_ID_URI_PATTERN
            = Uri.parse(SCHEME + AUTHORITY + PATH_MEASURE_ID + "/#");

        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of measures.
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.google.measure";

        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single
         * measure.
         */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.google.measures";

        //Column definitions
        /**
         * Column name for the name of the measure's profile
         * <P>Type: TEXT</P>
         */
        public static final String COLUMN_NAME_PROFILE_NAME = "profile_name";

        /**
         * Column name for the ANS_AGE of the measure
         * <P>Type: INTEGER</P>
         */
        public static final String COLUMN_NAME_ANS_AGE = "ansAge";
        
        /**
         * Column name for the name of the Agility
         * <P>Type: INTEGER</P>
         */
        public static final String COLUMN_NAME_AGILITY = "agility";
        
        /**
         * Column name for the name of the measure's bpm
         * <P>Type: INTEGER</P>
         */
        public static final String COLUMN_NAME_BPM = "bpm";
        /**
         * Column name for the name of the measure's sdnn
         * <P>Type: INTEGER</P>
         */
        public static final String COLUMN_NAME_SDNN = "sdnn";
        /**
         * Column name for person status x 
         * <P>Type: INTEGER</P>
         */
		public static final String COLUMN_NAME_AGE_LF = "agedLF";
	    /**
         * Column name for person status y
         * <P>Type: INTEGER</P>
         */
		public static final String COLUMN_NAME_AGE_HF= "ageHF";
	    /**
         * Column name for the person status
         * <P>Type: INTEGER</P>
         */
		public static final String COLUMN_NAME_EMOTION_STATUS = "status";
        
        /**
         * Column name for the name of the personality type,for example:balanced
         * <P>Type: TEXT</P>
         */
        public static final String COLUMN_NAME_PERSONALITY_TYPE = "type";
       
        /**
         * Column name for the name of the test time
         * <P>Type: LONG</P>
         */
        public static final String COLUMN_NAME_TEST_TIME = "time";
        /**
         * Column name to use in the future 
         * <P>Type: TEXT </P>
         */
        public static final String COLUMN_NAME_DATA1 = "data1";
        
        /**
         * Column name to use in the future 
         * <P>Type: TEXT </P>
         */
        public static final String COLUMN_NAME_DATA2 = "data2";
        /**
         * Column name to use in the future 
         * <P>Type: TEXT </P>
         */
        public static final String COLUMN_NAME_DATA3 = "data3";
        
        /**
         * Column name to use in the future 
         * <P>Type: TEXT </P>
         */
        public static final String COLUMN_NAME_DATA4 = "data4";
        
        /**
         * Column name to use in the future 
         * <P>Type: TEXT </P>
         */
        public static final String COLUMN_NAME_DATA5 = "data5";

		
    }
    
    public static final class Coaching implements BaseColumns{
        
        public static final String TABLE_NAME = "coaching";
        
        /**
         * Path part for the coaching URI
         */
        private static final String PATH_COACHING = "/coaching";

        /**
         * Path part for the coaching ID URI
         */
        private static final String PATH_COACHING_ID = "/coaching/";
        
        /**
         * Path part for db content item size change
         */
        //private static final String CHANGE_SIZE_PATH = "/change";

        /**
         * 0-relative position of a coaching ID segment in the path part of a coaching ID URI
         */
        public static final int COACHING_ID_PATH_POSITION = 1;

        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI = Uri.parse(SCHEME + AUTHORITY + PATH_COACHING);
        
        /**
         * The content:// style URL for this table size change
         */
        //public static final Uri CONTENT_URI_SIZE_CHANGE = Uri.parse(SCHEME + AUTHORITY + CHANGE_SIZE_PATH);

        /**
         * The content URI base for a single coaching. Callers must
         * append a numeric coaching id to this Uri to retrieve a coaching
         */
        public static final Uri CONTENT_ID_URI_BASE
            = Uri.parse(SCHEME + AUTHORITY + PATH_COACHING_ID);

        /**
         * The content URI match pattern for a single coaching, specified by its ID. Use this to match
         * incoming URIs or to construct an Intent.
         */
        public static final Uri CONTENT_ID_URI_PATTERN
            = Uri.parse(SCHEME + AUTHORITY + PATH_COACHING_ID + "/#");

        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of coachings.
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.google.coaching";

        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single
         * coaching.
         */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.google.coachings";

        //Column definitions
        /**
         * Column name for the name of the coaching's profile
         * <P>Type: TEXT</P>
         */
        public static final String COLUMN_NAME_PROFILE_NAME = "profile_name";

        /**
         * Column name for the name of the coaching's level
         * <P>Type: TEXT</P>
         */
        public static final String COLUMN_NAME_LEVEL = "level";
        
        /**
         * Column name for the name of the coaching's cycle
         * <P>Type: INTEGER</P>
         */
        public static final String COLUMN_NAME_CYCLE = "cycle";
        
        /**
         * Column name for the name of the coaching's hit num
         * <P>Type: INTEGER</P>
         */
        public static final String COLUMN_NAME_HIT_NUM = "hit";
        /**
         * Column name for the ANS_AGE of the Coaching
         * <P>Type: INTEGER</P>
         */
        public static final String COLUMN_NAME_ANS_AGE = "ansAge";
        
        /**
         * Column name for the name of the Agility
         * <P>Type: INTEGER</P>
         */
        public static final String COLUMN_NAME_AGILITY = "agility";
        
        /**
         * Column name for the name of the measure's bpm
         * <P>Type: INTEGER</P>
         */
        public static final String COLUMN_NAME_BPM = "bpm";
        
        /**
         * Column name for the name of the personality type,for example:balanced
         * <P>Type: TEXT</P>
         */
        public static final String COLUMN_NAME_PERSONALITY_TYPE = "type";
       
        /**
         * Column name for the name of the test time
         * <P>Type: LONG</P>
         */
        public static final String COLUMN_NAME_TEST_TIME = "time";
        
        /**
         * Column name to use in the future 
         * <P>Type: TEXT </P>
         */
        public static final String COLUMN_NAME_DATA1 = "data1";
        
        /**
         * Column name to use in the future 
         * <P>Type: TEXT </P>
         */
        public static final String COLUMN_NAME_DATA2 = "data2";
        /**
         * Column name to use in the future 
         * <P>Type: TEXT </P>
         */
        public static final String COLUMN_NAME_DATA3 = "data3";
        
        /**
         * Column name to use in the future 
         * <P>Type: TEXT </P>
         */
        public static final String COLUMN_NAME_DATA4 = "data4";
        
        /**
         * Column name to use in the future 
         * <P>Type: TEXT </P>
         */
        public static final String COLUMN_NAME_DATA5 = "data5";
    }
    
    public static class DayCoaching implements BaseColumns{
        public static final String TABLE_NAME = "daycoaching";
        
        /**
         * Path part for the profile URI
         */
        private static final String PATH_DAYCOACHING = "/daycoaching";

        /**
         * Path part for the profile ID URI
         */
        private static final String PATH_DAYCOACHING_ID = "/daycoaching/";
        
        /**
         * Path part for db content item size change
         */
        //private static final String CHANGE_SIZE_PATH = "/change";

        /**
         * 0-relative position of a profile ID segment in the path part of a profile ID URI
         */
        public static final int DAYCOACHING_ID_PATH_POSITION = 1;

        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI = Uri.parse(SCHEME + AUTHORITY + PATH_DAYCOACHING);
        
        /**
         * The content:// style URL for this table size change
         */
        //public static final Uri CONTENT_URI_SIZE_CHANGE = Uri.parse(SCHEME + AUTHORITY + CHANGE_SIZE_PATH);

        /**
         * The content URI base for a single profile. Callers must
         * append a numeric profile id to this Uri to retrieve a profile
         */
        public static final Uri CONTENT_ID_URI_BASE
            = Uri.parse(SCHEME + AUTHORITY + PATH_DAYCOACHING_ID);

        /**
         * The content URI match pattern for a single profile, specified by its ID. Use this to match
         * incoming URIs or to construct an Intent.
         */
        public static final Uri CONTENT_ID_URI_PATTERN
            = Uri.parse(SCHEME + AUTHORITY + PATH_DAYCOACHING_ID + "/#");

        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of profiles.
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.google.daycoaching";

        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single
         * profile.
         */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.google.daycoachings";

        //Column definitions
        /**
         * Column name for the date name of the daycoaching
         * <P>Type: TEXT</P>
         */
        public static final String COLUMN_NAME_DAYCOACHING_DATE = "date";

        /**
         * Column name for the cycle1 of the daycoaching
         * <P>Type: INTEGER</P>
         */
        public static final String COLUMN_NAME_CYCLE1 = "cycle1";
        
        /**
         * Column name for the name of the hit1
         * <P>Type: INTEGER</P>
         */
        public static final String COLUMN_NAME_HIT1 = "hit1";
        /**
         * Column name for the name of cycle2
         * <P>Type: INTEGER</P>
         */
        public static final String COLUMN_NAME_CYCLE2 = "cycle2";
        
        /**
         * Column name for the name of the hit2
         * <P>Type: INTEGER</P>
         */
        public static final String COLUMN_NAME_HIT2 = "hit2";
        
        /**
         * Column name for the name of cycle3
         * <P>Type: INTEGER</P>
         */
        public static final String COLUMN_NAME_CYCLE3 = "cycle3";
        
        /**
         * Column name for the name of the hit3
         * <P>Type: INTEGER</P>
         */
        public static final String COLUMN_NAME_HIT3 = "hit3";
        /**
         * Column name for the name of cycle4
         * <P>Type: INTEGER</P>
         */
        public static final String COLUMN_NAME_CYCLE4 = "cycle4";
        
        /**
         * Column name for the name of the hit4
         * <P>Type: INTEGER</P>
         */
        public static final String COLUMN_NAME_HIT4 = "hit4";
        /**
         * Column name for the name of cycle5
         * <P>Type: INTEGER</P>
         */
        public static final String COLUMN_NAME_CYCLE5 = "cycle5";
        
        /**
         * Column name for the name of the hit5
         * <P>Type: INTEGER</P>
         */
        public static final String COLUMN_NAME_HIT5 = "hit5";
        /**
         * Column name for the name of cycleM
         * <P>Type: INTEGER</P>
         */
        public static final String COLUMN_NAME_CYCLEM = "cycleM";
        
        /**
         * Column name for the name of the hitM
         * <P>Type: INTEGER</P>
         */
        public static final String COLUMN_NAME_HITM = "hitM";
        
        /**
         * Column name for the name of the cost time
         * <P>Type: LONG</P>
         */
        public static final String COLUMN_NAME_TEST_TIME = "time";
        /**
         * Column name to use in the future 
         * <P>Type: TEXT </P>
         */
        public static final String COLUMN_NAME_DATA1 = "data1";
        
        /**
         * Column name to use in the future 
         * <P>Type: TEXT </P>
         */
        public static final String COLUMN_NAME_DATA2 = "data2";
        /**
         * Column name to use in the future 
         * <P>Type: TEXT </P>
         */
        public static final String COLUMN_NAME_DATA3 = "data3";
        
        /**
         * Column name to use in the future 
         * <P>Type: TEXT </P>
         */
        public static final String COLUMN_NAME_DATA4 = "data4";
        
        /**
         * Column name to use in the future 
         * <P>Type: TEXT </P>
         */
        public static final String COLUMN_NAME_DATA5 = "data5";
    }
}
