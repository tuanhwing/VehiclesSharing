package project.com.vehiclessharing.activity;

import android.app.Activity;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TabHost;

import project.com.vehiclessharing.R;
import project.com.vehiclessharing.adapter.HistoryFragmentPagerAdapter;
import project.com.vehiclessharing.fragment.HistoryOfGraber_Fragment;

public class HistoryActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private TabLayout tabLayout;
    public static Activity activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        addControls();
        addEvents();
    }

    private void addEvents() {

    }

    private void addControls() {
        /*TabHost tabHost= (TabHost) findViewById(R.id.tabHost);//
        tabHost.setup();//lệnh này đẻ tạo 1 tabHost la nơi chứa đựng các tab
        TabHost.TabSpec tab1=tabHost.newTabSpec("tab_graber");//tạo 1 tab mới
        tab1.setIndicator("History go with needer");//chỉ hiển thị chuỗi hoặc hình nếu vừa hình vừa chuối thì phải làm 1 hình có sẵn chuỗi trong đó
        //tab1.setIndicator("",getResources().getDrawable(R.drawable.ic_accessibility_orange_a700_24dp));//hiển thị hình ảnh(hoặc hình có chứa hình và chữ)
        tab1.setContent(R.id.tabGraber);//nội dung bên trong tab này ở đây id.tab1 là linear đã include man hinh1
        tabHost.addTab(tab1);//thêm tab mới vào tabhost

        TabHost.TabSpec tab2=tabHost.newTabSpec("tab_needer");
        tab2.setIndicator("History go with graber");
        //tab2.setIndicator("",getResources().getDrawable(R.drawable.ic_motorcycle_green_900_24dp));
        tab2.setContent(R.id.tabNeeder);
        tabHost.addTab(tab2);*/
        activity=HistoryActivity.this;
        viewPager= (ViewPager) findViewById(R.id.viewpager);
        tabLayout= (TabLayout) findViewById(R.id.sliding_tabs);

        viewPager.setAdapter(new HistoryFragmentPagerAdapter(getSupportFragmentManager(),this));
        tabLayout.setupWithViewPager(viewPager);
    }
}
