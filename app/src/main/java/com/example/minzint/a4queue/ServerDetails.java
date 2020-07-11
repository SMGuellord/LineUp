package com.example.minzint.a4queue;

public class ServerDetails {

    // ADDRESSES FOR THE API METHODS

    final static String SERVER_ADDRESS = "lineup.gq";
    final static String PORT = "80";
    public static final String BASE_URL = "http://"+SERVER_ADDRESS+"/";

    final static String PROFILE_DIR_URL = BASE_URL;
    final static String LOGIN_URL = BASE_URL +"mobile_app/login_mobile.php";
    final static String REGISTER_URL = BASE_URL +"mobile_app//register_mobile.php";
    final static String DELETE_ACCOUNT_URL = BASE_URL +"mobile_app//delete_account_mobile.php";
    final static String REACTIVATE_ACCOUNT_URL = BASE_URL +"mobile_app//reactivate_account_mobile.php";
    final static String UPDATE_DETAILS_URL = BASE_URL +"mobile_app//update_user.php";
    final static String MATCHING_URL = BASE_URL +"mobile_app//matching_mobile.php";
    final static String MATCHING_CHECK_URL = BASE_URL+"mobile_app//match_check_mobile.php";
    final static String MATCHING_DELETE = BASE_URL+"mobile_app//delete_match_db.php";
    final static String MATCHING_UPDATE = BASE_URL+"mobile_app//matching_update_match.php";
    final static String UPDATE_USER_DETAILS = BASE_URL+"mobile_app//updateUserLocation.php";
    final static String UPDATE_NOTIFICATION_DETAILS = BASE_URL+"mobile_app//update_booking_notification_50.php";
    final static String UPDATE_NOTIFICATION_DETAILS_75 = BASE_URL+"mobile_app//update_booking_notification_75.php";
    final static String UPDATE_NOTIFICATION_DETAILS_90 = BASE_URL+"mobile_app//update_booking_notification_90.php";
    final static String CHECK_NOTIFICATION = BASE_URL+"mobile_app//check_notificaion.php";
    final static String GET_USER_DETAILS = BASE_URL+"mobile_app//get_user_details.php";
    final static String QUEUE_UPDATE = BASE_URL+"mobile_app//queue_update.php";
    final static String QUEUE_UPDATE_END_QUEUE = BASE_URL+"mobile_app//update_booking_end_queue.php";
    final static String UPDATE_QUEUE_STARTED = BASE_URL+"mobile_app//update_queue_started.php";
    final static String INSERT_USER_REVIEWS = BASE_URL+"mobile_app//insert_user_reviews.php";
    final static String FIND_RANDOM_USER = BASE_URL+"mobile_app//find_random_user.php";
    final static String ADD_RECIEPT = BASE_URL+"mobile_app//add_reciept.php";
    final static String GET_QUEUE_DETAILS = BASE_URL+"mobile_app//resume_queue.php";
    final static String SAVE_QUEUE_STATE = BASE_URL+"mobile_app//save_queue_state.php";
    final static String DELETE_QUEUE_STATE = BASE_URL+"mobile_app//delete_queue_state.php";
    final static String UPDATE_MATCH_FROM_BOOKING = BASE_URL+"/mobile_app//update_match_from_booking.php";
    final static String CREATE_SETTINGS_ROW = BASE_URL+"mobile_app//enter_settings.php";
    final static String UPDATE_SETTINGS = BASE_URL+"mobile_app//update_settings.php";
    final static String GET_SETTINGS = BASE_URL+"mobile_app//get_settings.php";
    final static String CREATE_QUEUE = BASE_URL+"mobile_app//create_queue.php";
    final static String GET_BOOKING_DATES = BASE_URL+"mobile_app//get_user_booking_dates.php";
    final static String GET_BOOKING_TIMES = BASE_URL+"mobile_app//get_user_booking_times.php";
    final static String GET_USERS_NEW = BASE_URL+"mobile_app//get_users.php";
    final static String FIND_USERS = BASE_URL+"api.php/users/";

}
