package com.moemoe.lalala.view.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.gyf.barlibrary.ImmersionBar;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerSimpleComponent;
import com.moemoe.lalala.di.modules.SimpleModule;
import com.moemoe.lalala.dialog.AlertDialog;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.entity.AuthorInfo;
import com.moemoe.lalala.presenter.SimpleContract;
import com.moemoe.lalala.presenter.SimplePresenter;
import com.moemoe.lalala.utils.CountryCode;
import com.moemoe.lalala.utils.CustomUrlSpan;
import com.moemoe.lalala.utils.EncoderUtils;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.NetworkUtils;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.PhoneUtil;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.StringUtils;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * Created by yi on 2016/12/1.
 */

public class PhoneRegisterActivity extends BaseAppCompatActivity implements SimpleContract.View{

    private String TAG = "PhoneRegisterActivity";
    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.edt_phone_number)
    EditText mEdtAccount;
    @BindView(R.id.edt_password)
    EditText mEdtPassword;
    @BindView(R.id.tv_to_next)
    View mTxtNext;
    @BindView(R.id.tv_regist_privace)
    TextView mTxtPrivate;
    @BindView(R.id.tv_country_code)
    TextView mTvCountry;
    @BindView(R.id.tv_toolbar_title)
    TextView mTitle;
    @Inject
    SimplePresenter mPresenter;
    private String mPhoneCountry;
    private String mCountryCode;
    private String mAccount;
    private String mPassword;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_register_phone;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        ImmersionBar.with(this)
                .statusBarView(R.id.top_view)
                .statusBarDarkFont(true,0.2f)
                .init();
        DaggerSimpleComponent.builder()
                .simpleModule(new SimpleModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        mTitle.setText(R.string.label_register);
        String privaceStr = getString(R.string.label_register_contract);
        String content = getString(R.string.label_register_contract_url_content);
        CustomUrlSpan span = new CustomUrlSpan(this, content, ApiService.REGISTER_PRICACE_URL);
        SpannableStringBuilder style = new SpannableStringBuilder(privaceStr);
        style.setSpan(span, privaceStr.indexOf(content), privaceStr.indexOf(content) + content.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mTxtPrivate.setText(style);
        mTxtPrivate.setMovementMethod(LinkMovementMethod.getInstance());
        initPhoneCountry();
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {

    }

    @Override
    protected void onDestroy() {
        if(mPresenter != null)mPresenter.release();
        super.onDestroy();
    }

    @Override
    protected void initListeners() {
        mIvBack.setVisibility(View.VISIBLE);
        mIvBack.setImageResource(R.drawable.btn_back_black_normal);
        mIvBack.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                finish();
            }
        });
        mEdtAccount.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mAccount = "+" + mCountryCode + mEdtAccount.getText().toString();
                mTxtNext.setEnabled(PhoneUtil.isPhoneFormated(mAccount, mCountryCode) &&
                        StringUtils.isLegalPassword(mPassword));
            }
        });
        mEdtPassword.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {
                mPassword = mEdtPassword.getText().toString();
                mTxtNext.setEnabled(
                        PhoneUtil.isPhoneFormated(mAccount, mCountryCode) &&
                                StringUtils.isLegalPassword(mPassword));
            }
        });
        mTxtNext.setOnClickListener(new NoDoubleClickListener() {

            @Override
            public void onNoDoubleClick(View v) {
                if (NetworkUtils.checkNetworkAndShowError(PhoneRegisterActivity.this)) {
                    createDialog();
                    mPresenter.doRequest(mAccount,1);
                }
            }
        });

        mTvCountry.setOnClickListener(new NoDoubleClickListener() {

            @Override
            public void onNoDoubleClick(View v) {
                showPhoneCountryDialog();
            }
        });
    }

    private void initPhoneCountry(){
        try {
            mPhoneCountry = PhoneUtil.getLocalDisplayStr(this);
            mCountryCode = PhoneUtil.getLocalCountryCode(this);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        mTvCountry.setText(mPhoneCountry + "(+" + mCountryCode + ")");
    }

    @Override
    public void onSuccess(Object o) {
        finalizeDialog();
        AuthorInfo authorInfo = new AuthorInfo();
        authorInfo.setPhone(mAccount);
        authorInfo.setPassword(EncoderUtils.MD5(mPassword));
        PreferenceUtils.setAuthorInfo(authorInfo);
        Intent intent = new Intent(PhoneRegisterActivity.this, PhoneStateCheckActivity.class);
        intent.putExtra(PhoneStateCheckActivity.EXTRA_ACTION, PhoneStateCheckActivity.ACTION_REGISTER);
        startActivity(intent);
        finish();
    }

    @Override
    public void onFailure(int code,String msg) {
        finalizeDialog();
        ErrorCodeUtils.showErrorMsgByCode(PhoneRegisterActivity.this,code,msg);
    }

    public interface CountryCodeSelectListener{
        void onItemSelected(CountryCode countryCode);
    }

    @Override
    protected void initData() {

    }

    public static class PhoneCountryDialogFragment extends DialogFragment implements AdapterView.OnItemClickListener, TextWatcher{

        List<CountryCode> countryCodes = null;
        private CountryCodeSelectListener mSelectListener = null;
        CountryCodeAdapter adapter;
        public void setOnCountryCodeSelectListener(CountryCodeSelectListener listener){
            mSelectListener = listener;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            adapter.getFilter().filter(s.toString());
        }

        @Override
        public void onCreate(Bundle savedInstanceState){
            countryCodes = PhoneUtil.getAllCountryCodes(getActivity());
            super.onCreate(savedInstanceState);
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            View root = View.inflate(getActivity(), R.layout.choose_country_code, null);
            EditText inputBox = (EditText) root.findViewById(R.id.search_input_box);
            inputBox.addTextChangedListener(this);
            ListView listView = (ListView) root.findViewById(android.R.id.list);
            adapter = new CountryCodeAdapter(getActivity(), 0, 0, countryCodes);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(this);
            builder.setTitle(R.string.label_dlg_select_country);
            builder.setView(root);
            return builder.create();
        }
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            CountryCode code = (CountryCode) parent.getItemAtPosition(position);
            if (mSelectListener!=null) {
                mSelectListener.onItemSelected(code);
            }
            dismiss();
        }
    }

    public static class CountryCodeAdapter extends ArrayAdapter<CountryCode> {
        CountryCodeAdapter(Context context, int resource, int textViewResourceId, List<CountryCode> objects){
            super(context, resource, textViewResourceId, objects);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = View.inflate(getContext(), R.layout.choose_country_code_item, null);
            }
            CountryCode countryCode = getItem(position);
            TextView codeTextView = (TextView) view.findViewById(R.id.country_code);
            TextView countryTextView = (TextView) view.findViewById(R.id.regis_country_name);
            String code = "+" + countryCode.getCode();
            codeTextView.setText(code);
            countryTextView.setText(countryCode.getCountry());
            return view;
        }
    }

    private void showPhoneCountryDialog(){
        PhoneCountryDialogFragment dialogFragment = new PhoneCountryDialogFragment();
        dialogFragment.setOnCountryCodeSelectListener(new CountryCodeSelectListener()
        {
            @Override
            public void onItemSelected(CountryCode countryCode) {
                mCountryCode = countryCode.getCode();
                mPhoneCountry = countryCode.getCountry();
                mTvCountry.setText(mPhoneCountry + "(+" + mCountryCode + ")");
                mAccount = "+" + mCountryCode + mEdtAccount.getText().toString();
                mTxtNext.setEnabled(
                        PhoneUtil.isPhoneFormated(mAccount, mCountryCode) &&
                                StringUtils.isLegalPassword(mPassword));
            }
        });
        dialogFragment.show(getSupportFragmentManager(), TAG +" CountryCode");
    }
}
