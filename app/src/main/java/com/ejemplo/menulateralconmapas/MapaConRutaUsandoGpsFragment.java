package com.ejemplo.menulateralconmapas;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MapaConRutaUsandoGpsFragment extends Fragment {

    private static final int PERMISION_FINE_LOCATION = 99;
    private EditText etLatitud;
    private EditText etLongitud;
    private Button btBuscar;
    private GoogleMap mMap;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            //componentes
            locationRequest = new LocationRequest();
            locationRequest.setInterval(30000);
            locationRequest.setFastestInterval(5000);
            locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

            actualizarGPS();

            //iniciar componentes
            mMap = googleMap;
            etLatitud = (EditText) getView().findViewById(R.id.etLatitud);
            etLongitud = (EditText) getView().findViewById(R.id.etLongitud);

            etLatitud.setText("-18.478973");
            etLongitud.setText("-70.320696");

            btBuscar = (Button) getView().findViewById(R.id.btBuscar);
            btBuscar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //crear marcador ust
                    LatLng ust = new LatLng(-18.48341, -70.310184);
                    //tomar valores de los EditText
                    try {
                        double lat = Double.parseDouble(etLatitud.getText().toString());
                        double lng = Double.parseDouble(etLongitud.getText().toString());
                        //marcador
                        LatLng marcador = new LatLng(lat, lng);
                        //título
                        mMap.addMarker(new MarkerOptions().position(marcador).title("Mi marcador"));
                        //código ruta
                        String url = getRequestUrl(ust, marcador);

                        com.ejemplo.menulateralconmapas.MapaConRutaUsandoGpsFragment.TaskResquestDirections taskResquestDirections = new com.ejemplo.menulateralconmapas.MapaConRutaUsandoGpsFragment.TaskResquestDirections();
                        taskResquestDirections.execute(url);

                    }catch (Exception ex){
                        Toast.makeText(getContext(), "Valor inválido!!", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }

        private void actualizarGPS() {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
            if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        actualizarUbicacion(location);
                    }
                });

            }else{
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISION_FINE_LOCATION);
                }
            }

        }
    };

    private String getRequestUrl(LatLng origen, LatLng destino) {
        String resultado = "";

        String string_origen = "origin="+origen.latitude+","+origen.longitude;
        String string_destino = "destination="+destino.latitude+","+destino.longitude;

        String sensor = "sensor=false";
        String modo = "mode=driving";

        String param = string_origen+"&"+string_destino+"&"+sensor+"&"+modo;
        String salida = "json";

        resultado = "https://maps.googleapis.com/maps/api/directions/"+salida+"?"+param;

        //https://maps.googleapis.com/maps/api/directions/json?origin=123,123&destination=456,654&sensor=false&mode=driving

        return resultado;
    }

    private void actualizarUbicacion(Location location) {
        Toast.makeText(getContext(), "Hay conexión con el GPS", Toast.LENGTH_SHORT).show();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mapa_con_ruta, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    public class TaskResquestDirections extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String responseString = "";

            try{
                responseString = requestDirection(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return responseString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            com.ejemplo.menulateralconmapas.MapaConRutaUsandoGpsFragment.TaskParser taskParser = new com.ejemplo.menulateralconmapas.MapaConRutaUsandoGpsFragment.TaskParser();
            taskParser.execute(s);
        }

    }

    private String requestDirection(String reqUrl) throws IOException {
        String responseString = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;

        try{
            URL url = new URL(reqUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuffer stringBuffer = new StringBuffer();
            String linea = "";

            while ((linea = bufferedReader.readLine())!=null){
                stringBuffer.append(linea);
            }

            responseString = stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(inputStream != null)
                inputStream.close();

            httpURLConnection.disconnect();
        }

        return responseString;
    }

    public class TaskParser extends AsyncTask<String, Void, List<List<HashMap<String, String>>>>{

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jsonObject = null;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jsonObject = new JSONObject(strings[0]);
                DirectionsParser directionsParser = new DirectionsParser();
                routes = directionsParser.parse(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            super.onPostExecute(lists);

            ArrayList points = null;

            PolylineOptions polylineOptions = null;

            for(List<HashMap<String, String>> path : lists){
                points = new ArrayList();
                polylineOptions = new PolylineOptions();

                for(HashMap<String, String> point : path){
                    double lat = Double.parseDouble(point.get("lat"));
                    double lon = Double.parseDouble(point.get("lon"));

                    points.add(new LatLng(lat, lon));
                }

                polylineOptions.addAll(points);
                polylineOptions.width(15);
                polylineOptions.color(Color.BLUE);
                polylineOptions.geodesic(true);
            }

            if(polylineOptions!= null){
                mMap.addPolyline(polylineOptions);
            } else{
                Toast.makeText(getContext(), "Dirección no encontrada", Toast.LENGTH_SHORT).show();
            }
        }
    }
}