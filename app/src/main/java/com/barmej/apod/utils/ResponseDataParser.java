package com.barmej.apod.utils;

import com.barmej.apod.entity.ResponseInfo;

import org.json.JSONException;
import org.json.JSONObject;

public class ResponseDataParser {
    private static final String IMAGE = "image";

    // title of Picture
    private static final String OWM_title = "title";
    // Description of te picture
    private static final String OWM_Explanation = "explanation";
    // url of the media
    private static final String OWM_Url = "url";
    // HD url of the media
    private static final String OWM_HDurl = "hdurl";
    //Type of the media
    private static final String OWM_MediaType = "media_type";

    public static ResponseInfo getResponseInfoFromJson(String response) throws JSONException {

        JSONObject jsonObject = new JSONObject(response);

        ResponseInfo responseInfo = new ResponseInfo();

        responseInfo.setTitle(jsonObject.getString(OWM_title));
        responseInfo.setExplanation(jsonObject.getString(OWM_Explanation));
        responseInfo.setUrl(jsonObject.getString((OWM_Url)));
        responseInfo.setMediaType(jsonObject.getString(OWM_MediaType));

         String mediaType = jsonObject.getString(OWM_MediaType);
         if (mediaType.equals(IMAGE)){
             responseInfo.setHdurl(jsonObject.getString(OWM_HDurl));
         }

        return responseInfo;
    }

}
