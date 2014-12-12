package de.dada.praisification;

import de.dada.praisification.hostlistitem.HostListItem;
import de.dada.praisification.model.DAO;
import de.dada.praisification.model.ProtocolContent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.app.Activity;
import android.app.AlertDialog;




/**
 * An activity representing a list of People. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link PersonDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link PersonListFragment} and the item details
 * (if present) is a {@link PersonDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link PersonListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class PersonListActivity extends Activity
        implements PersonListFragment.Callbacks, MenuItem.OnMenuItemClickListener{

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private ProtocolContent protocol;
    private DAO dao = new DAO(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_list);
        if (findViewById(R.id.person_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            ((PersonListFragment) getFragmentManager()
                    .findFragmentById(R.id.person_list))
                    .setActivateOnItemClick(true);
        }

        // TODO: If exposing deep links into your app, handle intents here.
    }

    /**
     * Callback method from {@link PersonListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String hostName) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(PersonDetailFragment.ARG_HOSTNAME, hostName);
            PersonDetailFragment fragment = new PersonDetailFragment();
            fragment.setArguments(arguments);
            getFragmentManager().beginTransaction()
            .replace(R.id.person_detail_container, fragment)
            .addToBackStack("detail");
            getFragmentManager().beginTransaction()
                    .replace(R.id.person_detail_container, fragment)
                    .commit();
        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, PersonDetailActivity.class);
            detailIntent.putExtra(PersonDetailFragment.ARG_HOSTNAME, hostName);
            startActivity(detailIntent);
        }
        this.protocol= new ProtocolContent(hostName);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem actionNewHost = menu.findItem(R.id.actionNewHost);
        actionNewHost.setOnMenuItemClickListener(this);
        if(((PersonDetailFragment) getFragmentManager()
                .findFragmentById(R.id.person_detail_container)) != null)
        {
        	MenuItem actionDeleteHost = menu.findItem(R.id.actionDeleteHost);
        	actionDeleteHost.setOnMenuItemClickListener(this);
        }
        
        return super.onCreateOptionsMenu(menu);
    }

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		switch (item.getItemId()) {
        	case R.id.actionDeleteHost:
        		if(((PersonDetailFragment) getFragmentManager()
	                    .findFragmentById(R.id.person_detail_container)) != null)
        		{
        			getDao().deleteProtocol(protocol);
            		((PersonListFragment) getFragmentManager()
    	                    .findFragmentById(R.id.person_list)).deleteHostItem(protocol.getName());
            		Intent i = getBaseContext().getPackageManager()
            	             .getLaunchIntentForPackage( getBaseContext().getPackageName() );
            		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            		startActivity(i);
        		}
        		break;
        	case R.id.actionNewHost:
        		LayoutInflater li = LayoutInflater.from(this);
				View promptsView = li.inflate(R.layout.dialog, null);
 
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						this);
 
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
							protocol = new ProtocolContent(userInput.getText().toString());
							getDao().createProtocol(protocol);
							HostListItem item = new HostListItem(userInput.getText().toString());
							PersonListFragment plf = ((PersonListFragment) getFragmentManager()
				                    .findFragmentById(R.id.person_list));
							plf.updateHostList(item);
							int position = plf.ITEMS.size() - 1;
				        	plf.getListView().requestFocusFromTouch();
				        	plf.getListView().setSelection(position);
				        	plf.getListView().performItemClick(plf.getListView().
				        			getAdapter().getView(position, null, null),
				        			position, position);
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
				break;
        	default: break;
			}
		
		return false;
	}

	public DAO getDao() {
		return dao;
	}

	public void setDao(DAO dao) {
		this.dao = dao;
	}
	
	public ProtocolContent getProtocol() {
		return protocol;
	}

	public void setProtocol(ProtocolContent protocol) {
		this.protocol = protocol;
	}
}
