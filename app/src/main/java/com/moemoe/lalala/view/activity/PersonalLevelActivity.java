package com.moemoe.lalala.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.webkit.WebChromeClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.entity.AuthorInfo;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.GlideCircleTransform;
import com.moemoe.lalala.utils.NetworkUtils;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.view.fragment.WebViewFragment;

import butterknife.BindView;

/**
 * Created by yi on 2016/12/1.
 */

public class PersonalLevelActivity extends BaseAppCompatActivity {

    @BindView(R.id.iv_back)
    View mIvBack;
    @BindView(R.id.tv_toolbar_title)
    TextView mTvTitle;
    @BindView(R.id.iv_avatar)
    ImageView ivAvatar;
    @BindView(R.id.pb_curr_score)
    ProgressBar pbScore;
    @BindView(R.id.tv_content_level_name)
    TextView tvLevelName;
    @BindView(R.id.tv_curr_score)
    TextView tvScore;
    @BindView(R.id.tv_level)
    TextView tvLevel;
    @BindView(R.id.tv_up_level)
    TextView tvUpLevel;
    private Handler mHander;
    private AuthorInfo mAuthorInfo;
    private String levelName;
    private String levelColor;
    private int score;
    private int scoreStart;
    private int scoreEnd;
    private int level;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_personal_level;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        mTvTitle.setTextColor(ContextCompat.getColor(this,R.color.main_cyan));
        mTvTitle.setText(R.string.label_level_introduce);
        mHander = new Handler();
        mProgressBar = (ProgressBar) findViewById(R.id.pgbar_progress);
        WebViewFragment mWebViewFragment = WebViewFragment.newInstance(ApiService.LEVEL_DETAILS_URL);
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, mWebViewFragment).commit();
        levelName = getIntent().getStringExtra("levelName");
        levelColor = getIntent().getStringExtra("levelColor");
        score = getIntent().getIntExtra("score",0);
        scoreStart = getIntent().getIntExtra("scoreStart",0);
        scoreEnd = getIntent().getIntExtra("scoreEnd",0);
        level = getIntent().getIntExtra("level",0);
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {

    }

    @Override
    protected void initListeners() {
        mIvBack.setVisibility(View.VISIBLE);
        mIvBack.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void initData() {

    }

    public static void startActivity(Context context,String levelName,String levelColor,int score,int scoreStart,int scoreEnd,int level) {
        Intent intent = new Intent(context, PersonalLevelActivity.class);
        intent.putExtra("levelName",levelName);
        intent.putExtra("levelColor",levelColor);
        intent.putExtra("score",score);
        intent.putExtra("scoreStart",scoreStart);
        intent.putExtra("scoreEnd",scoreEnd);
        intent.putExtra("level",level);
        context.startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHander.post(new Runnable() {
            @Override
            public void run() {
                mAuthorInfo = PreferenceUtils.getAuthorInfo();
                if (mAuthorInfo != null) {
                    Glide.with(PersonalLevelActivity.this)
                            .load(StringUtils.getUrl(PersonalLevelActivity.this,mAuthorInfo.getHeadPath(),DensityUtil.dip2px(PersonalLevelActivity.this,80), DensityUtil.dip2px(PersonalLevelActivity.this,80),false,true))
                            .override(DensityUtil.dip2px(PersonalLevelActivity.this,80), DensityUtil.dip2px(PersonalLevelActivity.this,80))
                            .placeholder(R.drawable.bg_default_circle)
                            .error(R.drawable.bg_default_circle)
                            .transform(new GlideCircleTransform(PersonalLevelActivity.this))
                            .into(ivAvatar);
                }
                tvLevelName.setText(levelName);
                tvLevelName.setTextColor(StringUtils.readColorStr(levelColor, ContextCompat.getColor(PersonalLevelActivity.this,R.color.main_cyan)));
                tvLevel.setText(String.format(getResources().getString(R.string.label_level), level));
                tvUpLevel.setText(String.format(getResources().getString(R.string.label_level), level + 1));
                SpannableStringBuilder span = new SpannableStringBuilder();
                String prefixStr = getString(R.string.label_level_introduce_prefix);
                String suffixStr = getString(R.string.label_level_introduce_suffix);
                String lvScoreStr = (scoreEnd - score) + "";
                span.append(prefixStr).append(lvScoreStr).append(suffixStr);
                span.setSpan(new ForegroundColorSpan(ContextCompat.getColor(PersonalLevelActivity.this,R.color.orange_f6d27b)), prefixStr.length(), prefixStr.length() + lvScoreStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                tvScore.setText(span);
                pbScore.setMax(scoreEnd - scoreStart);
                pbScore.setProgress(score - scoreStart);

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
    public void showCustomView(View view, WebChromeClient.CustomViewCallback callback, boolean isUseNew){

    }

    @Override
    public void hideCustomView(boolean isUseNew){}

    private void showProgressBar(){
        if(NetworkUtils.isNetworkAvailable(this)){
            mProgressBar.setVisibility(View.VISIBLE);
        }else {
            mProgressBar.setVisibility(View.GONE);
            showToast(R.string.msg_connection);
        }
    }
}
