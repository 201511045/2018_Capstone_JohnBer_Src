package smu.ac.kr.johnber.my;

import static smu.ac.kr.johnber.util.LogUtils.LOGD;
import static smu.ac.kr.johnber.util.LogUtils.makeLogTag;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.realm.Realm;
import smu.ac.kr.johnber.BaseActivity;
import smu.ac.kr.johnber.R;
import smu.ac.kr.johnber.run.Record;
import smu.ac.kr.johnber.util.BitmapUtil;

public class MyActivity extends BaseActivity implements MyCourseViewHolder.itemClickListener {
    private final static String TAG = makeLogTag(MyActivity.class);
    private static final int REQUEST_IMAGE_CAPTURE = 101;
    private TextView courseName;
    private TextView userName;
    public TextView startPoint;
    public TextView distance;
    public TextView calories;
    public TextView time;
    public ImageView profileView;
    private int dataNO;
    private View mView;
    private Marker mMarker;
    private MapView mMapView;
    private GoogleMap mgoogleMap;
    private Realm mRealm;
    private FirebaseAuth mAuth;
    private MyCourseViewHolder.itemClickListener listener;
    //  private List<Record> mockRecords ;
    private List<Record> recordItems;
    private MyCourseAdapter adapter;
    private ScrollView scrollView;
    private SharedPreferences pref ;
    private HashMap<String, Record> recordHashMap = new HashMap<String, Record>();

    private SkeletonScreen skeletonScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_act);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        userName = findViewById(R.id.tv_my_username);
        userName.setText(user.getDisplayName());
            pref = getSharedPreferences("pref", MODE_PRIVATE);
        //TODO : 프로필 사진
        profileView = findViewById(R.id.imageView3);

        if (pref.getString("profileImage",null) != null) {
            Glide.with(this)
                    .load(BitmapUtil.decodeBase64(pref.getString("profileImage", null)))
                    .apply(new RequestOptions().circleCrop()).into(profileView)
            ;
        }
        profileView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //프로필사진 찍기
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                }

            }
        });





//        myStatisticsviewPager.setOffscreenPageLimit(2);

        //        set on page listener 구현?
//    if (mockRecords != null) {
//      mockRecords.clear();
//    }

        if (recordItems == null) {
            recordItems = new ArrayList<>();
            this.getRecord();
        }
//    mockRecords = generateMockRecords();
        adapter = new MyCourseAdapter(this, recordItems, this);
        RecyclerView recyclerView = findViewById(R.id.my_rv_course);
//        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
//            recyclerView.addItemDecoration(new DividerItemDecoration(this, VERTICAL));

        //TODO : 통계
        MyStatisticsPagerAdapter myStatisticsPagerAdapter = new MyStatisticsPagerAdapter(this,recordHashMap);
        ViewPager myStatisticsviewPager = (ViewPager)findViewById(R.id.my_statistics_viewPager);
        myStatisticsviewPager.setAdapter(myStatisticsPagerAdapter);

        /**
         * skeleton 로딩 구현
         * option1 : recyclerview - setAdapter 주석처리할것
         * option2 : View
         */
        //*** option1
//        final SkeletonScreen skeletonScreen = Skeleton.bind(recyclerView)
//                .adapter(adapter)
//                .shimmer(true)
//                .angle(20)
//                .frozen(false)
//                .duration(1200)
//                .count(10)
//                .load(R.layout.item_skeleton_news)
//                .show(); //default count is 10
//        recyclerView.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                skeletonScreen.hide();
//            }
//        }, 3000);
        recyclerView.setAdapter(adapter);
        //*** option2
        View rootView = findViewById(R.id.mycourse_item_container);
        skeletonScreen = Skeleton.bind(rootView)
                .shimmer(true)
                .duration(2000)
                .color(R.color.shimmer_color)
                .load(R.layout.activity_view_skeleton)
                .show();
        MyHandler myHandler = new MyHandler(this);
        myHandler.sendEmptyMessageDelayed(1, 1000);



    }

    //사진 결과 받아와서 sharedPreference에 저장
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            //sharedPreference에 저장
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("profileImage", BitmapUtil.encodeTobase64(imageBitmap));
            editor.commit();

            Glide.with(this)
                    .load(imageBitmap)
                    .apply(new RequestOptions().circleCrop()).into(profileView)
            ;
        }
    }

    @Override
    protected int getNavigationItemID() {
        return R.id.action_statistics;
    }


