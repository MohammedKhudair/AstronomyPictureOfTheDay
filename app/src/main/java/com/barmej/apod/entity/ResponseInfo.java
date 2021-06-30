package com.barmej.apod.entity;

public class ResponseInfo{

	private String mediaType;

	private String hdurl;

	private String explanation;

	private String title;

	private String url;


	public void setMediaType(String mediaType){
		this.mediaType = mediaType;
	}

	public String getMediaType(){
		return mediaType;
	}

	public void setHdurl(String hdurl){
		this.hdurl = hdurl;
	}

	public String getHdurl(){
		return hdurl;
	}

	public void setExplanation(String explanation){
		this.explanation = explanation;
	}

	public String getExplanation(){
		return explanation;
	}

	public void setTitle(String title){
		this.title = title;
	}

	public String getTitle(){
		return title;
	}

	public void setUrl(String url){
		this.url = url;
	}

	public String getUrl(){
		return url;
	}
}