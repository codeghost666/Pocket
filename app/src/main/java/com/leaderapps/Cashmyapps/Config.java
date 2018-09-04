

/**
 * Configuration File 
 * Edit this data according to your Requirement
 *
 * @author DroidOXY
 */

package com.leaderapps.Cashmyapps;

public class Config
{

	// Adxmi AppId
	static String AppId ="37425ce3e3559005";

	// Adxmi AppSecret
	static String AppSecret ="faae7459f7a99a1a";

	// SuperSonic AppKey
	static String AppKey ="57ce89c5";

	// your UserId - see documentataion - or mail us - droid@oxywebs.com
	// Get your user id - http://yash.oxywebs.in/getting-userid-to-integrate-supersonic-into-pocketandroid/
	static String UserId ="4d2516a0-7364-4d5a-a90d-2a14f4af4fce";

	// Server URL ie., Webpanel Hosted Url -
	// must be http://folder.example.com/
	// do-not use http://example.com/folder/
	public static String Base_Url = "http://www.cashmyapps.com/";
	// public static String Base_Url = "http://example.com/";
	// public static String Base_Url = "http://pocket.example.com/";

	// Daily Reward Points
	static int daily_reward = 25;

	static String Push_Title = "Pocket v1.3";

	// Images for MainActivity
	static int[] images={R.drawable.ic_checkin,
			R.drawable.ic_about,
			R.drawable.ic_instructions,
			R.drawable.ic_video,
			R.drawable.ic_super_sonic,
			R.drawable.ic_adxmi,
			R.drawable.ic_redeem,
			R.drawable.ic_about
	};

	//Titles for MainActivity
	static String[] titles ={"Daily Check-In",
			"Invite",
			"Instructions",
			"Watch Videos",
			"SuperSonic OfferWall",
			"Adxmi OfferWall",
			"Redeem",
			"About"
	};

	//Description for MainActuvity Titles
	static String[] description={"Open Daily and Earn 25 Points",
			"Invite your friends",
			"How to Earn Points",
			"Watch Videos to Earn Points",
			"Install Apps and Earn Points",
			"Install Apps and Earn Points",
			"Turn your Points into Cash",
			"Advertise with Us"
	};

	//---------------------------------------------------
	//Images for Redeem Activity

	static int[] payout_images={
			R.drawable.ic_paypal_logo,
			R.drawable.ic_paypal_logo,
			R.drawable.ic_paytm,
			R.drawable.ic_paytm,
			R.drawable.ic_amazon_icon,
			R.drawable.ic_googleplay_icon};

	//Titles for Redeem Activity
	static String[] payout_titles ={
			"Paypal",
			"Paypal",
			"Paytm",
			"Paytm",
			"Amazon",
			"Google Play"};

	//Description for Redeem Activity Titles
	static String[] payout_description={
			"1000 Points = $1 USD",
			"5000 Points = $5 USD",
			"1000 Points = 100 INR",
			"5000 Points = 500 INR",
			"3000 Points = $2.5 USD",
			"9000 Points = $10 USD"};


	// Google Analytics OPTIONAL
	static String analytics_property_id = "UA-76982496-1";

	// Share text and link for Share Button
	static String share_text = "Hello, look what a beautiful app that I found here:";
	static String  share_link = "https://play.google.com/store/apps/details?id=com.leaderapps.Cashmyapps";

	// APP RATING
	static String rate_later = "Perhaps Later";
	static String rate_never = "No Thanks";
	static String rate_yes="Rate Now";
	static String rate_message = "We hope you enjoy using %1$s. Would you like to help us by rating us in the Store?";
	static String rate_title = "Enjoying our app?";

}