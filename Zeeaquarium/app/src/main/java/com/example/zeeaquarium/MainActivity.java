package com.example.zeeaquarium;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.anastr.speedviewlib.Speedometer;
import com.microsoft.windowsazure.messaging.notificationhubs.NotificationHub;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

public class MainActivity extends AppCompatActivity {

    private RequestQueue requestQueue;
    private Timer timer;

    private List<IMeter> meters = new ArrayList<>();
    private List<IDeviceSwitch> deviceSwitches = new ArrayList<>();
    private List<Screenshot> screenshots = new ArrayList<>();
    private List<ITrafficLight> trafficLights = new ArrayList<>();
    private List<ISettingSetter> settingSetters = new ArrayList<>();
    private List<CommandButton> commandButtons = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestQueue = Volley.newRequestQueue(this);

        NotificationHub.setListener(new CustomNotificationListener());
        NotificationHub.start(
                this.getApplication(),
                getString(R.string.notification_hub_name),
                getString(R.string.notification_hub_connection_string));

        timer = new Timer();

        // Initialize meters
        meters.add(new WaterTemperatureMeter((Speedometer) findViewById(R.id.watertemperature), this));
        meters.add(new LightMeter((Speedometer) findViewById(R.id.light), this));
        meters.add(new WaterVolumeMeter((Speedometer) findViewById(R.id.watervolume), this));
        meters.add(new WaterVolumeReservoirMeter((Speedometer) findViewById(R.id.watervolume_reservoir), this));
        meters.add(new AmbientTemperatureMeter((Speedometer) findViewById(R.id.ambienttemperature), this));
        meters.add(new DosingPumpWeightMeter("dosing_pump_1_weight", (Speedometer) findViewById(R.id.dosingpump_1_weight), this));
        meters.add(new DosingPumpWeightMeter("dosing_pump_2_weight", (Speedometer) findViewById(R.id.dosingpump_2_weight), this));
        meters.add(new DosingPumpWeightMeter("dosing_pump_3_weight", (Speedometer) findViewById(R.id.dosingpump_3_weight), this));
        getMeasurements();

        // Initialize traffic lights
        trafficLights.add(new TrafficLight("pomp_links_aan", (ImageView) findViewById(R.id.pomp_l_status)));
        trafficLights.add(new TrafficLight("pomp_rechts_voor_aan", (ImageView) findViewById(R.id.pomp_rv_status)));
        trafficLights.add(new TrafficLight("pomp_rechts_achter_boven_aan", (ImageView) findViewById(R.id.pomp_rab_status)));
        trafficLights.add(new TrafficLight("verwarming_aan", (ImageView) findViewById(R.id.verwarming_status)));
        trafficLights.add(new TrafficLight("verlichting_aan", (ImageView) findViewById(R.id.verlichting_status)));
        trafficLights.add(new TrafficLight("ventilatoren_aan", (ImageView) findViewById(R.id.ventilatoren_status)));
        trafficLights.add(new TrafficLight("ato_aan", (ImageView) findViewById(R.id.ato_status)));
        getDeviceStatuses();

        // Initialize switches
        deviceSwitches.add(new SimpleDeviceSwitch((Switch) findViewById(R.id.pomp_l), (ProgressBar) findViewById(R.id.pomp_l_syncing), "pomp_links", this));
        deviceSwitches.add(new SimpleDeviceSwitch((Switch) findViewById(R.id.pomp_rv), (ProgressBar) findViewById(R.id.pomp_rv_syncing), "pomp_rechts_voor", this));
        deviceSwitches.add(new SimpleDeviceSwitch((Switch) findViewById(R.id.pomp_rab), (ProgressBar) findViewById(R.id.pomp_rab_syncing), "pomp_rechts_achter_boven", this));
        deviceSwitches.add(new SimpleDeviceSwitch((Switch) findViewById(R.id.verlichting), (ProgressBar) findViewById(R.id.verlichting_syncing), "verlichting", this));
        deviceSwitches.add(new SimpleDeviceSwitch((Switch) findViewById(R.id.verwarming), (ProgressBar) findViewById(R.id.verwarming_syncing), "verwarming", this));
        deviceSwitches.add(new SimpleDeviceSwitch((Switch) findViewById(R.id.ventilatoren), (ProgressBar) findViewById(R.id.ventilatoren_syncing), "ventilatoren", this));
        deviceSwitches.add(new SimpleDeviceSwitch((Switch) findViewById(R.id.ato), (ProgressBar) findViewById(R.id.ato_syncing), "ato", this));
        getSwitchStatuses();
        handleSwitchChanges();

