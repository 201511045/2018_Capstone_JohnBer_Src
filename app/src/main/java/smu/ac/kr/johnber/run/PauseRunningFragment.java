package smu.ac.kr.johnber.run;


import static smu.ac.kr.johnber.util.LogUtils.makeLogTag;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import smu.ac.kr.johnber.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PauseRunningFragment extends Fragment {
  private final static String TAG = makeLogTag(PauseRunningFragment.class);

  public PauseRunningFragment() {
    // Required empty public constructor
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.run_pause_running_fragment, container, false);
  }

}
