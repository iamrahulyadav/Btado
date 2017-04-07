package com.cybussolutions.bataado.Fragments;


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
import android.widget.ListView;
import android.widget.TextView;

import com.cybussolutions.bataado.Activities.Find_Friends;
import com.cybussolutions.bataado.Activities.Login;
import com.cybussolutions.bataado.Activities.User_Friends;
import com.cybussolutions.bataado.Activities.User_Profile;
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
    ImageView profile_image;
    TextView photo,friend,reviews;

    String  strphoto,strfriend,strreviews ;


    String[] nameArray = new String[] {"Notifications","Find Friends ","Message","Log Out"};
    int[] images =  new int[] {R.drawable.notification,R.drawable.find_friend,R.drawable.message,R.drawable.logout};
    ListView drawer_list;

    public Drawer_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_drawer, container, false);


        drawer_list = (ListView) v.findViewById(R.id.drawer_list);
        username = (TextView) v.findViewById(R.id.drawer_user_name);
        profile_image = (ImageView) v.findViewById(R.id.profile_image);
        photo = (TextView) v.findViewById(R.id.photos_count);
        friend = (TextView) v.findViewById(R.id.friends_count);
        reviews = (TextView) v.findViewById(R.id.review_count);


        String  strname="";
        final String pp;
        final String strid;
        SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences("BtadoPrefs", getActivity().MODE_PRIVATE);
        strname = pref.getString("user_name","");;
        strid = pref.getString("user_id","");
        pp = pref.getString("profile_pic","");;
        strphoto = pref.getString("total_photos","");;
        strfriend = pref.getString("total_friends","");;
        strreviews = pref.getString("total_reviews","");;


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

        if(pp.equals(""))
        {
            Picasso.with(getContext())
                    .load("http://bataado.cybussolutions.com/uploads/no-image-icon-hi.png")
                    .resize(150, 150)
                    .centerCrop().transform(new CircleTransform())
                    .into(profile_image);
        }

        if(pp.startsWith("https://fb"))
        {
            Picasso.with(getContext())
                    .load(pp)
                    .resize(150, 150)
                    .centerCrop().transform(new CircleTransform())
                    .into(profile_image);
        }
        else
        {
            Picasso.with(getContext())
                    .load(End_Points.IMAGE_PROFILE_PIC +pp )
                    .resize(150, 150)
                    .centerCrop().transform(new CircleTransform())
                    .into(profile_image);

        }


        username.setText(strname);


        username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent= new Intent(getContext(), User_Profile.class);
                intent.putExtra("username", finalStrname);
                intent.putExtra("userID",strid);
                getContext().startActivity(intent);

            }
        });



        drawer_addapter = new draw_addapter(getActivity(),nameArray,images);

        drawer_list.setAdapter(drawer_addapter);

        drawer_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {



                if (position == 0 )
                {



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


                }

                if (position == 3 )
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


}
