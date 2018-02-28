package com.yatang.xc.xcr.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.yatang.xc.xcr.MyApplication;
import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.config.Constants;
import com.yatang.xc.xcr.db.CityDao;
import com.yatang.xc.xcr.db.ProvinceDao;
import com.yatang.xc.xcr.dialog.NomalDialog;
import com.yatang.xc.xcr.dialog.SelectDialog;
import com.yatang.xc.xcr.service.HttpRequestService;
import com.yatang.xc.xcr.uitls.ResultParam;
import com.yatang.xc.xcr.views.DivisionEditText;

import org.jocerly.jcannotation.ui.BindView;
import org.jocerly.jcannotation.ui.ContentView;
import org.jocerly.jcannotation.utils.StringUtils;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 添加银行卡信息页面
 * Created by dengjiang on 2017/8/3.
 */
@ContentView(R.layout.activity_add_bankcard)
public class AddBankCardActivity extends BaseActivity {
    @BindView(id = R.id.btnLeft, click = true)
    private TextView btnLeft;
    @BindView(id = R.id.textTitle)
    private TextView textTitle;
    @BindView(id = R.id.editCustomName)
    private EditText editCustomName;
    @BindView(id = R.id.editIDNumber)
    private EditText editIDNumber;
    @BindView(id = R.id.editPhoneNumber)
    private EditText editPhoneNumber;
    @BindView(id = R.id.editCardNumber)
    private DivisionEditText editCardNumber;
    @BindView(id = R.id.btn_explain, click = true)
    private TextView btn_explain;

    @BindView(id = R.id.rlBank, click = true)
    private RelativeLayout rlBank;
    @BindView(id = R.id.textBank)
    private TextView textBank;

    @BindView(id = R.id.rlProvince, click = true)
    private RelativeLayout rlProvince;
    @BindView(id = R.id.textProvince)
    private TextView textProvince;

    @BindView(id = R.id.rlBranchBank, click = true)
    private RelativeLayout rlBranchBank;
    @BindView(id = R.id.textBranchBank)
    private TextView textBranchBank;

    @BindView(id = R.id.btnConfirm, click = true)
    private TextView btnConfirm;

    @BindView(id = R.id.cbZhiFuBao)
    private CheckBox cbZhiFuBao;

    @BindView(id = R.id.textProtocol, click = true)
    private TextView textProtocol;

