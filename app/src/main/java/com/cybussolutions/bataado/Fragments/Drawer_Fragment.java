package com.cybussolutions.bataado.Fragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cybussolutions.bataado.Activities.Account_Settings;
import com.cybussolutions.bataado.Activities.Find_Friends;
import com.cybussolutions.bataado.Activities.Friend_Request;
import com.cybussolutions.bataado.Activities.ImageGallary;
import com.cybussolutions.bataado.Activities.Login;
import com.cybussolutions.bataado.Activities.MessagesScreen;
import com.cybussolutions.bataado.Activities.ProfilePhotos;
import com.cybussolutions.bataado.Activities.User_Friends;
import com.cybussolutions.bataado.Activities.User_Profile;
import com.cybussolutions.bataado.Activities.User_Reviews;
import com.cybussolutions.bataado.Adapter.draw_addapter;
import com.cybussolutions.bataado.Helper.CircleTransform;
import com.cybussolutions.bataado.Network.End_Points;
import com.cybussolutions.bataado.R;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;


public class Drawer_Fragment extends Fragment {

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    draw_addapter drawer_addapter;
    TextView username;
    ImageView profile_image,ivProfile;
    TextView photo,friend,reviews;

    String  strphoto,strfriend,strreviews ;


    String[] nameArray = new String[] {"Friend Request","Find Friends ","Message","Account Settings","Log Out"};
    int[] images =  new int[] {R.drawable.notification,R.drawable.find_friend,R.drawable.message,R.drawable.profile,R.drawable.logout};
    ListView drawer_list;
    LinearLayout friendsLayout,reviewsLayout,photosLayout;
    public Drawer_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_drawer, container, false);

        drawer_list = v.findViewById(R.id.drawer_list);
        username = v.findViewById(R.id.drawer_user_name);
        profile_image = v.findViewById(R.id.profile_image);
        ivProfile = v.findViewById(R.id.ivProfile);
        photo = v.findViewById(R.id.photos_count);
        friend = v.findViewById(R.id.friends_count);
        reviews = v.findViewById(R.id.review_count);
        friendsLayout=v.findViewById(R.id.friendsLayout);
        reviewsLayout=v.findViewById(R.id.reviewsLayout);
        photosLayout=v.findViewById(R.id.photosLayout);

        String  strname="";
        final String pp;
        final String strid;
        SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences("BtadoPrefs", getActivity().MODE_PRIVATE);
        strname = pref.getString("user_name","");
        strid = pref.getString("user_id","");
        pp = pref.getString("profile_pic","");
        strphoto = pref.getString("total_photos","");
        strfriend = pref.getString("total_friends","");
        strreviews = pref.getString("total_reviews","");


        photo.setText(strphoto);
        friend.setText(strfriend);
        reviews.setText(strreviews);

        friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),User_Friends.class);
                intent.putExtra("user_id",strid);
                getContext().startActivity(intent);
            }
        });

        final String finalStrname = strname;
        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getContext(), User_Profile.class);
                intent.putExtra("username", finalStrname);
                intent.putExtra("userID",strid);
                getContext().startActivity(intent);
            }
        });
        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getContext(), User_Profile.class);
                intent.putExtra("username", finalStrname);
                intent.putExtra("userID",strid);
                getContext().startActivity(intent);
            }
        });

        if(pp.equals(""))
        {
            Picasso.with(getContext())
                    .load(End_Points.IMAGE_RREVIEW_URL+"no-image-icon-hi.png")
                    .resize(150, 150)
                    .centerCrop().transform(new CircleTransform())
                    .into(profile_image);
        }
        else {
            if (pp.startsWith("https://graph.facebook.com/")) {
                Picasso.with(getContext())
                        .load(pp)
                        .resize(150, 150)
                        .centerCrop().transform(new CircleTransform())
                        .into(profile_image);
            } else {
                Picasso.with(getContext())
                        .load(End_Points.IMAGE_PROFILE_PIC + pp)
                        .resize(150, 150)
                        .centerCrop().transform(new CircleTransform())
                        .into(profile_image);

            }
        }

        username.setText(strname);
        photosLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ImageGallary.class);
                intent.putExtra("username", finalStrname);
                intent.putExtra("userID", strid);
                intent.putExtra("profilePic", pp);
                startActivity(intent);
            }
        });
        friendsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), User_Friends.class);
                intent.putExtra("user_id", strid);
                startActivity(intent);
            }
        });
        reviewsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), User_Reviews.class);
                intent.putExtra("username", finalStrname);
                intent.putExtra("userID", strid);
                intent.putExtra("profilePic", pp);
                startActivity(intent);
            }
        });
        username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent= new Intent(getContext(), User_Profile.class);
                intent.putExtra("username", finalStrname);
                intent.putExtra("userID",strid);
                getContext().startActivity(intent);

            }
        });

        final SharedPreferences notification_pref = getContext().getApplicationContext().getSharedPreferences("Notifications", getContext().MODE_PRIVATE);
        final SharedPreferences.Editor editor= notification_pref.edit();


        drawer_addapter = new draw_addapter(getActivity(),nameArray,images);

        drawer_list.setAdapter(drawer_addapter);

        drawer_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {



                if (position == 0 )
                {
                    editor.putString("friend_request","deactive");
                    editor.apply();
                    drawer_addapter.notifyDataSetChanged();
                    Intent intent = new Intent(getActivity(), Friend_Request.class);
                    startActivity(intent);


                }

                if (position == 1 )
                {
                    SharedPreferences pref = getActivity().getSharedPreferences("BtadoPrefs", Context.MODE_PRIVATE);
                    String ids= pref.getString("fb_ids","");

                    Intent intent = new Intent(getActivity(), Find_Friends.class);
                    intent.putExtra("fb_ids",ids);
                    startActivity(intent);

                }

                if (position == 2 )
                {
                    Intent intent = new Intent(getActivity(), MessagesScreen.class);
                    startActivity(intent);
                }
                if(position==3){
                    SharedPreferences pref = getActivity().getSharedPreferences("BtadoPrefs", Context.MODE_PRIVATE);
                    String pp = pref.getString("profile_pic","");
                    String strname = pref.getString("user_name","");
                    String strid = pref.getString("user_id","");
                    Intent intent= new Intent(getContext(), Account_Settings.class);
                    intent.putExtra("username", strname);
                    intent.putExtra("userProfile",pp);
                    intent.putExtra("userID",strid);
                    startActivity(intent);
                }
                if (position == 4 )
                {

                    SharedPreferences pref = getActivity().getSharedPreferences("BtadoPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.remove("remmember_me");
                    editor.commit();

                    LoginManager.getInstance().logOut();

                    Intent i = new Intent(getActivity(), Login.class);

                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);



                }



            }
        });


        return v;
    }
    public void setup(DrawerLayout dawerLayout , Toolbar toolbar)


    {


        mDrawerLayout = dawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), mDrawerLayout, toolbar, R.string.draweropem, R.string.drawerclose) {
            @Override
            public void onDrawerOpened(View drawerView) {

                super.onDrawerOpened(drawerView);



                // getActivity().supportInvalidateOptionsMenu();

            }

            @Override
            public void onDrawerClosed(View drawerView) {

                super.onDrawerClosed(drawerView);
                //  getActivity().supportInvalidateOptionsMenu();


            }
        };



    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences("BtadoPrefs", getActivity().MODE_PRIVATE);
        strphoto = pref.getString("total_photos","");
        strfriend = pref.getString("total_friends","");
        strreviews = pref.getString("total_reviews","");


        photo.setText(strphoto);
        friend.setText(strfriend);
        reviews.setText(strreviews);
    }
}
