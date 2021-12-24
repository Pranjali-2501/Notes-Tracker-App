package com.example.android.notestracker;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.appcompat.widget.Toolbar;
import com.github.dhaval2404.imagepicker.ImagePicker;

import com.example.android.notestracker.database.AppDatabase;
import com.example.android.notestracker.database.TaskEntry;
import com.google.android.material.bottomappbar.BottomAppBar;

import java.util.Date;

public class AddTaskActivity extends AppCompatActivity {
    // Extra for the task ID to be received in the intent
    public static final String EXTRA_TASK_ID = "extraTaskID";

    // Extra for the task ID to be received after rotation
    public static final String INSTANCE_TASK_ID = "instanceTaskID";


    // Constant for default task id to be used when not in update mode
    private static final int DEFAULT_TASK_ID = -1;
    // Constant for logging
    private static final String TAG = AddTaskActivity.class.getSimpleName();
    EditText mEditText;
    private  EditText mTitleEditText;
    private Uri mImageUri = null;
    private ImageView mImageNote;
    private String mImageString = null;
//    Button mButton;
    private BottomAppBar mBottomAppBar;

    TaskEntry intentTaskEntry;
    private int mTaskId = DEFAULT_TASK_ID;
    private final int TAKE_PHOTO_ID = 1;
    private final int ADD_IMAGE_ID = 2;
    //    Member variable for AppDatabase
    private AppDatabase mdb;
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
//        getSupportActionBar().setCustomView(R.layout.back_btn);

        initViews();
        mdb = AppDatabase.getInstance(getApplicationContext());

        mTitleEditText.setTextIsSelectable(true);
        mTitleEditText.setFocusableInTouchMode(true);
        mEditText.requestFocus();
        mEditText.setTextIsSelectable(true);

