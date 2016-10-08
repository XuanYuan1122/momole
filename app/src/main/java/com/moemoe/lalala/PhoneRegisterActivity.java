package com.moemoe.lalala;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
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
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.app.annotation.ContentView;
import com.app.annotation.FindView;
import com.app.common.Callback;
import com.moemoe.lalala.app.AlertDialog;
import com.moemoe.lalala.data.AuthorInfo;
import com.moemoe.lalala.network.CallbackFactory;
import com.moemoe.lalala.network.OnNetWorkCallback;
import com.moemoe.lalala.network.Otaku;
import com.moemoe.lalala.utils.CountryCode;
import com.moemoe.lalala.utils.CustomUrlSpan;
import com.moemoe.lalala.utils.EncoderUtils;
import com.moemoe.lalala.utils.NetworkUtils;
import com.moemoe.lalala.utils.PhoneUtil;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.ToastUtil;
import com.moemoe.lalala.view.NoDoubleClickListener;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by Haru on 2016/4/28 0028.
 */
@ContentView(R.layout.ac_register_phone)
public class PhoneRegisterActivity extends BaseActivity {

    private String TAG = "PhoneRegisterActivity";

    @FindView(R.id.toolbar)
    private Toolbar mToolbar;
    @FindView(R.id.edt_phone_number)
    private EditText mEdtAccount;
    @FindView(R.id.edt_password)
    private EditText mEdtPassword;
    @FindView(R.id.tv_to_next)
    private View mTxtNext;
    @FindView(R.id.tv_regist_privace)
    private TextView mTxtPrivate;
    @FindView(R.id.tv_country_code)
    private TextView mTvCountry;
    @FindView(R.id.tv_toolbar_title)
    private TextView mTitle;

    private String mPhoneCountry;
    private String mCountryCode;
    private String mAccount;
    private String mPassword;

    @Override
    protected void initView() {
        mToolbar.setNavigationOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                finish();
            }
        });
        mTitle.setText(R.string.label_register);
        String privaceStr = getString(R.string.label_register_contract);
        String content = getString(R.string.label_register_contract_url_content);
        CustomUrlSpan span = new CustomUrlSpan(this, content, Otaku.REGISTER_PRICACE_URL);
        SpannableStringBuilder style = new SpannableStringBuilder(privaceStr);
        style.setSpan(span, privaceStr.indexOf(content), privaceStr.indexOf(content) + content.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mTxtPrivate.setText(style);
        mTxtPrivate.setMovementMethod(LinkMovementMethod.getInstance());
        initPhoneCountry();
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
                if (NetworkUtils.isNetworkAvailable(PhoneRegisterActivity.this)) {
                    createDialog();
                    Otaku.getAccountV2().register(mAccount).enqueue(CallbackFactory.getInstance().callback(new OnNetWorkCallback<String, String>() {
                        @Override
                        public void success(String token, String s) {
                            finalizeDialog();
                            AuthorInfo authorInfo = new AuthorInfo();
                            authorInfo.setmPhone(mAccount);
                            authorInfo.setmPassword(EncoderUtils.MD5(mPassword));
                            mPreferMng.saveThirdPartyLoginMsg(authorInfo);
                            Intent intent = new Intent(PhoneRegisterActivity.this, PhoneStateCheckActivity.class);
                            intent.putExtra(PhoneStateCheckActivity.EXTRA_ACTION, PhoneStateCheckActivity.ACTION_REGISTER);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void failure(String e) {
                            finalizeDialog();
                            ToastUtil.showToast(PhoneRegisterActivity.this, R.string.msg_register_failed);
                        }
                    }));
                } else {
                    ToastUtil.showCenterToast(PhoneRegisterActivity.this, R.string.msg_server_connection);
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
        }
        mTvCountry.setText(mPhoneCountry + "(+" + mCountryCode + ")");
    }

    public interface CountryCodeSelectListener{
        public void onItemSelected(CountryCode countryCode);
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
        public CountryCodeAdapter(Context context, int resource, int textViewResourceId, List<CountryCode> objects){
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
            codeTextView.setText("+" + countryCode.getCode());
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
