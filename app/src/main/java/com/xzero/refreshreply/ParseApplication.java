package com.xzero.refreshreply;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.PushService;
import com.xzero.refreshreply.activities.SignInActivity;
import com.xzero.refreshreply.models.Ad;
import com.xzero.refreshreply.models.Message;


//June needs to be a buyer
//Johne needs to be a seller.

public class ParseApplication extends Application {

    // App level variable to retain selected male/female value
    public static int currentPosition;

	@Override
	public void onCreate() {

		super.onCreate();

        Parse.enableLocalDatastore(this);
        ParseObject.registerSubclass(Ad.class);
        ParseObject.registerSubclass(Message.class);

        Parse.initialize(this, "iySQdLjCiKkwnV9LCIRbFkXrGd90ds4V1rY9QHRv", "WxmgKcnzutrTDq1C2q5wAy5Zbw3rYUnUTcSEJwp7");
        ParseInstallation.getCurrentInstallation().saveInBackground();
        Parse.setLogLevel(Parse.LOG_LEVEL_VERBOSE);

        PushService.setDefaultPushCallback(this, SignInActivity.class);

        // only one time installation:
/*
        ParseUser user = new ParseUser();
        user.setUsername("john");
        user.setPassword("123123");
        ParseGeoPoint point = new ParseGeoPoint(
                37.400385, -122.006132
        );
        user.put("location", point);

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                //dialog.dismiss();
                if (e != null) {
                    Log.d("Debug", "sign up");
                    // Show the error message
                    //Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                } else {
                    // Start an intent for the dispatch activity
                    //Intent intent = new Intent(SignUpActivity.this, DispatchActivity.class);
                    //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    //startActivity(intent);
                }
            }
        });
        */


//                Ad ad = new Ad();
//        ad.setCurrentStatus("Sale");
//        ad.setPrice("$100");
//        ad.setCategoryId("2");
//        ad.setAddress("1" + "2000  Blacow Rd Fremont, CA");
//        ad.setDescription("car for sell ");
//        ad.setLocation(new ParseGeoPoint(37.5059315, -121.9469625 ));
//        ad.setOwnerId("fINhuLpnZw");
//        ad.setTitle("New car");
//        ad.setOwnerName("john");
//        ad.setPhotoUrl("http://www.china-caraccessories.com/china-car-mat/rubber-car-mat-2303.jpg");
//        ad.saveInBackground();
//
//        ad = new Ad();
//        ad.setCurrentStatus("Sale");
//        ad.setPrice("$28");
//        ad.setCategoryId("2");
//        ad.setAddress("1" + "2000  Blacow Rd Fremont, CA");
//        ad.setDescription("car for sell ");
//        ad.setLocation(new ParseGeoPoint(37.5059315, -121.9469625 ));
//        ad.setOwnerId("fINhuLpnZw");
//        ad.setTitle("New car");
//        ad.setOwnerName("john");
//        ad.setPhotoUrl("hhttp://www.theonecar.com/wp-content/uploads/2014/03/accessories-for-cars-74.jpg");
//        ad.saveInBackground();
//
//
//        ad = new Ad();
//        ad.setCurrentStatus("Sale");
//        ad.setPrice("$21");
//        ad.setCategoryId("2");
//        ad.setAddress("1" + "2000  Blacow Rd Fremont, CA");
//        ad.setDescription("car for sell ");
//        ad.setLocation(new ParseGeoPoint(37.5059315, -121.9469625 ));
//        ad.setOwnerId("fINhuLpnZw");
//        ad.setTitle("New car");
//        ad.setOwnerName("john");
//        ad.setPhotoUrl("https://ukcaraccessories.files.wordpress.com/2014/01/car-accessories-seat-covers.jpg");
//        ad.saveInBackground();
//
//        ad = new Ad();
//        ad.setCurrentStatus("Sale");
//        ad.setPrice("$6");
//        ad.setCategoryId("2");
//        ad.setAddress("1" + "2000  Blacow Rd Fremont, CA");
//        ad.setDescription("car for sell ");
//        ad.setLocation(new ParseGeoPoint(37.5059315, -121.9469625 ));
//        ad.setOwnerId("fINhuLpnZw");
//        ad.setTitle("New car");
//        ad.setOwnerName("john");
//        ad.setPhotoUrl("        http://img02.taobaocdn.com/bao/uploaded/i2/14910020468838172/T1dC4EXtlXXXXXXXXX_!!0-item_pic.jpg");
//        ad.saveInBackground();



//        Ad ad = new Ad();
//        ad.setCurrentStatus("Sale");
//        ad.setPrice("$100");
//        ad.setCategoryId("1");
//        ad.setAddress("1" + "2000  Blacow Rd Fremont, CA");
//        ad.setDescription("car for sell ");
//        ad.setLocation(new ParseGeoPoint(37.5059315, -121.9469625 ));
//        ad.setOwnerId("fINhuLpnZw");
//        ad.setTitle("New car");
//        ad.setOwnerName("john");
//        ad.setPhotoUrl("http://www.affordablesportsandimports.net/post/dealerimagefiles/custom/117001/ss/2.jpg");
//        ad.saveInBackground();
//
//        ad = new Ad();
//        ad.setCurrentStatus("Sale");
//        ad.setPrice("$1");
//        ad.setCategoryId("1");
//        ad.setAddress("1" + "2000  Blacow Rd Fremont, CA");
//        ad.setDescription("car for sell ");
//        ad.setLocation(new ParseGeoPoint(37.5059315, -121.9469625 ));
//        ad.setOwnerId("fINhuLpnZw");
//        ad.setTitle("New car");
//        ad.setOwnerName("john");
//        ad.setPhotoUrl("http://media.caranddriver.com/images/media/638444/porsche-cayman-photo-640546-s-original.jpg");
//        ad.saveInBackground();
//
//        ad = new Ad();
//        ad.setCurrentStatus("Sale");
//        ad.setPrice("$50");
//        ad.setCategoryId("1");
//        ad.setAddress("1" + "2000  Blacow Rd Fremont, CA");
//        ad.setDescription("car for sell ");
//        ad.setLocation(new ParseGeoPoint(37.5059315, -121.9469625 ));
//        ad.setOwnerId("fINhuLpnZw");
//        ad.setTitle("New car");
//        ad.setOwnerName("john");
//        ad.setPhotoUrl("http://vignette2.wikia.nocookie.net/pixar/images/b/bd/Cat_tuners_car.png/revision/latest?cb=20121228220524");
//        ad.saveInBackground();
//
//        ad = new Ad();
//        ad.setCurrentStatus("Sale");
//        ad.setPrice("$35");
//        ad.setCategoryId("1");
//        ad.setAddress("1" + "2000  Blacow Rd Fremont, CA");
//        ad.setDescription("car for sell ");
//        ad.setLocation(new ParseGeoPoint(37.5059315, -121.9469625 ));
//        ad.setOwnerId("fINhuLpnZw");
//        ad.setTitle("New car");
//        ad.setOwnerName("john");
//        ad.setPhotoUrl("http://www.fancyicons.com/free-icons/145/racing-cars/png/256/sport_car_256.png");
//        ad.saveInBackground();

        //http://www.affordablesportsandimports.net/post/dealerimagefiles/custom/117001/ss/2.jpg
        //http://media.caranddriver.com/images/media/638444/porsche-cayman-photo-640546-s-original.jpg
        //http://vignette2.wikia.nocookie.net/pixar/images/b/bd/Cat_tuners_car.png/revision/latest?cb=20121228220524
        //http://www.fancyicons.com/free-icons/145/racing-cars/png/256/sport_car_256.png


//        for (int i = 1 ; i < 3; i++) {
//            Ad ad = new Ad();
//            ad.setCurrentStatus("Sale");
//            ad.setPrice("$" + i + "0");
//            ad.setCategoryId("1");
//            ad.setAddress(i + "1" + "20" + i + " Blacow Rd Fremont, CA");
//            ad.setDescription("car for sell " + i);
//            ad.setLocation(new ParseGeoPoint(37.5059315 + i, -121.9469625 - i));
//            int odd = i % 2;
//            if (odd == 1) {
//                ad.setOwnerId("fINhuLpnZw");
//                ad.setTitle("New car");
//                ad.setOwnerName("john");
//                ad.setPhotoUrl("http://3.bp.blogspot.com/-Y6k2hQsaIfY/Thv2bJPhbwI/AAAAAAAAAXw/YeGjaN-2GEM/s400/xasc.png");
//            } else {
//                ad.setTitle("Old car");
//                ad.setOwnerName("june");
//                ad.setOwnerId("Hh0IG6erg6");
//                ad.setPhotoUrl("http://wallpapers111.com/wp-content/uploads/2015/03/Old-Cars-Wallpaper-660x330.jpg");
//            }
//            ad.saveInBackground();
//        }
//
//        for (int i = 1 ; i < 3; i++) {
//            Ad ad = new Ad();
//            ad.setCurrentStatus("Sale");
//            ad.setPrice("$" + i + "0");
//            ad.setCategoryId("2");
//            ad.setAddress(i + "1" + "20" + i + " Blacow Rd Fremont, CA");
//            ad.setDescription("car mug for sell " + i);
//            ad.setLocation(new ParseGeoPoint(37.5059315 + i, -121.9469625 - i));
//            int odd = i % 2;
//            if (odd == 1) {
//                ad.setOwnerId("fINhuLpnZw");
//                ad.setOwnerName("john");
//                ad.setTitle("New Car Mug");
//                ad.setPhotoUrl("http://static.artfire.com/uploads/product/0/50/39050/3339050/3339050/large/blue_classic_car_mini_cooper_mug_art_decorative_gifts_collectible_0003_1989b1a8.jpg");
//            } else {
//                ad.setTitle("Old Car Mug");
//                ad.setOwnerId("Hh0IG6erg6");
//                ad.setOwnerName("june");
//                ad.setPhotoUrl("http://static.artfire.com/uploads/product/6/6/39006/3339006/3339006/large/red_classic_volk_beagle_mini_car_ceramic_mug_handmade_gifts_0002_7ac45798.jpg");
//            }
//            ad.saveInBackground();
//        }


/*
        ParseUser user2 = new ParseUser();
        user2.setUsername("june");
        user2.setPassword("123123");
        ParseGeoPoint point2 = new ParseGeoPoint(
                37.385383, -122.020552
        );
        user.put("location", point2);

        user2.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                //dialog.dismiss();
                if (e != null) {
                    Log.d("Debug", "sign up");
                    // Show the error message
                    //Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                } else {
                    // Start an intent for the dispatch activity
                    //Intent intent = new Intent(SignUpActivity.this, DispatchActivity.class);
                    //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    //startActivity(intent);
                }
            }
        });
*/
    }


}

