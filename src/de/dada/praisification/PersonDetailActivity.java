package de.dada.praisification;

import de.dada.praisification.hostlistitem.HostListItem;
import de.dada.praisification.model.DAO;
import de.dada.praisification.model.ProtocolContent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.widget.EditText;


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
	
	private DAO dao = new DAO(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_detail);

        // Show the Up button in the action bar.
        getActionBar().setDisplayHomeAsUpEnabled(true);

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(PersonDetailFragment.ARG_HOSTNAME,
                    getIntent().getStringExtra(PersonDetailFragment.ARG_HOSTNAME));
            PersonDetailFragment fragment = new PersonDetailFragment();
            fragment.setArguments(arguments);
            getFragmentManager().beginTransaction()
                    .add(R.id.person_detail_container, fragment)
                    .commit();
        }
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
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem actionDeleteHost = menu.findItem(R.id.actionDeleteHost);
        actionDeleteHost.setOnMenuItemClickListener(this);
        
        return super.onCreateOptionsMenu(menu);
    }

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		switch (item.getItemId()) {
        	case R.id.actionDeleteHost:
        		if(((PersonDetailFragment) getFragmentManager()
	                    .findFragmentById(R.id.person_detail_container)) != null)
        		{
        			ProtocolContent protocol = ((PersonDetailFragment) getFragmentManager()
    	                    .findFragmentById(R.id.person_detail_container)).getProtocol();
        			((PersonDetailFragment) getFragmentManager()
    	                    .findFragmentById(R.id.person_detail_container)).getDao().deleteProtocol(protocol);
            		Intent i = getBaseContext().getPackageManager()
            	             .getLaunchIntentForPackage( getBaseContext().getPackageName() );
            		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            		startActivity(i);
        		}
        		break;
        	default: break;
			}
		
		return false;
	}
}
