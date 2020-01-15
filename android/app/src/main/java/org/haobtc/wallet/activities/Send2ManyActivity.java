package org.haobtc.wallet.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.gyf.immersionbar.ImmersionBar;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.common.Constant;

import org.haobtc.wallet.R;
import org.haobtc.wallet.activities.base.BaseActivity;
import org.haobtc.wallet.adapter.SendmoreAddressAdapter;
import org.haobtc.wallet.event.SendMoreAddressEvent;
import org.haobtc.wallet.utils.CommonUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Send2ManyActivity extends BaseActivity implements View.OnClickListener, TextWatcher {
    public static final String TOTAL_AMOUNT = "org.haobtc.wallet.activities.Send2ManyActivity.TOTAL";
    public static final String ADDRESS = "org.haobtc.activities.Send2ManyActivity.ADDRESS";
    private Button buttonSweep, buttonPaste, buttonNext;
    private EditText editTextAddress, editTextAmount;
    private LinearLayout buttonAdd;
    private TextView textViewTotal;
    private RecyclerView recyclerView;
    private static final int REQUEST_CODE = 0;
    private RxPermissions rxPermissions;
    private String straddress;
    private String strSmount;
    private List<SendMoreAddressEvent> sendMoreAddressList;
    private String wallet_name;
    private BigDecimal totalAmount;
    private ImageView imgBack;
    private Map<String, String> pramasBtc;
    private ArrayList<Map<String, String>> mapsBtc;
    private String strmapBtc;

    public int getLayoutId() {
        return R.layout.send_to_many;
    }

    public void initView() {
        rxPermissions = new RxPermissions(this);
        buttonAdd = findViewById(R.id.lin_add_to);
        buttonSweep = findViewById(R.id.bn_sweep_2many);
        buttonPaste = findViewById(R.id.bn_paste_2many);
        buttonNext = findViewById(R.id.bn_send2many_next);
        editTextAddress = findViewById(R.id.edit_address_2many);
        editTextAmount = findViewById(R.id.edit_amount_2many);
        textViewTotal = findViewById(R.id.total);
        recyclerView = findViewById(R.id.recycler_add_to);
        imgBack = findViewById(R.id.img_back);
        buttonAdd.setOnClickListener(this);
        buttonSweep.setOnClickListener(this);
        buttonPaste.setOnClickListener(this);
        buttonNext.setOnClickListener(this);
        imgBack.setOnClickListener(this);
        buttonNext.setEnabled(false);
        buttonNext.setBackground(getResources().getDrawable(R.color.button_bk_grey));
        editTextAmount.addTextChangedListener(this);

        init();
        // recyclerView.setAdapter();
    }

    private void init() {
        Intent intent = getIntent();
        wallet_name = intent.getStringExtra("wallet_name");
        totalAmount = new BigDecimal("0");
    }

    @Override
    public void initData() {
        sendMoreAddressList = new ArrayList<>();
        pramasBtc = new HashMap<>();
        mapsBtc = new ArrayList<>();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lin_add_to:
                addressList();

                break;
            case R.id.bn_sweep_2many:
                rxPermissions
                        .request(Manifest.permission.CAMERA)
                        .subscribe(granted -> {
                            if (granted) { // Always true pre-M
                                //If you have already authorized it, you can directly jump to the QR code scanning interface
                                Intent intent2 = new Intent(this, CaptureActivity.class);
                                startActivityForResult(intent2, REQUEST_CODE);
                            } else { // Oups permission denied
                                Toast.makeText(this, R.string.photopersion, Toast.LENGTH_SHORT).show();
                            }
                        }).dispose();
                break;
            case R.id.bn_paste_2many:
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                if (clipboard != null) {
                    ClipData data = clipboard.getPrimaryClip();
                    if (data != null && data.getItemCount() > 0) {
                        editTextAddress.setText(data.getItemAt(0).getText());
                    }
                }
                break;
            case R.id.bn_send2many_next:
                String bigAmont = String.valueOf(totalAmount);
                int size = sendMoreAddressList.size();
                if (size == 0) {
                    mToast(getResources().getString(R.string.pleas_add_outputAdrs));
                } else {
                    Intent intent = new Intent(this, SendOne2ManyMainPageActivity.class);
                    intent.putExtra("listdetail", (Serializable) sendMoreAddressList);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(TOTAL_AMOUNT, textViewTotal.getText().toString());
                    intent.putExtra(ADDRESS, "");
                    intent.putExtra("wallet_name", wallet_name);
                    intent.putExtra("addressNum", size);
                    intent.putExtra("totalAmount", bigAmont);
                    intent.putExtra("strmapBtc", strmapBtc);
                    startActivity(intent);
                }

                break;
            case R.id.img_back:
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Scan QR code / barcode return
        if (requestCode == 0 && resultCode == RESULT_OK) {
            if (data != null) {
                String content = data.getStringExtra(Constant.CODED_CONTENT);
                Log.i("content", "on------: " + content);
                if (!TextUtils.isEmpty(content)) {
                    if (content.contains("bitcoin:")) {
                        String replace = content.replaceAll("bitcoin:", "");
                        editTextAddress.setText(replace);
                    } else {
                        editTextAddress.setText(content);
                    }
                }
            }
        }
    }

    @SuppressLint("DefaultLocale")
    private void addressList() {
        straddress = editTextAddress.getText().toString();
        strSmount = editTextAmount.getText().toString();
        if (TextUtils.isEmpty(straddress)) {
            mToast(getResources().getString(R.string.please_input_address));
            return;
        }
        if (TextUtils.isEmpty(strSmount)) {
            mToast(getResources().getString(R.string.please_input_amount));
            return;
        }
        SendMoreAddressEvent sendMoreAddressEvent = new SendMoreAddressEvent();
        sendMoreAddressEvent.setInputAddress(straddress);
        sendMoreAddressEvent.setInputAmount(strSmount);
        sendMoreAddressList.add(sendMoreAddressEvent);
        totalAmount = new BigDecimal("0");
        for (int i = 0; i < sendMoreAddressList.size(); i++) {
            BigDecimal bignum1 = new BigDecimal(sendMoreAddressList.get(i).getInputAmount());
            Log.i("bignum1", "add------: " + bignum1);
            //Total transfer quantity
            totalAmount = totalAmount.add(bignum1);
        }
        textViewTotal.setText(String.format("%s", totalAmount));

        //hashmap
        pramasBtc.put(straddress, strSmount);
        mapsBtc.clear();
        mapsBtc.add(pramasBtc);
        strmapBtc = new Gson().toJson(mapsBtc);//intent to sendone2manypage
        Log.i("mapsBtc", "mapsBtc-----: " + strmapBtc);

        //edittext to null
        editTextAddress.setText("");
        editTextAmount.setText("");
        buttonNext.setEnabled(true);
        buttonNext.setBackground(getResources().getDrawable(R.color.button_bk));
        SendmoreAddressAdapter sendmoreAddressAdapter = new SendmoreAddressAdapter(Send2ManyActivity.this, sendMoreAddressList);
        recyclerView.setAdapter(sendmoreAddressAdapter);
        sendmoreAddressAdapter.setmOnDeleteItemClickListener(new SendmoreAddressAdapter.OnItemDeleteClickListener() {
            @Override
            public void onItemClick(int position) {
                //change string
                pramasBtc.remove(sendMoreAddressList.get(position).getInputAddress());
                mapsBtc.clear();
                mapsBtc.add(pramasBtc);
                strmapBtc = new Gson().toJson(mapsBtc);//intent to sendone2manypage
                Log.i("mapsBtc", "mapsBtc-----: " + strmapBtc);

                //change view
                sendMoreAddressList.remove(position);
                sendmoreAddressAdapter.notifyDataSetChanged();

                if (sendMoreAddressList.size() == 0) {
                    textViewTotal.setText(String.format("%d", 0));
                    buttonNext.setEnabled(false);
                    buttonNext.setBackground(getResources().getDrawable(R.color.button_bk_grey));
                }else{
                    totalAmount = new BigDecimal("0");
                    for (int i = 0; i < sendMoreAddressList.size(); i++) {
                        BigDecimal bignum1 = new BigDecimal(sendMoreAddressList.get(i).getInputAmount());
                        Log.i("bignum1", "add------: " + bignum1);
                        //Total transfer quantity
                        totalAmount = totalAmount.add(bignum1);
                    }
                    textViewTotal.setText(String.format("%s", totalAmount));
                }

            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.toString().contains(".")) {
            if (s.length() - 1 - s.toString().indexOf(".") > 7) {
                s = s.toString().subSequence(0,
                        s.toString().indexOf(".") + 8);
                editTextAmount.setText(s);
                editTextAmount.setSelection(s.length());
            }
        }
        if (s.toString().trim().substring(0).equals(".")) {
            s = "0" + s;
            editTextAmount.setText(s);
            editTextAmount.setSelection(2);
        }
        if (s.toString().startsWith("0")
                && s.toString().trim().length() > 1) {
            if (!s.toString().substring(1, 2).equals(".")) {
                editTextAmount.setText(s.subSequence(0, 1));
                editTextAmount.setSelection(1);
            }
            return;
        }

    }

    @Override
    public void afterTextChanged(Editable s) {
        String strAmount = editTextAmount.getText().toString();
        if (!TextUtils.isEmpty(strAmount)) {
            BigDecimal bignum1 = new BigDecimal(strAmount);
            BigDecimal bigDecimal = new BigDecimal(21000000);
            int mathMax = bignum1.compareTo(bigDecimal);
            if (mathMax == 1) {
                mToast(getResources().getString(R.string.sendMore));
            }
        }

    }
}