    private SelectDialog selectDialog;//选择省市对话框
    private NomalDialog dialog;//手机号提示对话框
    private ProvinceDao provinceDao;
    private String Cardholder;//持卡人
    private String ID;//身份证
    private String phoneNumber;//电话号码
    private String cardNumber;//卡号
    private String bank;//开户行
    private String bankID;//开户行ID
    private String province;//省份
    private String city;//城市
    private String provinceID;//省份ID
    private String cityID;//城市ID
    private String branchBank;//支行名称
    private String branchBankID;//支行ID;
    private String ProtocolURL;
    private CityDao cityDao;
    private java.util.List<ConcurrentHashMap<String, String>> provinces;
    private List<ConcurrentHashMap<String, String>> citys;

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("添加银行卡");
        MobclickAgent.onResume(aty);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("添加银行卡");
        MobclickAgent.onPause(aty);
    }

    @Override
    public void initWidget() {
        textTitle.setText("添加银行卡");
    }

    @Override
    public void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Cardholder = bundle.getString("Cardholder");
            ID = bundle.getString("ID");
            ProtocolURL = bundle.getString("ServiceAgreeUrl");
            editCustomName.setText(Cardholder);
            editIDNumber.setText(ID);
        }
        dialog = new NomalDialog(aty);
        dialog.setOnNoamlLickListener(new NomalDialog.OnNoamlLickListener() {
            @Override
            public void onOkClick() {
                setResult(RESULT_OK);
                finish();
            }
        });
        selectDialog = new SelectDialog(aty);
        provinceDao = new ProvinceDao(aty);
        cityDao = new CityDao(AddBankCardActivity.this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        Bundle bundle = null;
        switch (v.getId()) {
            case R.id.btnLeft:
                onBackPressed();
                break;
            case R.id.rlBank:
                //选择银行
//                searchBankDialog.show(SearchBankDialog.SEARCH_BANK, "");
                bundle = new Bundle();
                bundle.putString("Type", SearchBankActivity.SEARCH_BANK);
                bundle.putString("BankID", "");
                skipActivityForResult(aty, SearchBankActivity.class, bundle, Integer.parseInt(SearchBankActivity.SEARCH_BANK));
                break;
            case R.id.rlBranchBank:
                if (StringUtils.isEmpty(bankID)) {
                    toast("请选择开户行");
                    break;
                }
                //选择支行
                bundle = new Bundle();
                bundle.putString("Type", SearchBankActivity.SEARCH_BRANCH);
                bundle.putString("BankID", bankID);
                skipActivityForResult(aty, SearchBankActivity.class, bundle, Integer.parseInt(SearchBankActivity.SEARCH_BRANCH));
                break;
            case R.id.rlProvince:
                //选择省市
                selectDialog.setOnSelectListener(onSelectListener_Province);
                provinces = provinceDao.getAllData();
                citys = cityDao.getAllDataByProvinceId(provinces.get(0).get("ProvinceId"));
                selectDialog.show("选择省市", provinces, citys);
                break;
            case R.id.textProtocol:
                //跳转到协议详情页面
                bundle = new Bundle();
                bundle.putString("ClassUrl", ProtocolURL);
                bundle.putString("ClassName", "服务协议");
                skipActivity(aty, WebViewActivity.class, bundle);
                break;
            case R.id.btn_explain:
                //手机号提示对话框
                new NomalDialog(AddBankCardActivity.this).showTitle("手机号说明", "银行预留的手机号码是办理该银行卡时所填写的手机号码。" +
                        "没有预留、手机号码忘记或者已停用，请联系银行客服更新处理。", "我知道了");
                break;
            case R.id.btnConfirm:
                if (canConfirm()) {
                    //提交银行卡信息
                    CommitBankInformation();
                }
                break;
        }
    }

    /**
     * 选择省市对话框回调
     */
    private SelectDialog.OnSelectListener onSelectListener_Province = new SelectDialog.OnSelectListener() {

        @Override
        public void onConfirm(ConcurrentHashMap<String, String> map_province, ConcurrentHashMap<String, String> map_city) {
            provinceID = map_province.get("ProvinceId");
            cityID = map_city.get("CityId");
            province = map_province.get("Province");
            city = map_city.get("City");
            textProvince.setText(province + "  " + city);
        }

        @Override
        public void onCancel() {
        }
    };

    /**
     * check 输入信息是否合法
     */
    private boolean canConfirm() {
        cardNumber = editCardNumber.getText().toString().trim().replace(" ", "");
        if (StringUtils.isEmpty(cardNumber)) {
            toast("请输入银行卡号");
            return false;
        }
        if (cardNumber.length() > 30) {
            toast("卡号最多30个字符");
            return false;
        }

        if (StringUtils.isEmpty(bankID)) {
            toast("请选择开户行");
            return false;
        }
        phoneNumber = editPhoneNumber.getText().toString().trim();
        if (StringUtils.isEmpty(phoneNumber)) {
            toast("请输入手机号");
            return false;
        }
        if (phoneNumber.length() != 11) {
            toast("请输入正确的手机号码");
            return false;
        }

        if (StringUtils.isEmpty(province) || StringUtils.isEmpty(city)) {
            toast("请选择省市");
            return false;
        }

        if (StringUtils.isEmpty(branchBankID)) {
            toast("请选择支行");
            return false;
        }
        if (!cbZhiFuBao.isChecked()) {
            toast("必须同意服务协议");
            return false;
        }
        return true;
    }

    /**
     * 提交银行卡信息
     */
    private void CommitBankInformation() {
        params.clear();
        params.put("UserId", MyApplication.instance.UserId);
        params.put("StoreSerialNo", MyApplication.instance.StoreSerialNoDefault);
        params.put("Token", MyApplication.instance.Token);
        params.put("OpenAccount", MyApplication.instance.FinancialAccount);//一账通帐号
        params.put("Cardholder", Cardholder);
        params.put("ID", ID);
        params.put("PhoneNum", phoneNumber);
        params.put("BankCardNum", cardNumber.replace(" ", ""));
        params.put("BankCardId", bankID);
        params.put("BankCardName", bank);
        params.put("Province", province);
        params.put("ProvinceId", provinceID);
        params.put("City", city);
        params.put("CityId", cityID);
        params.put("BankCardBranch", branchBank);
        params.put("BankCardBranchId", branchBankID);
        httpRequestService.doRequestData(aty, "User/SetBankCardMsg", params, new HttpRequestService.IHttpRequestCallback() {

            @Override
            public void onRequestCallBack(ResultParam resultParam) {
                if (Constants.M00.equals(resultParam.resultId)) {
                    MobclickAgent.onEvent(aty, "Firm_Store_Bank");
                    dialog.showClose("添加银行卡成功，请耐心等待平台为您办理结算账户手续！", "知道了");
                    //提交成功
                } else if (Constants.M01.equals(resultParam.resultId)) {
                    toast(getResources().getString(R.string.account_overdue));
                    doEmpLoginOut();//帐号过期
                } else {
                    toast(resultParam.message);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode + "") {
            case SearchBankActivity.SEARCH_BANK:
                if (resultCode == RESULT_OK) {
                    bank = data.getExtras().getString("BankName");
                    bankID = data.getExtras().getString("BankID");
                    textBank.setText(bank);
                    branchBank = "";
                    branchBankID = "";
                    textBranchBank.setText("请选择");
                }
                break;
            case SearchBankActivity.SEARCH_BRANCH:
                if (resultCode == RESULT_OK) {
                    branchBank = data.getExtras().getString("BankName");
                    branchBankID = data.getExtras().getString("BankID");
                    textBranchBank.setText(branchBank);
                }
                break;
        }
    }
}
