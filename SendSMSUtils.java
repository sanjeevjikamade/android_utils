package cordova.plugin.swsms;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Sanjeev on 28/2/18.
 */

public class SendSMSUtils {

    final String TAG = SendSMSUtils.class.getSimpleName();
    List<SMSList> smsList;
    CallbackContext mCallbackContext;
    private static BroadcastReceiver smsSentBrocarsReceiver = null;
    private static BroadcastReceiver smsDelivedBrocarsReceiver = null;

    private static String isSMSSent = "";

    JSONArray statusArray;
    private void executeCode(Context context, JSONArray args){
            try {
                smsList = new ArrayList<SMSList>();
                JSONObject jsonObject = args.getJSONObject(0);
                JSONArray array = jsonObject.getJSONArray("sms_list");
                for (int i = 0; i < array.length(); i++) {
                    smsList.add(new SMSList(array.getJSONObject(i).getString("number"), array.getJSONObject(i).getString("message")));
                }

                if (smsList != null && smsList.size() > 0) {
                    statusArray = new JSONArray();
                    sendSMS(context, smsList.get(0).getNumber(), smsList.get(0).getMessage());

                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
    }

    public JSONArray sendSMS(final Context context, final String mobileNo, final String smsKeyWord) {
        try {
            String smsText = "";
            try {
                smsText = smsKeyWord;
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (smsText != null && !smsText.trim().equalsIgnoreCase("")) {

                String SENT = "SMS_SENT";
                String DELIVERED = "SMS_DELIVERED";

                PendingIntent sentPI = PendingIntent.getBroadcast(context, 0, new Intent(SENT), 0);
                PendingIntent deliveredPI = PendingIntent.getBroadcast(context, 0, new Intent(DELIVERED), 0);

                try {

                    isSMSSent = "NO";

                    if (smsSentBrocarsReceiver != null) {
                        context.unregisterReceiver(smsSentBrocarsReceiver);
                        smsSentBrocarsReceiver = null;
                    }

                    if(smsDelivedBrocarsReceiver != null) {
                        context.unregisterReceiver(smsDelivedBrocarsReceiver);
                        smsDelivedBrocarsReceiver = null;
                    }

                    if (smsSentBrocarsReceiver == null) {
                        smsSentBrocarsReceiver = new BroadcastReceiver() {

                            @Override
                            public void onReceive(Context arg0, Intent arg1) {
                                switch (getResultCode()) {
                                    case Activity.RESULT_OK:
                                        isSMSSent = "S";
                                        break;
                                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                                        isSMSSent = "F";
                                        break;
                                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                                        isSMSSent = "F";
                                        break;
                                    case SmsManager.RESULT_ERROR_NULL_PDU:
                                        isSMSSent = "F";
                                        break;
                                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                                        isSMSSent = "F";
                                        break;

                                }
                                try {
                                    JSONObject jsonObject = new JSONObject();
                                    jsonObject.put("mobile", mobileNo);
                                    jsonObject.put("status", isSMSSent);

                                    statusArray.put(jsonObject);
                                }catch (Exception ex){
                                    ex.printStackTrace();
                                }

                                try {
                                    if(smsList.size() > 0)
                                        smsList.remove(0);

                                    if (smsList != null && smsList.size() > 0) {
                                        sendSMS(context, smsList.get(0).getNumber(), smsList.get(0).getMessage());
                                    }
                                }catch (Exception ex){
                                    ex.printStackTrace();
                                }
                            }
                        };

                        if (smsList.size() <= 0) {
                            return statusArray;
                        }

                        context.registerReceiver(smsSentBrocarsReceiver, new IntentFilter(SENT));


                    }

                } catch(Exception ex) {
                    isSMSSent = "ERROR";
                }

                    SmsManager sms = SmsManager.getDefault();
                    sms.sendTextMessage(mobileNo, null, smsText, sentPI, deliveredPI);
            }
        } catch(Exception ex) {
            Log.e("Error Occured in ISDKUtils::sendSMS : ",ex.toString());
        }finally {
            if (smsSentBrocarsReceiver != null) {
                context.unregisterReceiver(smsSentBrocarsReceiver);
            }
        }
        return null;
    }

}

