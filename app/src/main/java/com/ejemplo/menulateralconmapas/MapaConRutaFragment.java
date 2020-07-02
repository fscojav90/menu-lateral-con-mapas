package com.ejemplo.menulateralconmapas;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapaConRutaFragment extends Fragment {

    private EditText etLatitudOrigen;
    private EditText etLongitudOrigen;
    private EditText etLatitudDestino;
    private EditText etLongitudDestino;


    private Button btnBuscar;
    private GoogleMap mMap;

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
            mMap = googleMap;
            etLatitudOrigen = (EditText) getView().findViewById(R.id.etLatitudOrigen);
            etLongitudOrigen = (EditText) getView().findViewById(R.id.etLongitudOrigen);

            etLatitudDestino = (EditText) getView().findViewById(R.id.etLatitudDestino);
            etLongitudDestino = (EditText) getView().findViewById(R.id.etLongitudDestino);

            etLatitudOrigen.setText("-18.4834105");
            etLongitudOrigen.setText("-70.3101844");

            etLatitudDestino.setText("-18.478973");
            etLongitudDestino.setText("-70.320696");

            btnBuscar = (Button) getView().findViewById(R.id.btBuscar);

            btnBuscar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        //origen
                        float latitudOrigen = Float.parseFloat(etLatitudOrigen.getText().toString());
                        float longitudOrigen = Float.parseFloat(etLongitudOrigen.getText().toString());
                        LatLng origen = new LatLng(latitudOrigen, longitudOrigen);
                        mMap.addMarker(new MarkerOptions().position(origen).title("Origen"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origen, 16));
                        //destino
                        float latitudDestino = Float.parseFloat(etLatitudDestino.getText().toString());
                        float longitudDestino = Float.parseFloat(etLongitudDestino.getText().toString());
                        LatLng destino = new LatLng(latitudDestino, longitudDestino);
                        mMap.addMarker(new MarkerOptions().position(destino).title("Destino"));



                    }catch (Exception ex){
                        Toast.makeText(getContext(), "Error: Ubicación inválida", Toast.LENGTH_SHORT).show();
                    }



                }
            });
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mapa_con_ruta, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }
}