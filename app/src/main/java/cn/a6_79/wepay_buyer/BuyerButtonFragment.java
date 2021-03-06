package cn.a6_79.wepay_buyer;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.a6_79.wepay_buyer.NetPack.HttpThreadTask;
import cn.a6_79.wepay_buyer.NetPack.ImageTaskResponse;
import cn.a6_79.wepay_buyer.NetPack.ImageThreadTask;
import cn.a6_79.wepay_buyer.NetPack.OnAsyncImageTaskListener;

public class BuyerButtonFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.buyer_button_fragment, null);
        initInterface();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initInterface();
    }

    private void initInterface() {
        API.init();

        HttpThreadTask task = API.getButton(getButtonListener);
        if (task != null)
            task.execute();

    }

    ResponseListener getButtonListener = new ResponseListener() {
        @Override
        public void callback(String response) throws JSONException {
            JSONObject jsonObject = API.ResponseShow(getActivity().getApplicationContext(), response);
            JSONArray body = jsonObject.getJSONArray("body");
            LinearLayout buttonCardList = getView().findViewById(R.id.button_card_list);
            buttonCardList.removeAllViews();
            if (body != null) {
                for (int i = 0; i < body.length(); i++) {
                    JSONObject buttonInfo = body.getJSONObject(i);
                    int buttonID = buttonInfo.getInt("button_id");
                    int categoryID = buttonInfo.getInt("category_id");
                    String categoryName = buttonInfo.getString("category_name");
                    String goodID = buttonInfo.getString("good_id");
                    String goodName = buttonInfo.getString("good_name");
                    int number = buttonInfo.getInt("number");
                    String goodPic = buttonInfo.getString("good_pic");
                    Double price = buttonInfo.getDouble("price");

                    ButtonCard aButtonCard = new ButtonCard(buttonID, categoryID, categoryName, goodID, goodName, number, goodPic, price, getActivity().getApplicationContext(),
                            getActivity(), deleteButtonListener);
                    aButtonCard.setCardBackgroundColor(0xffffffff);
                    buttonCardList.addView(aButtonCard);
                }
                AddButtonCard addButtonCard = new AddButtonCard(getActivity().getApplicationContext(), getActivity());
                addButtonCard.setCardBackgroundColor(0xffffffff);
                buttonCardList.addView(addButtonCard);
            }
        }
    };

    ResponseListener deleteButtonListener = new ResponseListener() {
        @Override
        public void callback(String response) throws JSONException {
            JSONObject jsonObject = API.ResponseShow(getActivity().getApplicationContext(), response);
            initInterface();
        }
    };
}


class ButtonCard extends CardView {
    private int buttonID;
    private int categoryID;
    private String categoryName;
//    private String goodID;
//    private String goodName;
    private int number;
    private Activity activity;

    private TextView mDeleteButton;
    private TextView mCategoryName;
    private TextView mGoodName;
    private TextView mGoodNumber;
    private TextView mGoodPrice;
    private ImageView mGoodImg;


    public ButtonCard (final int buttonID, final int categoryID, String categoryName, String goodID, String goodName, int number, String goodPic, Double price,
                       Context context, final Activity activity, final ResponseListener deleteButtonListener) {
        super(context);
        this.buttonID = buttonID;
        this.categoryID = categoryID;
        this.categoryName = categoryName;
//        this.goodID = goodID;
//        this.goodName = goodName;
        this.number = number;
        this.activity = activity;

        LayoutInflater.from(context).inflate(R.layout.button_card, this);

        mCategoryName = findViewById(R.id.category_name);
        mCategoryName.setText(categoryName);
        mGoodName = findViewById(R.id.good_name);
        mGoodName.setText(goodName);
        mGoodPrice = findViewById(R.id.good_price);
        mGoodPrice.setText("¥"+price);
        mGoodNumber = findViewById(R.id.good_number);
        mGoodNumber.setText("数量:" + number);
        mGoodImg = findViewById(R.id.good_img);
        new ImageThreadTask(goodPic, new OnAsyncImageTaskListener() {
            @Override
            public void callback(ImageTaskResponse imageTaskResponse) {
                Bitmap bitmap = imageTaskResponse.getBitmap();
                mGoodImg.setImageBitmap(bitmap);
            }
        }).execute();

        mDeleteButton = findViewById(R.id.delete_button);
        mDeleteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity).setMessage("确定删除该按钮及对应商品?")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                HttpThreadTask task = API.deleteButton(buttonID, deleteButtonListener);
                                if (task != null)
                                    task.execute();
                            }
                        }).setNegativeButton("取消", null);
                builder.show();
            }
        });
        mCategoryName.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                HttpThreadTask task = API.getGoodInCategory(categoryID, checkButtonCategoryListener);
                if (task != null)
                    task.execute();
            }
        });

        mGoodName.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                HttpThreadTask task = API.getGoodInCategory(categoryID, checkButtonCategoryListener);
                if (task != null)
                    task.execute();
            }
        });

        mGoodNumber.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                HttpThreadTask task = API.getGoodInCategory(categoryID, checkButtonCategoryListener);
                if (task != null)
                    task.execute();
            }
        });

        mGoodImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                HttpThreadTask task = API.getGoodInCategory(categoryID, checkButtonCategoryListener);
                if (task != null)
                    task.execute();
            }
        });
    }

    ResponseListener checkButtonCategoryListener = new ResponseListener() {
        @Override
        public void callback(String response) throws JSONException {
            Intent intent = new Intent(activity.getApplicationContext(), CategoryGood.class);
            intent.putExtra("category_id", categoryID);
            intent.putExtra("category_name", categoryName);
            intent.putExtra("button_id", buttonID);
            intent.putExtra("number", number);
            activity.startActivity(intent);
        }
    };
}

class AddButtonCard extends CardView {
    private LinearLayout mAddButtonCard;
    public AddButtonCard(Context context, final Activity activity) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.add_button_card, this);

        mAddButtonCard = findViewById(R.id.add_button_card_area);
        mAddButtonCard.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity.getApplicationContext(), ChooseGood.class);
                activity.startActivity(intent);
            }
        });
    }
}