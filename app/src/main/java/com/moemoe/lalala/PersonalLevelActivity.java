package com.moemoe.lalala;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.webkit.WebChromeClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app.annotation.ContentView;
import com.app.annotation.FindView;
import com.app.common.util.DensityUtil;
import com.moemoe.lalala.data.AuthorInfo;
import com.moemoe.lalala.fragment.WebViewFragment;
import com.moemoe.lalala.utils.NetworkUtils;
import com.moemoe.lalala.utils.ToastUtil;
import com.moemoe.lalala.view.NoDoubleClickListener;
import com.squareup.picasso.Picasso;

;

/**
 * Created by Haru on 2016/4/29 0029.
 */
@ContentView(R.layout.ac_personal_level)
public class PersonalLevelActivity extends BaseActivity {

    public static final String EXTRA_KEY_URL = "targeturl";

    @FindView(R.id.toolbar)
    private Toolbar mToolbar;
    @FindView(R.id.tv_toolbar_title)
    private TextView mTvTitle;
    @FindView(R.id.iv_avatar)
    private ImageView ivAvatar;
    @FindView(R.id.pb_curr_score)
    private ProgressBar pbScore;
    @FindView(R.id.tv_content_level_name)
    private TextView tvLevelName;
    @FindView(R.id.tv_curr_score)
    private TextView tvScore;
    @FindView(R.id.tv_level)
    private TextView tvLevel;
    @FindView(R.id.tv_up_level)
    private TextView tvUpLevel;

    private WebViewFragment mWebViewFragment;
    private AuthorInfo mAuthorInfo;
    private Handler mHander;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            // 透明状态栏
//            getWindow().addFlags(
//                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            // 透明导航栏
//            getWindow().addFlags(
//                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//            SystemBarTintManager tintManager = new SystemBarTintManager(this);
//            tintManager.setStatusBarTintEnabled(true);
//            // 设置状态栏的颜色
//            tintManager.setStatusBarTintResource(R.color.main_title_cyan);
//            getWindow().getDecorView().setFitsSystemWindows(true);
//        }
//    }

    @Override
    protected void initView() {
        mToolbar.setNavigationOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                finish();
            }
        });
        mTvTitle.setText(R.string.label_level_introduce);
        String mStartUrl = mIntent.getStringExtra(EXTRA_KEY_URL);
        mHander = new Handler();
        mProgressBar = (ProgressBar) findViewById(R.id.pgbar_progress);
        mWebViewFragment = WebViewFragment.newInstance(mStartUrl);
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, mWebViewFragment).commit();
    }

    public static void startActivity(Context context, String url) {
        Intent intent = new Intent(context, PersonalLevelActivity.class);
        intent.putExtra(EXTRA_KEY_URL, url);
        context.startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHander.post(new Runnable() {
            @Override
            public void run() {
                mAuthorInfo = mPreferMng.getThirdPartyLoginMsg();
                if (mAuthorInfo != null) {
//                    Utils.image().bind(ivAvatar, mAuthorInfo.getmHeadPath(), new ImageOptions.Builder()
//                            .setSize(DensityUtil.dip2px(80), DensityUtil.dip2px(80))
//                            .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
//                            .setLoadingDrawableId(R.drawable.ic_default_avatar_l)
//                            .setFailureDrawableId(R.drawable.ic_default_avatar_l)
//                            .build());
                    Picasso.with(PersonalLevelActivity.this)
                            .load(mAuthorInfo.getmHeadPath())
                            .resize(DensityUtil.dip2px(80), DensityUtil.dip2px(80))
                            .placeholder(R.drawable.ic_default_avatar_l)
                            .error(R.drawable.ic_default_avatar_l)
                            .config(Bitmap.Config.RGB_565)
                            .into(ivAvatar);
                }
                tvLevelName.setText(mAuthorInfo.getLevel_name());
                tvLevelName.setTextColor(mAuthorInfo.getLevel_color());
                tvLevel.setText(String.format(getResources().getString(R.string.label_level), mAuthorInfo.getLevel()) + "");
                tvUpLevel.setText(String.format(getResources().getString(R.string.label_level), mAuthorInfo.getLevel() + 1) + "");
                SpannableStringBuilder span = new SpannableStringBuilder();
                String prefixStr = getString(R.string.label_level_introduce_prefix);
                String suffixStr = getString(R.string.label_level_introduce_suffix);
                String lvScoreStr = (mAuthorInfo.getLevel_score_end() - mAuthorInfo.getScore()) + "";
                span.append(prefixStr).append(lvScoreStr).append(suffixStr);
                span.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.main_orange)), prefixStr.length(), prefixStr.length() + lvScoreStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                tvScore.setText(span);
                pbScore.setMax(mAuthorInfo.getLevel_score_end() - mAuthorInfo.getLevel_score_start());
                pbScore.setProgress(mAuthorInfo.getScore() - mAuthorInfo.getLevel_score_start());
            }
        });
    }

    @Override
    public void onPageFinished(String url){

    }

    @Override
    public void cancelProgressBar(){
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onPageStarted(String url){
        showProgressBar();
    }

    @Override
    public void showCustomView(View view,WebChromeClient.CustomViewCallback callback,boolean isUseNew){

    }

    @Override
    public void hideCustomView(boolean isUseNew){}

    private void showProgressBar(){
        if(NetworkUtils.isNetworkAvailable(this)){
            mProgressBar.setVisibility(View.VISIBLE);
        }else {
            mProgressBar.setVisibility(View.GONE);
            ToastUtil.showToast(this,R.string.msg_server_connection);
        }
    }
}
