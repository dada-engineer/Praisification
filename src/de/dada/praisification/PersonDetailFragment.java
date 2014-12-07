package de.dada.praisification;

import android.os.Bundle;
import android.app.Fragment;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;


import de.dada.praisification.hostlistitem.HostListItem;
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

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PersonDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_HOSTNAME)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_person_detail, container, false);
        ratingBar = (RatingBar) rootView.findViewById(R.id.ratingBar);
        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(getResources().getColor(R.color.treeGreen), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(1).setColorFilter(getResources().getColor(R.color.lightGreen), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(0).setColorFilter(getResources().getColor(R.color.lightestGreen), PorterDuff.Mode.SRC_ATOP);
        
        // Show the dummy content as text in a TextView.
        if (mItem != null) {
           ((TextView) rootView.findViewById(R.id.detailHeader)).setText(
        		   getResources().getText(R.string.sDeatilHeader) + " " + mItem.hostName);
        }
        addListenerOnRatingBar();
        
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getActivity(), 
        		android.R.layout.simple_spinner_item);
        
        spinnerAdapter.add(getResources().getText(R.string.sDrinkSpinnerItem).toString());
        spinnerAdapter.add(getResources().getText(R.string.sFoodSpinnerItem).toString());
        spinnerAdapter.add(getResources().getText(R.string.sExtrasSpinnerItem).toString());
        spinnerAdapter.add(getResources().getText(R.string.sSelectHint).toString());
        
        Spinner spinner = (Spinner) rootView.findViewById(R.id.categorySpinner);
        spinner.setAdapter(spinnerAdapter);
        spinner.setSelection(spinnerAdapter.getCount() - 1);
        return rootView;
    }
    
    public void addListenerOnRatingBar() {     
    	//if rating value is changed,
    	ratingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
    		public void onRatingChanged(RatingBar ratingBar, float rating,
    			boolean fromUser) {
     
    			//update host-ranking data-object
     
    		}
    	});
      }
}
