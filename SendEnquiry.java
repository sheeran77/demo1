package com.shinecity.customerpanel;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.shinecity.customerpanel.constants.BaseActivity;
import com.shinecity.customerpanel.constants.Cons;

import net.simonvt.menudrawer.MenuDrawer;
import net.simonvt.menudrawer.Position;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by Az on 25-Mar-17.
 */
public class SendEnquiry extends BaseActivity implements SideMenu.MenuItemSelectListener {
    private Bundle param = new Bundle();
    private ImageView side_menu, cart_img;
    private SideMenu mRightMenuActivity;
    private MenuDrawer mdrawer;
    private TextView title, submit;
    private EditText enquiry_type, enquiry_details;
    private ListView send_enquiry_list;
    ImageView booked_image, agreement_image, project_gallery_image, slp_booking_image, updates_image;
    TextView total_amt, booked_textView, agreement_textView, project_gallery_textView, slp_booking_textView, updates_textView;
    private LinearLayout booked_plot, agreement, project_gallery, slp_booking, updates;
    private String error_msg = "";

    private ArrayList<JSONObject> get_enquiry_arr = new ArrayList<JSONObject>();
    private GetEnquiry_Adapter get_enquiry_adapter = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_enquiry);

        try {
            context = this;
            write("INSIDE----- HOME--------");
            mdrawer = MenuDrawer.attach(this, MenuDrawer.Type.OVERLAY, Position.LEFT, MenuDrawer.MENU_DRAG_WINDOW);
            mdrawer.setContentView(R.layout.send_enquiry);
            mdrawer.setMenuView(R.layout.sidemenu);
            if (mdrawer != null && mdrawer.getMenuView() != null && mRightMenuActivity == null)
                mRightMenuActivity = new SideMenu(SendEnquiry.this, mdrawer);
            side_menu = (ImageView) findViewById(R.id.side_menu);
            side_menu.setImageDrawable(getResources().getDrawable(R.drawable.sidemenu));

            title = (TextView) this.findViewById(R.id.title);
            title.setText(getString(R.string.send_enquiry));

            cart_img = (ImageView) findViewById(R.id.cart_img);
            cart_img.setVisibility(View.VISIBLE);

            enquiry_type = (EditText) findViewById(R.id.enquiry_type);
            enquiry_type.setOnClickListener(this);

            send_enquiry_list = (ListView) findViewById(R.id.send_enquiry_list);

            enquiry_details = (EditText) findViewById(R.id.enquiry_details);
            submit = (TextView) findViewById(R.id.submit);
            submit.setOnClickListener(this);


            write("NAME------------ " + getPrefrence(context, "DisplayName"));

            side_menu.setOnClickListener(this);
            cart_img.setOnClickListener(this);

            booked_plot = (LinearLayout) findViewById(R.id.booked_plot);
            agreement = (LinearLayout) findViewById(R.id.agreement);
            project_gallery = (LinearLayout) findViewById(R.id.project_gallery);
            slp_booking = (LinearLayout) findViewById(R.id.slp_booking);
            updates = (LinearLayout) findViewById(R.id.updates);

            booked_image = (ImageView) findViewById(R.id.booked_image);
            agreement_image = (ImageView) findViewById(R.id.agreement_image);
            project_gallery_image = (ImageView) findViewById(R.id.project_gallery_image);
            slp_booking_image = (ImageView) findViewById(R.id.slp_booking_image);
            updates_image = (ImageView) findViewById(R.id.updates_image);

            total_amt = (TextView) findViewById(R.id.total_amt);
            booked_textView = (TextView) findViewById(R.id.booked_textView);
            agreement_textView = (TextView) findViewById(R.id.agreement_textView);
            project_gallery_textView = (TextView) findViewById(R.id.project_gallery_textView);
            slp_booking_textView = (TextView) findViewById(R.id.slp_booking_textView);
            updates_textView = (TextView) findViewById(R.id.updates_textView);

            booked_plot.setOnClickListener(this);
            agreement.setOnClickListener(this);
            project_gallery.setOnClickListener(this);
            slp_booking.setOnClickListener(this);
            updates.setOnClickListener(this);

            booked_textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            booked_textView.setText("Booked Plot");
            booked_textView.setSelected(true);
            booked_textView.setSingleLine(true);

            agreement_textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            agreement_textView.setText("Agreement");
            agreement_textView.setSelected(true);
            agreement_textView.setSingleLine(true);

            project_gallery_textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            project_gallery_textView.setText("Project Gallery");
            project_gallery_textView.setSelected(true);
            project_gallery_textView.setSingleLine(true);

            slp_booking_textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            slp_booking_textView.setText("SLP Booking");
            slp_booking_textView.setSelected(true);
            slp_booking_textView.setSingleLine(true);

            updates_textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            updates_textView.setText("Updates");
            updates_textView.setSelected(true);
            updates_textView.setSingleLine(true);

            if (getPrefrence(context, "selected").equalsIgnoreCase("1")) {
                booked_clicked(booked_textView, agreement_textView, project_gallery_textView, slp_booking_textView, updates_textView, booked_image, agreement_image, project_gallery_image, slp_booking_image, updates_image);
            } else if (getPrefrence(context, "selected").equalsIgnoreCase("2")) {
                agreement_clicked(booked_textView, agreement_textView, project_gallery_textView, slp_booking_textView, updates_textView, booked_image, agreement_image, project_gallery_image, slp_booking_image, updates_image);
            } else if (getPrefrence(context, "selected").equalsIgnoreCase("3")) {
                gallery_clicked(booked_textView, agreement_textView, project_gallery_textView, slp_booking_textView, updates_textView, booked_image, agreement_image, project_gallery_image, slp_booking_image, updates_image);
            } else if (getPrefrence(context, "selected").equalsIgnoreCase("4")) {
                slp_clicked(booked_textView, agreement_textView, project_gallery_textView, slp_booking_textView, updates_textView, booked_image, agreement_image, project_gallery_image, slp_booking_image, updates_image);
            } else if (getPrefrence(context, "selected").equalsIgnoreCase("5")) {
                updates_clicked(booked_textView, agreement_textView, project_gallery_textView, slp_booking_textView, updates_textView, booked_image, agreement_image, project_gallery_image, slp_booking_image, updates_image);
            }



        } catch (Error e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkNetworkConnection()) {
                getHomePageData();
        } else {
            createInfoDialog(context, "Alert", getString(R.string.alert_internet));
        }
    }


    public void getHomePageData() {
        chechSSLcertificate();
        String url = Cons.getEnquiry + "/" + getPrefrence(context, "FK_MemId");
        write(">>>>" + url);
        pd.show();
        try {
            JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST, url, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    pd.dismiss();
                    Log.e("Response >>>>>> ", response.toString());
                    try {
                        JSONArray jarr = new JSONArray(response.toString());
                        if ((jarr.getJSONObject(0).getString("Response")).equalsIgnoreCase("Success")) {

                           get_enquiry_arr.clear();
                            for (int i = 0; i < jarr.length(); i++) {
                                get_enquiry_arr.add(i, jarr.getJSONObject(i));
                            }
                            write("Hiiiiiiiiiiiii " + get_enquiry_arr.size());
                            get_enquiry_adapter = new GetEnquiry_Adapter(context);
                            get_enquiry_adapter.notifyDataSetChanged();
                            send_enquiry_list.setAdapter(get_enquiry_adapter);
//
                        } else {
                            showToastS("No Records Found.");
                        }
                    } catch (Error e) {
                        pd.dismiss();
                        e.printStackTrace();
                    } catch (Exception e) {
                        pd.dismiss();
                        e.printStackTrace();
                    }

                }
            },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            pd.dismiss();
                            Toast.makeText(context, "Unable to fetch data: " + volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
            request.setRetryPolicy(new RetryPolicy() {
                @Override
                public int getCurrentTimeout() {
                    return 50000;
                }

                @Override
                public int getCurrentRetryCount() {
                    return 50000;
                }

                @Override
                public void retry(VolleyError error) throws VolleyError {
                }
            });
            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(request);

        } catch (Error e) {
            pd.dismiss();
            e.printStackTrace();
        } catch (Exception e) {
            pd.dismiss();
            e.printStackTrace();
        }
    }

    public class GetEnquiry_Adapter extends BaseAdapter {
        LayoutInflater mInflater;

        public GetEnquiry_Adapter(Context con) {
            mInflater = LayoutInflater.from(con);
        }

        @Override
        public int getCount() {
            return get_enquiry_arr.size();
        }

        @Override
        public Object getItem(int position) {
            return get_enquiry_arr.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = mInflater.inflate(R.layout.send_enquiry_adapter, null);

            TextView enquiry_date = (TextView) convertView.findViewById(R.id.enquiry_date);
            TextView enquiry_type = (TextView) convertView.findViewById(R.id.enquiry_type);
            TextView enqiry = (TextView) convertView.findViewById(R.id.enqiry);
            TextView status = (TextView) convertView.findViewById(R.id.status);

//
//            {
//                "EnqDate": "5/29/2017 4:21:44 PM",
//                    "Enquiry": "Testing",
//                    "EnquiryType": "Agreement",
//                    "Response": "Success",
//                    "Status": "Pending"
//            },


            try {

                enquiry_date.setText(get_enquiry_arr.get(position).getString("EnqDate").substring(0, get_enquiry_arr.get(position).getString("EnqDate").indexOf(" ")));
                enquiry_type.setText(get_enquiry_arr.get(position).getString("EnquiryType"));
                enqiry.setText(get_enquiry_arr.get(position).getString("Enquiry"));
                status.setText(get_enquiry_arr.get(position).getString("Status"));

                if (get_enquiry_arr.get(position).getString("Status").equals("Pending"))
                {
                    status.setTextColor(Color.RED);

                } else if(get_enquiry_arr.get(position).getString("Status").equals("Done"))
                {
                    status.setText("View Response");
                    status.setTextColor(Color.GREEN);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }


            return convertView;
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.side_menu:
                drawer_toggle(mdrawer);
                break;
            case R.id.cart_img:
                logoutclick(cart_img);
                break;
            case R.id.enquiry_type:
                PopupMenu popup = new PopupMenu(this, enquiry_type);
                popup.getMenuInflater().inflate(R.menu.enquiry_type, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        try {
                            enquiry_type.setText(item.getTitle());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return true;
                    }
                });
                popup.show();
                break;
            case R.id.submit:
                if (Validation()) {
                    sendEnquiry();
                } else {
                    showToastS(error_msg);
                }
                break;
            case R.id.booked_plot:
                write("CLICKED============================ ");
                booked_clicked(booked_textView, agreement_textView, project_gallery_textView, slp_booking_textView, updates_textView, booked_image, agreement_image, project_gallery_image, slp_booking_image, updates_image);
                nevigateTab(context, AllBooking.class, null);
                break;
            case R.id.agreement:
                agreement_clicked(booked_textView, agreement_textView, project_gallery_textView, slp_booking_textView, updates_textView, booked_image, agreement_image, project_gallery_image, slp_booking_image, updates_image);
                nevigateTab(context, AgreementListing.class, null);
                write("CLICKED============================ ");
                break;
            case R.id.project_gallery:
                gallery_clicked(booked_textView, agreement_textView, project_gallery_textView, slp_booking_textView, updates_textView, booked_image, agreement_image, project_gallery_image, slp_booking_image, updates_image);
                nevigateTab(context, Gallery.class, null);
                write("CLICKED============================ ");
                break;
            case R.id.slp_booking:
                slp_clicked(booked_textView, agreement_textView, project_gallery_textView, slp_booking_textView, updates_textView, booked_image, agreement_image, project_gallery_image, slp_booking_image, updates_image);
                nevigateTab(context, SlpBooking.class, null);
                write("CLICKED============================ ");
                break;
            case R.id.updates:
                updates_clicked(booked_textView, agreement_textView, project_gallery_textView, slp_booking_textView, updates_textView, booked_image, agreement_image, project_gallery_image, slp_booking_image, updates_image);
                nevigateTab(context, Updates.class, null);
                write("CLICKED============================ ");
                break;
        }
    }


    public void sendEnquiry() {

        String url = Cons.sendEnquiry + "/" + enquiry_type.getText().toString().trim().replaceAll(" ", "%20") + "/"
                + enquiry_details.getText().toString().trim().replaceAll(" ", "%20").replaceAll(System.getProperty("line.separator"), "%20") + "/" + getPrefrence(context, "FK_MemId");

        write("sendEnquiry url = " + url);
        pd.show();
        try {
            JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST, url, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    pd.dismiss();
                    Log.e("Response >>>>>> ", response.toString());
                    try {
                        JSONArray jarr = new JSONArray(response.toString());
                        if ((jarr.getJSONObject(0).getString("Response")).equalsIgnoreCase("Success")) {

                            showToastS("Successfully sent");
                            enquiry_details.setText("");
                            enquiry_type.setText("");

                        }
                    } catch (Error e) {
                        pd.dismiss();
                        e.printStackTrace();
                    } catch (Exception e) {
                        pd.dismiss();
                        e.printStackTrace();
                    }

                }
            },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            pd.dismiss();
                            Toast.makeText(context, "Unable to fetch data: " + volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
            request.setRetryPolicy(new RetryPolicy() {
                @Override
                public int getCurrentTimeout() {
                    return 50000;
                }

                @Override
                public int getCurrentRetryCount() {
                    return 50000;
                }

                @Override
                public void retry(VolleyError error) throws VolleyError {
                }
            });

            hostNameVerifier();
            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(request);

        } catch (Error e) {
            pd.dismiss();
            e.printStackTrace();
        } catch (Exception e) {
            pd.dismiss();
            e.printStackTrace();
        }
    }


    @Override
    public void onItemSelect() {

    }


    @Override
    public void onBackPressed() {
        mdrawer.closeMenu();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                context.runOnUiThread(new Runnable() {
                    public void run() {
                        ExitAlert(context);
                    }
                });
            }
        }, 300);
    }

    private boolean Validation() {
        error_msg = "";
        if (enquiry_type.getText().toString().trim().equalsIgnoreCase("")) {
            error_msg = "Enquiry Type can not be empty";
            return false;
        } else if (enquiry_details.getText().toString().trim().equalsIgnoreCase("")) {
            error_msg = "Enquiry Details can not be empty";
            return false;
        }
        return true;
    }


}
