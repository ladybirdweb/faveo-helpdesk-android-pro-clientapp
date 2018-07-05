package co.faveo.helpdesk.pro.client;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;



/**
 * This helper class is responsible for parsing ticket,client and for the notifications.
 * Here we are doing the JSON parsing for the particular model class.After
 * getting the json data we are creating object for that model class.
 */
public class Helper {
    /**
     * Tickets Page.
     * @param jsonArray refers to the array of JSON elements.
     * @param i position of the element in array.
     * @return object for ticket overview.
     */
    public static TicketOverview parseTicketOverview(JSONArray jsonArray, int i) {
        String agentName = null;
        try {
            //Date updated_at = null;

            String firstName = jsonArray.getJSONObject(i).getJSONObject("from").getString("first_name");
            String lastName = jsonArray.getJSONObject(i).getJSONObject("from").getString("last_name");
            String username = jsonArray.getJSONObject(i).getJSONObject("from").getString("user_name");
            // String email = jsonArray.getJSONObject(i).getString("email");
            //String profilePic= String.valueOf(android.R.drawable.ic_delete);
            String ticketNumber = jsonArray.getJSONObject(i).getString("ticket_number");
            String ID = jsonArray.getJSONObject(i).getString("id");
            //String title = jsonArray.getJSONObject(i).getString("ticket_title");
            String title=jsonArray.getJSONObject(i).getString("title");

//            String createdAt = jsonArray.getJSONObject(i).getString("created_at");
//            String departmentName = jsonArray.getJSONObject(i).getString("department_name");
//            String priorityName = jsonArray.getJSONObject(i).getString("priotity_name");
//            String slaPlanName = jsonArray.getJSONObject(i).getString("sla_plan_name");
//            String helpTopicName = jsonArray.getJSONObject(i).getString("help_topic_name");
            String ticketStatusName = jsonArray.getJSONObject(i).getString("status_name");
            //String ticketStatusName="open";
            String updatedAt = jsonArray.getJSONObject(i).getString("updated_at");
            //String priorityColor = jsonArray.getJSONObject(i).getJSONObject("priority").getString("color");
            //String attachment = jsonArray.getJSONObject(i).getString("attachment_count");

            //String last_replier=jsonArray.getJSONObject(i).getString("last_replier");
            if (jsonArray.getJSONObject(i).get("assignee")!=JSONObject.NULL) {
                if (jsonArray.getJSONObject(i).getJSONObject("assignee").getString("first_name").equals("") && jsonArray.getJSONObject(i).getJSONObject("assignee").getString("last_name").equals("")) {
                    agentName = jsonArray.getJSONObject(i).getJSONObject("assignee").getString("user_name");
                } else if ((jsonArray.getJSONObject(i).getJSONObject("assignee").getString("first_name").equals("null") ||
                        jsonArray.getJSONObject(i).getJSONObject("assignee").getString("first_name").equals("")) &&
                        (jsonArray.getJSONObject(i).getJSONObject("assignee").getString("last_name").equals("null") || jsonArray.getJSONObject(i).getJSONObject("assignee").
                                getString("last_name").equals(""))) {
                    agentName = jsonArray.getJSONObject(i).getJSONObject("assignee").getString("user_name");
                } else if (!jsonArray.getJSONObject(i).getJSONObject("assignee").getString("first_name").equals("") &&
                        jsonArray.getJSONObject(i).getJSONObject("assignee").getString("last_name").equals("")) {
                    agentName = jsonArray.getJSONObject(i).getJSONObject("assignee").getString("first_name");
                } else if (jsonArray.getJSONObject(i).getJSONObject("assignee").getString("first_name").equals("") &&
                        !jsonArray.getJSONObject(i).getJSONObject("assignee").getString("last_name").equals("")) {
                    agentName = jsonArray.getJSONObject(i).getJSONObject("assignee").getString("last_name");
                } else {
                    agentName = jsonArray.getJSONObject(i).getJSONObject("assignee").getString("first_name") + " " + jsonArray.getJSONObject(i).getJSONObject("assignee").getString("last_name");
                }
            }
            else{
                agentName="Unassigned";
            }
            String clientname;
            //String agentname;

            String agentname;
            if (agentName.equals("null null")){
                agentname="Unassigned";
            }
            else if (agentName.equals("nullnull")){
                agentname="Unassigned";
            }
            else if (agentName.equals("null")){
                agentname="Unassigned";
            }
            else{
                agentname=agentName;
            }

            if (firstName == null || firstName.equals("")) {
                clientname = username;
            }
            else
                clientname = firstName + " " + lastName;
            return new TicketOverview(Integer.parseInt(ID),
                    ticketNumber, clientname, title, updatedAt, ticketStatusName, clientname,agentname);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Converting UTC to local returns long timeinmillseconds.
     * @param dateToParse is the date that we have to parse.
     * @return
     */
    public static Long relativeTime(String dateToParse) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date d = null;

        try {
            d = sdf.parse(dateToParse);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat output = new SimpleDateFormat("d MMM yyyy  HH:mm");

        output.setTimeZone(TimeZone.getDefault());

        String formattedTime = output.format(d);
        //Date gg = null;
        Date gg = new Date();
        //long diff = gg.getTime();

        try {
            gg = output.parse(formattedTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // SimpleDateFormat day = new SimpleDateFormat("dd");
//            String formattedDay = day.format(d) + Helper.getDayOfMonthSuffix(Integer.parseInt(day.format(d)));
//            formattedTime = formattedTime.replaceFirst(formattedTime.substring(0, formattedTime.indexOf(" ")), formattedDay);
//            sdf.parse(dateToParse);
        return gg != null ? gg.getTime() : 0;
    }

    /**
     * UTC time conversion to local time returns String Date.
     * @param dateToParse
     * @return
     */
    public static String parseDate(String dateToParse) {
        try {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date dAte = sdf.parse(dateToParse);

            SimpleDateFormat output = new SimpleDateFormat("d MMM yyyy  HH:mm");
            output.setTimeZone(TimeZone.getDefault());

            String formattedTime = output.format(dAte);
            SimpleDateFormat day = new SimpleDateFormat("dd");
            String formattedDay = day.format(dAte) + Helper.getDayOfMonthSuffix(Integer.parseInt(day.format(dAte)));
            formattedTime = formattedTime.replaceFirst(formattedTime.substring(0, formattedTime.indexOf(" ")), formattedDay);
            sdf.parse(dateToParse);
            return formattedTime;
        } catch (ParseException e) {
            e.printStackTrace();
            return dateToParse;
        }
    }

    /**
     * Comparing two dates for DUEDATE.
     * @param duedate1
     * @return
     */
    public static int compareDates(String duedate1) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar1 = Calendar.getInstance();
        SimpleDateFormat formatter1 = new SimpleDateFormat("dd/M/yyyy h:mm");
        String currentTime = formatter1.format(calendar1.getTime());
        Log.d("currentTime",currentTime);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        sdf1.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = null;
        Date date2=null;
        try {
            date = sdf.parse(duedate1);
            date2=sdf1.parse(duedate1);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat output = new SimpleDateFormat("d MMM yyyy  HH:mm");
        SimpleDateFormat output1 = new SimpleDateFormat("d MMM yyyy");
        output.setTimeZone(TimeZone.getDefault());
        output1.setTimeZone(TimeZone.getDefault());
        String formattedTime = output.format(date);
        String formattedTime1=output1.format(date2);
        Date dueDate = null;
        Date curntDate = null;
        Date dueDate1=null;
        Date currDate1=null;

        String currentStringDate = new SimpleDateFormat("d MMM yyyy  HH:mm", Locale.getDefault()).format(new Date());
        String currentStringDate1 = new SimpleDateFormat("d MMM yyyy", Locale.getDefault()).format(new Date());
        try {
            dueDate = output.parse(formattedTime);
            curntDate = output.parse(currentStringDate);
            dueDate1=output1.parse(formattedTime1);
            currDate1=output1.parse(currentStringDate1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int i = 0;

        if (dueDate1.equals(currDate1)) {

            i = 2;
        }

        else if (dueDate.after(curntDate)) {
            i = 0;
        } else if (dueDate.before(curntDate)) {
            i = 1;
        }


        return i;

    }

    private static String getDayOfMonthSuffix(final int n) {
        if (n >= 11 && n <= 13) {
            return "th";
        }
        switch (n % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }

    /**
     * Email validation.
     * @param target
     * @return
     */
    public static boolean isValidEmail(CharSequence target) {
        return target != null && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

}