        ImageView plusImage = findViewById(R.id.plus_image);
        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_TASK_ID)) {
            mTaskId = savedInstanceState.getInt(INSTANCE_TASK_ID, DEFAULT_TASK_ID);
        }

        Intent intent = getIntent();
        if(intent != null && intent.hasExtra(EXTRA_TASK_ID)){
            setTitle("Update Task");
            if(mTaskId == DEFAULT_TASK_ID) {
                //populate the UI
                mTaskId = intent.getIntExtra(EXTRA_TASK_ID,DEFAULT_TASK_ID);
                AddTaskViewModelFactory factory = new AddTaskViewModelFactory(mdb , mTaskId);
                final AddTaskViewModel model = new ViewModelProvider(this,factory).get(AddTaskViewModel.class);
                model.getTask().observe(this, new Observer<TaskEntry>() {
                    @Override
                    public void onChanged(TaskEntry taskEntry) {
                        intentTaskEntry = taskEntry;
                        model.getTask().removeObserver(this);
                        populateUI(taskEntry);
                    }
                });


            }
        }
        mBottomAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.delete:
                        deleteNote();
                        return true;
                    case R.id.copy:
                        createCopy();
                        return true;
                    case R.id.share:
                        String note = mEditText.getText().toString().trim();
                        Intent sendIntent = new Intent(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, note);
                        sendIntent.setType("text/plain");
                        startActivity(Intent.createChooser(sendIntent, null));
                        return true;
                }
                return true;
            }
        });
        plusImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(AddTaskActivity.this, plusImage);

                // Inflating popup menu from popup_menu.xml file
                popupMenu.getMenuInflater().inflate(R.menu.plus_menu, popupMenu.getMenu());
                Menu menu = popupMenu.getMenu();
                menu.add(0, TAKE_PHOTO_ID, 1, menuIconWithText(getResources().getDrawable(R.drawable.ic_outline_camera_alt_24), getResources().getString(R.string.action_take_photo)));
                menu.add(0, ADD_IMAGE_ID, 2, menuIconWithText(getResources().getDrawable(R.drawable.ic_outline_insert_photo_24), getResources().getString(R.string.action_add_image)));

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        System.out.println("onMenuItemClick");
                        switch (item.getItemId()) {
                            case TAKE_PHOTO_ID:
                                System.out.println("Take");
                                ImagePicker.Companion.with(AddTaskActivity.this)
                                        .cameraOnly()
                                        .crop()
                                        .compress(2048)
                                        .maxResultSize(1080, 1080)
                                        .start();
                                return true;
                            case ADD_IMAGE_ID:
                                System.out.println("Add");
                                ImagePicker.Companion.with(AddTaskActivity.this)
                                        .galleryOnly()
                                        .crop()
                                        .compress(2048)
                                        .maxResultSize(1080, 1080)
                                        .start();
                                return true;
                        }
                        return true;
                    }
                });
                // Showing the popup menu
                popupMenu.show();
            }
        });
    }
    private void deleteNote()
    {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if(mTaskId == DEFAULT_TASK_ID)
                {
                    finish();
                }
                else
                {
                    mdb.taskDao().deleteTask(intentTaskEntry);
                    finish();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(mTaskId == DEFAULT_TASK_ID)
                        {
                            Toast.makeText(AddTaskActivity.this , "Can't delete  Empyt note",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
    private void createCopy()
    {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if(mTaskId == DEFAULT_TASK_ID)
                {
                    finish();
                }
                else
                {
                    TaskEntry copyTask = new TaskEntry(intentTaskEntry.getTitle(),intentTaskEntry.getText(),intentTaskEntry.getUpdatedAt(),intentTaskEntry.getImageUri());
                    mdb.taskDao().insertTask(copyTask);
                    finish();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(mTaskId == DEFAULT_TASK_ID)
                        Toast.makeText(AddTaskActivity.this,"Cannot Copy Empty Note",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        outState.putInt(INSTANCE_TASK_ID,mTaskId);
        super.onSaveInstanceState(outState);
    }

    /**
     * initViews is called from onCreate to init the member variable views
     */
    private void initViews() {
        mTitleEditText = findViewById(R.id.title);
        mEditText = findViewById(R.id.note);
        mImageNote = findViewById(R.id.image_note);
        mBottomAppBar = findViewById(R.id.bottom_bar);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: // for up arrow button
                onSave();
                Toast.makeText(this, "Note saved", Toast.LENGTH_SHORT).show();
                System.out.println("Arrow pressed");
                NavUtils.navigateUpFromSameTask(AddTaskActivity.this);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onSave(){

        String title = mTitleEditText.getText().toString().trim();
        String note = mEditText.getText().toString().trim();
        Date date = new Date();
        TaskEntry notesEntry = new TaskEntry(title, note, date, mImageString);
        AppExecutors.getInstance().diskIO().execute((new Runnable() {
            @Override
            public void run() {
                if(mTaskId == DEFAULT_TASK_ID) {
                    mdb.taskDao().insertTask(notesEntry);
                }
                else
                {
                    notesEntry.setID(mTaskId);
                    mdb.taskDao().updateTask(notesEntry);
                }
                finish();
            }
        }));
    }

    /**
     * populateUI would be called to populate the UI when in update mode
     *
     * @param task the taskEntry to populate the UI
     */
    private void populateUI(TaskEntry task) {
        if(task == null)
            return;
        mTitleEditText.setText(task.getTitle());

        mEditText.setText(task.getText());

        if(task.getImageUri() != null) {
            mImageNote.setVisibility(View.VISIBLE);
            mImageString = task.getImageUri();
            mImageUri = Uri.parse(mImageString);
            mImageNote.setImageURI(mImageUri);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.out.println("Back button pressed");
        onSave();
        Toast.makeText(this, "Note saved", Toast.LENGTH_SHORT).show();
        finish();
    }

    private CharSequence menuIconWithText(Drawable r, String title) {

        System.out.println("in charsequence" + title);
        r.setBounds(0, 0, r.getIntrinsicWidth(), r.getIntrinsicHeight());
        SpannableString sb = new SpannableString("    " + title);
        ImageSpan imageSpan = new ImageSpan(r, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return sb;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        System.out.println("in on Activityresult");
        super.onActivityResult(requestCode, resultCode, data);
        mImageUri = data.getData();
        if(mImageUri != null)
        {
            mImageString = mImageUri.toString();
            mImageNote.setVisibility(View.VISIBLE);
            mImageNote.setImageURI(mImageUri);
        }
    }




}


