package de.dada.praisification;

import android.os.Bundle;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.text.TextUtils;
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
    private DAO dao;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PersonDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dao =  new DAO(getActivity());
        if (getArguments().containsKey(ARG_HOSTNAME)) {
        	dao.open();
        	protocol = dao.getProtocolByName(getArguments().getString(ARG_HOSTNAME));
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
        
        if (getArguments().containsKey(ARG_HOSTNAME)) {
        	TextView hostHeader = (TextView)(rootView.findViewById(R.id.detailHeader));
        	hostHeader.setText(getResources().getString(R.string.sDeatilHeader) + " " +
        			getArguments().getString(ARG_HOSTNAME));
        }

        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(getResources().getColor(R.color.treeGreen), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(1).setColorFilter(getResources().getColor(R.color.lightGreen), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(0).setColorFilter(getResources().getColor(R.color.lightestGreen), PorterDuff.Mode.SRC_ATOP);
        
        // Show the dummy content as text in a TextView.
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
		// TODO Auto-generated method stub
		
	}

	public void addListenerOnRatingBar() {     
    	//if rating value is changed,
    	ratingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
    		public void onRatingChanged(RatingBar ratingBar, float rating,
    			boolean fromUser) {
    			protocol.setRating(ratingBar.getRating());
    			dao.updateProtocol(protocol);
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
				dao.updateProtocol(protocol);
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
				dao.updateProtocol(protocol);
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
						showDialog(contentList, tv);
					}
				}
				else if (s.equals(getResources().getString(R.string.sFoodSpinnerItem).toString()))
				{
					TextView tv = (TextView)rootView.findViewById(R.id.servedFoodTextView);
					if (!tv.getText().equals(""))
					{
						contentList = Arrays.asList(tv.getText().toString().split(","));
						showDialog(contentList, tv);
					}
				}
				else if (s.equals(getResources().getString(R.string.sExtrasSpinnerItem).toString()))
				{
					TextView tv = (TextView)rootView.findViewById(R.id.servedExtrasTextView);
					if (!tv.getText().equals(""))
					{
						contentList = Arrays.asList(tv.getText().toString().split(","));
						showDialog(contentList, tv);
					}
				}
				else {/*do nothing*/}
			}

			private void showDialog(final List<String> contentList, final TextView tv) {
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
		             }
		         });            
	            AlertDialog dialog = builder.create();//AlertDialog dialog; create like this outside onClick
	            dialog.show();
			}
		});
      }
    
    public void setProtocol(ProtocolContent protocol){
    	this.protocol = protocol;
    }
    
    public ProtocolContent getProtocol(){
    	return protocol;
    }
}
