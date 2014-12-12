package de.dada.praisification;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.dada.praisification.hostlistitem.HostListItem;
import de.dada.praisification.model.DAO;
import de.dada.praisification.model.ProtocolContent;

/**
 * A fragment representing a single Person detail screen.
 * This fragment is either contained in a {@link PersonListActivity}
 * in two-pane mode (on tablets) or a {@link PersonDetailActivity}
 * on handsets.
 */
public class PersonDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_HOSTNAME = "hostname";

    /**getResources().getText(R.string.sDrinkSpinnerItem).toString()
     * The dummy content this fragment is presenting.
     */
    private HostListItem mItem;
    private RatingBar ratingBar;
    private ProtocolContent protocol;
    private Button arrivalTimeButton;
    private Button leavingTimeButton;
    private Button addContentButton;
    private Button removeContentButton;
    private Button imageButton;
    private DAO dao;

	private String mCurrentPhotoPath;
	
	static final int REQUEST_TAKE_PHOTO = 1;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PersonDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDao(new DAO(getActivity()));
        if (getArguments().containsKey(ARG_HOSTNAME)) {
        	getDao().open();
        	protocol = getDao().getProtocolByName(getArguments().getString(ARG_HOSTNAME));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_person_detail, container, false);
        ratingBar = (RatingBar) rootView.findViewById(R.id.ratingBar);
        arrivalTimeButton = (Button) rootView.findViewById(R.id.arrivalButton);
        leavingTimeButton = (Button) rootView.findViewById(R.id.leavingButton);
        addContentButton = (Button) rootView.findViewById(R.id.addContentButton);
        removeContentButton = (Button) rootView.findViewById(R.id.removeContentButton);
        imageButton = (Button) rootView.findViewById(R.id.thumbnailView);
        
        if (getArguments().containsKey(ARG_HOSTNAME)) {
        	TextView hostHeader = (TextView)(rootView.findViewById(R.id.detailHeader));
        	hostHeader.setText(getResources().getString(R.string.sDeatilHeader) + " " +
        			getArguments().getString(ARG_HOSTNAME));
        }

        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(getResources().getColor(R.color.treeGreen), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(1).setColorFilter(getResources().getColor(R.color.lightGreen), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(0).setColorFilter(getResources().getColor(R.color.lightestGreen), PorterDuff.Mode.SRC_ATOP);
        
        if (mItem != null) {
           ((TextView) rootView.findViewById(R.id.detailHeader)).setText(
        		   getResources().getText(R.string.sDeatilHeader) + " " + mItem.hostName);
        }
        
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getActivity(), 
        		android.R.layout.simple_spinner_item);
        
        spinnerAdapter.add(getResources().getText(R.string.sDrinkSpinnerItem).toString());
        spinnerAdapter.add(getResources().getText(R.string.sFoodSpinnerItem).toString());
        spinnerAdapter.add(getResources().getText(R.string.sExtrasSpinnerItem).toString());
        
        Spinner spinner = (Spinner) rootView.findViewById(R.id.categorySpinner);
        spinner.setAdapter(spinnerAdapter);
        spinner.setSelection(0);
        addListenerOnRatingBar();
        addListenerOnButtons(rootView);
        loadViewContent(rootView);
        
        return rootView;
    }
    
    private void loadViewContent(View rootView) {
    	TextView arrival = (TextView) rootView.findViewById(R.id.arrivalDateTextView);
		TextView leaving = (TextView) rootView.findViewById(R.id.leavingDateTextView);
		TextView drinks = (TextView) rootView.findViewById(R.id.servedDrinksTextView);
		TextView food = (TextView) rootView.findViewById(R.id.servedFoodTextView);
		TextView extras = (TextView) rootView.findViewById(R.id.servedExtrasTextView);
		
		arrival.setText(protocol.getArrivalTime());
		leaving.setText(protocol.getDepatureTime());
		drinks.setText(protocol.getDrinks());
		food.setText(protocol.getFood());
		extras.setText(protocol.getExtras());
		ratingBar.setRating(protocol.getRating());
		
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
    		public void onRatingChanged(RatingBar ratingBar, float rating,
    			boolean fromUser) {
    			protocol.setRating(ratingBar.getRating());
    			getDao().updateProtocol(protocol);
    		}
    	});
      }
    
    public void addListenerOnButtons(final View rootView) {    	
    	arrivalTimeButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
				Date date = new Date();
				TextView dateTextView = (TextView) rootView.findViewById(R.id.arrivalDateTextView);
				dateTextView.setText(sdf.format(date));
				protocol.setArrivalTime(sdf.format(date));
				getDao().updateProtocol(protocol);
			}
		});
    	
    	leavingTimeButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
				Date date = new Date();
				TextView dateTextView = (TextView) rootView.findViewById(R.id.leavingDateTextView);
				dateTextView.setText(sdf.format(date));
				protocol.setDepatureTime(sdf.format(date));
				getDao().updateProtocol(protocol);
			}
		});
    	
    	addContentButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				LayoutInflater li = LayoutInflater.from(getActivity());
				View promptsView = li.inflate(R.layout.dialog, null);
 
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						getActivity());
 
				// set dialog.xml to alert dialog builder
				alertDialogBuilder.setView(promptsView);
 
				final EditText userInput = (EditText) promptsView
						.findViewById(R.id.editTextDialogUserInput);
 
				// set dialog message
				alertDialogBuilder
					.setCancelable(false)
					.setPositiveButton(getResources().getText(R.string.sOK),
					  new DialogInterface.OnClickListener() {
					    public void onClick(DialogInterface dialog,int id) {
						// get user input and set it to result
						// edit text
						pushToView(((Spinner)rootView.findViewById(R.id.categorySpinner)).getSelectedItem().toString(),
								userInput.getText().toString());
					    }
					  })
					.setNegativeButton("Cancel",
					  new DialogInterface.OnClickListener() {
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
					TextView tv = (TextView)rootView.findViewById(R.id.servedDrinksTextView);
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
					TextView tv = (TextView)rootView.findViewById(R.id.servedFoodTextView);
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
					TextView tv = (TextView)rootView.findViewById(R.id.servedExtrasTextView);
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
				getDao().updateProtocol(protocol);
			}
		});
    	
    	removeContentButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				removeformView(((Spinner)rootView.findViewById(R.id.categorySpinner)).getSelectedItem().toString(),
						"userInput from Dialog");				
			}

			private void removeformView(String s, String content) {
				List<String> contentList = new ArrayList<String>();
				if (s.equals(getResources().getString(R.string.sDrinkSpinnerItem).toString()))
				{
					TextView tv = (TextView)rootView.findViewById(R.id.servedDrinksTextView);
					if (!tv.getText().equals(""))
					{
						contentList = Arrays.asList(tv.getText().toString().split(","));
						showDialog(contentList, tv, 1);
					}
				}
				else if (s.equals(getResources().getString(R.string.sFoodSpinnerItem).toString()))
				{
					TextView tv = (TextView)rootView.findViewById(R.id.servedFoodTextView);
					if (!tv.getText().equals(""))
					{
						contentList = Arrays.asList(tv.getText().toString().split(","));
						showDialog(contentList, tv, 2);
					}
				}
				else if (s.equals(getResources().getString(R.string.sExtrasSpinnerItem).toString()))
				{
					TextView tv = (TextView)rootView.findViewById(R.id.servedExtrasTextView);
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

	            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
						getDao().updateProtocol(protocol);
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
					getDao().updateProtocol(protocol);
				}
				else
				{
					AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	            	builder.setMessage(getResources().getString(R.string.sImageButtonSecondHit).toString())
	            	       .setPositiveButton(getResources().getString(R.string.sShow).toString(),
	            	    		   new DialogInterface.OnClickListener() {
	            	           public void onClick(DialogInterface dialog, int id) {
	            	        	   Intent intent = new Intent(getActivity().getBaseContext(), ShowPictureActivity.class);
	            	        	   intent.putExtra("PATH", protocol.getPicturePath());
	            	        	   startActivityForResult(intent, 1);
	            	           }
	            	       })
	            	       .setNegativeButton(getResources().getString(R.string.sRetake).toString(),
	            	    		   new DialogInterface.OnClickListener() {
	            	           public void onClick(DialogInterface dialog, int id) {
	            	        	   dispatchTakePictureIntent();
	           						protocol.setPicturePath(mCurrentPhotoPath);
	           						getDao().updateProtocol(protocol);
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
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            	builder.setMessage(getResources().getString(R.string.sError).toString())
            	       .setCancelable(false)
            	       .setPositiveButton(getResources().getString(R.string.sOK).toString(), 
            	    		   new DialogInterface.OnClickListener() {
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
    
    public void setProtocol(ProtocolContent protocol){
    	this.protocol = protocol;
    }
    
    public ProtocolContent getProtocol(){
    	return protocol;
    }

	public DAO getDao() {
		return dao;
	}

	public void setDao(DAO dao) {
		this.dao = dao;
	}
}
