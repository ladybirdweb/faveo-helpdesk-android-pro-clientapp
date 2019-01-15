package co.faveo.helpdesk.pro.client.application;

import android.util.Log;

import com.pixplicity.easyprefs.library.Prefs;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * In this class we are basically setting the API
 * which we need to call ,so when ever we need to call that API we wil create the object for this class.
 * This class contains all of the API's which we have used in our application.
 */
public class Helpdesk {

    public static String token;
    static String apiKey;
    static String IP;
    String domain = Prefs.getString("domain", null);
    String newurl = Prefs.getString("companyUrl", null);

    //Log.d("newurl",newurl);
    public Helpdesk() {
        apiKey = Constants.API_KEY;
        token = Prefs.getString("TOKEN", "");
        //token = Preference.getToken();
        IP = null;
    }

    public String getBaseURL(String companyURL) {
        Log.d("checkingURL", companyURL + "api/v1/helpdesk/url?url=" + companyURL.substring(0, companyURL.length() - 1) + "&api_key=" + apiKey);
        Prefs.putString("companyurl", companyURL + "api/v2/helpdesk/");
        Log.d("companyUrl", Prefs.getString("companyurl", null));
        //Constants.URL1=Prefs.getString("companyurl",null);
        //Log.d("Constants.URL1",Constants.URL1);


        return new HTTPConnection().HTTPResponseGet(companyURL + "api/v1/helpdesk/url?url=" + companyURL.substring(0, companyURL.length() - 1) + "&api_key=" + apiKey);
    }

    public String postCreateTicket(String subject, String body, int helpTopic,
                                   int priority, String fname, String email) {
        Log.d("postCreateTicketAPI", Constants.URL + "helpdesk/create/satellite/ticket?" +
                "token=" + token +
                "&subject=" + subject +
                "&body=" + body +
                "&help_topic=" + helpTopic +
                // "&sla=" + sla +
                "&priority=" + priority +
                //"&dept=" + dept +
                "&first_name=" + fname +
                "&email=" + email);
        Prefs.putString("createTicketApi", Constants.URL + "helpdesk/create/satellite/ticket?" +
                "token=" + token +
                "&subject=" + subject +
                "&body=" + body +
                "&help_topic=" + helpTopic +
                // "&sla=" + sla +
                "&priority=" + priority +
                //"&dept=" + dept +
                "&first_name=" + fname +
                "&email=" + email);

        String result = new HTTPConnection().HTTPResponsePost(Constants.URL + "helpdesk/create/satellite/ticket?" +
                "token=" + token +
                "&subject=" + subject +
                "&body=" + body +
                "&help_topic=" + helpTopic +
                // "&sla=" + sla +
                "&priority=" + priority +
                // "&dept=" + dept +
                "&first_name=" + fname +
                "&email=" + email, null);

        if (result != null && result.equals("tokenRefreshed"))
            return new HTTPConnection().HTTPResponsePost(Constants.URL + "helpdesk/create/satellite/ticket?" +
                    "token=" + token +
                    "&subject=" + subject +
                    "&body=" + body +
                    "&help_topic=" + helpTopic +
                    // "&sla=" + sla +
                    "&priority=" + priority +
                    //  "&dept=" + dept +
                    "&first_name=" + fname +
                    "&email=" + email, null);
        return result;
    }

    public String postReplyTicket(String ticketID, String replyContent) {
        Log.d("ReplyTicketAPI", Constants.URL + "helpdesk/reply/withdetails?" +
                "api_key=" + apiKey +
                "&ip=" + IP +
                "&token=" + token +
                "&ticket_id=" + ticketID +
                "&reply_content=" + replyContent);
        String result = new HTTPConnection().HTTPResponsePost(Constants.URL + "helpdesk/reply/withdetails?" +
                        "api_key=" + apiKey +
                        "&ip=" + IP +
                        "&token=" + token +
                        "&ticket_id=" + ticketID +
                        "&reply_content=" + replyContent,
                null);
        if (result != null && result.equals("tokenRefreshed"))
            return new HTTPConnection().HTTPResponsePost(Constants.URL + "helpdesk/reply/withdetails?" +
                    "api_key=" + apiKey +
                    "&ip=" + IP +
                    "&token=" + token +
                    "&ticket_id=" + ticketID +
                    "&reply_content=" + replyContent, null);
        return result;
    }

