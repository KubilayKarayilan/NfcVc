package com.nfcvcard;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Build;
import android.os.Parcelable;
import android.provider.MediaStore;

//import android.renderscript.*;
import android.provider.Settings;
import android.support.v8.renderscript.*;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;


import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.nfcvcard.db.DatabaseExtnd;
import com.nfcvcard.receivers.BroadCastNfc;
import com.nfcvcard.tasks.NdefGeneralTasks;
import com.nfcvcard.tasks.NdefReaderTask;
import com.nfcvcard.tasks.NdefWriterTask;


@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class MyActivity extends ActionBarActivity implements NfcAdapter.CreateNdefMessageCallback {
    private static final String TURN_NFC_ON = "turnOnNfc";
    private PendingIntent pendingIntent;
    private IntentFilter intentFilter;
    private String TAG = "frag";
    private Intent nfcSettingsIntent;
    private FileUriCallback fileUriCallback;

    SectionsPagerAdapter mSectionsPagerAdapter;
    DatabaseExtnd databaseExtnd;
    SQLiteDatabase db;
    NfcAdapter nfcAdapter;
    Bundle mainBundle = new Bundle();
    IntentFilter intent;
    NdefReaderTask ndefReaderTask;
    public static final String MIME_TEXT_PLAIN = "text/plain";
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    private File extDir;


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getActionBar().hide();
        setContentView(R.layout.activity_my);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        databaseExtnd = new DatabaseExtnd(this);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (!nfcAdapter.isEnabled() && !nfcAdapter.isNdefPushEnabled()) {
            Toast.makeText(getApplicationContext(), "Please activate: " +
                    "\nNFC AND Android Beam " +
                    "\nthan press Back to return!", Toast.LENGTH_LONG).show();
            nfcSettingsIntent = new Intent(Settings.ACTION_NFC_SETTINGS);
            nfcSettingsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(nfcSettingsIntent);
        }
        extDir = getExternalFilesDir(null);

        nfcAdapter.setNdefPushMessageCallback(this, this);
        nfcAdapter.setBeamPushUrisCallback(new FileUriCallback(extDir), this);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        handleIntent(getIntent());

    }

    @Override
    protected void onNewIntent(Intent intent) {
        Toast.makeText(this, "new intent", Toast.LENGTH_LONG).show();
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        String dataString = intent.getDataString();
        Uri urien = intent.getData();
        String action = intent.getAction();
        if (action.equals("android.intent.action.MAIN"))
            return;
        Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);
        if (null != rawMsgs) {
            NdefMessage msg = (NdefMessage) rawMsgs[0];
            // record 0 contains the MIME type, record 1 is the AAR, if present
            NdefRecord[] records = msg.getRecords();
            String incomeMsg = new String(msg.getRecords()[0].getPayload());
            String incomeMsg2 = new String(msg.getRecords()[1].getPayload());
            Log.i(TAG, incomeMsg);
            Toast.makeText(this, "incoming: " + incomeMsg + incomeMsg2, Toast.LENGTH_LONG).show();
        }
        if (null != tagFromIntent) {
            String[] techList = tagFromIntent.getTechList();
            StringBuilder stringBuilder = new StringBuilder("");
            for (String s : techList) {
                stringBuilder.append(s + "\n");
            }
            Log.i(TAG, stringBuilder.toString());
        }


    }

    @Override
    protected void onResume() {
        super.onResume();

        /**
         * It's important, that the activity is in the foreground (resumed). Otherwise
         * an IllegalStateException is thrown.
         */
        setupForegroundDispatch(this, nfcAdapter);
    }

    @Override
    protected void onPause() {
        /**
         * Call this before onPause, otherwise an IllegalArgumentException is thrown as well.
         */
        stopForegroundDispatch(this, nfcAdapter);

        super.onPause();
    }

    /**
     * @param activity The corresponding {@link Activity} requesting the foreground dispatch.
     * @param adapter  The {@link NfcAdapter} used for the foreground dispatch.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void setupForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);

        IntentFilter[] filters = new IntentFilter[1];
        String[][] techList = new String[][]{};


        adapter.enableForegroundDispatch(activity, pendingIntent, null, techList);
    }

    public static void stopForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        adapter.disableForegroundDispatch(activity);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void saveData(int id, Bundle data) {
        // based on the id you'll know which fragment is trying to save data(see below)
        // the Bundle will hold the data
        mainBundle.putBundle("" + id, data);

    }

    public Bundle getSavedData(int id) {
        // here you'll save the data previously retrieved from the fragments and
        // return it in a Bundle
        return mainBundle.getBundle(String.valueOf(id));
    }

    public SQLiteDatabase getDataBase() {
        return db;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        Bundle bundle = getSavedData(1);
        String text = ("Beam me up, Android!\n\n" +
                "Beam Time: " + System.currentTimeMillis());
        String text2 = bundle.getString("name");
       /* NdefRecord[] recordsToSend = new NdefRecord[3];
        recordsToSend[0]= NdefRecord.createMime("com.nfcvcard", text.getBytes());
        recordsToSend[1]= NdefRecord.createMime("com.nfcvcard", bundle.getString("tlf").getBytes());
         //recordsToSend[2]= NdefRecord.createMime("NfcVc app vc", bundle.getByteArray("contactPic"));
        NdefMessage msg = new NdefMessage(recordsToSend );*/

        NdefMessage msg = new NdefMessage(
                new NdefRecord[]{NdefRecord.createMime(
                        "application/com.nfcvcard", text.getBytes()), NdefRecord.createMime(
                        "application/com.nfcvcard", text2.getBytes())});
        return msg;
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).


            switch (position) {
                case 0:
                    return PlaceholderFragment.newInstance(position + 1);

                case 1:
                    return SendInfo.newInstance(position + 1);

                case 2:
                    return Contacts.newInstance(position + 1);
            }
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }

    /**
     * Use for decoding camera response data. * * @param data * @param context * @return
     */
    public static Bitmap getBitmapFromCameraData(Intent data, Context context) {
        Uri selectedImage = data.getData();
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        return BitmapFactory.decodeFile(picturePath);
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private ImageView cvPic;
        private EditText name;
        private TextView tlf;
        private TextView email;
        private ImageView logo;
        private View rootView;

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.fragment_my, container, false);
            byte[] byteArray = null;
            byte[] byteArray2 = null;
            cvPic = (ImageView) rootView.findViewById(R.id.cvpic);
            name = (EditText) rootView.findViewById(R.id.nameview);
            tlf = (TextView) rootView.findViewById(R.id.tlfview);
            email = (TextView) rootView.findViewById(R.id.emailview);
            logo = (ImageView) rootView.findViewById(R.id.logoview);

            clickListnerInit();


            Bitmap bmp = ((BitmapDrawable) cvPic.getDrawable()).getBitmap();
            if (null != bmp) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byteArray = stream.toByteArray();
            }
            Bitmap bmp2 = ((BitmapDrawable) logo.getDrawable()).getBitmap();
            if (null != bmp) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byteArray2 = stream.toByteArray();
            }

            MyActivity myActivity = (MyActivity) getActivity();
            Bundle bundle = new Bundle();
            bundle.putString("name", name.getText().toString());
            bundle.putByteArray("contactPic", byteArray);
            bundle.putByteArray("logo", byteArray2);
            bundle.putString("tlf", tlf.getText().toString());
            bundle.putString("email", email.getText().toString());
            myActivity.saveData(1, bundle);

            Bitmap bitmap = ((BitmapDrawable) cvPic.getDrawable()).getBitmap();
            //  Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 120, 120, false));
            Bitmap cropedBit = getCroppedBitmap(bitmap);
            Bitmap bluredBit = getBlurImage(bitmap);
            Drawable d = new BitmapDrawable(getResources(), Bitmap.createBitmap(cropedBit));
            Drawable backgroundD = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bluredBit, 400, 120, false));

            cvPic.setBackgroundDrawable(backgroundD);
            cvPic.setImageDrawable(d);
            cvPic.getBackground().setAlpha(Color.argb(-10, -50, -50, -50));

            return rootView;
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
        private Bitmap getBlurImage(Bitmap input) {

            RenderScript rsScript = RenderScript.create(getActivity());
            Allocation alloc = Allocation.createFromBitmap(rsScript, input);

            ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(rsScript, alloc.getElement());
            blur.setRadius(24);

            blur.setInput(alloc);

            Bitmap result = Bitmap.createBitmap(input.getWidth(), input.getHeight(), input.getConfig());
            Allocation outAlloc = Allocation.createFromBitmap(rsScript, result);
            blur.forEach(outAlloc);
            outAlloc.copyTo(result);

            rsScript.destroy();
            return result;

        }


        public Bitmap getCroppedBitmap(Bitmap bitmap) {
            Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                    bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);

            final int color = Color.WHITE;
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
            canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                    bitmap.getWidth() / 2, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);
            //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
            //return _bmp;

            return output;
        }

        private void clickListnerInit() {
            cvPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, 1001);/* */
                }
            });
            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    name.setCursorVisible(true);
                }
            });
            tlf.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tlf.setCursorVisible(true);
                }
            });
            email.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    email.setCursorVisible(true);
                }
            });
            logo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, 1002);/* */
                }
            });
        }

        @Override
        public void onPause() {
            super.onPause();
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
        }

        @Override
        public void onResume() {
            super.onResume();
        }


        public void onActivityResult(int requestCode, int resultCode, Intent data) {

            if (requestCode == 1001 && resultCode == Activity.RESULT_OK) {
                Bitmap bitmap = getBitmapFromCameraData(data, cvPic.getContext());
                Bitmap cropedBit = getCroppedBitmap(bitmap);
                Drawable d = new BitmapDrawable(getResources(), Bitmap.createBitmap(cropedBit));
                Drawable backgroundD = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(getBlurImage(bitmap), 400, 120, false));
                cvPic.setBackgroundDrawable(backgroundD);
                cvPic.setImageDrawable(d);
                cvPic.getBackground().setAlpha(Color.argb(-10, -50, -50, -50));
            }
            if (requestCode == 1002 && resultCode == Activity.RESULT_OK) {
                Bitmap bitmap = getBitmapFromCameraData(data, cvPic.getContext());
                Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, logo.getWidth(), logo.getHeight(), false));
                logo.setImageDrawable(d);
            }
        }
    }

    public static class SendInfo extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static SendInfo newInstance(int sectionNumber) {
            SendInfo fragment = new SendInfo();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public SendInfo() {

        }

        @Override
        public void onPause() {
            super.onPause();
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
        }

        @Override
        public void onResume() {
            super.onResume();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_send_info, container, false);

            return rootView;
        }
    }

    public static class Contacts extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        Bundle contactBundle = new Bundle();
        private Intent intent;

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static Contacts newInstance(int sectionNumber) {
            Contacts fragment = new Contacts();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);

            fragment.setArguments(args);

            return fragment;
        }

        public Contacts() {
        }

        @Override
        public void onPause() {
            super.onPause();
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
        }

        @Override
        public void onResume() {
            super.onResume();

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_contacts, container, false);
            MyActivity myActivity = (MyActivity) getActivity();
            contactBundle = myActivity.getSavedData(1);
            StringBuilder stringBuilder = new StringBuilder();
            ArrayList list = myActivity.databaseExtnd.getAllCotacts();
            for (int i = 0; i < list.size(); i++) {
                stringBuilder.append(list.get(i));
            }
            String s = stringBuilder.toString();
            Toast.makeText(getActivity(), contactBundle.getString("name") + " is saved budy :)" + s
                    , Toast.LENGTH_LONG).show();

            //     myActivity.databaseExtnd.insertContact("kubilay","999999","home",null,null);
            if (myActivity.nfcAdapter == null) {
                // Stop here, we definitely need NFC
                Toast.makeText(getActivity(), "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            }

            if (!myActivity.nfcAdapter.isEnabled()) {
                Toast.makeText(getActivity(), "NFC is disabled.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getActivity(), "NFC is enabled.", Toast.LENGTH_LONG).show();
            }


          /*  if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(myActivity.getIntent().getAction())) {
                Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
                if (rawMsgs != null) {
                    NdefMessage[] msgs = new NdefMessage[rawMsgs.length];
                    for (int i = 0; i < rawMsgs.length; i++) {
                        msgs[i] = (NdefMessage) rawMsgs[i];
                    }
                }
            }*/
            return rootView;
        }
    }

}