        // Initialize setting setters
        settingSetters.add(new SimpleSettingSetter((EditText) findViewById(R.id.watervolume_setpoint), (ProgressBar) findViewById(R.id.watervolume_setpoint_syncing), "watervolume_streefwaarde", this));
        settingSetters.add(new SimpleSettingSetter((EditText) findViewById(R.id.watervolume_timestamp1), (ProgressBar) findViewById(R.id.watervolume_timestamp1_syncing), "water_bijvul_tijdstip1", this));
        settingSetters.add(new SimpleSettingSetter((EditText) findViewById(R.id.watervolume_timestamp2), (ProgressBar) findViewById(R.id.watervolume_timestamp2_syncing), "water_bijvul_tijdstip2", this));
        settingSetters.add(new SimpleSettingSetter((EditText) findViewById(R.id.lighting_on_timestamp), (ProgressBar) findViewById(R.id.lighting_on_timestamp_syncing), "verlichting_aan_tijdstip", this));
        settingSetters.add(new SimpleSettingSetter((EditText) findViewById(R.id.lighting_off_timestamp), (ProgressBar) findViewById(R.id.lighting_off_timestamp_syncing), "verlichting_uit_tijdstip", this));
        settingSetters.add(new SimpleSettingSetter((EditText) findViewById(R.id.heating_threshold_timestamp), (ProgressBar) findViewById(R.id.heating_threshold_syncing), "verwarming_drempel", this));
        settingSetters.add(new SimpleSettingSetter((EditText) findViewById(R.id.fans_threshold_timestamp), (ProgressBar) findViewById(R.id.fans_threshold_syncing), "ventilator_drempel", this));
        settingSetters.add(new SimpleSettingSetter((EditText) findViewById(R.id.dosing_timestamp), (ProgressBar) findViewById(R.id.dosing_timestamp_syncing), "doseer_tijdstip", this));
        settingSetters.add(new SimpleSettingSetter((EditText) findViewById(R.id.dosingpump_1_volume), (ProgressBar) findViewById(R.id.dosingpump_1_volume_syncing), "doseerpomp_1_volume", this));
        settingSetters.add(new SimpleSettingSetter((EditText) findViewById(R.id.dosingpump_2_volume), (ProgressBar) findViewById(R.id.dosingpump_2_volume_syncing), "doseerpomp_2_volume", this));
        settingSetters.add(new SimpleSettingSetter((EditText) findViewById(R.id.dosingpump_3_volume), (ProgressBar) findViewById(R.id.dosingpump_3_volume_syncing), "doseerpomp_3_volume", this));
        settingSetters.add(new SimpleSettingSetter((EditText) findViewById(R.id.dosingpump_4_volume), (ProgressBar) findViewById(R.id.dosingpump_4_volume_syncing), "doseerpomp_4_volume", this));
        settingSetters.add(new SimpleSettingSetter((EditText) findViewById(R.id.dosingpump_1_initial_weight), (ProgressBar) findViewById(R.id.dosingpump_1_initial_weight_syncing), "dosing_pump_1_initial_weight", this));
        settingSetters.add(new SimpleSettingSetter((EditText) findViewById(R.id.dosingpump_2_initial_weight), (ProgressBar) findViewById(R.id.dosingpump_2_initial_weight_syncing), "dosing_pump_2_initial_weight", this));
        settingSetters.add(new SimpleSettingSetter((EditText) findViewById(R.id.dosingpump_3_initial_weight), (ProgressBar) findViewById(R.id.dosingpump_3_initial_weight_syncing), "dosing_pump_3_initial_weight", this));
        getSettings();
        handleSettingChanges();

        // Initialize button
        commandButtons.add(new CommandButton((Button) findViewById(R.id.reboot_button), "reboot", "Weet je zeker dat je de controller wilt herstarten?", this));
        handleCommandButtonClicks();

        // Initialize screenshot
        screenshots.add(new Screenshot((ImageView) findViewById(R.id.screenshot), this));
        getScreenshots();

        // send stream command
        sendCommand("stream");

