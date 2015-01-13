package com.android.camera;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.util.Log;
import android.media.CameraProfile;
import java.util.HashMap;

public class DefaultValue {
	private static Context mContext = null;
    private static DefaultValue mDV = null;
    private final static String mCDACmd = "@FIHCDA@getCameraSettings";
    private static String mCDAXml = null;
    private static final String TAG = "DefaultValue";    

    public static final int mIndex_Camera_Picture_Size = 0;
    public static final int mIndex_Camera_Picture_Quality = 1;
    public static final int mIndex_Camera_Effect = 2;
    public static final int mIndex_Camera_Metering_Mode = 3;
    public static final int mIndex_Camera_Anti_Banding = 4;
    public static final int mIndex_Camera_Saturation = 5;
    public static final int mIndex_Camera_Contrast = 6;
    public static final int mIndex_Camera_Sharpness = 7;
    public static final int mIndex_Camera_Brightness = 8;
    public static final int mIndex_Camera_Grid = 9;
    public static final int mIndex_Camera_Shutter_Sound = 10;
    public static final int mIndex_Camera_Store_Location = 11;
    public static final int mIndex_Camera_White_Balance = 12;
    public static final int mIndex_Camera_Flash_Mode = 13;
    public static final int mIndex_Video_Size = 14;
    public static final int mIndex_Video_Encoder = 15;
    public static final int mIndex_Video_Duration = 16;
    public static final int mIndex_Video_Quality = 17;


    private static class DVProcess{
        public String getCDA_DV(int index)
        {
        	return mDVTable[index].cdaValue;
        }
        public String getDV(int index)
        {
        	return null;
        }
    }

    private static class DVItem {
        private String cdaTag;
        private String cdaValue;
        private String defaultValue;
        private DVProcess proc;

        public DVItem(String icdaTag, String icdaValue, String idefaultValue, DVProcess iproc)
        {
            cdaTag = icdaTag;
            cdaValue = icdaValue;
            defaultValue = idefaultValue;
            proc = iproc;
        }
    }
    

    private static class GetPictureSize_DV extends DVProcess{
		@Override
		public String getCDA_DV(int index) {
			String result = super.getCDA_DV(index);
            if((null != result) && (0 != result.length())){					
				if(0 == result.compareToIgnoreCase("5M"))
				{
					result = "2560x1920"; 
				}
				else if(0 == result.compareToIgnoreCase("3M"))
				{
					result = "2048x1536";
				}
				else if(0 == result.compareToIgnoreCase("2M"))
				{
					result = "1600x1200";
				}
				else if(0 == result.compareToIgnoreCase("1M"))
				{
					result = "1024x768";
				}
				else if(0 == result.compareToIgnoreCase("VGA"))
				{
					result = "640x480";
				}
				else if(0 == result.compareToIgnoreCase("QVGA"))
				{
					result = "320x240";
				}						
				else
				{
					result = null;
				}
		    }
			return result;
		}

		@Override
		public String getDV(int index) {
			return super.getDV(index);
		}
    };

    private static class GetPictureQuality_DV extends DVProcess{
		@Override
		public String getCDA_DV(int index) {
			return super.getCDA_DV(index);
		}

		@Override
		public String getDV(int index) {
			return mContext.getString(R.string.pref_camera_jpegquality_default);
		}
    };


    private static class GetEffect_DV extends DVProcess{
		@Override
		public String getCDA_DV(int index) {
			return super.getCDA_DV(index);
		}

		@Override
		public String getDV(int index) {
			return mContext.getString(R.string.pref_camera_coloreffect_default);
		}
    };

    private static class GetMeteringMode_DV extends DVProcess{
		@Override
		public String getCDA_DV(int index) {
			return super.getCDA_DV(index);
		}

		@Override
		public String getDV(int index) {
			return mContext.getString(R.string.pref_camera_autoexposure_default);
		}
    };

    private static class GetAntiBanding_DV extends DVProcess{
		@Override
		public String getCDA_DV(int index) {
			return super.getCDA_DV(index);
		}

		@Override
		public String getDV(int index) {
			return mContext.getString(R.string.pref_camera_antibanding_default);
		}
    };

    private static class GetSaturation_DV extends DVProcess{
		@Override
		public String getCDA_DV(int index) {
			return super.getCDA_DV(index);
		}

		@Override
		public String getDV(int index) {
			//return Camera.saturationPref;
			return mContext.getString(R.string.pref_camera_saturation_default);
		}
    };

    private static class GetContrast_DV extends DVProcess{
		@Override
		public String getCDA_DV(int index) {
			return super.getCDA_DV(index);
		}