    public String postFCMToken(String token, String ID) {
        Log.d("FCM token beforesending", token + "");
        String parameters = null;
        JSONObject obj = new JSONObject();
        try {
            obj.put("fcm_token", token);
            obj.put("user_id", ID);
            parameters = obj.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("fcm call", Constants.URL + "fcmtoken?");
        return new HTTPConnection().HTTPResponsePost(Constants.URL + "fcmtoken?", parameters);
    }

    public String getCheckBillingURL(String baseURL) {
        Log.d("getBillingURL", Constants.BILLING_URL + "?url=" + baseURL);
        return new HTTPConnection().HTTPResponseGet(Constants.BILLING_URL + "?url=" + baseURL);
    }


    public String getTicketDetail(String ticketID) {
        Log.d("TicketDetailAPI", Constants.URL + "helpdesk/ticket?api_key=" + apiKey + "&ip=" + IP + "&token=" + token + "&id=" + ticketID);
        String result = new HTTPConnection().HTTPResponseGet(Constants.URL + "helpdesk/ticket?api_key=" + apiKey + "&ip=" + IP + "&token=" + token + "&id=" + ticketID);
        if (result != null && result.equals("tokenRefreshed"))
            return new HTTPConnection().HTTPResponseGet(Constants.URL + "helpdesk/ticket?api_key=" + apiKey + "&ip=" + IP + "&token=" + token + "&id=" + ticketID);
        return result;
    }

    public String getTicketThread(String ticketID) {
        Log.d("TicketThreadAPI", Constants.URL + "helpdesk/ticket-thread?api_key=" + apiKey + "&ip=" + IP + "&token=" + token + "&id=" + ticketID);
        String result = new HTTPConnection().HTTPResponseGet(Constants.URL + "helpdesk/ticket-thread?api_key=" + apiKey + "&ip=" + IP + "&token=" + token + "&id=" + ticketID);
        if (result != null && result.equals("tokenRefreshed"))
            return new HTTPConnection().HTTPResponseGet(Constants.URL + "helpdesk/ticket-thread?api_key=" + apiKey + "&ip=" + IP + "&token=" + token + "&id=" + ticketID);
        return result;
    }

    public String nextPageURL(String URL) {
        //Log.d("URL",URL);
        // int lastSlash = URL.lastIndexOf("/");
        // URL = URL.substring(0, lastSlash) + URL.substring(lastSlash + 1, URL.length());
        Log.d("nextPageURLAPI", URL + "&api_key=" + apiKey + "&token=" + token);
        String result = new HTTPConnection().HTTPResponseGet(URL + "&api_key=" + apiKey + "&token=" + token);
        if (result != null && result.equals("tokenRefreshed"))
            return new HTTPConnection().HTTPResponseGet(URL + "&api_key=" + apiKey + "&token=" + token);
        return result;
    }

    public String nextpageurl(String show, int page) {
        Log.d("Inboxapi", newurl + "api/v2/helpdesk/get-tickets?token=" + token + "&api=1&show=" + show + "&departments=all&page=" + page);
        String result = new HTTPConnection().HTTPResponseGet(newurl + "api/v2/helpdesk/get-tickets?token=" + token + "&api=1&show=" + show + "&departments=all&page=" + page);
        if (result != null && result.equals("tokenRefreshed"))
            return new HTTPConnection().HTTPResponseGet(newurl + "api/v2/helpdesk/get-tickets?token=" + token + "&api=1&show=" + show + "&departments=all&page=" + page);
        //Log.d("URL",result);
        return result;

    }


    public String nextPageURL(String URL, String userID) {

        Log.d("nextPageURLAPI", URL + "&api_key=" + apiKey + "&token=" + token + "&user_id=" + userID);
        String result = new HTTPConnection().HTTPResponseGet(URL + "&api_key=" + apiKey + "&token=" + token + "&user_id=" + userID);
        if (result != null && result.equals("tokenRefreshed"))
            return new HTTPConnection().HTTPResponseGet(URL + "&api_key=" + apiKey + "&token=" + token + "&user_id=" + userID);
        return result;
    }

    public String getTicketsByAgent() {
        Log.d("MYticketAPI", Constants.URL + "helpdesk/user/ticket/list?token=" + token);
        String result = new HTTPConnection().HTTPResponseGet(Constants.URL + "helpdesk/user/ticket/list?token=" + token);
        if (result != null && result.equals("tokenRefreshed"))
            return new HTTPConnection().HTTPResponseGet(Constants.URL + "helpdesk/user/ticket/list?token=" + token);
//        Log.d("URL",result);
        return result;
    }

    public String getTicketsByAgentWithStatus(String status) {
        Log.d("MYticketAPI", Constants.URL + "helpdesk/user/ticket/list?token=" + token + "&status=" + status);
        String result = new HTTPConnection().HTTPResponseGet(Constants.URL + "helpdesk/user/ticket/list?token=" + token + "&status=" + status);
        if (result != null && result.equals("tokenRefreshed"))
            return new HTTPConnection().HTTPResponseGet(Constants.URL + "helpdesk/user/ticket/list?token=" + token + "&status=" + status);
//        Log.d("URL",result);
        return result;
    }


    public String getTicketsByUser(String userID) {
        Log.d("TicketsByUserAPI", Constants.URL + "helpdesk/user?token=" + token + "&user_id=" + userID);
        String result = new HTTPConnection().HTTPResponseGet(Constants.URL + "helpdesk/user?token=" + token + "&user_id=" + userID);
        if (result != null && result.equals("tokenRefreshed"))
            return new HTTPConnection().HTTPResponseGet(Constants.URL + "helpdesk/user?token=" + token + "&user_id=" + userID);
        return result;
    }

    public String getDependency() {
        Log.d("DependencyAPI", Constants.URL + "helpdesk/dependency?api_key=" + apiKey + "&ip=" + IP + "&token=" + token);
        String result = new HTTPConnection().HTTPResponseGet(Constants.URL + "helpdesk/dependency?api_key=" + apiKey + "&ip=" + IP + "&token=" + token);
        if (result != null && result.equals("tokenRefreshed")) {
            return new HTTPConnection().HTTPResponseGet(Constants.URL + "helpdesk/dependency?api_key=" + apiKey + "&ip=" + IP + "&token=" + token);
        }
        return result;
    }

    public String postStatusChanged(String ticketID, int statusID) {
        Log.d("StatusChangedApi", Constants.URL1 + "helpdesk/status/change?api_key=" + apiKey + "&token=" + token + "&ticket_id=" + ticketID + "&status_id=" + statusID);
        String result = new HTTPConnection().HTTPResponsePost(Constants.URL1 + "helpdesk/status/change?api_key=" + apiKey + "&token=" + token + "&ticket_id=" + ticketID + "&status_id=" + statusID, null);
        if (result != null && result.equals("tokenRefreshed"))
            return new HTTPConnection().HTTPResponsePost(Constants.URL1 + "helpdesk/status/change?api_key=" + apiKey + "&token=" + token + "&ticket_id=" + ticketID + "&status_id=" + statusID, null);
        return result;
    }

    public String postStatusChangedMultiple(String ticketID, int statusID) {
        Log.d("StatusChangedApi", Constants.URL1 + "helpdesk/status/change?api_key=" + apiKey + "&token=" + token + "&ticket_id=" + ticketID + "&status_id=" + statusID);
        String result = new HTTPConnection().HTTPResponsePost(Constants.URL1 + "helpdesk/status/change?api_key=" + apiKey + "&token=" + token + "&ticket_id=" + ticketID + "&status_id=" + statusID, null);
        if (result != null && result.equals("tokenRefreshed"))
            return new HTTPConnection().HTTPResponsePost(Constants.URL1 + "helpdesk/status/change?api_key=" + apiKey + "&token=" + token + "&ticket_id=" + ticketID + "&status_id=" + statusID, null);
        return result;
    }
//
//    public String nextPageUrlFilter(String url,int page){
//        Log.d("ticketFiltrationApi",newurl + "api/v2/helpdesk/get-tickets?token=" + token + "&api=1&"+url+"&page="+page);
//        String result=new HTTPConnection().HTTPResponseGet(newurl + "api/v2/helpdesk/get-tickets?token=" + token + "&api=1&"+url+"&page="+page);
//        if (result!=null&&result.equals("tokenRefreshed"))
//            return new HTTPConnection().HTTPResponseGet(domain+Constants.URL1 + "api/v2/helpdesk/get-tickets?token=" + token + "&api=1&"+url+"&page="+page);
//        return result;
//    }
//    public String nextPagecustomerFiltration(int page,String url){
//        Log.d("customerFiltration",newurl + "api/v2/helpdesk/user/filter?token=" + token + "&api=1&"+url+"&page="+page);
//        String result=new HTTPConnection().HTTPResponseGet(newurl + "api/v2/helpdesk/user/filter?token=" + token + "&api=1&"+url+"&page="+page);
//        if (result!=null&&result.equals("tokenRefreshed"))
//            return  new HTTPConnection().HTTPResponseGet(newurl + "api/v2/helpdesk/user/filter?token=" + token + "&api=1&"+url+"&page="+page);
//        return result;
//    }

    public String saveCustomerDetails(String firstname, String lastname, String username) {
        Log.d("editCustomerApi", Constants.URL1 + "helpdesk/user/edit?token=" + token + "&first_name=" + firstname + "&last_name=" + lastname + "&user_name=" + username);
        String result = new HTTPConnection().HTTPResponsePatch(Constants.URL1 + "helpdesk/user/edit?token=" + token + "&first_name=" + firstname + "&last_name=" + lastname + "&user_name=" + username, null);
        if (result != null && result.equals("tokenRefreshed"))
            return new HTTPConnection().HTTPResponsePatch(Constants.URL1 + "helpdesk/user/edit?token=" + token + "&first_name=" + firstname + "&last_name=" + lastname + "&user_name=" + username, null);
        return result;

    }
//    public String nextPageURLForSearching(String URL,String querry) {
//        //Log.d("URL",URL);
//        // int lastSlash = URL.lastIndexOf("/");
//        // URL = URL.substring(0, lastSlash) + URL.substring(lastSlash + 1, URL.length());
//        Log.d("nextPageURLSearching", URL + "&api_key=" + apiKey + "&token=" + token+"&search="+querry);
//        String result = new HTTPConnection().HTTPResponseGet(URL + "&api_key=" + apiKey + "&token=" + token+"&search="+querry);
//        if (result != null && result.equals("tokenRefreshed"))
//            return new HTTPConnection().HTTPResponseGet(URL + "&api_key=" + apiKey + "&token=" + token+"&search="+querry);
//        return result;
//    }

//        public String customerFeedback(String subject,String message){
//        Log.d("customerFeedback",Constants.URL + "helpdesk/helpsection/mails?token="+token+"&help_email=faveoservicedesk@gmail.com&help_subject="+subject+"&help_massage="+message);
//        String result=new HTTPConnection().HTTPResponsePost(Constants.URL + "helpdesk/helpsection/mails?token="+token+"&help_email=faveoservicedesk@gmail.com&help_subject="+subject+"&help_massage="+message,null);
//        if (result!=null&&result.equals("tokenRefreshed"))
//            return new HTTPConnection().HTTPResponsePost(Constants.URL + "helpdesk/helpsection/mails?token="+token+"&help_email=faveoservicedesk@gmail.com&help_subject="+subject+"&help_massage="+message,null);
//            return result;
//        }

    public String changePassword(String oldPass, String newPass, String confirmPass) {
        Log.d("passwordchange", Constants.URL1 + "helpdesk/user/change/password?token=" + token + "&old_password=" + oldPass + "&new_password=" + newPass + "&confirm_password=" + confirmPass);
        String result = new HTTPConnection().HTTPResponsePost(Constants.URL1 + "helpdesk/user/change/password?token=" + token + "&old_password=" + oldPass + "&new_password=" + newPass + "&confirm_password=" + confirmPass, null);
        if (result != null && result.equals("tokenRefreshed"))
            return new HTTPConnection().HTTPResponsePost(Constants.URL1 + "helpdesk/user/change/password?token=" + token + "&old_password=" + oldPass + "&new_password=" + newPass + "&confirm_password=" + confirmPass, null);
        return result;
    }


}
