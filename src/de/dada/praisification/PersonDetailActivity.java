package de.dada.praisification;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.dada.praisification.model.DAO;
import de.dada.praisification.model.ProtocolContent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.RatingBar.OnRatingBarChangeListener;


/**
 * An activity representing a single Person detail screen. This
 * activity is only used on handset devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link PersonListActivity}.
 * <p>
 * This activity is mostly just a 'shell' activity containing nothing
 * more than a {@link PersonDetailFragment}.
 */
public class PersonDetailActivity extends Activity implements OnMenuItemClickListener {
	
	private String host;
    private RatingBar ratingBar;
    private RatingBar treeBar;
    private ProtocolContent protocol;
    private Button arrivalTimeButton;
    private Button leavingTimeButton;
    private Button addContentButton;
    private Button removeContentButton;
    private Button imageButton;
	private DAO dao = new DAO(this);
	private String mCurrentPhotoPath;
	
	static final int REQUEST_TAKE_PHOTO = 1;
	public static final String ARG_HOSTNAME = "hostname";
	private int RESULT_DELETED_HOST = -1;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_detail);
        Intent i = getIntent();
        // Show the Up button in the action bar.
        getActionBar().setDisplayHomeAsUpEnabled(true);
        if (i.hasExtra(ARG_HOSTNAME)) {
        	dao.open();
        	protocol = dao.getProtocolByName(i.getStringExtra(ARG_HOSTNAME));
        	TextView hostHeader = (TextView)(this.findViewById(R.id.detailHeader));
        	hostHeader.setText(getResources().getString(R.string.sDeatilHeader) + " " +
        			protocol.getName());
        }
        
        ratingBar = (RatingBar) this.findViewById(R.id.ratingBar);
		treeBar = (RatingBar) this.findViewById(R.id.treeRatingBar);
        arrivalTimeButton = (Button) this.findViewById(R.id.arrivalButton);
        leavingTimeButton = (Button) this.findViewById(R.id.leavingButton);
        addContentButton = (Button) this.findViewById(R.id.addContentButton);
        imageButton = (Button) this.findViewById(R.id.thumbnailView);
        
        addListenerOnRatingBar();
        addListenerOnButtons(this);
        loadViewContent();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            navigateUpTo(new Intent(this, PersonListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
 
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        MenuItem actionDeleteHost = menu.findItem(R.id.actionDeleteHost);
        actionDeleteHost.setOnMenuItemClickListener(this);
        
        return super.onCreateOptionsMenu(menu);
    }

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		switch (item.getItemId()) {
        	case R.id.actionDeleteHost:
        	{
        		dao.deleteProtocol(protocol);
				setResult(RESULT_DELETED_HOST);
        		finish();
        		break;
        	}
        	default: break;
			}
		
		return false;
	}
	
	private void loadViewContent() {
    	TextView arrival = (TextView) this.findViewById(R.id.arrivalDateTextView);
		TextView leaving = (TextView) this.findViewById(R.id.leavingDateTextView);
		TextView drinks = (TextView) this.findViewById(R.id.servedDrinksTextView);
		TextView food = (TextView) this.findViewById(R.id.servedFoodTextView);
		TextView extras = (TextView) this.findViewById(R.id.servedExtrasTextView);

        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(1).setColorFilter(getResources().getColor(R.color.lightGray), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(0).setColorFilter(getResources().getColor(R.color.lightestGreen), PorterDuff.Mode.SRC_ATOP);
        
        stars = (LayerDrawable) treeBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(1).setColorFilter(getResources().getColor(R.color.lightGray), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(0).setColorFilter(getResources().getColor(R.color.lightestGreen), PorterDuff.Mode.SRC_ATOP);
        
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, 
        		R.layout.spinner_item);
        
        spinnerAdapter.add(getResources().getText(R.string.sDrinkSpinnerItem).toString());
        spinnerAdapter.add(getResources().getText(R.string.sFoodSpinnerItem).toString());
        spinnerAdapter.add(getResources().getText(R.string.sExtrasSpinnerItem).toString());
        
        Spinner spinner = (Spinner) this.findViewById(R.id.categorySpinner);
        spinner.setAdapter(spinnerAdapter);
        spinner.setSelection(0);
		
		arrival.setText(protocol.getArrivalTime());
		leaving.setText(protocol.getDepatureTime());
		drinks.setText(protocol.getDrinks());
		food.setText(protocol.getFood());
		extras.setText(protocol.getExtras());
		ratingBar.setRating(protocol.getRating());
		treeBar.setRating(protocol.getTreeRating());
		
		if(!(protocol.getPicturePath().equals("")))
		{
			if(Drawable.createFromPath(protocol.getPicturePath()) != null)
			{
				imageButton.setBackground(Drawable.createFromPath(protocol.getPicturePath()));
			}
			else
				protocol.setPicturePath("");
		}
		else
			imageButton.setBackground(getResources().getDrawable(R.drawable.placeholder));
		
	}

	public void addListenerOnRatingBar() {     
    	//if rating value is changed,
    	ratingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
    		@Override
			public void onRatingChanged(RatingBar ratingBar, float rating,
    			boolean fromUser) {
    			protocol.setRating(ratingBar.getRating());
    			dao.updateProtocol(protocol);
    		}
    	});
    	
    	treeBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
    		@Override
			public void onRatingChanged(RatingBar ratingBar, float rating,
    			boolean fromUser) {
    			protocol.setTreeRating(treeBar.getRating());
    			dao.updateProtocol(protocol);
    		}
    	});
      }
    
    public void addListenerOnButtons(final Activity a) { 
        removeContentButton = (Button) this.findViewById(R.id.removeContentButton);

    	arrivalTimeButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
				Date date = new Date();
				TextView dateTextView = (TextView) a.findViewById(R.id.arrivalDateTextView);
				dateTextView.setText(sdf.format(date));
				protocol.setArrivalTime(sdf.format(date));
				dao.updateProtocol(protocol);
			}
		});
    	
    	leavingTimeButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
				Date date = new Date();
				TextView dateTextView = (TextView) a.findViewById(R.id.leavingDateTextView);
				dateTextView.setText(sdf.format(date));
				protocol.setDepatureTime(sdf.format(date));
				dao.updateProtocol(protocol);
			}
		});
    	
    	addContentButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				LayoutInflater li = LayoutInflater.from(a);
				View promptsView = li.inflate(R.layout.dialog, null);
 
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(a);
 
				// set dialog.xml to alert dialog builder
				alertDialogBuilder.setView(promptsView);
 
				final EditText userInput = (EditText) promptsView
						.findViewById(R.id.editTextDialogUserInput);
 
				// set dialog message
				alertDialogBuilder
					.setCancelable(false)
					.setPositiveButton(getResources().getText(R.string.sOK),
					  new DialogInterface.OnClickListener() {
					    @Override
						public void onClick(DialogInterface dialog,int id) {
						// get user input and set it to result
						// edit text
						pushToView(((Spinner) a.findViewById(R.id.categorySpinner)).getSelectedItem().toString(),
								userInput.getText().toString());
					    }
					  })
					.setNegativeButton("Cancel",
					  new DialogInterface.OnClickListener() {
					    @Override
						public void onClick(DialogInterface dialog,int id) {
						dialog.cancel();
					    }
					  });
 
				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();
 
				// show it
				alertDialog.show();
			}

			private void pushToView(String s, String content) {
				if (s.equals(getResources().getString(R.string.sDrinkSpinnerItem).toString()))
				{
					TextView tv = (TextView) a.findViewById(R.id.servedDrinksTextView);
					if (tv.getText().equals(""))
					{
						tv.setText(tv.getText() + content);
						protocol.setDrinks(tv.getText().toString());
					}
					else
					{
						tv.setText(tv.getText() + ", " + content);
						protocol.setDrinks(tv.getText().toString());
					}
				}
				else if (s.equals(getResources().getString(R.string.sFoodSpinnerItem).toString()))
				{
					TextView tv = (TextView) a.findViewById(R.id.servedFoodTextView);
					if (tv.getText().equals(""))
					{
						tv.setText(tv.getText() + content);
						protocol.setDrinks(tv.getText().toString());
					}
					else
					{
						tv.setText(tv.getText() + ", " + content);
						protocol.setDrinks(tv.getText().toString());
					}
				}
				else if (s.equals(getResources().getString(R.string.sExtrasSpinnerItem).toString()))
				{
					TextView tv = (TextView) a.findViewById(R.id.servedExtrasTextView);
					if (tv.getText().equals(""))
					{
						tv.setText(tv.getText() + content);
						protocol.setDrinks(tv.getText().toString());
					}
					else
					{
						tv.setText(tv.getText() + ", " + content);
						protocol.setDrinks(tv.getText().toString());
					}
				}
				else {/*do nothing*/}
				dao.updateProtocol(protocol);
			}
		});
    	
    	removeContentButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				removeformView(((Spinner) a.findViewById(R.id.categorySpinner)).getSelectedItem().toString(),
						"userInput from Dialog");				
			}

			private void removeformView(String s, String content) {
				List<String> contentList = new ArrayList<String>();
				if (s.equals(getResources().getString(R.string.sDrinkSpinnerItem).toString()))
				{
					TextView tv = (TextView) a.findViewById(R.id.servedDrinksTextView);
					if (!tv.getText().equals(""))
					{
						contentList = Arrays.asList(tv.getText().toString().split(","));
						showDialog(contentList, tv, 1);
					}
				}
				else if (s.equals(getResources().getString(R.string.sFoodSpinnerItem).toString()))
				{
					TextView tv = (TextView) a.findViewById(R.id.servedFoodTextView);
					if (!tv.getText().equals(""))
					{
						contentList = Arrays.asList(tv.getText().toString().split(","));
						showDialog(contentList, tv, 2);
					}
				}
				else if (s.equals(getResources().getString(R.string.sExtrasSpinnerItem).toString()))
				{
					TextView tv = (TextView) a.findViewById(R.id.servedExtrasTextView);
					if (!tv.getText().equals(""))
					{
						contentList = Arrays.asList(tv.getText().toString().split(","));
						showDialog(contentList, tv, 3);
					}
				}
				else {/*do nothing*/}
			}

			private void showDialog(final List<String> contentList, final TextView tv, final int num) {
				final String[] items = (String[]) contentList.toArray();
	            final ArrayList<Integer> selectedItems = new ArrayList<Integer>();

	            AlertDialog.Builder builder = new AlertDialog.Builder(a);
	            builder.setTitle(getResources().getString(R.string.sDialogDeleteHeader).toString());
	            builder.setMultiChoiceItems(items, null,
	                    new DialogInterface.OnMultiChoiceClickListener() {
				             @Override
				             public void onClick(DialogInterface dialog, int indexSelected,
				                     boolean isChecked) {
				                 if (isChecked) 
				                 {
				                     // If the user checked the item, add it to the selected items
				                     selectedItems.add(Integer.valueOf(indexSelected));
				                 } 
				                 else if (selectedItems.contains(indexSelected)) 
				                 {
				                     // Else, if the item is already in the array, remove it
				                     selectedItems.remove(Integer.valueOf(indexSelected));
				                 }
				             }
				         })
		          // Set the action buttons
		         .setPositiveButton(getResources().getText(R.string.sOK),
		        		 new DialogInterface.OnClickListener() {
		             @Override
		             public void onClick(DialogInterface dialog, int id) {
		                 //  Your code when user clicked on OK
		                 //  You can write the code  to save the selected item here
		            	 String text = "";
		            	 for(String s: contentList){
		            		 if (!selectedItems.contains(Integer.valueOf(contentList.indexOf(s))))
		            		 	{
		            		 		if (text.equals(""))
		            		 		text = s;
			            		 	else
			            		 		text += ", " + s;
		            		 	}
		            	 }
						 tv.setText(text);
						 switch (num) {
						case 1:
							protocol.setDrinks(text);
							break;
						case 2:
							protocol.setFood(text);
							break;
						case 3:
							protocol.setExtras(text);
							break;
						default:
							break;
						}
						dao.updateProtocol(protocol);
		             }
		         });            
	            AlertDialog dialog = builder.create();//AlertDialog dialog; create like this outside onClick
	            dialog.show();
			}
		});
    	imageButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(protocol.getPicturePath().equalsIgnoreCase(""))
				{
					dispatchTakePictureIntent();
					protocol.setPicturePath(mCurrentPhotoPath);
					dao.updateProtocol(protocol);
				}
				else
				{
					AlertDialog.Builder builder = new AlertDialog.Builder(a);
	            	builder.setTitle(getResources().getString(R.string.sImageButtonSecondHit).toString())
	            	       .setPositiveButton(getResources().getString(R.string.sShow).toString(),
	            	    		   new DialogInterface.OnClickListener() {
	            	           @Override
							public void onClick(DialogInterface dialog, int id) {
	            	        	   Intent intent = new Intent(a.getBaseContext(), ShowPictureActivity.class);
	            	        	   intent.putExtra("PATH", protocol.getPicturePath());
	            	        	   startActivityForResult(intent, 1);
	            	           }
	            	       })
	            	       .setNegativeButton(getResources().getString(R.string.sRetake).toString(),
	            	    		   new DialogInterface.OnClickListener() {
	            	           @Override
							public void onClick(DialogInterface dialog, int id) {
	            	        	   dispatchTakePictureIntent();
	           						protocol.setPicturePath(mCurrentPhotoPath);
	           						dao.updateProtocol(protocol);
	            	           }
	            	       });
	            	AlertDialog alert = builder.create();
	            	alert.show();
				}
			}
		});
      }
    
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).
        		format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",         /* suffix */
            storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
    
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            	AlertDialog.Builder builder = new AlertDialog.Builder(this);
            	builder.setMessage(getResources().getString(R.string.sError).toString())
            	       .setCancelable(false)
            	       .setPositiveButton(getResources().getString(R.string.sOK).toString(), 
            	    		   new DialogInterface.OnClickListener() {
            	           @Override
						public void onClick(DialogInterface dialog, int id) {
            	                dialog.cancel();
            	           }
            	       });
            	AlertDialog alert = builder.create();
            	alert.show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode,
            Intent data) {
    	if(resultCode == 0)
		{	
			if(Drawable.createFromPath(protocol.getPicturePath()) == null)
			{
				File f =new File(mCurrentPhotoPath);
				f.delete();
	    		imageButton.setBackground(getResources().getDrawable(R.drawable.placeholder));
	    		protocol.setPicturePath("");
			}
		}
    	else
    		imageButton.setBackground(Drawable.createFromPath(protocol.getPicturePath()));
    }

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}
}
