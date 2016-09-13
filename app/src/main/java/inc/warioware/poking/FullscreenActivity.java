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
import android.graphics.Rect;
import android.widget.Toast;

import java.util.HashSet;

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
    ImageView[] rocks, players, walls;
    Button left,right;
    Runnable move_rock;

    int height;
    int width;
    int _speed;
    DisplayMetrics metrics = new DisplayMetrics();



    public static boolean in(int low, int high, int n) {
        return n >= low && n <= high;
    }

    public static boolean boundsY (ImageView pad, ImageView obj) {
        float aHalf = pad.getHeight()/2;
        return ((pad.getY()-aHalf)>=obj.getBottom())&&((pad.getY()+aHalf)<=obj.getBottom()+obj.getHeight());
    }

    public float getHitPoint (ImageView pad) {
        float hit_point = 0;
        for (ImageView rock : rocks)
            if (boundsY(pad, rock))
                hit_point = rock.getX();
        if (hit_point == 0)
            for (ImageView wall : walls)
                if (boundsY(pad, wall))
                    hit_point = wall.getX();
        else
            hit_point += 10;
        return hit_point;
    }

     final Runnable r1 = new Runnable() {

        public int direction = 15;
        public void run() {
            players[0].setY(players[0].getY()+direction);
            if ((players[0].getY() >= (height - (height/10)) ) || (players[0].getY() <= height/10))
                direction = -direction;
            handler.postDelayed(this, 5);
        }
    };
     final Runnable r2 = new Runnable() {
        public int direction = -15;
        @Override
        public void run() {
            players[1].setY(players[1].getY()+direction);
            if ((players[1].getY() >= (height - (height/10))) || (players[1].getY() <= height/10))
                direction = -direction;
            handler.postDelayed(this, 5);
        }
    };

    final Runnable r_hit = new Runnable() {
        int direction = 15;
        float hit_point = 0;
        @Override
        public void run() {
            if (hit_point == 0)
                hit_point = getHitPoint(players[0]);
            players[0].setX(players[0].getX()+direction);
            if (players[0].getX() <= width/20) {
                direction = -direction;
                players[0].setX(players[0].getX()+direction);
                handler.removeCallbacks(this);
                left.setEnabled(true);
                handler.post(r1);
            }
            else
                handler.postDelayed(this, direction);
            if ((players[0].getX()+players[0].getWidth()) >= hit_point)
                direction = -direction;

        }
    };

    final Runnable b_hit = new Runnable() {
        int direction = -15;
        float hit_point = 0;
        @Override
        public void run() {
            if (hit_point == 0)
                hit_point = getHitPoint(players[0]);
            players[1].setX(players[1].getX()+direction);
            if ((players[1].getX()+players[1].getWidth()) >= (width)-25) {
                direction = -direction;
                players[1].setX(players[1].getX()+direction);
                handler.removeCallbacks(this);
                right.setEnabled(true);
                handler.post(r2);
            }
            else
                handler.postDelayed(this, direction);

            if ((players[1].getX()) <= hit_point)
                direction = -direction;

        }
    };

    final Runnable rock_check = new Runnable() {
        @Override
        public void run() {
            Rect rect1 = new Rect();
            Rect rect2 = new Rect();
            for (ImageView rock : rocks) {
                rock.getDrawingRect(rect1);
                for (ImageView p : players) {
                    p.getDrawingRect(rect2);
                    if (Rect.intersects(rect1, rect2))
                        handler.post(gen_rock_move(rock, (p == red)));
                }
            }
            // re-post this runnable
        }
    };

    private Runnable gen_rock_move(final ImageView rock, final boolean right){
        return new Runnable() {
            public void run(){
                int direction = right ? 10 : -10;
                rock.setX(rock.getX()+direction);

                float cheto = rock.getX();
                int chetoesche = width/2-rock.getWidth();

                if (rock.getX() == width/2-rock.getWidth())
                    /*             МОМЕНТ ПАДЕНИЯ
                    * move.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(ObjectAnimator animation) {
                        super.onAnimationEnd(animation);
                        fall(rock);
                    }

                });*/

                if ((rock.getX()+rock.getWidth()) >= (width)-25) {
                    direction = -direction;
                    rock.setX(rock.getX()+direction);
                    handler.removeCallbacks(this);
                    handler.post(r2);
                }
                else
                    handler.postDelayed(this, direction);

                if ((rock.getX()) <= width/2)
                    direction = -direction;
            }
        };
    }


    public void R(View view) {
        handler.removeCallbacks(r1);
        left.setEnabled(false);
  /*      ImageView[] rocks = new ImageView[] {rock1,rock2,rock3};
        left.setEnabled(false);/*
        ImageView[] rocks = new ImageView[] {rock1,rock2,rock3};
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

    public void onStartButtonClick(View view)
    {
        Button start_b = (Button)findViewById(R.id.start_button);
        start_b.setEnabled(false);
        right.setEnabled(true);
        left.setEnabled(true);
        handler.post(r1);
        handler.post(r2);
        handler.post(rock_check);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        width = metrics.heightPixels;
        height = metrics.widthPixels;

        setContentView(R.layout.activity_fullscreen);
        // rotate the screen
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);
        
        rocks = new ImageView[] {
            (ImageView)findViewById(R.id.rock1), 
            (ImageView)findViewById(R.id.rock2),
            (ImageView)findViewById(R.id.rock3)
        };
        
        players = new ImageView[] {
            (ImageView)findViewById(R.id.imageView),
            (ImageView)findViewById(R.id.imageView2)
        };

        walls = new ImageView[] {
            (ImageView)findViewById(R.id.upperWall),
            (ImageView)findViewById(R.id.midWall1),
            (ImageView)findViewById(R.id.midWall2),
            (ImageView)findViewById(R.id.lowerWall)
        };

        handler = new Handler();
        gen_rock_move(rocks[0],true);

        players[0].setX(width/50);
        players[1].setX(width/30);


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
