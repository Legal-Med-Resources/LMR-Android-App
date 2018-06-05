package com.example.util;

import java.io.Serializable;

public class Constant implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public static String SERVER_URL = "http://www.viaviweb.in/envato/cc/android_yellow_pages_demo/";

	public static final String IMAGE_PATH=SERVER_URL + "images/";

	public static final String CATEGORY_URL = SERVER_URL + "api.php?cat_list";

	public static final String LATEST_URL = SERVER_URL + "api.php?latest";

	public static final String CATEGORY_ITEM_URL = SERVER_URL + "api.php?cat_id=";

	public static final String ABOUT_URL = SERVER_URL + "api.php";

	public static final String SEARCH_URL = SERVER_URL + "api.php?search_text=";

	public static final String SINGLE_LISTING_URL = SERVER_URL + "api.php?listing_id=";

	public static final String HOME_URL = SERVER_URL + "api.php?home";

	public static final String ARRAY_NAME="YELLOW_PAGE_APP";

	public static final String CATEGORY_NAME="category_name";
	public static final String CATEGORY_CID="cid";
	public static final String CATEGORY_IMAGE="category_image";

	public static final String LISTING_ID="id";
	public static final String LISTING_NAME="listing_name";
	public static final String LISTING_DESC="listing_description";
	public static final String LISTING_ADDRESS="listing_address";
	public static final String LISTING_EMAIL="listing_email";
	public static final String LISTING_PHONE="listing_phone";
	public static final String LISTING_WEBSITE="listing_website";
	public static final String LISTING_LATITUDE="listing_latitude";
	public static final String LISTING_LONGITUDE="listing_longitude";
	public static final String LISTING_IMAGE="listing_image_b";

	public static final String APP_NAME="app_name";
	public static final String APP_IMAGE="app_logo";
	public static final String APP_VERSION="app_version";
	public static final String APP_AUTHOR="app_author";
	public static final String APP_CONTACT="app_contact";
	public static final String APP_EMAIL="app_email";
	public static final String APP_WEBSITE="app_website";
	public static final String APP_DESC="app_description";
	public static final String APP_PRIVACY_POLICY="app_privacy_policy";

	public static int AD_COUNT=0;
	public static int AD_COUNT_SHOW=5;


}
