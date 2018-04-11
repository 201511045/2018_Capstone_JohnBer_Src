package smu.ac.kr.johnber.run;

import static smu.ac.kr.johnber.util.LogUtils.LOGD;
import static smu.ac.kr.johnber.util.LogUtils.LOGV;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import smu.ac.kr.johnber.BaseActivity;
import smu.ac.kr.johnber.R;
import smu.ac.kr.johnber.opendata.WeatherForecast;
import smu.ac.kr.johnber.util.LogUtils;

/**
 * 메인화면
 * - 위치, 네트워크. 퍼미션 세팅/ 체크
 * - 지도 설정
 * - 동네예보 주기적으로 싱크
 * - 현재 위치 표시
 * - 위도 경도값 알아내기
 */
//TODO: 지도 처리
public class MainActivity extends BaseActivity implements OnClickListener {

  private static final String TAG = LogUtils.makeLogTag(MainActivity.class);

  private TextView mRegion;
  private TextView mSky;
  private TextView mCelcius;

  private Button mRun;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_run_main);
    initView();
    seListeners();
    //TODO: 일정 시간마다 데이터를 갱신해야함
    WeatherForecast.loadWeatherData(this);
    LOGD(TAG, "onCreate");

  }

  @Override
  protected void onStart() {
    super.onStart();
    LOGD(TAG, "onStart");
  }

  @Override
  protected void onStop() {
    super.onStop();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
  }

  @Override
  protected void onResume() {
    super.onResume();
  }

  @Override
  protected void onRestart() {
    super.onRestart();
  }






  @Override
  protected int getNavigationItemID() {
    return R.id.action_run;
  }

  /**
   * view 초기화 설정
   */
  public void initView() {
    mRun = findViewById(R.id.btn_run);
  }
  public void seListeners(){
    mRun.setOnClickListener(this);
  }


  @Override
  public void onClick(View view) {
    switch (view.getId()){
      case R.id.btn_run:
        Intent intent = new Intent(this,RunningActivity.class);
        startActivity(intent);
        break;
    }
  }
}
