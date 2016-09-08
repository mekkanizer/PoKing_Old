package inc.warioware.poking;

import android.animation.TimeInterpolator;
import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.support.annotation.Dimension;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {

    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */

    private Handler handler;

    // выводим сообщение
    //Toast.makeText(this, "Зачем вы нажали?", Toast.LENGTH_SHORT).show();

    ImageView red,blue;
    ImageView rock1,rock2,rock3;
    Button left,right;

    int height;
    int width;
    int _speed;
    DisplayMetrics metrics = new DisplayMetrics();



    public static boolean in(int low, int high, int n) {
        return n >= low && n <= high;
    }

    public static boolean bounds (ImageView pad, ImageView rock) {
        float aHalf = pad.getHeight()/2;
        return ((pad.getY()-aHalf)>=rock.getBottom())&&((pad.getY()+aHalf)<=rock.getBottom()+rock.getHeight());
    }

     final Runnable r1 = new Runnable() {

        public int direction = 15;
        public void run() {
            red.setY(red.getY()+direction);
            if ((red.getY() >= (height - (height/10)) ) || (red.getY() <= height/10))
                direction = -direction;
            handler.postDelayed(this, 5);
        }
    };
     final Runnable r2 = new Runnable() {
        public int direction = -15;
        @Override
        public void run() {
            blue.setY(blue.getY()+direction);
            if ((blue.getY() >= (height - (height/10))) || (blue.getY() <= height/10))
                direction = -direction;
            handler.postDelayed(this, 5);
        }
    };

    final Runnable r_hit = new Runnable() {
        int direction = step;
        @Override
        public void run() {
            if (bounds(red,rock1))


            red.setX(red.getX()+direction);
            if (red.getX() <= 25) {
                direction = -direction;
                red.setX(red.getX()+direction);
                handler.removeCallbacks(this);
                left.setEnabled(true);
                handler.post(r1);
            }
            else
                handler.postDelayed(this, speed);

            if ((red.getX() >= 460))// && (in()))
                direction = -direction;
        }
    };

    /*
    * if (red.getX() == 460)
	      if (blue.getX() != 670) {
**/

    final int step = 3;
    final int speed = 5;

    final Runnable b_hit = new Runnable() {
        int direction = -step;
        @Override
        public void run() {
            blue.setX(blue.getX()+direction);
            if ((blue.getX() <= 670))// && ())
                direction = -direction;
            if (blue.getX() >= 1100) {
                direction = -direction;
                blue.setX(blue.getX()+direction);
                handler.removeCallbacks(this);
                right.setEnabled(true);
                handler.post(r2);
            }
            else
                handler.postDelayed(this, speed);
        }
    };

    public int calc_flight() {
        return step * speed;
    }

    public void fall(ImageView rock) {
//        rock.setX(rock.getParent().getHeight());
    }

    public void R(View view) {
        handler.removeCallbacks(r1);
        left.setEnabled(false);
  /*      ImageView[] rocks = new ImageView[] {rock1,rock2,rock3};
        for (final ImageView rock : rocks) {
            if (bounds(red,rock)) {
                ObjectAnimator move = ObjectAnimator.ofFloat(rock, "translationX", +10f);
                move.setDuration(1000);

                move.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(ObjectAnimator animation) {
                        super.onAnimationEnd(animation);
                        fall(rock);
                    }

                });

                move.setStartDelay(calc_flight());
                if (move.getStartDelay() > 0)
                    move.start();
            }

        }*/
        handler.post(r_hit);
    }

    public void B(View view) {
        handler.removeCallbacks(r2);
        right.setEnabled(false);
        handler.post(b_hit);
    }

    public void onMyButtonClick(View view)
    {
        // getX for collision
        // think of precalculation
        // ANDDDD
        // is there a way to bypass constant array of wall and rock borders?
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        height = metrics.heightPixels;
        width = metrics.widthPixels;

        handler = new Handler();
        handler.post(r1);
        handler.post(r2);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);
        // rotate the screen
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);

        rock1 = (ImageView)findViewById(R.id.rock1);
        rock2 = (ImageView)findViewById(R.id.rock2);
        rock3 = (ImageView)findViewById(R.id.rock3);
        red = (ImageView)findViewById(R.id.imageView);
        blue = (ImageView)findViewById(R.id.imageView2);
        left = (Button)findViewById(R.id.l);
        right = (Button)findViewById(R.id.r);

        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}
