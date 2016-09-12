package com.okry.newstuff.activity;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.okry.newstuff.R;
import com.orhanobut.logger.Logger;

import java.util.Arrays;
import java.util.Random;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class LamdbaActivity extends AppCompatActivity {
    @InjectView(R.id.tv1)
    TextView mTv1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lamdba);
        ButterKnife.inject(this);
        mTv1.setOnClickListener(v -> Snackbar.make(mTv1, "xiaogang", Snackbar.LENGTH_SHORT).show());
        sortText();
        postTest();
        String s = "Effect=Noraml|effect-kfk|kkjk";
        String[] array = s.split("\\|");
        for (String s1 : array) {
            Logger.d("s is:" + s1);
        }
        Logger.d("random start======");
        long startTime = System.currentTimeMillis();
        //int[] randomInts = randomIntWithoutRepeat(4);
        int[] randomInts = randomArray(0, 3, 4);
        for (int i = 0; i < 4; i++) {
            Logger.d("random int is:" + randomInts[i]);
        }
        Logger.d("random use time:" + (System.currentTimeMillis() - startTime));
    }

    private void sortText() {
        String[] datas = new String[]{"peng", "zhao", "li"};
        Arrays.sort(datas, (v1, v2) -> {
            return Integer.compare(v1.length(), v2.length());
        });
    }

    /**
     * 产生[0,n)的不重复的随机数
     *
     * @param n
     * @return
     */
    private int[] randomIntWithoutRepeat(final int n) {
        int[] randomInts = new int[n];
        Random random = new Random();
        for (int i = 0; i < n; i++) {
            if (i == n - 1) {
                //最后一个直接找,提高效率
                randomInts[i] = findLastRandom(randomInts, n);
            } else {
                randomInts[i] = random.nextInt(n);
            }
            for (int j = 0; j < i; j++) {
                if (randomInts[j] == randomInts[i]) {
                    randomInts[i] = random.nextInt(n);
                    j = -1;
                }
            }
        }
        return randomInts;
    }

    private int findLastRandom(int[] randomInts, int n) {
        boolean[] booleanArray = new boolean[n];
        for (int i = 0; i < n; i++) {
            booleanArray[i] = false;
        }
        for (int i = 0; i < n - 1; i++) {
            booleanArray[randomInts[i]] = true;
        }
        for (int i = 0; i < n; i++) {
            if (!booleanArray[i]) {
                return i;
            }
        }
        return 0;
    }

    public static int[] randomArray(int min, int max, int n) {
        int len = max - min + 1;

        if (max < min || n > len) {
            return null;
        }

        //初始化给定范围的待选数组
        int[] source = new int[len];
        for (int i = min; i < min + len; i++) {
            source[i - min] = i;
        }

        int[] result = new int[n];
        Random rd = new Random();
        int index = 0;
        for (int i = 0; i < result.length; i++) {
            //待选数组0到(len-2)随机一个下标
            index = Math.abs(rd.nextInt() % len--);
            //将随机到的数放入结果集
            result[i] = source[index];
            //将待选数组中被随机到的数，用待选数组(len-1)下标对应的数替换
            source[index] = source[len];
        }
        return result;
    }

    private void postTest() {
        mTv1.post(() -> {
            Logger.d("post");
            Logger.d("post2");
        });
    }
}
