package controllers;

/**
 * Created by Yiko on 2015-03-17.
 */

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.JsonNode;
import models.*;
import play.*;
import play.data.DynamicForm;
import play.data.Form;
import play.db.ebean.Model;
import play.mvc.*;
import play.libs.Json;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.*;
import java.util.List;

import play.mvc.Security;
import scala.util.parsing.json.JSONObject$;
import views.html.*;

import play.data.validation.Constraints;
import java.net.*;
import java.io.BufferedOutputStream;
import java.io.OutputStream;

import static play.libs.Json.toJson;
import org.json.JSONArray;
import org.json.JSONObject;

public class  CallOperatorController extends Controller{
//    private static EventHandlerPool eventHandlerPool;
//
//    public static void setEventHandlerPool
//            (EventHandlerPool eventHandlerPool){
//        CallOperatorController.eventHandlerPool = eventHandlerPool;
//    }


    public static Result index(){
        return ok(index.render("ok"));
    }

    public static Result login(){
        return ok(index.render("ok"));
    }
    // Call Operator Log In Method
    public static Result LogIn(){
        DynamicForm requestData = Form.form().bindFromRequest();
        String id = requestData.get("COID");
        String password = requestData.get("password");
        CallOperator callOperator = CallOperator.authenticate(id, password); // method to be declared in Model CallOperator
        if (callOperator == null){
            session().clear();
            return ok("Login Failure: Call Operator ID or Password is not correct.");
        }
        session("connected","COID"); // create new session for call operator
        return ok(ControllerUtil.jsonNodeForSuccess("login succeeded..."));
    }
   // public static Result index(List<EventType> eventTypes) {return ok();}

   // @Security.Authenticated(CallOperatorSecured.class) //check whether a particular CO has logged in
    public static Result addEvent(){
        DynamicForm requestData = Form.form().bindFromRequest();
        //CallOperator callOperator = Ebean.find(CallOperator.class,Long.parseLong(requestData.get("COID")));// get call operator ID
        //JsonNode json = request().body().asJson();
       String reporterName = requestData.get("reporterName");
        //String priority = requestData.get("priority");

       String postalCode = requestData.get("postalCode");
        //String postalCode = json.findPath("postalCode").getTextValue();
        String location = requestData.get("location");

        String callerPhone = requestData.get("callerPhone");

        String description = requestData.get("description");
        String NRIC = requestData.get("NRIC");



       // Event newEvent = new Event(json.get("id").toString(),json.get("postalCode").toString(),json.get("phone").toString());


        Event newEvent = new Event();
        Reporter reporter =  new Reporter();

       // newEvent.setEventType(eventType);

        reporter.setContactNumber(callerPhone);
        reporter.setName(reporterName);
        reporter.setNRIC(NRIC);


        //newEvent.setId();// unfinished, to generate eventID
      //  newEvent.setEventType(eventType);
        //newEvent.setCallOperator(callOperator);

        //newEvent.setCallerNumber(callerPhone);
        //newEvent.setCallingTime();// unfinished, to get current time
        newEvent.setDescription(description);
        newEvent.setLocation(location);
       newEvent.setPostalCode(postalCode);
       newEvent.setCoords(geoConverter(postalCode));
        //newEvent.setPriority(Integer.parseInt(priority));
        newEvent.save();
        reporter.save();
       // return ok(newEvent.size());
        //return redirect(routes.CallOperatorController.index());
       // return ok(ControllerUtil.jsonNodeForSuccess("Uploading succeeded..."));

       return ok(toJson(newEvent));

    }

 //country = "Singapore"
    public static String geoConverter(String zipcode) {

        String data = "";
        String lantitute="", longtitute="";
        try {
            URL url = new URL("http://maps.googleapis.com/maps/api/geocode/json?address=santa+cruz&components=postal_code:"+zipcode+"&sensor=false");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.connect();

            BufferedReader input = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();

            String line;
            while((line = input.readLine()) != null){
                stringBuilder.append(line);
            }

            JSONObject jsonData = new JSONObject(stringBuilder.toString());
            JSONArray results = (JSONArray) jsonData.get("results");
            String formattedAddress =  "Singapore " + zipcode;
            for (int i = 0; i < results.length(); i++) {
                JSONObject singleResult = results.getJSONObject(i);
                String address = singleResult.getString("formatted_address");
                if(address.equalsIgnoreCase(formattedAddress)){
                    JSONObject geometry = singleResult.getJSONObject("geometry");
                    JSONObject location = geometry.getJSONObject("location");
                    lantitute = Double.toString(location.getDouble("lat"));
                    longtitute = Double.toString(location.getDouble("lng"));
                }
            }

            data = lantitute + " " + longtitute;
            System.out.println("result: =============");
            System.out.println(data);

        }catch(Exception e){
            e.printStackTrace();
        }

        return data;
    }




   /*@Security.Authenticated(CallOperatorSecured.class) //check whether a particular CO has logged in
    public static Result addEvent(){
        DynamicForm requestData = Form.form().bindFromRequest();

            EventType eventType = Ebean.find(EventType.class,Long.parseLong(requestData.get("ETID"))); //get event type ID

            //CallOperator callOperator = Ebean.find(CallOperator.class,Long.parseLong(requestData.get("COID")));// get call operator ID

            String priority = requestData.get("priority");

            String postalCode = requestData.get("postalCode");

            String location = requestData.get("location");

            String callerPhone = requestData.get("callerPhone");

            String description = requestData.get("description");

            Event newEvent = new Event();

           // newEvent.setId();// unfinished, to generate eventID
            newEvent.setEventType(eventType);
            newEvent.setCallOperator(callOperator);
            newEvent.setCallerPhone(callerPhone);
            newEvent.setCallingTime();// unfinished, to get current time
            newEvent.setDescription(description);
            newEvent.setLocation(location);
            newEvent.setPostalCode(postalCode);
            newEvent.setPriority(Integer.parseInt(priority));

        return ok("Uploading succeeded!");
    }*/

//    @Security.Authenticated(CallOperatorSecured.class) //check whether a particular CO has logged in
//    public static Result updateEvent(){
//        DynamicForm requestData = Form.form().bindFromRequest();
//            EventType eventType = Ebean.find(EventType.class,Long.parseLong(requestData.get("eventTypeID")));
//            Event updatedEvent = Ebean.find(Event.class, Long.parseLong(requestData.get("eventID")));
//            CallOperator callOperator = Ebean.find(CallOperator.class,Long.parseLong(requestData.get("callOperatorID")));
//
//            String status = requestData.get("status");
//            String description = requestData.get("description");
//            updatedEvent.setStatus(status);
//            updatedEvent.setDescription(description);
//            if (status == "2"){
//               // updatedEvent.setSolvedTime();// unfinished, to set solve time
//            }
//
//        return ok("Event " + updatedEvent.getId() + " has been successfully updated.");
//    }

    //for call operator UI dropdown list
    public static String getEventTypes()
    {
//        List <EventType> eventTypes = Ebean.find(EventType.class).orderBy("id").findList();
//        return ok("hello");
        return "yes";
    }

}