		@Override
		public String getDV(int index) {
			//return Camera.contrastPref;
			return mContext.getString(R.string.pref_camera_contrast_default);
		}
    };

    private static class GetSharpness_DV extends DVProcess{
		@Override
		public String getCDA_DV(int index) {
			return super.getCDA_DV(index);
		}

		@Override
		public String getDV(int index) {
			//return Camera.sharpnessPref;
			return mContext.getString(R.string.pref_camera_sharpness_default);
		}
    };

    private static class GetBrightness_DV extends DVProcess{
		@Override
		public String getCDA_DV(int index) {
			return super.getCDA_DV(index);
		}

		@Override
		public String getDV(int index) {
			//return Camera.brightnessPref;
			return mContext.getString(R.string.pref_camera_brightness_default);
		}
    };

    private static class GetGrid_DV extends DVProcess{
		@Override
		public String getCDA_DV(int index) {
			return super.getCDA_DV(index);
		}

		@Override
		public String getDV(int index) {
			return mContext.getString(R.string.fih_pref_camera_grid_default);
		}
    };

    private static class GetShutterSound_DV extends DVProcess{
		@Override
		public String getCDA_DV(int index) {
			return super.getCDA_DV(index);
		}

		@Override
		public String getDV(int index) {
			return mContext.getString(R.string.fih_pref_camera_shutter_sound_default);
		}
    };

    private static class GetStoreLocation_DV extends DVProcess{
		@Override
		public String getCDA_DV(int index) {
			return super.getCDA_DV(index);
		}

		@Override
		public String getDV(int index) {
			return mContext.getString(R.string.pref_camera_recordlocation_default);
		}
    };

    private static class GetWhiteBalance_DV extends DVProcess{
		@Override
		public String getCDA_DV(int index) {
			return super.getCDA_DV(index);
		}

		@Override
		public String getDV(int index) {
			return mContext.getString(R.string.pref_camera_whitebalance_default);
		}
    };

    private static class GetFlashMode_DV extends DVProcess{
		@Override
		public String getCDA_DV(int index) {
			return super.getCDA_DV(index);
		}

		@Override
		public String getDV(int index) {
			return mContext.getString(R.string.pref_camera_flashmode_default);
		}
    };

    private static class GetVideoSize_DV extends DVProcess{
		@Override
		public String getCDA_DV(int index) {
			String result = super.getCDA_DV(index);
            if((null != result) && (0 != result.length())){			
				if(0 == result.compareToIgnoreCase("VGA"))
				{
					result = "640x480";
				}
				else if(0 == result.compareToIgnoreCase("CIF"))
				{
					result = "352x288";
				}
				else if(0 == result.compareToIgnoreCase("QVGA"))
				{
					result = "320x240";
				}
				else if(0 == result.compareToIgnoreCase("QCIF"))
				{
					result = "176x144";
				}
				else if(0 == result.compareToIgnoreCase("WVGA"))
                {
                    result = "800x480";
                }
				else if(0 == result.compareToIgnoreCase("480P"))
                {
                    result = "720x480";
                }
				else
				{
					result = null;
				}
		    }
            Log.i(TAG,"GetVideoSize_DV result:"+result);
			return result;
		}

		@Override
		public String getDV(int index) {
			return mContext.getString(R.string.pref_camera_videosize_default);
		}
    };

    private static class GetVideoEncoder_DV extends DVProcess{
		@Override
		public String getCDA_DV(int index) {
			String result = super.getCDA_DV(index);
            if((null != result) && (0 != result.length())){			
				if(0 == result.compareToIgnoreCase("MPEG4"))
				{
					result = "m4v";
				}
				else if(0 == result.compareToIgnoreCase("H.263"))
				{
					result = "h263";
				}
				else if(0 == result.compareToIgnoreCase("H.264"))
				{
					//Temp solution to avoid the default encoder is "H.264".				
					result = "m4v";
					//result = "h264";
				}
				else
				{
					result = null;
				}
		    }
			return result;
		}

		@Override
		public String getDV(int index) {
			return mContext.getString(R.string.pref_camera_videoencoder_default);
		}
    };

    private static class GetVideoDuration_DV extends DVProcess{
		@Override
		public String getCDA_DV(int index) {
			String result = super.getCDA_DV(index);
            if((null != result) && (0 != result.length())){			
				if(0 == result.compareToIgnoreCase("30 second(MMS)"))
				{
					result = "-1";
				}
				else if(0 == result.compareToIgnoreCase("10 minutes"))
				{
					result = "10";
				}
				else if(0 == result.compareToIgnoreCase("30 minutes"))
				{
					result = "30";
				}
				else
				{
					result = null;
				}
			}
			return result;
		}