        // Check if latest measurements are still up-to-date
        checkUpToDateOnThread();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void getMeasurements() {
        meters.forEach(meter -> {
            String url ="https://zeeaquarium-streamingapp.azurewebsites.net/api/measurement?measurement=" + meter.getName();

            JsonObjectRequest request = createJsonObjectRequest(url, null, response -> {
                try {
                    String date = response.getString("date");
                    String value = response.getString("value");
                    Measurement measurement = new Measurement(meter.getName(), date, value);
                    meter.setValue(measurement);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });

            timer.schedule(new AddToQueueTimerTask(requestQueue, request), 0, 1000);
        });
    }

    private void getDeviceStatuses() {
        trafficLights.forEach(trafficLight -> {
            String url ="https://zeeaquarium-streamingapp.azurewebsites.net/api/measurement?measurement=" + trafficLight.getName();

            JsonObjectRequest request = createJsonObjectRequest(url, null, response -> {
                try {
                    String date = response.getString("date");
                    String value = response.getString("value");
                    DeviceStatus status = new DeviceStatus(trafficLight.getName(), date, value);
                    trafficLight.setValue(status);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });

            timer.schedule(new AddToQueueTimerTask(requestQueue, request), 0, 1000);
        });
    }

    private void getSwitchStatuses() {
        deviceSwitches.forEach(deviceSwitch -> {
            String url ="https://zeeaquarium-streamingapp.azurewebsites.net/api/switch?deviceswitch=" + deviceSwitch.getName();

            JsonObjectRequest request = createJsonObjectRequest(url, null, response -> {
                try {
                    boolean value = response.getBoolean("value");
                    DeviceSwitchStatus status = new DeviceSwitchStatus(deviceSwitch.getName(), value);
                    deviceSwitch.setStatus(status);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });

            timer.schedule(new AddToQueueTimerTask(requestQueue, request), 0, 1000);
        });
    }

    private void handleSwitchChanges() {
        deviceSwitches.forEach(deviceSwitch -> {
            deviceSwitch.status().subscribe(
                    status -> sendSwitch(status.getName(), status.getStatus()),
                    error -> error.printStackTrace()
            );
        });
    }

    private void sendSwitch(String deviceSwitch, boolean value) {
        String url ="https://zeeaquarium-streamingapp.azurewebsites.net/api/switch";
        String body = "{\"switch\":\"" + deviceSwitch + "\",\"value\":" + value + "}";
        StringRequest request = createStringRequest(url, body);

        requestQueue.add(request);
    }

    private void getSettings() {
        settingSetters.forEach(settingSetter -> {
            String url ="https://zeeaquarium-streamingapp.azurewebsites.net/api/setting?setting=" + settingSetter.getName();

            JsonObjectRequest request = createJsonObjectRequest(url, null, response -> {
                try {
                    String value = response.getString("value");
                    settingSetter.setSetting(settingSetter.getName(), value);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });

            timer.schedule(new AddToQueueTimerTask(requestQueue, request), 0, 1000);
        });
    }

    private void handleSettingChanges() {
        settingSetters.forEach(settingSetter ->
            settingSetter.settingValue().subscribe(
                    value -> sendSetting(settingSetter.getName(), value),
                    error -> error.printStackTrace()
            )
        );
    }

    private void sendSetting(String setting, String value) {
        String url ="https://zeeaquarium-streamingapp.azurewebsites.net/api/setting";
        String body = "{\"setting\":\"" + setting + "\",\"value\":\"" + value + "\"}";
        StringRequest request = createStringRequest(url, body);

        requestQueue.add(request);
    }

    private void getScreenshots() {
        String url ="https://zeeaquarium-streamingapp.azurewebsites.net/api/screenshot";

        JsonObjectRequest request = createJsonObjectRequest(url, null, response -> {
            screenshots.forEach(s -> {
                try {
                    JSONArray jsonResponse = (JSONArray) response.get("screenshot");

                    byte[] byteArray = new byte[jsonResponse.length()];
                    for(int idx = 0; idx < jsonResponse.length(); idx++){
                        byteArray[idx] = (byte) jsonResponse.getInt(idx);
                    }

                    s.setScreenshot(byteArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        });

        timer.schedule(new AddToQueueTimerTask(requestQueue, request), 0, 1000);
    }

    private void handleCommandButtonClicks() {
        commandButtons.forEach(button -> {
            button.sendCommand().subscribe(
                    command -> sendCommand(command),
                    error -> error.printStackTrace()
            );
        });
    }

    private void sendCommand(String command) {
        String url ="https://zeeaquarium-streamingapp.azurewebsites.net/api/command";
        String body = "{\"command\":\"" + command + "\"}";
        StringRequest request = createStringRequest(url, body);

        requestQueue.add(request);
    }

    private void checkUpToDateOnThread() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    while (!Thread.interrupted()) {
                        Thread.sleep(500);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                long currentTimeStamp = System.currentTimeMillis();
                                meters.forEach(meter -> {
                                    if (currentTimeStamp - meter.getValue().getTimeStamp() > 300000l
                                            || meter.getValue().getValue() == 0f) {
                                        meter.setGrayoutColoring();
                                    } else {
                                        meter.setDefaultColoring();
                                    }
                                });
                                trafficLights.forEach(trafficLight -> {
                                    if (currentTimeStamp - trafficLight.getValue().getTimeStamp() > 300000l
                                            || trafficLight.getValue().getStatus().equals(SwitchStatus.UNKNOWN)) {
                                        trafficLight.setGrayoutColoring();
                                    } else {
                                        trafficLight.setDefaultColoring();
                                    }
                                });
                            }
                        });
                    }
                }
                catch (InterruptedException e) {
                }
            }
        };
        thread.start();
    }

    private StringRequest createStringRequest(String url, String body) {
        return new StringRequest(Request.Method.POST, url, response -> {}, error -> error.printStackTrace()) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return body.getBytes("utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return createHeaders();
            }
        };
    }

    private JsonObjectRequest createJsonObjectRequest(String url, JSONObject body, Response.Listener<JSONObject> responseListener) {
        return new JsonObjectRequest(url, body, responseListener, error -> error.printStackTrace()
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return createHeaders();
            }
        };
    }

    private Map<String, String> createHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + getString(R.string.token));

        return headers;
    }
}