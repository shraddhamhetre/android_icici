package com.youtility.intelliwiz20.Activities;

import android.app.Activity;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.youtility.intelliwiz20.DataAccessObject.AssetDAO;
import com.youtility.intelliwiz20.DataAccessObject.TypeAssistDAO;
import com.youtility.intelliwiz20.Model.Address;
import com.youtility.intelliwiz20.Model.Asset;
import com.youtility.intelliwiz20.R;

/**
 * A fragment representing a single Asset detail screen.
 * This fragment is either contained in a {@link AssetListActivity}
 * in two-pane mode (on tablets) or a {@link AssetDetailActivity}
 * on handsets.
 */
public class AssetDetailFragment extends Fragment implements OnMapReadyCallback {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    private Asset mItem;
    private TextView assetNameTextView,assetCodeTextView,assetEnableTextView,assetCriticalTextView,assetIdentifierTextView,assetRunningstatusTextView;
    private AssetDAO assetDAO;
    private TextView asset_runningstatus;
    private TextView phoneNumber;
    private TextView mobileNumber;
    private TextView assetGeoLocation;
    private TypeAssistDAO typeAssistDAO;
    private Address address=null;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AssetDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        typeAssistDAO=new TypeAssistDAO(getActivity());

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = AssetListActivity.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem.getAssetcode());
            }
        }


        assetDAO=new AssetDAO(getActivity());
        address=new Address();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.asset_detail, container, false);


        // Show the dummy content as text in a TextView.
        if (mItem != null) {
            //((TextView) rootView.findViewById(R.id.asset_detail)).setText(mItem.getAssetname());
            ((TextView) rootView.findViewById(R.id.asset_code)).setText(mItem.getAssetcode());
            ((TextView) rootView.findViewById(R.id.asset_code)).setTextIsSelectable(true);
            ((TextView) rootView.findViewById(R.id.asset_name)).setText(mItem.getAssetname());
            ((TextView) rootView.findViewById(R.id.asset_enable)).setText(mItem.getEnable());
            ((TextView) rootView.findViewById(R.id.asset_iscritical)).setText(mItem.getIscritical());
            ((TextView) rootView.findViewById(R.id.asset_identifier)).setText(typeAssistDAO.getEventTypeName(mItem.getIdentifier()));
            ((TextView) rootView.findViewById(R.id.asset_location)).setText(mItem.getLocname());

            ((TextView) rootView.findViewById(R.id.asset_type)).setText(typeAssistDAO.getEventTypeName(mItem.getType()));
            ((TextView) rootView.findViewById(R.id.asset_category)).setText(typeAssistDAO.getEventTypeName(mItem.getCategory()));
            ((TextView) rootView.findViewById(R.id.asset_subcat)).setText(typeAssistDAO.getEventTypeName(mItem.getSubcategory()));
            ((TextView) rootView.findViewById(R.id.asset_brand)).setText(typeAssistDAO.getEventTypeName(mItem.getBrand()));
            ((TextView) rootView.findViewById(R.id.asset_model)).setText(typeAssistDAO.getEventTypeName(mItem.getModel()));
            ((TextView) rootView.findViewById(R.id.asset_supplier)).setText(mItem.getSupplier());
            ((TextView) rootView.findViewById(R.id.asset_capacity)).setText(mItem.getCapacity()+"");
            ((TextView) rootView.findViewById(R.id.asset_unit)).setText(typeAssistDAO.getEventTypeName(mItem.getUnit()));
            ((TextView) rootView.findViewById(R.id.asset_yom)).setText(mItem.getYom());
            ((TextView) rootView.findViewById(R.id.asset_msn)).setText(mItem.getMsn());
            ((TextView) rootView.findViewById(R.id.asset_bdate)).setText(mItem.getBdate());
            ((TextView) rootView.findViewById(R.id.asset_pdate)).setText(mItem.getPdate());
            ((TextView) rootView.findViewById(R.id.asset_insdate)).setText(mItem.getIsdate());
            ((TextView) rootView.findViewById(R.id.asset_billvalue)).setText(mItem.getBillval()+"");
            ((TextView) rootView.findViewById(R.id.asset_serprov)).setText((mItem.getServprovname()));
            ((TextView) rootView.findViewById(R.id.asset_service)).setText(typeAssistDAO.getEventTypeName(mItem.getService()));
            ((TextView) rootView.findViewById(R.id.asset_sfdate)).setText(mItem.getSfdate());
            ((TextView) rootView.findViewById(R.id.asset_stdate)).setText(mItem.getStdate());
            ((TextView) rootView.findViewById(R.id.asset_meter)).setText(mItem.getMeter()+"");
            ((TextView) rootView.findViewById(R.id.asset_mfactor)).setText(mItem.getMultiplicationfactor()+"");

            System.out.println("AssetFragement: "+mItem.getAssetid());

            address=assetDAO.getAssetAddress(mItem.getAssetid());
            if(address!=null)
            {
                ((TextView) rootView.findViewById(R.id.asset_address)).setText(address.getAddress());
                ((TextView) rootView.findViewById(R.id.asset_landmark)).setText(address.getLandmark());
                ((TextView) rootView.findViewById(R.id.asset_website)).setText(address.getWebsite());

                mobileNumber=(TextView)rootView.findViewById(R.id.asset_mobile);
                mobileNumber.setText(address.getMobileno());
                /*mobileNumber.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callDialIntent(mobileNumber.getText().toString().trim());
                    }
                });*/

                phoneNumber=(TextView)rootView.findViewById(R.id.asset_phone);
                phoneNumber.setText(address.getPhoneno());
                /*phoneNumber.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callDialIntent(phoneNumber.getText().toString().trim());
                    }
                });*/

                assetGeoLocation=(TextView)rootView.findViewById(R.id.asset_geolocation);
                assetGeoLocation.setText(address.getGpslocation());
                assetGeoLocation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callMapIntent();
                    }
                });
            }





            System.out.println("Qsertname: "+mItem.getQsetname());

            asset_runningstatus=(TextView) rootView.findViewById(R.id.asset_runningstatus);
            asset_runningstatus.setText(typeAssistDAO.getEventTypeName(mItem.getRunningstatus()));



        }


        return rootView;
    }

    private void callMapIntent()
    {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void callDialIntent(String dialNumber)
    {
        /*Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + dialNumber));
        startActivity(intent);*/
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if(address!=null) {
            if (address.getGpslocation() != null && !address.getGpslocation().equalsIgnoreCase("0.0")) {
                String[] gpsLoc = address.getGpslocation().split(",");
                googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(Double.valueOf(gpsLoc[0]), Double.valueOf(gpsLoc[1])))
                        .title(mItem.getAssetname())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.valueOf(gpsLoc[0]), Double.valueOf(gpsLoc[1])), 10));
            }
        }

    }
}