		@Override
		public String getDV(int index) {
			return mContext.getString(R.string.pref_camera_video_duration_default);
		}
    };

    private static class GetVideoQuality_DV extends DVProcess{
		@Override
		public String getCDA_DV(int index) {
			String result = super.getCDA_DV(index);
			//Fit framework/base/..../camcorderProfile.java
            if((null != result) && (0 != result.length())){			
				if(0 == result.compareToIgnoreCase("High(30m)"))
				{
					//result = "high";
					//Quality level corresponding to the highest available resolution.QUALITY_HIGH = 1;
					result = "1";  
				}
				else if(0 == result.compareToIgnoreCase("Low(30m)"))
				{
					//result = "low";
					//Quality level corresponding to the lowest available resolution.
					result = "0";  
				}
				else if(0 == result.compareToIgnoreCase("MMS(Low,30s)"))
				{
					//result = "mms";
					result = "0";  
				}
				else if(0 == result.compareToIgnoreCase("YouTub(High, 10m)"))
				{
					//result = "youtube";
					result = "1";  
				}
				else if(0 == result.compareToIgnoreCase("1080P"))
				{
					result = "6";  //(1920 x 1088) 
				}
				else if(0 == result.compareToIgnoreCase("720P"))
				{
					result = "5";  //(1280 x 720)
				}
				else if(0 == result.compareToIgnoreCase("480P"))
				{
					result = "4";  //(720 x 480)
				}
				else if(0 == result.compareToIgnoreCase("CIF"))
				{
					result = "3";  //(352 x 288)
				}
				else if(0 == result.compareToIgnoreCase("QCIF"))
				{
					result = "2";  //(176 x 144)
				}
				else if(0 == result.compareToIgnoreCase("custom"))
				{
					//result = "custom";
					//Quality level corresponding to the highest available resolution.QUALITY_HIGH = 1;
					result = "1";
				}
				else
				{
					//result = null;
					Log.e(TAG, "CDA Video size Null: set 720x480 ");
					result = "4";  //(720 x 480)
				}
		    }
			return result;
		}

		@Override
		public String getDV(int index) {
			return mContext.getString(R.string.pref_video_quality_default);
		}
    };

    private static DVItem[] mDVTable = new DVItem[]{
        new DVItem("PictureSize",       null, null, new GetPictureSize_DV()), //mIndex_Camera_Picture_Size
        new DVItem("PictureQuality",    null, null, new GetPictureQuality_DV()), //mIndex_Camera_Picture_Quality
        new DVItem("Effect",            null, null, new GetEffect_DV()), //mIndex_Camera_Effect
        new DVItem("MeteringMode",      null, null, new GetMeteringMode_DV()), //mIndex_Camera_Metering_Mode
        new DVItem("AntiBanding",       null, null, new GetAntiBanding_DV()), //mIndex_Camera_Anti_Banding
        new DVItem("Saturation",        null, null, new GetSaturation_DV()), //mIndex_Camera_Saturation
        new DVItem("Contrast",          null, null, new GetContrast_DV()), //mIndex_Camera_Contrast
        new DVItem("Sharpness",         null, null, new GetSharpness_DV()), //mIndex_Camera_Sharpness
        new DVItem("Brightness",        null, null, new GetBrightness_DV()), //mIndex_Camera_Brightness
        new DVItem("Grid",              null, null, new GetGrid_DV()), //mIndex_Camera_Grid
        new DVItem("ShutterSound",      null, null, new GetShutterSound_DV()), //mIndex_Camera_Shutter_Sound
        new DVItem("StoreLocation",     null, null, new GetStoreLocation_DV()), //mIndex_Camera_Store_Location
        new DVItem("WhiteBalance",      null, null, new GetWhiteBalance_DV()), //mIndex_Camera_White_Balance
        new DVItem("FlashMode",         null, null, new GetFlashMode_DV()), //mIndex_Camera_Flash_Mode
        new DVItem("VideoSize",         null, null, new GetVideoSize_DV()), //mIndex_Video_Size
        new DVItem("VideoEncoder",      null, null, new GetVideoEncoder_DV()), //mIndex_Video_Encoder
        new DVItem("VideoDuration",     null, null, new GetVideoDuration_DV()), //mIndex_Video_Duration
        new DVItem("VideoQuality",      null, null, new GetVideoQuality_DV()), //mIndex_Video_Quality
    };

