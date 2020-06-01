package ru.kahn.recordvoice.activity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

import ru.kahn.recordvoice.R;
import ru.kahn.recordvoice.fragments.PageArchive;
import ru.kahn.recordvoice.fragments.PageVoice;
import ru.kahn.recordvoice.other.PermissionCheck;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    ViewPager pager;
    AdapterFragmentPager pagerAdapter;
    String tagFragmentVoice, tagFragmentArchive;

    @SuppressLint("NewApi")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!PermissionCheck.accessAll(this)) {
            //здесь прописать блокировку кнопок чтобы избежать вылета
        }

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextAppearance(this, R.style.AppTheme);
        toolbar.setTitle(getResources().getString(R.string.main_title));
        setSupportActionBar(toolbar);

        pager = findViewById(R.id.pager_main);
        pagerAdapter = new AdapterFragmentPager(getSupportFragmentManager());
        pager.setOffscreenPageLimit(1); //к-во страниц сохраняемых в памяти слева и справа от текущей
        pager.setAdapter(pagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tab_main);
        tabLayout.setupWithViewPager(pager);

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            Objects.requireNonNull(tab).setCustomView(pagerAdapter.getTabView(i));
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

    }

    @SuppressLint("NewApi")
    @Override
    protected void onDestroy() {
        pagerAdapter.getItem(0).onDestroy();
        pagerAdapter.getItem(1).onDestroy();
        super.onDestroy();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public class AdapterFragmentPager extends FragmentPagerAdapter {

        public AdapterFragmentPager(FragmentManager fm) {
            super(fm);
        }

        private final PageVoice fragmentPageVoice = PageVoice.newInstance();
        private final PageArchive fragmentPageArchive = PageArchive.newInstance();

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return fragmentPageVoice;
                case 1:
                    return fragmentPageArchive;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getResources().getStringArray(R.array.page_main_title_array)[position];
        }

        private View getTabView(int position) {
            View tab = LayoutInflater.from(MainActivity.this).inflate(R.layout.tab_main, null);
            TextView tv = tab.findViewById(R.id.tv_tab_main);
            tv.setText(getPageTitle(position));
            return tab;
        }
    }

    public String getTagFragmentVoice() {
        return tagFragmentVoice;
    }

    public void setTagFragmentVoice(String tagFragmentVoice) {
        this.tagFragmentVoice = tagFragmentVoice;
    }

    public String getTagFragmentArchive() {
        return tagFragmentArchive;
    }

    public void setTagFragmentArchive(String tagFragmentArchive) {
        this.tagFragmentArchive = tagFragmentArchive;
    }

    public ViewPager getPager() {
        return pager;
    }
}