//    private List<Record> generateMockR`ecords() {
//
//        List<Record> mock = new ArrayList<>();
//
//        SharedPreferences preferences;
//        preferences = getApplicationContext().getSharedPreferences("savedRecord", Context.MODE_PRIVATE);
//        Gson gson = new Gson();
//        String response = preferences.getString("LOCATIONLIST", "");
//        ArrayList<JBLocation> locationArrayList = gson.fromJson(response, new TypeToken<List<JBLocation>>() {
//        }.getType());
//
//        double distance = Double.parseDouble(preferences.getString("DISTANCE", "0"));
//        double elapsedTime = Double.parseDouble(preferences.getString("ELAPSEDTIME", "0"));
//        double calories = Double.parseDouble(preferences.getString("CALORIES", "0"));
//        double startTime = Double.parseDouble(preferences.getString("STARTTIME", "0"));
//        double endTime = Double.parseDouble(preferences.getString("ENDTIME", "0"));
//        String sDate = preferences.getString("DATE", "0");
//        try {
//            Date date = new SimpleDateFormat("MM/dd/yyy").parse(sDate);
//
//            String title = sDate + " RUN";   // date를 변환해서 우선 넣기로함
//
//
//            for (int i = 0; i < 10; i++) {
//                mock.add(new Record(distance, elapsedTime, calories, locationArrayList, date, startTime, endTime, title,null));
//            }
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//        return mock;
//
//    }

    public void getRecord() {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FirebaseUser user = mAuth.getCurrentUser();
                String uid = user.getUid();

                Iterable<DataSnapshot> ds = dataSnapshot.child(uid).child("userRecord").getChildren();
//                LOGD(TAG,"test : "+ds.getKey().toString());
                //DB에 저장된 데이터 HashMap에 저장.
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //DB에서 로그인한 아이디와 일치하는지 확인 후 해당 데이터만 읽어옴.
                    if (snapshot.getKey().toString().equals(uid)) {
                        Log.d("MainActivity", "Single ValueEventListener : " + snapshot.getValue());
                        String keyDate1 = "";
                        for (DataSnapshot snapshot1 : snapshot.child("userRecord").getChildren()) {
                            keyDate1 = snapshot1.getKey().toString();
                            for (DataSnapshot snapshot2 : snapshot1.getChildren()) {
                                String keyDate = keyDate1 + '/' + snapshot2.getKey().toString();
                                recordHashMap.put(keyDate, snapshot2.getValue(Record.class));
                                recordItems.add(snapshot2.getValue(Record.class));
//             LOGD(TAG,"HASHMAPSIZE0 : "+recordHashMap.size());
//             LOGD(TAG,"record-location : "+ recordHashMap.get(keyDate).getJBLocation().get(0).getmLatitude());
                            }
                        }
                    }
//          LOGD(TAG,"Size : "+recordItems.size();
                    if (adapter != null) {

                        adapter.notifyDataSetChanged();
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public List<Record> getRecordsItems() {
        return this.recordItems;
    }

    @Override
    public void onItemClicked(View view, int position) {
        LOGD(TAG, "CLICKED!" + position);
        showDeatilView(position, view);
    }


    public void showDeatilView(int position, View view) {
        Bundle data = new Bundle();
        data.putInt("position", position);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        MyCourseDetailFragment fragment = new MyCourseDetailFragment();
        fragment.setArguments(data);
        fragmentTransaction.add(R.id.mycourse_item_container, fragment, "MYCOURSEDETAILFRAGMENT")
                .addToBackStack(null)
                .commit();
    }

    //for skeleton view
    public static class MyHandler extends android.os.Handler {
        private final WeakReference<MyActivity> activityWeakReference;

        MyHandler(MyActivity activity) {
            this.activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (activityWeakReference.get() != null) {
                activityWeakReference.get().skeletonScreen.hide();
            }
        }
    }
}