    public DefaultValue(Context c)
    {    
    	mContext = c;
        Resources r = c.getResources();
        TypedValue outValue = new TypedValue();

        try
        {

            r.getValue("@FIHCDA@isCDAValid", outValue, false);
            if(outValue.coerceToString().equals("true"))
            {
                if(null == mCDAXml)
                {
                    r.getValue(mCDACmd, outValue, false);
                    String xmlstr = outValue.coerceToString().toString();
                    if((null != xmlstr) && (0 != xmlstr.length()))
                    {
                        mCDAXml = xmlstr;
                    }
                }
                //mCDAXml = "  <Camera-Settings>     <PictureSize>5M</PictureSize> <PictureQuality>Super fine</PictureQuality>     <Effect>None</Effect>     <MeteringMode>Frame Average</MeteringMode>     <AntiBanding>Off</AntiBanding>     <Saturation>2</Saturation>     <Contrast>2</Contrast>     <Sharpness>0</Sharpness>     <Brightness>3</Brightness>     <Grid>off</Grid>     <ShutterSound>on</ShutterSound>     <StoreLocation>off</StoreLocation>     <WhiteBalance>Auto</WhiteBalance>     <FlashMode>Auto</FlashMode>     <VideoSize>VGA</VideoSize>     <VideoEncoder>H.264</VideoEncoder>     <VideoDuration>30 minutes</VideoDuration>     <VideoQuality>High(30m)</VideoQuality>   </Camera-Settings>";
                if((null != mCDAXml) && (0 != mCDAXml.length()) && (0 != mDVTable.length))
                {
                    Element root = null;
                    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                    DocumentBuilder db = dbf.newDocumentBuilder();
                    Document xmldoc = db.parse(new InputSource(new StringReader(mCDAXml)));
                    root = xmldoc.getDocumentElement();
                    for(int i = 0; i < mDVTable.length; i++)
                    {
                        NodeList result = root.getElementsByTagName(mDVTable[i].cdaTag);
                        try
                        {
                            String cdaValue = result.item(0).getFirstChild().getNodeValue();
                            if((null != cdaValue) && (0 != cdaValue.length()))
                            {
                                mDVTable[i].cdaValue = cdaValue;
                            }
                        }
                        catch(Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }


            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public static DefaultValue getDefaultValueObject(Context c)
    {
        if(null == mDV)
        {
            mDV = new DefaultValue(c);
        }
        return mDV;
    }

    public static DefaultValue getDefaultValueObject()
    {
        return mDV;
    }

    public String getDefaultValue(int id)
    {
    	String result = null;
    	Log.e(TAG, "Run getDefaultValue");
    	if(null != mDVTable[id].proc)
    	{
    		String CDAresult = null;
    	    //Log.e(TAG, "mDVTable not null");
    		CDAresult = mDVTable[id].proc.getCDA_DV(id);
            //Log.e(TAG, "CDAresult: " + CDAresult);     		
    		if((null != CDAresult) && (0 != CDAresult.length()))
    		{    		
    			result = CDAresult;
                //Log.e(TAG, "CDAresult2: " + CDAresult);    			
    		}
    		if((null == result) || (0 == result.length()))
    		{
    			result = null;
    			String origResult = null;
    			origResult = mDVTable[id].proc.getDV(id);
        		if((null != origResult) && (0 != origResult.length()))
        		{
        			result = origResult;
        			 Log.e(TAG, "origResult: " + origResult);  
        		}
    		}
    	}

        return result;
    }
}

//FihtdcCode@20111121 Peiming Wang add ICS begin*****
/*
 * Provide a mapping for Jpeg encoding quality levels
 * from String representation to numeric representation.
 */
class JpegEncodingQualityMappings {
    private static final String TAG = "JpegEncodingQualityMappings";
    private static final int DEFAULT_QUALITY = 85;
    private static HashMap<String, Integer> mHashMap =
            new HashMap<String, Integer>();

    static {
        mHashMap.put("normal",    CameraProfile.QUALITY_LOW);
        mHashMap.put("fine",      CameraProfile.QUALITY_MEDIUM);
        mHashMap.put("superfine", CameraProfile.QUALITY_HIGH);
    }

    // Retrieve and return the Jpeg encoding quality number
    // for the given quality level.
    public static int getQualityNumber(String jpegQuality) {
        Integer quality = mHashMap.get(jpegQuality);
        if (quality == null) {
            Log.w(TAG, "Unknown Jpeg quality: " + jpegQuality);
            return DEFAULT_QUALITY;
        }
        return CameraProfile.getJpegEncodingQualityParameter(quality.intValue());
    }
}
//FihtdcCode@20111121 Peiming Wang add ICS end*****

